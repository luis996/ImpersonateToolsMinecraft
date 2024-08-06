package org.vortex.impersonate.client.commands;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.session.Session;
import net.minecraft.text.Text;
import org.vortex.impersonate.client.ImpersonateClient;
import org.vortex.impersonate.client.misc.GameJoinEventHelper;
import org.vortex.impersonate.client.misc.SessionManager;
import org.vortex.impersonate.client.mixin.MinecraftAccesor;

import java.util.Collection;

public class forcekickalleach {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("forcekickall")
                .then(ClientCommandManager.literal("eachNoOpInjectMethod")
                        .executes(forcekickalleach::execute)));
    }

    public static int execute(CommandContext<FabricClientCommandSource> context) {
        Collection<PlayerListEntry> playerList = context.getSource().getPlayer().networkHandler.getPlayerList();
        (new Thread(() -> {
            ServerInfo serverInfo = MinecraftClient.getInstance().getCurrentServerEntry();
            assert serverInfo != null;
            playerList.forEach(playerListEntry -> {
                Session ogSession = MinecraftClient.getInstance().getSession();
                ImpersonateClient.LOGGER.info(playerListEntry.getProfile().getName());
                ClientPlayNetworkHandler handler = context.getSource().getPlayer().networkHandler;
                handler.getConnection().disconnect(Text.literal("Kicking..."));
                MinecraftAccesor clientAccessor = (MinecraftAccesor) MinecraftClient.getInstance();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                clientAccessor.imp$session(SessionManager.getCrackedSession(playerListEntry.getProfile().getName()));
                RenderSystem.recordRenderCall(() -> ConnectScreen.connect(MinecraftClient.getInstance().currentScreen, MinecraftClient.getInstance(), ServerAddress.parse(serverInfo.address), serverInfo, true, null));
                GameJoinEventHelper.registerOneTimeCallback(() -> handler.getConnection().disconnect(Text.literal("Kicking...")));
                try {
                    Thread.sleep(720);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                clientAccessor.imp$session(ogSession);
            });
        })).start();
        return 1;
    }
}