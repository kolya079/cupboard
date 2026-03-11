package com.cupboard.compat;

import com.cupboard.Cupboard;
import com.cupboard.config.CupboardConfig;
import com.cupboard.config.ICommonConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ClientConfigCompat
{
    public static void initCompat(final CupboardConfig config)
    {
        try
        {
            if (FabricLoader.getInstance().isModLoaded("modmenu") && Cupboard.IS_CLIENT_OR_INTEGRATED)
            {
                ModMenuCompat.setup(config);
            }
        }
        catch (Exception e)
        {
            Cupboard.LOGGER.warn("Failed to setup config compatibility: ", e);
        }
    }

    public static <C extends ICommonConfig> void onLoad(final CupboardConfig config)
    {
        if (Minecraft.getInstance().player != null)
        {
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Reloaded config: " + config.getFilename()), false);
        }
    }
}
