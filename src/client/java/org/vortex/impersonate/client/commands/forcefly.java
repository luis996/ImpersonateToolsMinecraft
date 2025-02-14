package org.vortex.impersonate.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class forcefly {
    public static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public static boolean noGroundEnabled;
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("forcefly").executes(forcefly::execute));
    }

    public static int execute(CommandContext<FabricClientCommandSource> context) {
        ClientPlayerEntity player = context.getSource().getPlayer();
        player.getAbilities().allowFlying = true;
        player.getAbilities().creativeMode = true;
        player.getAbilities().invulnerable = true;
        player.sendAbilitiesUpdate();
        player.sendMessage(Text.literal("[!] NoFall (NoGround) Mixin: ON"));
        noGroundEnabled = true;
        Objects.requireNonNull(context.getSource().getClient().interactionManager).setGameMode(GameMode.CREATIVE);
        scheduler.shutdownNow();
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            if (player.age % 40 == 0) {
                player.setVelocity(new Vec3d(player.getVelocity().x, -0.04, player.getVelocity().z));
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
        return 1;
    }
}
