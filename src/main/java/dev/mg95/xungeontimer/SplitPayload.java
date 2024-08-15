package dev.mg95.xungeontimer;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;


public record SplitPayload(long time, String name) implements CustomPayload {
    public static final CustomPayload.Id<SplitPayload> ID = new CustomPayload.Id<>(PacketConstants.SPLIT_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, SplitPayload> CODEC = PacketCodec.tuple(PacketCodecs.VAR_LONG, SplitPayload::time, PacketCodecs.STRING, SplitPayload::name, SplitPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static String getName(String name) {return name;}
}