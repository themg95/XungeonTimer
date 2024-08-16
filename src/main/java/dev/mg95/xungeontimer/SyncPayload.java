package dev.mg95.xungeontimer;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;

public record SyncPayload(String splitNames) implements CustomPayload {
    public static final Id<SyncPayload> ID = new Id<>(PacketConstants.SYNC_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, SyncPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, SyncPayload::splitNames, SyncPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}