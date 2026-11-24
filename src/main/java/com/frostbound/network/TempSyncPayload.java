package com.frostbound.network;

import com.frostbound.Frostbound;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Сервер -> клиент: температура тела, тренд (-1 остывает, 0 стабильно, 1 согревается) и сила бури. */
public record TempSyncPayload(float temp, byte trend, float storm) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TempSyncPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(Frostbound.MODID, "temp_sync"));

    public static final StreamCodec<ByteBuf, TempSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, TempSyncPayload::temp,
            ByteBufCodecs.BYTE, TempSyncPayload::trend,
            ByteBufCodecs.FLOAT, TempSyncPayload::storm,
            TempSyncPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
