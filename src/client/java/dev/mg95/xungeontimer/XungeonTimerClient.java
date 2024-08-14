package dev.mg95.xungeontimer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.apache.commons.lang3.time.StopWatch;
import dev.mg95.xungeontimer.TimerConfig;

import java.util.Objects;

public class XungeonTimerClient implements ClientModInitializer {
    private StopWatch timer = new StopWatch();
    private String username;
    private boolean inXungeon = false;
    private boolean wasDead = false;


    public static final TimerConfig CONFIG = TimerConfig.createAndLoad();

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(new CustomHudOverlay(timer));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            resetTimer();
        });

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (message.getString().endsWith("has been Imprisoned!")) {
                username = Objects.requireNonNullElse(MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(MinecraftClient.getInstance().player.getUuid()).getDisplayName(), MinecraftClient.getInstance().player.getDisplayName()).getString();
                if (!Objects.equals(message.getString(), String.format("%s has been Imprisoned!", username))) return;
                startTimer();
            } else if (Objects.equals(message.getString(), String.format("%s has escaped the Prison!", username))) {
                stopTimer();
            }
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
}
