package voiidstudios.wex.core.managers;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import voiidstudios.wex.EXABoot;
import voiidstudios.wonderevents.core.bootstrap.WonderFeatureContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class UserDataManager {
    private final EXABoot bootstrap;
    private final WonderFeatureContext context;
    private final File dataFile;

    private FileConfiguration database;

    public UserDataManager(EXABoot bootstrap, WonderFeatureContext context) {
        this.bootstrap = bootstrap;
        this.context = context;
        this.dataFile = new File(context.getDataFolder(), "data/wex_db.yml");
    }

    public void bootstrap() {
        File parent = dataFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        context.saveResource("data/wex_db.yml", false);
        reload();
    }

    public void reload() {
        this.database = YamlConfiguration.loadConfiguration(dataFile);
    }

    public synchronized boolean hasPlayer(UUID uuid) {
        return uuid != null && database.contains(uuid.toString());
    }

    public synchronized boolean ensurePlayer(OfflinePlayer player) {
        if (player == null || player.getUniqueId() == null) {
            return false;
        }
        return ensurePlayer(player.getUniqueId(), player.getName());
    }

    public synchronized boolean ensurePlayer(UUID uuid, String name) {
        Objects.requireNonNull(uuid, "uuid");

        String root = uuid.toString();
        boolean created = !database.contains(root);

        if (created) {
            ConfigurationSection section = database.createSection(root);
            section.set("name", name == null || name.isBlank() ? "NICKNAME" : name);
            saveQuietly();
            return true;
        }

        ConfigurationSection section = database.getConfigurationSection(root);
        if (section != null && name != null && !name.isBlank() && !name.equals(section.getString("name"))) {
            section.set("name", name);
            saveQuietly();
        }

        return false;
    }

    public synchronized List<String> getKnownPlayerNames() {
        Set<String> names = new LinkedHashSet<>();
        for (String root : database.getKeys(false)) {
            ConfigurationSection section = database.getConfigurationSection(root);
            if (section == null) {
                continue;
            }

            String name = section.getString("name");
            if (name != null && !name.isBlank()) {
                names.add(name);
            }
        }

        List<String> result = new ArrayList<>(names);
        result.sort(String::compareToIgnoreCase);
        return result;
    }

    public synchronized void save() throws IOException {
        database.save(dataFile);
    }

    public File getDataFile() {
        return dataFile;
    }

    public EXABoot getBootstrap() {
        return bootstrap;
    }

    private void saveQuietly() {
        try {
            save();
        } catch (IOException exception) {
            bootstrap.getLogger().passiveInfo("Could not save wex_db.yml: " + exception.getMessage());
        }
    }
}
