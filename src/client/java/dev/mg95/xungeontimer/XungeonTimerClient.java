package dev.mg95.xungeontimer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.apache.commons.lang3.time.StopWatch;

import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Duration;
import java.util.List;
import java.util.LinkedHashMap;

public class XungeonTimerClient implements ClientModInitializer {
    public StopWatch timer = new StopWatch();
    public boolean inXungeon = false;
    public boolean wasDead = false;
    public Duration igt = Duration.ZERO;
    public LinkedHashMap<String, Duration> splits = new LinkedHashMap<>();

    public static final dev.mg95.xungeontimer.TimerConfig CONFIG = dev.mg95.xungeontimer.TimerConfig.createAndLoad();

    @Override
    public void onInitializeClient() {
        loadSplits();
        networking();

        HudRenderCallback.EVENT.register(new CustomHudOverlay(this));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            resetTimer();
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!inXungeon || !CONFIG.resetOnDeath()) return;
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return;

            if (player.isAlive() && wasDead) startTimer();
            else if (player.isDead() && !timer.isStopped()) {
                resetTimer();
                inXungeon = true;
            }
            wasDead = player.isDead();
        });


        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("reset_timer").executes(context -> {
            resetTimer();
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
        inXungeon = false;
        igt = Duration.ZERO;
        splits.clear();
        loadSplits();
        timer.reset();
    }

    public void startTimer() {
        resetTimer();
        inXungeon = true;
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
        Gson gson = new Gson();
        try (Reader reader = new InputStreamReader(
                XungeonTimerClient.class.getResourceAsStream("/assets/xungeontimer/splits.json"))) {

            List<String> splitNames = gson.fromJson(reader, new TypeToken<List<String>>() {
            }.getType());
            for (String split : splitNames) {
                splits.put(split, Duration.ZERO);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
