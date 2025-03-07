package eu.pabl.twitchchat.badge;

import eu.pabl.twitchchat.TwitchChatMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureContents;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Badge {
    private final String name;
    Map<String, ChannelOverride> channelOverrides = new HashMap<>();
    private NativeImage image;

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
     * A clone constructor to clone a badge.
     * @param badge the badge to clone
     */
    Badge(Badge badge) {
        this.name = badge.name;
        this.image = badge.image;
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
     * Adds or replaces an override for the given channel.
     * @param channelID The channel ID to add the override for.
     * @param image The image to use for that channel.
     * @throws IllegalStateException when this badge is not allowed to have channel overrides e.g. it is already a
     * channel override.
     */
    public void setChannelOverride(@NotNull String channelID, NativeImage image) throws IllegalStateException {
        if (channelOverrides == null) throw new IllegalStateException("This badge cant have overrides (is it an channel override?");
        this.channelOverrides.put(channelID, new ChannelOverride(channelID, image));
    }

    /**
     * Gets the {@link ChannelOverride} for a channel.
     * @param channelID The channelID to get the override from
     * @return The override for the given channel, or null if this badge contains no override for the channel.
     */
    public @Nullable ChannelOverride getChannelOverride(@NotNull String channelID) {
        if (this.channelOverrides == null) return null;
        return this.channelOverrides.get(channelID);
    }

    /**
     * Clears all channel overrides of this badge, if any.
     */
    public void clearChannelOverrides() {
        this.channelOverrides.clear();
    }

    /**
     * @return The name of the badge
     */
    @Override
    public String toString() {
            return name;
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

    public class ChannelOverride {
        final String channelID;
        final NativeImage image;

        private ChannelOverride(String channelID, NativeImage image) {
            this.channelID = channelID;
            this.image = image;
        }

        /**
         * @return The name of the badge
         */
        public String getChannelID() {
            return channelID;
        }

        /**
         * Turn this override into an actual badge to use.
         * <p> The returned badge cant have channel overrides. Trying to set a channel override on this badge will throw
         * an {@link IllegalStateException}.
         * @return the badge representation of this override.
         */
        Badge toBadge() {
            Badge badge = new Badge(Badge.this);
            badge.channelOverrides = null;
            badge.image = this.image;
            return badge;
        }
    }
}
