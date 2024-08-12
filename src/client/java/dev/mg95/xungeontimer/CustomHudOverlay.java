package dev.mg95.xungeontimer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.apache.commons.lang3.time.StopWatch;

import java.time.Duration;

public class CustomHudOverlay implements HudRenderCallback {
    private StopWatch timer;

    public CustomHudOverlay(StopWatch timer) {
        this.timer = timer;
    }

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

        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(1.5F, 1.5F, 1.5F);
        drawContext.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, text, 5, 5, -1);
        drawContext.getMatrices().pop();
    }

}