package org.vortex.impersonate.client.misc;

import net.minecraft.client.session.Session;
import org.vortex.impersonate.client.ImpersonateClient;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class SessionManager {
    public static Session getCrackedSession(String username) {
        ImpersonateClient.LOGGER.info("Got call to Impersonate Session Manager.");
        return new Session(
                username, uuid(username), "", Optional.empty(), Optional.empty(), Session.AccountType.LEGACY
        );
    }
    public static UUID uuid(String name) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
    }
}
