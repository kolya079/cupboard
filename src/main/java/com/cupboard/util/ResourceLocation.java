package com.cupboard.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.IdentifierException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.function.UnaryOperator;

public class ResourceLocation extends Identifier
{
    public static Codec<ResourceLocation>                CODEC         = Codec.STRING.comapFlatMap(ResourceLocation::readRes, ResourceLocation::toString).stable();
    public static StreamCodec<ByteBuf, ResourceLocation> STREAM_CODEC  = ByteBufCodecs.STRING_UTF8.map(ResourceLocation::parse, ResourceLocation::toString);
    public static SimpleCommandExceptionType             ERROR_INVALID = new SimpleCommandExceptionType(Component.translatable("argument.id.invalid"));

    public ResourceLocation(final String namespace, final String path)
    {
        super(namespace, path);
    }

    static ResourceLocation createUntrusted(String string, String string2)
    {
        return new ResourceLocation(assertValidNamespace(string, string2), assertValidPath(string, string2));
    }

    public static ResourceLocation fromNamespaceAndPath(String string, String string2)
    {
        return createUntrusted(string, string2);
    }

    public static ResourceLocation parse(String string)
    {
        return bySeparator(string, ':');
    }

    public static ResourceLocation withDefaultNamespace(String string)
    {
        return new ResourceLocation("minecraft", assertValidPath("minecraft", string));
    }

    public static @Nullable ResourceLocation tryParse(String string)
    {
        return tryBySeparator(string, ':');
    }

    public static @Nullable ResourceLocation tryBuild(String string, String string2)
    {
        return isValidNamespace(string) && isValidPath(string2) ? new ResourceLocation(string, string2) : null;
    }

    public static ResourceLocation ofIdentifier(final Identifier identifier)
    {
        if (identifier == null)
        {
            return null;
        }

        return new ResourceLocation(identifier.getNamespace(), identifier.getPath());
    }

    public static ResourceLocation bySeparator(String string, char c)
    {
        int i = string.indexOf(c);
        if (i >= 0)
        {
            String string2 = string.substring(i + 1);
            if (i != 0)
            {
                String string3 = string.substring(0, i);
                return createUntrusted(string3, string2);
            }
            else
            {
                return withDefaultNamespace(string2);
            }
        }
        else
        {
            return withDefaultNamespace(string);
        }
    }

    public static @Nullable ResourceLocation tryBySeparator(String string, char c)
    {
        int i = string.indexOf(c);
        if (i >= 0)
        {
            String string2 = string.substring(i + 1);
            if (!isValidPath(string2))
            {
                return null;
            }
            else if (i != 0)
            {
                String string3 = string.substring(0, i);
                return isValidNamespace(string3) ? new ResourceLocation(string3, string2) : null;
            }
            else
            {
                return new ResourceLocation("minecraft", string2);
            }
        }
        else
        {
            return isValidPath(string) ? new ResourceLocation("minecraft", string) : null;
        }
    }

    public static DataResult<ResourceLocation> readRes(String string)
    {
        try
        {
            return DataResult.success(parse(string));
        }
        catch (IdentifierException var2)
        {
            IdentifierException ResourceLocationException = var2;
            return DataResult.error(() -> {
                return "Not a valid resource location: " + string + " " + ResourceLocationException.getMessage();
            });
        }
    }

    static String readGreedy(StringReader stringReader)
    {
        int i = stringReader.getCursor();

        while (stringReader.canRead() && isAllowedInResourceLocation(stringReader.peek()))
        {
            stringReader.skip();
        }

        return stringReader.getString().substring(i, stringReader.getCursor());
    }

    public static ResourceLocation read(StringReader stringReader) throws CommandSyntaxException
    {
        int i = stringReader.getCursor();
        String string = readGreedy(stringReader);

        try
        {
            return parse(string);
        }
        catch (IdentifierException var4)
        {
            stringReader.setCursor(i);
            throw ERROR_INVALID.createWithContext(stringReader);
        }
    }

    public static ResourceLocation readNonEmpty(StringReader stringReader) throws CommandSyntaxException
    {
        int i = stringReader.getCursor();
        String string = readGreedy(stringReader);
        if (string.isEmpty())
        {
            throw ERROR_INVALID.createWithContext(stringReader);
        }
        else
        {
            try
            {
                return parse(string);
            }
            catch (IdentifierException var4)
            {
                stringReader.setCursor(i);
                throw ERROR_INVALID.createWithContext(stringReader);
            }
        }
    }

    static boolean isAllowedInResourceLocation(char c)
    {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
    }

    public static boolean isValidPath(String string)
    {
        for (int i = 0; i < string.length(); ++i)
        {
            if (!validPathChar(string.charAt(i)))
            {
                return false;
            }
        }

        return true;
    }

    public static boolean isValidNamespace(String string)
    {
        for (int i = 0; i < string.length(); ++i)
        {
            if (!validNamespaceChar(string.charAt(i)))
            {
                return false;
            }
        }

        return true;
    }

    static String assertValidNamespace(String string, String string2)
    {
        if (!isValidNamespace(string))
        {
            throw new IdentifierException("Non [a-z0-9_.-] character in namespace of location: " + string + ":" + string2);
        }
        else
        {
            return string;
        }
    }

    public static boolean validPathChar(char c)
    {
        return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '/' || c == '.';
    }

    public static boolean validNamespaceChar(char c)
    {
        return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '.';
    }

    static String assertValidPath(String string, String string2)
    {
        if (!isValidPath(string2))
        {
            throw new IdentifierException("Non [a-z0-9/._-] character in path of location: " + string + ":" + string2);
        }
        else
        {
            return string2;
        }
    }

    @Override
    public ResourceLocation withPath(String string)
    {
        return new ResourceLocation(string, string);
    }

    @Override
    public ResourceLocation withPath(UnaryOperator<String> unaryOperator)
    {
        return this.withPath((String) unaryOperator.apply(this.getPath()));
    }

    @Override
    public ResourceLocation withPrefix(String string)
    {
        return this.withPath(string + this.getPath());
    }

    @Override
    public ResourceLocation withSuffix(String string)
    {
        return this.withPath(this.getPath() + string);
    }
}
