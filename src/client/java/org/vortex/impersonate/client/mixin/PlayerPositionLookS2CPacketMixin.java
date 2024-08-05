package org.vortex.impersonate.client.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vortex.impersonate.client.commands.forcefly;

@Mixin(ClientPlayNetworkHandler.class)
public class PlayerPositionLookS2CPacketMixin {
    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "onPlayerPositionLook(Lnet/minecraft/network/packet/s2c/play/PlayerPositionLookS2CPacket;)V", at = @At("HEAD"), cancellable = true)
    private void onReceivePacket(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        if (forcefly.noGroundEnabled) {
            LOGGER.info("{}{}{}", String.valueOf(packet.getX()), String.valueOf(packet.getY()), String.valueOf(packet.getZ()));
        }
    }
}
