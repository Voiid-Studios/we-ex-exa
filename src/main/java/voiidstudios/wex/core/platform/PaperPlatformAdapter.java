package voiidstudios.wex.core.platform;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import voiidstudios.wonderevents.core.log.YALogger;
import voiidstudios.wex.utils.TextUtils;
import voiidstudios.wex.utils.UniversalFormatter;

import java.lang.reflect.Method;

public class PaperPlatformAdapter implements PlatformAdapter {
    private final Plugin plugin;
    private final YALogger logger;
    private final UniversalFormatter formatter;
    private Class<?> audienceClass;
    private Class<?> componentClass;
    private Method audienceSendMessageMethod;
    private boolean warnedSendFallback;

    public PaperPlatformAdapter(Plugin plugin, YALogger logger) {
        this.plugin = plugin;
        this.logger = logger;
        this.formatter = new UniversalFormatter(plugin, logger);
        initializeAudience();
    }

    public static boolean isAvailable() {
        return hasClass("io.papermc.paper.configuration.Configuration")
                || hasClass("com.destroystokyo.paper.PaperConfig")
                || hasClass("io.papermc.paper.threadedregions.RegionizedServer");
    }

    private static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }

    private void initializeAudience() {
        try {
            this.audienceClass = Class.forName("net.kyori.adventure.audience.Audience");
            this.componentClass = Class.forName("net.kyori.adventure.text.Component");
            this.audienceSendMessageMethod = audienceClass.getMethod("sendMessage", componentClass);
        } catch (ReflectiveOperationException | LinkageError exception) {
            warnSendFallback("Adventure is not available", exception);
            this.audienceClass = null;
            this.componentClass = null;
            this.audienceSendMessageMethod = null;
        }
    }

    @Override
    public String getName() {
        return supportsAdventure() ? "Paper/Fork + Adventure" : "Paper/Fork";
    }

    @Override
    public boolean isPaper() {
        return true;
    }

    @Override
    public boolean supportsAdventure() {
        return audienceClass != null && componentClass != null && audienceSendMessageMethod != null;
    }

    @Override
    public void sendMessage(CommandSender sender, String message) {
        Object formatted = formatter.format(message);
        if (formatted instanceof String text) {
            sender.sendMessage(text);
            return;
        }

        if (!supportsAdventure() || !componentClass.isInstance(formatted) || !audienceClass.isInstance(sender)) {
            sender.sendMessage(TextUtils.toLegacy(message));
            return;
        }

        try {
            audienceSendMessageMethod.invoke(sender, formatted);
        } catch (ReflectiveOperationException | LinkageError exception) {
            warnSendFallback("I was unable to send the Adventure component", exception);
            sender.sendMessage(TextUtils.toLegacy(message));
        }
    }

    private void warnSendFallback(String message, Throwable throwable) {
        if (warnedSendFallback) {
            return;
        }

        warnedSendFallback = true;
        String warning = message + ", so I use the classic format: "
                + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();

        if (logger != null) {
            logger.warning(warning);
        } else if (plugin != null) {
            plugin.getLogger().warning(warning);
        }
    }
}
