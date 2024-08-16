package dev.mg95.xungeontimer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.text.Text;
import org.apache.commons.lang3.time.StopWatch;

import java.time.Duration;
import java.util.List;
import java.util.LinkedHashMap;

public class XungeonTimerClient implements ClientModInitializer {
    public StopWatch timer = new StopWatch();
    public Duration igt = Duration.ZERO;
    public LinkedHashMap<String, Duration> splits = new LinkedHashMap<>();

    public static final dev.mg95.xungeontimer.TimerConfig CONFIG = XungeonTimer.CONFIG;

    @Override
    public void onInitializeClient() {
        loadSplits();
        networking();

        HudRenderCallback.EVENT.register(new CustomHudOverlay(this));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            resetTimer();
        });


        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("reset_timer").executes(context -> {
            resetTimer();
            ClientPlayNetworking.send(new ResetPayload());
            context.getSource().sendFeedback(Text.literal("Reset the timer"));
            return 1;
        })));
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("start_timer").executes(context -> {
            startTimer();
            context.getSource().sendFeedback(Text.literal("Started the timer"));
            return 1;
        })));
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("stop_timer").executes(context -> {
            stopTimer();
            context.getSource().sendFeedback(Text.literal("Stopped the timer"));
            return 1;
        })));
    }

    public void resetTimer() {
        igt = Duration.ZERO;
        splits.clear();
        loadSplits();
        timer.reset();
    }

    public void startTimer() {
        resetTimer();
        timer.start();
    }

    public void stopTimer() {
        timer.suspend();
    }

    public Duration getIGT() {
        return igt;
    }

    public LinkedHashMap<String, Duration> getSplits() {
        return splits;
    }

    public void networking() {
        ClientPlayNetworking.registerGlobalReceiver(StartPayload.ID, (payload, context) -> {
            context.client().execute(this::startTimer);
        });

        ClientPlayNetworking.registerGlobalReceiver(EndPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                stopTimer();
                igt = Duration.ofMillis(payload.time());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(SplitPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                var time = Duration.ofMillis(payload.time());
                var name = payload.name();
                igt = time;
                splits.put(name, time);
            });
        });
    }

    public void loadSplits() {

        List<String> splitNames = CONFIG.splits();
        for (String split : splitNames) {
            splits.put(split, Duration.ZERO);
        }

    }
}
