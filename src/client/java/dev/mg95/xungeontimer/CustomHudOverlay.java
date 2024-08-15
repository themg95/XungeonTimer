package dev.mg95.xungeontimer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.apache.commons.lang3.time.StopWatch;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomHudOverlay implements HudRenderCallback {
    private final StopWatch timer;
    private final XungeonTimerClient main;
    private Integer ww;
    private Integer wh;

    public CustomHudOverlay(XungeonTimerClient main) {
        this.timer = main.timer;
        this.main = main;
    }

    public static final dev.mg95.xungeontimer.TimerConfig CONFIG = XungeonTimerClient.CONFIG;

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        if (timer.getDuration().isZero()) return;


        Duration elapsed = this.timer.getDuration();

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        ww = drawContext.getScaledWindowWidth();
        wh = drawContext.getScaledWindowHeight();


        drawRTA(textRenderer, drawContext, elapsed);
        drawIGT(textRenderer, drawContext, main.getIGT());
        drawSplits(textRenderer, drawContext, main.getSplits());

    }

    public String formatDuration(Duration duration) {
        Integer milliseconds = duration.toMillisPart();
        Integer seconds = duration.toSecondsPart();
        Long minutes = duration.toMinutes();

        return String.format("%02d:%02d:%03d", minutes, seconds, milliseconds);
    }

    public void drawRTA(TextRenderer textRenderer, DrawContext drawContext, Duration rta) {
        var text = "RTA: " + formatDuration(rta);

        var w = textRenderer.getWidth(text);
        var h = textRenderer.fontHeight;

        int ww_divided = (int) Math.round(ww / 1.5);
        int wh_divided = (int) Math.round(wh / 1.5);

        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(1.5F, 1.5F, 1.5F);

        var offset = CONFIG.placementOffset();

        int x, y;
        switch (CONFIG.position()) {
            case TopRight -> {
                x = ww_divided - w - offset;
                y = offset;
            }
            case BottomLeft -> {
                x = offset;
                y = wh_divided - h - offset;
            }
            case BottomRight -> {
                x = ww_divided - w - offset;
                y = wh_divided - h - offset;
            }
            case null, default -> {
                x = offset;
                y = offset;
            }
        }

        drawContext.drawTextWithShadow(textRenderer, text, x, y, -1);
        drawContext.getMatrices().pop();
    }

    public void drawIGT(TextRenderer textRenderer, DrawContext drawContext, Duration igt) {
        var text = "IGT: " + formatDuration(igt);

        var w = textRenderer.getWidth(text);
        var h = (int) Math.round(textRenderer.fontHeight * 1.5);

        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(1F, 1F, 1F);

        var offset = (int) Math.round(CONFIG.placementOffset() * 1.5);

        int x, y;
        switch (CONFIG.position()) {
            case TopRight -> {
                x = ww - w - offset;
                y = (int) Math.round(offset * 1.5) + h;
            }
            case BottomLeft -> {
                x = offset;
                y = wh - h - offset - h;
            }
            case BottomRight -> {
                x = ww - w - offset;
                y = wh - h - offset - h;
            }
            case null, default -> {
                x = offset;
                y = (int) Math.round(offset * 1.5) + h;
            }
        }

        drawContext.drawTextWithShadow(textRenderer, text, x, y, -1);
        drawContext.getMatrices().pop();
    }

    public void drawSplits(TextRenderer textRenderer, DrawContext drawContext, LinkedHashMap<String, Duration> splits) {
        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(1F, 1F, 1F);

        var offset = (int) Math.round(CONFIG.placementOffset() * 1.5);

        int index = 0;
        for (var split : splits.entrySet()) {
            index++;
            drawSplit(textRenderer, drawContext, offset, split, index);
        }

        drawContext.getMatrices().pop();
    }

    public void drawSplit(TextRenderer textRenderer, DrawContext drawContext, int offset, Map.Entry<String, Duration> split, int index) {
        var text = String.format("%s: %s", split.getKey(), formatDuration(split.getValue()));
        int x, y;

        var w = textRenderer.getWidth(text);
        var h = (int) Math.round(textRenderer.fontHeight * 1.5);
        switch (CONFIG.position()) {
            case TopRight -> {
                x = ww - w - offset;
                y = (int) Math.round(offset * 1.5) + h + h * index;
            }
            case BottomLeft -> {
                x = offset;
                y = wh - h - offset - h - h * index;
            }
            case BottomRight -> {
                x = ww - w - offset;
                y = wh - h - offset - h - h * index;
            }
            case null, default -> {
                x = offset;
                y = (int) Math.round(offset * 1.5) + h + h * index;
            }
        }

        drawContext.drawTextWithShadow(textRenderer, text, x, y, -1);
    }

}


