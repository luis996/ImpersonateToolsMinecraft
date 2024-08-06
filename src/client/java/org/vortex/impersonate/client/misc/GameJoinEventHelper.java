package org.vortex.impersonate.client.misc;

import java.util.ArrayList;
import java.util.List;

public class GameJoinEventHelper {

    private static final List<GameJoinCallback> oneTimeCallbacks = new ArrayList<>();

    public static void registerOneTimeCallback(GameJoinCallback callback) {
        oneTimeCallbacks.add(callback);
    }

    public static void executeCallbacks() {
        for (GameJoinCallback callback : oneTimeCallbacks) {
            callback.execute();
        }
        oneTimeCallbacks.clear();
    }
}
