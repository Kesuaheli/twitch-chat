package eu.pabl.twitchchat.badge;

import eu.pabl.twitchchat.TwitchChatMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureContents;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class Badge {
    private final String name;
    String channelID;
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
}
