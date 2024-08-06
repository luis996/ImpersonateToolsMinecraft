package org.vortex.impersonate.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.vortex.impersonate.client.misc.GameJoinEventHelper;

public class test {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("test")
                .then(ClientCommandManager.literal("chat")
                        .then(ClientCommandManager.argument("chatMessage", StringArgumentType.greedyString())
                                .executes(test::execute))));
    }

    public static int execute(CommandContext<FabricClientCommandSource> context) {
        GameJoinEventHelper.registerOneTimeCallback(() -> {
            context.getSource().getPlayer().networkHandler.sendChatMessage(StringArgumentType.getString(context, "chatMessage"));
        });
        return 1;
    }
}
