package org.vortex.impersonate.client.mixin;

import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vortex.impersonate.client.misc.GameJoinEventHelper;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onGameJoin(Lnet/minecraft/network/packet/s2c/play/GameJoinS2CPacket;)V", at = @At("HEAD"))
    private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        GameJoinEventHelper.executeCallbacks();
    }
}