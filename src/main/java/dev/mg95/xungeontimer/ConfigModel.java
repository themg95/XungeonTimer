package dev.mg95.xungeontimer;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.ExcludeFromScreen;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Sync;

import java.util.ArrayList;
import java.util.List;

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

    @Sync(Option.SyncMode.INFORM_SERVER)
    public boolean resetOnDeath = true;

    @ExcludeFromScreen
    public int placementOffset = 5;

    @ExcludeFromScreen
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public List<String> splits = new ArrayList<>();
}
