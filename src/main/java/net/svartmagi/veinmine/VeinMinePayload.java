package net.svartmagi.veinmine;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.svartmagi.Svartmagi;

/**
 * Sendes fra klient KUN naar veinmine-tasten endrer tilstand
 * (trykkes/slippes) - aldri per tick.
 */
public record VeinMinePayload(boolean active) implements CustomPacketPayload {
    public static final Type<VeinMinePayload> TYPE = new Type<>(Svartmagi.id("veinmine"));

    public static final StreamCodec<io.netty.buffer.ByteBuf, VeinMinePayload> STREAM_CODEC =
            ByteBufCodecs.BOOL.map(VeinMinePayload::new, VeinMinePayload::active);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
