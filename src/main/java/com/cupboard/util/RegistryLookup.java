package com.cupboard.util;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility for registry lookups
 */
public class RegistryLookup
{
    public static ResourceLocation getID(final RegistryAccess registryAccess, final ResourceKey registry, final Object searched)
    {
        final ResourceLocation identifier = (registryAccess.registryOrThrow(registry).getKey(searched));
        return identifier;
    }

    public static ResourceLocation getID(final LevelReader level, final ResourceKey registry, final Object searched)
    {
        return getID(level.registryAccess(), registry, searched);
    }

    public static <T> Holder<T> getHolder(final LevelReader level, final ResourceKey<? extends Registry<T>> registry, final ResourceLocation id)
    {
        return getHolder(level.registryAccess(), registry, id);
    }

    public static <T> Holder<T> getHolder(final RegistryAccess registryAccess, final ResourceKey<? extends Registry<T>> registry, final ResourceLocation id)
    {
        return registryAccess.registryOrThrow(registry).getHolder(ResourceKey.create(registry, id)).orElse(null);
    }

    public static <T> Holder<T> getHolder(final LevelReader level, final ResourceKey<? extends Registry<? extends T>> registry, final ResourceKey<T> id)
    {
        return getHolder(level.registryAccess(), registry, id);
    }

    public static <T> Holder<T> getHolder(final RegistryAccess registryAccess, final ResourceKey<? extends Registry<? extends T>> registry, final ResourceKey<T> id)
    {
        return registryAccess.lookupOrThrow(registry).get(id).orElse(null);
    }

    @NotNull
    public static <T> List<Holder<T>> getHolders(final LevelReader level, final ResourceKey<? extends Registry<? extends T>> registry)
    {
        return getHolders(level.registryAccess(), registry);
    }

    @NotNull
    public static <T> List<Holder<T>> getHolders(final RegistryAccess registryAccess, final ResourceKey<? extends Registry<? extends T>> registry)
    {
        final List<Holder<T>> holders = new ArrayList<>();
        for (final Holder<T> holder : registryAccess.registryOrThrow(registry).asHolderIdMap())
        {
            holders.add(holder);
        }
        return holders;
    }

    @NotNull
    public static <T> List<Holder<T>> getHolders(final LevelReader level, final ResourceKey<? extends Registry<? extends T>> registry, final TagKey<T> tag)
    {
        return getHolders(level.registryAccess(), registry, tag);
    }

    @NotNull
    public static <T> List<Holder<T>> getHolders(final RegistryAccess registryAccess, final ResourceKey<? extends Registry<? extends T>> registry, final TagKey<T> tag)
    {
        final List<Holder<T>> holders = new ArrayList<>();

        final var set = registryAccess.lookupOrThrow(registry).get(tag);
        if (set.isEmpty())
        {
            return holders;
        }

        for (final Holder<T> holder : set.get())
        {
            holders.add(holder);
        }
        return holders;
    }

    @NotNull
    public static <T> Registry<T> getRegistry(final RegistryAccess registryAccess, final ResourceKey<? extends Registry<T>> registry)
    {
        return registryAccess.registryOrThrow(registry);
    }
}
