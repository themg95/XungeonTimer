package dev.mg95.xungeontimer;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record StartPayload() implements CustomPayload {
    public static final CustomPayload.Id<StartPayload> ID = new CustomPayload.Id<>(PacketConstants.START_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, StartPayload> CODEC = PacketCodec.unit(new StartPayload());

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}