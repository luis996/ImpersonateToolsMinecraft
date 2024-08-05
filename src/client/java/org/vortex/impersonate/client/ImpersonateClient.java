package org.vortex.impersonate.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vortex.impersonate.client.commands.*;
public class ImpersonateClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Impersonate");
    @Override
    public void onInitializeClient() {
        LOGGER.info("Client loaded.");

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            LOGGER.info("Getting commands ready...");
            forceop.register(dispatcher, ClientPlayConnectionEvents.JOIN);
            loadbackdoors.register(dispatcher);
            impersonate.register(dispatcher);
            forcefly.register(dispatcher);
            forceunfly.register(dispatcher);
            forcestop.register(dispatcher, ClientPlayConnectionEvents.JOIN);
            LOGGER.info("Got it!");
        });
    }
}
