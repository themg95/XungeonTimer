package dev.mg95.xungeontimer;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record EndPayload(long time) implements CustomPayload {
    public static final CustomPayload.Id<EndPayload> ID = new CustomPayload.Id<>(PacketConstants.END_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, EndPayload> CODEC = PacketCodec.tuple(PacketCodecs.VAR_LONG, EndPayload::time, EndPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}