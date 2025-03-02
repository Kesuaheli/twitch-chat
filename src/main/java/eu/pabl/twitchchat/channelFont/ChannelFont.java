package eu.pabl.twitchchat.channelFont;

import eu.pabl.twitchchat.TwitchChatMod;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.font.BitmapFont;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontFilterType;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChannelFont implements Font {
    public static final Identifier CHANNEL_ICON_FONT_STORAGE = Identifier.of("twitchchat", "channel_icon2");
    public static final List<Font.FontFilterPair> CHANNEL_ICON_FONT_FILTER = List.of(new Font.FontFilterPair(new ChannelFont(), FontFilterType.FilterMap.NO_FILTER));
    private static final int BADGE_SIZE = 8;

    @Override
    public void close() {
        Font.super.close();
    }

    @Nullable
    @Override
    public Glyph getGlyph(int codePoint) {
        NativeImage image = Badge.get(codePoint).image();
        if (image == null) {
            TwitchChatMod.LOGGER.error("No badge exists for code point '" + codePoint + "'");
            return Font.super.getGlyph(codePoint);
        }

        float scaleFactor = (float) BADGE_SIZE / image.getWidth();
        BitmapFont.BitmapFontGlyph glyph = new BitmapFont.BitmapFontGlyph(scaleFactor, image, 0, 0, image.getWidth(), image.getHeight(), BADGE_SIZE+1, BADGE_SIZE);
        return glyph;
    }

    @Override
    public IntSet getProvidedGlyphs() {
        return Badge.codePoints();
    }
}
