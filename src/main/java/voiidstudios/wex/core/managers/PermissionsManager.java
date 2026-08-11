package voiidstudios.wex.core.managers;

import org.bukkit.command.CommandSender;

import voiidstudios.wex.EXABoot;

public final class PermissionsManager {

    private final EXABoot bootstrap;

    public PermissionsManager(EXABoot bootstrap) {
        this.bootstrap = bootstrap;
    }

    public boolean has(CommandSender sender, String permission) {
        return sender != null && (permission == null || permission.isBlank() || sender.hasPermission(permission));
    }

    public String node(String leaf) {
        return "wexample." + leaf;
    }

    public EXABoot getBootstrap() {
        return bootstrap;
    }
}
