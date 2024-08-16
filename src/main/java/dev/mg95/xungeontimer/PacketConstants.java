package dev.mg95.xungeontimer;

import net.minecraft.util.Identifier;

public class PacketConstants {
    public static final Identifier START_PACKET_ID = Identifier.of("xungeon", "start");
    public static final Identifier END_PACKET_ID = Identifier.of("xungeon", "end");
    public static final Identifier SPLIT_PACKET_ID = Identifier.of("xungeon", "split");
    public static final Identifier PREFERENCE_PACKET_ID = Identifier.of("xungeon", "preference");
    public static final Identifier RESET_PACKET_ID = Identifier.of("xungeon", "reset");
    public static final Identifier SYNC_PACKET_ID = Identifier.of("xungeon", "sync");
}
