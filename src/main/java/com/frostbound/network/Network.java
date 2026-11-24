package com.frostbound.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class Network {
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(TempSyncPayload.TYPE, TempSyncPayload.STREAM_CODEC, ClientPayloadHandler::handle);
    }

    private Network() {}
}
