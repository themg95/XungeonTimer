package dev.mg95.xungeontimer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.apache.commons.lang3.time.StopWatch;
import dev.mg95.xungeontimer.TimerConfig;

import java.time.Duration;

public class CustomHudOverlay implements HudRenderCallback {
    private StopWatch timer;

    public CustomHudOverlay(StopWatch timer) {
        this.timer = timer;
    }

    public static final TimerConfig CONFIG = XungeonTimerClient.CONFIG;

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        if (timer.getDuration().isZero()) return;

        Duration elapsed = this.timer.getDuration();

        Integer milliseconds = elapsed.toMillisPart();
        Integer seconds = elapsed.toSecondsPart();
        Long minutes = elapsed.toMinutes();

        String text = String.format("%02d:%02d:%03d", minutes, seconds, milliseconds);

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(1.5F, 1.5F, 1.5F);
        var ww = drawContext.getScaledWindowWidth();
        var wh = drawContext.getScaledWindowHeight();
        var w = textRenderer.getWidth(text);
        var h = textRenderer.fontHeight;

        var offset = CONFIG.placementOffset();

        int x, y;
        switch (CONFIG.position()) {
            case TopRight -> {
                x = (int) (ww / 1.5 - w) - offset;
                y = offset;
            }
            case BottomLeft -> {
                x = offset;
                y = (int) (wh / 1.5 - h) - offset;
            }
            case BottomRight -> {
                x = (int) (ww / 1.5 - w) - offset;
                y = (int) (wh / 1.5 - h) - offset;
            }
            case null, default -> {
                x = offset;
                y = offset;
            }
        }

        System.out.println(ww);

        drawContext.drawTextWithShadow(textRenderer, text, x, y, -1);
        drawContext.getMatrices().pop();
    }

}