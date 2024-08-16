package dev.mg95.xungeontimer;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record ResetPayload() implements CustomPayload {
    public static final Id<ResetPayload> ID = new Id<>(PacketConstants.RESET_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, ResetPayload> CODEC = PacketCodec.unit(new ResetPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}