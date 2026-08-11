package voiidstudios.wex.core.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import voiidstudios.wex.EXABoot;
import voiidstudios.wonderevents.core.bootstrap.WonderFeatureContext;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ConfigManager {
    public static final String CONFIG_ROOT = "Config";
    public static final String MESSAGES_ROOT = "Messages";

    private final EXABoot bootstrap;
    private final WonderFeatureContext context;

    private final Set<ConfigFile> files = new LinkedHashSet<>();
    private final Map<String, ConfigFile> roots = new LinkedHashMap<>();
    private final Set<ConfigFile> dirtyFiles = new LinkedHashSet<>();

    public ConfigManager(EXABoot bootstrap, WonderFeatureContext context) {
        this.bootstrap = bootstrap;
        this.context = context;

        register("config.yml", CONFIG_ROOT, MESSAGES_ROOT);
    }

    private void register(String resourceName, String... rootKeys) {
        ConfigFile file = new ConfigFile(resourceName, new File(context.getDataFolder(), resourceName));
        files.add(file);
        for (String root : rootKeys) {
            ConfigFile previous = roots.putIfAbsent(root, file);
            if (previous != null) {
                throw new IllegalStateException("Root '" + root + "' s already registered for " + previous.resourceName + "; it cannot be registered again for " + resourceName);
            }
        }
    }

    public void bootstrap() {
        for (ConfigFile file : files) {
            context.saveResource(file.resourceName, false);
        }
        reload();
    }

    public void reload() {
        for (ConfigFile file : files) {
            file.load();
        }
        dirtyFiles.clear();
    }

    public FileConfiguration getConfig() {
        return getRootConfiguration(CONFIG_ROOT);
    }

    private FileConfiguration getRootConfiguration(String root) {
        ConfigFile file = roots.get(root);
        return file == null ? null : file.configuration;
    }

    public boolean isEnabled() {
        return getBoolean(CONFIG_ROOT + ".enabled", true);
    }

    public String getPrefix() {
        return getString(MESSAGES_ROOT + ".prefix", "<light_purple>[<cyan>EXA</cyan>]</light_purple>");
    }

    public boolean getBoolean(String path, boolean def) {
        return getConfig().getBoolean(path, def);
    }

    public String getString(String path, String def) {
        return getConfig().getString(path, def);
    }

    public int getInt(String path, int def) {
        return getConfig().getInt(path, def);
    }

    public Object getValue(String path) {
        return getValue(path, null);
    }

    public Object getValue(String path, Object def) {
        ResolvedPath resolved = resolve(path);
        return resolved.file.configuration.get(resolved.path, def);
    }

    public boolean getValueAsBoolean(String path, boolean def) {
        Object value = getValue(path);
        return value instanceof Boolean bool ? bool : def;
    }

    public int getValueAsInt(String path, int def) {
        Object value = getValue(path);
        return value instanceof Number number ? number.intValue() : def;
    }

    public String getValueAsString(String path, String def) {
        Object value = getValue(path);
        return value == null ? def : String.valueOf(value);
    }

    public List<String> getValueAsStringList(String path) {
        ResolvedPath resolved = resolve(path);
        return resolved.file.configuration.getStringList(resolved.path);
    }

    public void setValue(String path, Object value) {
        ResolvedPath resolved = resolve(path);
        resolved.file.configuration.set(resolved.path, value);
        dirtyFiles.add(resolved.file);
    }

    public void saveDirty() {
        if (dirtyFiles.isEmpty()) {
            return;
        }
        for (ConfigFile file : dirtyFiles) {
            try {
                file.save();
            } catch (IOException exception) {
                bootstrap.getLogger().passiveInfo("Unable to save " + file.resourceName + ": " + exception.getMessage());
            }
        }
        dirtyFiles.clear();
    }

    private ResolvedPath resolve(String path) {
        if (path == null || path.isBlank()) {
            throw new ConfigPathException("The configuration path cannot be empty.");
        }

        String trimmed = path.trim();
        int separatorIndex = trimmed.indexOf('.');
        if (separatorIndex <= 0 || separatorIndex == trimmed.length() - 1) {
            throw new ConfigPathException("Invalid configuration path: '" + path + "'. An explicit root followed by at least one segment was expected, for example, 'Config.enabled'. Registered roots: " + roots.keySet());
        }

        String root = trimmed.substring(0, separatorIndex);
        ConfigFile file = roots.get(root);
        if (file == null) {
            throw new ConfigPathException("Unknown configuration root: '" + root + "' (path: '" + path + "'). Registered roots: " + roots.keySet());
        }

        return new ResolvedPath(file, trimmed);
    }

    public EXABoot getBootstrap() {
        return bootstrap;
    }

    public WonderFeatureContext getContext() {
        return context;
    }

    private record ResolvedPath(ConfigFile file, String path) {
    }

    private static final class ConfigFile {

        private final String resourceName;
        private final File file;
        private FileConfiguration configuration;

        private ConfigFile(String resourceName, File file) {
            this.resourceName = resourceName;
            this.file = file;
        }

        private void load() {
            this.configuration = YamlConfiguration.loadConfiguration(file);
        }

        private void save() throws IOException {
            if (configuration != null) {
                configuration.save(file);
            }
        }
    }
}
