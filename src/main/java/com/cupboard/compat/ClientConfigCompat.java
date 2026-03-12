package com.cupboard.compat;

import com.cupboard.Cupboard;
import com.cupboard.config.CupboardConfig;
import com.cupboard.config.ICommonConfig;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ClientConfigCompat
{
    public static void initCompat(final CupboardConfig config)
    {
        try
        {
            setupNeoforge(config);
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

    private static void setupNeoforge(final CupboardConfig config)
    {
        if (FMLEnvironment.dist != Dist.CLIENT)
        {
            return;
        }

        final List<ModContainer> modContainers = new ArrayList<>();
        ModList.get().forEachModInOrder(modContainers::add);

        final String modName = config.getFilename().replace("common", "").replace(".json", "");

        ModContainer bestMatch = null;
        double similarityScore = 0;
        for (final ModContainer container : modContainers)
        {
            double modSimilarity = similarity(modName, container.getModId());
            if (modSimilarity > similarityScore)
            {
                bestMatch = container;
                similarityScore = modSimilarity;
            }
        }

        if (similarityScore > 0.9)
        {
            bestMatch.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((container, modListScreen) ->
            {
                Util.getPlatform().openFile(config.getPath().toFile());
                return Minecraft.getInstance().screen;
            }));
        }
    }

    /**
     * Calculates the similarity (a number within 0 and 1) between two strings.
     */
    public static double similarity(String s1, String s2)
    {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length())
        { // longer should always have greater length
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0)
        {
            return 1.0; /* both strings are zero length */
        }
        return (longerLength - StringUtils.getLevenshteinDistance(longer, shorter)) / (double) longerLength;
    }
}
