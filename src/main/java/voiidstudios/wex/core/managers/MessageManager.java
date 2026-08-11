package voiidstudios.wex.core.managers;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import voiidstudios.wex.EXABoot;
import voiidstudios.wonderevents.core.bootstrap.WonderFeatureContext;
import voiidstudios.wex.core.platform.PaperPlatformAdapter;
import voiidstudios.wex.core.platform.PlatformAdapter;
import voiidstudios.wex.core.platform.SpigotPlatformAdapter;
import voiidstudios.wex.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class MessageManager {
    private static final String ROOT = "Messages.";
    private static final String MISSING_MESSAGE = "\u00A7cMissing message: ";

    private final EXABoot bootstrap;
    private final WonderFeatureContext context;
    private final ConfigManager configManager;
    private final PlatformAdapter platform;

    public MessageManager(EXABoot bootstrap, WonderFeatureContext context, ConfigManager configManager) {
        this.bootstrap = bootstrap;
        this.context = context;
        this.configManager = configManager;
        this.platform = createPlatformAdapter(context);
    }

    private static PlatformAdapter createPlatformAdapter(WonderFeatureContext context) {
        if (PaperPlatformAdapter.isAvailable()) {
            return new PaperPlatformAdapter(context.getPlugin(), context.getLogger());
        }
        return new SpigotPlatformAdapter(context.getPlugin(), context.getLogger());
    }

    public void reload() {}

    public String color(String message) {
        if (message == null) {
            return "";
        }
        return TextUtils.toLegacy(message);
    }

    public String get(String path) {
        return get(path, Collections.emptyMap());
    }

    public String get(String path, Map<String, String> placeholders) {
        String raw = lookup(path);
        if (raw == null) {
            return MISSING_MESSAGE + path;
        }
        return color(format(raw, placeholders));
    }

    public void send(CommandSender sender, String path) {
        send(sender, path, Collections.emptyMap());
    }

    public void send(CommandSender sender, String path, Map<String, String> placeholders) {
        if (sender == null) {
            return;
        }
        platform.sendMessage(sender, get(path, placeholders));
    }

    public void sendPrefixed(CommandSender sender, String path) {
        sendPrefixed(sender, path, Collections.emptyMap());
    }

    public void sendPrefixed(CommandSender sender, String path, Map<String, String> placeholders) {
        if (sender == null) {
            return;
        }
        platform.sendMessage(sender, getPrefixed(path, placeholders));
    }

    public void sendList(CommandSender sender, String path, Map<String, String> placeholders) {
        if (sender == null) {
            return;
        }
        for (String line : getList(path)) {
            platform.sendMessage(sender, color(format(line, placeholders)));
        }
    }

    public void sendBlock(CommandSender sender, String path, Map<String, String> placeholders) {
        sendList(sender, path, placeholders);
    }

    public String getPrefixed(String path, Map<String, String> placeholders) {
        String raw = lookup(path);
        if (raw == null) {
            return MISSING_MESSAGE + path;
        }

        String formatted = format(raw, placeholders);
        if (hasPrefixPlaceholder(raw)) {
            return color(formatted);
        }
        return color(getRawPrefix() + " " + formatted);
    }

    public List<String> getList(String path) {
        String resolvedPath = resolvePath(path);
        if (!hasMessagePath(resolvedPath)) {
            return Collections.emptyList();
        }

        List<String> list = configManager.getConfig().getStringList(resolvedPath);
        if (!list.isEmpty()) {
            return list;
        }

        String raw = configManager.getConfig().getString(resolvedPath);
        if (raw == null) {
            return Collections.emptyList();
        }

        if (!raw.contains("\n")) {
            return Collections.singletonList(raw);
        }

        return List.of(raw.split("\n"));
    }

    public String getListAsSingleString(String path, Map<String, String> placeholders) {
        List<String> lines = getList(path);
        if (lines.isEmpty()) {
            return "";
        }

        List<String> formatted = new ArrayList<>(lines.size());
        for (String line : lines) {
            formatted.add(color(format(line, placeholders)));
        }
        return String.join("\n", formatted);
    }

    public void sendRaw(CommandSender sender, String message) {
        if (sender == null || message == null) {
            return;
        }
        platform.sendMessage(sender, color(resolvePrefix(message)));
    }

    public void console(String message) {
        platform.sendMessage(Bukkit.getConsoleSender(), color(format(message, Collections.emptyMap())));
    }

    private String getRawPrefix() {
        return configManager.getPrefix();
    }

    private String lookup(String path) {
        String resolvedPath = resolvePath(path);
        return configManager.getConfig().getString(resolvedPath);
    }

    private String format(String message, Map<String, String> placeholders) {
        String formatted = resolvePrefix(message);
        if (formatted == null) {
            return null;
        }

        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    continue;
                }
                formatted = formatted.replace(entry.getKey(), entry.getValue());
            }
        }

        return formatted;
    }

    private String resolvePrefix(String message) {
        if (message == null) {
            return null;
        }

        String prefix = getRawPrefix();
        return message
                .replace("%PREFIX%", prefix)
                .replace("%prefix%", prefix);
    }

    private boolean hasPrefixPlaceholder(String message) {
        if (message == null) {
            return false;
        }
        return message.contains("%PREFIX%") || message.contains("%prefix%");
    }

    public boolean hasMessage(String path) {
        return hasMessagePath(resolvePath(path));
    }

    private boolean hasMessagePath(String path) {
        ConfigurationSection section = configManager.getConfig().getConfigurationSection(path);
        return section != null || configManager.getConfig().contains(path);
    }

    private String resolvePath(String path) {
        if (path == null || path.isBlank()) {
            return ROOT;
        }

        if (path.startsWith(ROOT)) {
            return path;
        }

        if (path.startsWith("messages.")) {
            return ROOT + path.substring("messages.".length());
        }

        if (path.startsWith("message.")) {
            return ROOT + path.substring("message.".length());
        }

        return path;
    }

    public EXABoot getBootstrap() {
        return bootstrap;
    }

    public WonderFeatureContext getContext() {
        return context;
    }
}
