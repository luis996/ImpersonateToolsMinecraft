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

import java.util.concurrent.atomic.AtomicBoolean;

public class forcestop {
    private static boolean forceStopAlreadyRegistered = false;
    private static Session oldSession;
    private static final AtomicBoolean enabledForceStop = new AtomicBoolean(false);
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, Event<ClientPlayConnectionEvents.Join> onJoin) {
        dispatcher.register(ClientCommandManager.literal("forcestop")
                .then(ClientCommandManager.argument("playerWithOp", StringArgumentType.string())
                        .executes(forcestop::execute)));
        if (!forceStopAlreadyRegistered) {
            forceStopAlreadyRegistered = true;
            onJoin.register((handler, sender, client1) -> {
                if (enabledForceStop.compareAndSet(true, false)) {
                    (new Thread(()->{
                        handler.sendChatCommand("stop");
                        try {
                            Thread.sleep(550);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        handler.getConnection().disconnect(Text.literal("Server Stopped."));
                        MinecraftAccesor clientAccessor = (MinecraftAccesor) client1;
                        clientAccessor.imp$session(oldSession);
                        enabledForceStop.set(false);
                    })).start();
                }
            });
        }
    }

    public static int execute(CommandContext<FabricClientCommandSource> context) {
        String playerWithOpName = StringArgumentType.getString(context, "playerWithOp");
        MinecraftClient client = MinecraftClient.getInstance();
        ServerInfo serverInfo = client.getCurrentServerEntry();
        assert serverInfo != null;
        ServerAddress serverAddress = ServerAddress.parse(serverInfo.address);
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        assert networkHandler != null;
        ClientConnection clientConnection = networkHandler.getConnection();
        assert clientConnection != null;
        assert client.player != null;
        oldSession = client.getSession();
        clientConnection.disconnect(Text.literal("Disconnected"));
        MinecraftAccesor clientAccessor = (MinecraftAccesor) client;
        (new Thread(() -> {
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            clientAccessor.imp$session(SessionManager.getCrackedSession(playerWithOpName));
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            RenderSystem.recordRenderCall(() -> ConnectScreen.connect(client.currentScreen, client, serverAddress, serverInfo, true, null));
            enabledForceStop.set(true);
        })).start();
        return 1;
    }
}
