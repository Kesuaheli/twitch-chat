package eu.pabl.twitchchat.badge;

import com.github.twitch4j.helix.domain.ChatBadge;
import com.github.twitch4j.helix.domain.ChatBadgeSet;
import eu.pabl.twitchchat.TwitchChatMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Badge {
    private final String name;
    private MutableText displayName;
    String channelID;
    int codepoint;
    NativeImage image;
    NativeImage resourcePackOverrideImage;

    /**
     * An empty badge with a name set to "" and null image.
     */
    public static final Badge EMPTY = new Badge("", null);

    public Badge(String name, NativeImage image) {
        this.name = name;
        this.image = image;
    }

    public Badge(ChatBadgeSet chatBadgeSet) {
        this.name = chatBadgeSet.getSetId();
        ChatBadge lastVersion = chatBadgeSet.getVersions().getLast();
        this.displayName = Text.literal(lastVersion.getTitle());

        try {
            URI imageURI = new URI(lastVersion.getLargeImageUrl());
            this.image = NativeImage.read(imageURI.toURL().openStream());
        } catch (URISyntaxException | MalformedURLException e) {
            TwitchChatMod.LOGGER.error("Couldn't parse " + this.name + " badge url '" + lastVersion.getLargeImageUrl() + "'");
            throw new RuntimeException(e);
        } catch (IOException e) {
            TwitchChatMod.LOGGER.error("Couldn't read image data for " + this.name + " badge url '" + lastVersion.getLargeImageUrl() + "'");
            throw new RuntimeException(e);
        }
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

    /**
     * @return Whether the badge has a display name.
     */
    public boolean hasDisplayName() {
        return this.getDisplayName() != null && !Objects.equals(this.getDisplayName().getLiteralString(), "");
    }

    public HoverEvent getHoverEvent() {
        Text hoverText;
        if (!this.hasDisplayName()) {
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
     * @return The image of the badge
     */
    public NativeImage image() {
        return hasResourcePackOverride() ? this.resourcePackOverrideImage : this.image;
    }

    public boolean hasResourcePackOverride() {
        return this.resourcePackOverrideImage != null;
    }

    public void unsetResourcePackOverride() {
        this.resourcePackOverrideImage = null;
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
     * Loads the badges from the applied resource packs
     */
    public static void loadBadges() {
        String startingPath = "textures/badge";
        ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
        ResourceFinder finder = new ResourceFinder(startingPath, ".png");

        Map<Identifier, Resource> resources = finder.findResources(resourceManager);

        if (resources.isEmpty()) {
            return;
        }

        final String regex = startingPath + "/(?:global|channel/(?<channelName>[a-z0-1_]+))/(?<badgeName>[a-z0-1_]+)\\.png";
        final Pattern pattern = Pattern.compile(regex);
        resources.forEach((identifier, resource) -> {
            if (!identifier.getNamespace().equals(BadgeFont.IDENTIFIER.getNamespace())) return;
            Matcher matcher = pattern.matcher(identifier.getPath());
            if (!matcher.matches()) return;

            String channelID = null;
            try {
                channelID = matcher.group("channelName");
            } catch (IllegalArgumentException ignored) {}

            NativeImage image;
            try {
                image = NativeImage.read(resource.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String name = matcher.group("badgeName");
            Badge badge = new Badge(name, null);
            badge.resourcePackOverrideImage = image;
            if (channelID == null) {
                TwitchChatMod.BADGES.add(badge);
            } else {
                TwitchChatMod.BADGES.add(channelID, badge);
            }
        });
    }
}
