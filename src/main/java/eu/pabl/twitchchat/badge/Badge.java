package eu.pabl.twitchchat.badge;

import eu.pabl.twitchchat.TwitchChatMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureContents;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.Objects;

public class Badge {
    private final String name;
    private MutableText displayName;
    String channelID;
    private int codepoint;
    private final NativeImage image;

    /**
     * An empty badge with a name set to "" and null image.
     */
    public static final Badge EMPTY = new Badge("", null);

    Badge(String name) throws IOException {
        this.name = name;

        ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
        try {
            image = TextureContents.load(resourceManager, Identifier.of("twitchchat", "textures/badge/" + this.name + ".png")).image();
        } catch (IOException e) {
            throw new IOException("badge texture for '" + this.name + "' badge: " + e);
        }
    }

    Badge(String name, NativeImage image) {
        this.name = name;
        this.image = image;
    }

    /**
     * @return The image of the badge
     */
    public NativeImage image() {
        return image;
    }

    /**
     * @return The name of the badge
     */
    public String getName() {
        return name;
    }

    /**
     * @return The display text of the badge.
     */
    public MutableText getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName The updated display text.
     */
    public void setDisplayName(MutableText displayName) {
        this.displayName = displayName;
    }

    /**
     * @param displayName The updated display text.
     */
    public void setDisplayName(String displayName) {
        setDisplayName(Text.literal(displayName));
    }

    public HoverEvent getHoverEvent() {
        Text hoverText;
        if (getDisplayName() == null || Objects.equals(getDisplayName().getLiteralString(), "")) {
            hoverText = Text.literal(this.name);
        } else {
            hoverText = getDisplayName().append(Text.literal("\n" + this.name).styled(style -> style
                .withColor(Formatting.DARK_GRAY)
                .withItalic(true)
            ));
        }
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText);
    }

    /**
     * @return The code point to use this badge in a text.
     */
    int getCodepoint() {
        return codepoint;
    }

    /**
     * @param codepoint The new code point.
     */
    void setCodepoint(int codepoint) {
        this.codepoint = codepoint;
    }

    /**
     * @return The ready to use text component of the badge.
     */
    public Text toText() {
        return Text.literal(Character.toString((char) this.codepoint)).styled(style -> style
            .withFont(BadgeFont.IDENTIFIER)
            .withHoverEvent(this.getHoverEvent())
        );
    }

    /**
     * Currently loads the hardcoded default badges.
     */
    public static void loadBadges() {
        try {
            TwitchChatMod.BADGES.add(33, new Badge("broadcaster"));
            TwitchChatMod.BADGES.add(34, new Badge("moderator"));
            TwitchChatMod.BADGES.add(35, new Badge("partner"));
            TwitchChatMod.BADGES.add(36, new Badge("vip"));
        } catch (IOException e) {
            TwitchChatMod.LOGGER.error("Error loading hardcoded badges: " + e);
        }
        TwitchChatMod.LOGGER.info("Loaded default badges!");
    }
}
