package com.cupboard.compat;

import com.cupboard.config.CupboardConfig;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DummyConfigScreen extends Screen
{
    private final CupboardConfig config;

    public DummyConfigScreen(final CupboardConfig config)
    {
        super(Component.literal("dummy"));
        this.config = config;
    }

    public void openConfigFile()
    {
        Util.getPlatform().openFile(config.getPath().toFile());
    }
}
