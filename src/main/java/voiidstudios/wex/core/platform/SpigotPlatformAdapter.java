package voiidstudios.wex.core.platform;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import voiidstudios.wonderevents.core.log.YALogger;
import voiidstudios.wex.utils.TextUtils;
import voiidstudios.wex.utils.UniversalFormatter;

public class SpigotPlatformAdapter implements PlatformAdapter {
    private final UniversalFormatter formatter;

    public SpigotPlatformAdapter(Plugin plugin, YALogger logger) {
        this.formatter = new UniversalFormatter(plugin, logger);
    }

    @Override
    public String getName() {
        return "Spigot/Bukkit";
    }

    @Override
    public boolean isPaper() {
        return false;
    }

    @Override
    public boolean supportsAdventure() {
        return false;
    }

    @Override
    public void sendMessage(CommandSender sender, String message) {
        Object formatted = formatter.format(message);
        sender.sendMessage(formatted instanceof String text ? text : TextUtils.toLegacy(message));
    }
}
