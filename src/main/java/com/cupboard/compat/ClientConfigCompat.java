package com.cupboard.compat;

import com.cupboard.config.CupboardConfig;
import com.cupboard.config.ICommonConfig;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ClientConfigCompat
{
    public static void initCompat(final CupboardConfig config)
    {
        setupNeoforge(config);
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

        final List<ModContainer> modContainers = ModList.get().getSortedMods();

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
            bestMatch.registerExtensionPoint(IConfigScreenFactory.class, (container, modListScreen) ->
            {
                Util.getPlatform().openFile(config.getPath().toFile());
                return Minecraft.getInstance().screen;
            });
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
