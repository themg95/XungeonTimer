package dev.mg95.xungeontimer;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;

@Modmenu(modId = "xungeontimer")
@Config(name = "xungeontimer", wrapperName = "TimerConfig")
public class ConfigModel {
    public enum Position {
        TopLeft,
        TopRight,
        BottomLeft,
        BottomRight
    }

    public Position position = Position.TopLeft;

    public boolean resetOnDeath = false;

    public int placementOffset = 5;
}
