package org.vortex.impersonate.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import java.util.Objects;
import java.util.concurrent.Executors;


public class forceunfly {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("forceunfly")
                .executes(forceunfly::execute));
    }

    public static int execute(CommandContext<FabricClientCommandSource> context) {
        ClientPlayerEntity player = context.getSource().getPlayer();
        player.getAbilities().allowFlying = false;
        player.getAbilities().creativeMode = false;
        player.getAbilities().invulnerable = false;
        player.sendAbilitiesUpdate();
        player.sendMessage(Text.literal("[!] NoFall (NoGround) Mixin: OFF"));
        Objects.requireNonNull(context.getSource().getClient().interactionManager).setGameMode(GameMode.SURVIVAL);
        forcefly.noGroundEnabled = false;
        forcefly.scheduler.shutdownNow();
        forcefly.scheduler = Executors.newScheduledThreadPool(1);
        return 1;
    }
}