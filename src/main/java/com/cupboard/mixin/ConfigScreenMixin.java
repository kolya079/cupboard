package com.cupboard.mixin;

import com.cupboard.compat.DummyConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class ConfigScreenMixin
{
    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void skipDummyScreen(final Screen screen, final CallbackInfo ci)
    {
        if (screen instanceof DummyConfigScreen dummyConfigScreen)
        {
            dummyConfigScreen.openConfigFile();
            ci.cancel();
        }
    }
}
