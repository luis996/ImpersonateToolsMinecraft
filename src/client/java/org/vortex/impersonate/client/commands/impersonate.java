package org.vortex.impersonate.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class impersonate {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("impersonate")
                .then(ClientCommandManager.argument("player", StringArgumentType.string())
                        .then(ClientCommandManager.argument("text", StringArgumentType.greedyString())
                                .executes(impersonate::execute))));
    }

    public static int execute(CommandContext<FabricClientCommandSource> context) {
        if (!context.getSource().getPlayer().hasPermissionLevel(4)) {
            context.getSource().sendFeedback(Text.literal("§4Can't inject. Get OP to do that."));
            return 1;
        }
        context.getSource().getPlayer().networkHandler.sendChatCommand("tellraw @a {\"text\":\"<" + StringArgumentType.getString(context, "player") + "> " + StringArgumentType.getString(context, "text") + "\"}");
        return 1;
    }
}
