package com.frostbound.network;

import com.frostbound.client.ClientTempData;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientPayloadHandler {
    public static void handle(TempSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientTempData.update(payload.temp(), payload.trend(), payload.storm()));
    }

    private ClientPayloadHandler() {}
}
