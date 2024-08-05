package org.vortex.impersonate.client.commands;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.session.Session;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import org.vortex.impersonate.client.misc.SessionManager;
import org.vortex.impersonate.client.mixin.MinecraftAccesor;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class forceop {
    private static boolean forceOpAlreadyRegistered = false;
    public static Session ogSession;
    public static String opNm;
    public static String printOppedText = "undefined";
    public static ServerInfo lastServer;
    public static AtomicBoolean forceopCommandExecuted = new AtomicBoolean(false);
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, Event<ClientPlayConnectionEvents.Join> onJoin) {
        dispatcher.register(ClientCommandManager.literal("forceop")
                .then(ClientCommandManager.argument("playerToOp", StringArgumentType.string())
                        .then(ClientCommandManager.argument("playerWithOp", StringArgumentType.string())
                                .executes(forceop::execute))));
        if (!forceOpAlreadyRegistered) {
            forceOpAlreadyRegistered = true;
            onJoin.register((handler, sender, client1) -> {
                if (forceopCommandExecuted.compareAndSet(true, false)) {
                    /* This will execute when the mod is 100% sure
                     * to op the player.
                     * @param opNm */
                    (new Thread(() -> {
                        handler.sendChatCommand("op " + opNm);
                        try {
                            Thread.sleep(550);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        handler.getConnection().disconnect(Text.literal("Opped " + opNm));

                        MinecraftAccesor accessor = (MinecraftAccesor) client1;

                        accessor.imp$session(ogSession);
                        RenderSystem.recordRenderCall(() -> ConnectScreen.connect(client1.currentScreen, client1, ServerAddress.parse(lastServer.address), lastServer, true, null));
                        printOppedText = "Opped " + opNm;
                    })).start();
                }
                if (!Objects.equals(printOppedText, "undefined")) {
                    assert client1.player != null;
                    client1.player.sendMessage(Text.literal(printOppedText));
                    printOppedText = "undefined";
                }
            });
        }
    }

    public static int execute(CommandContext<FabricClientCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "playerWithOp");
        MinecraftClient client = MinecraftClient.getInstance();
        ServerInfo serverInfo = client.getCurrentServerEntry();
        assert serverInfo != null;
        ServerAddress serverAddress = ServerAddress.parse(serverInfo.address);
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        assert networkHandler != null;
        ClientConnection clientConnection = networkHandler.getConnection();
        assert clientConnection != null;
        assert client.player != null;
        lastServer = serverInfo;
        opNm = StringArgumentType.getString(context, "playerToOp");
        clientConnection.disconnect(Text.literal("Disconnected"));
        ogSession = client.getSession();
        Session session = SessionManager.getCrackedSession(playerName);
        (new Thread(() -> {
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            MinecraftAccesor accessor = (MinecraftAccesor) client;
            accessor.imp$session(session);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            RenderSystem.recordRenderCall(() -> ConnectScreen.connect(client.currentScreen, client, serverAddress, serverInfo, true, null));
            forceopCommandExecuted.set(true);

        })).start();

        return 1;
    }
}
