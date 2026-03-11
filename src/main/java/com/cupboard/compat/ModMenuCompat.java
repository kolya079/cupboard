package com.cupboard.compat;

import com.cupboard.Cupboard;
import com.cupboard.config.CupboardConfig;
import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.impl.ModContainerImpl;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class ModMenuCompat
{
    public static void setup(final CupboardConfig config)
    {
        final Collection<ModContainer> modContainers = FabricLoader.getInstance().getAllMods();

        final String modName = config.getFilename().replace("common", "").replace(".json", "");

        ModContainerImpl bestMatch = null;
        double similarityScore = 0;
        for (final ModContainer container : modContainers)
        {
            if (container instanceof ModContainerImpl modContainer)
            {
                double modSimilarity = similarity(modName, modContainer.getMetadata().getId());
                if (modSimilarity > similarityScore)
                {
                    bestMatch = modContainer;
                    similarityScore = modSimilarity;
                }
            }
        }

        if (similarityScore > 0.9)
        {
            try
            {
                Field configScreenFactoriesField = ModMenu.class.getDeclaredField("configScreenFactories");
                configScreenFactoriesField.setAccessible(true);
                Map<String, ConfigScreenFactory<?>> factories = (Map<String, ConfigScreenFactory<?>>) configScreenFactoriesField.get(null);
                factories.put(bestMatch.getMetadata().getId(), screen -> new DummyConfigScreen(config));
            }
            catch (Exception e)
            {
                Cupboard.LOGGER.warn("Failed to register modmenu config screen: ", e);
            }
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
