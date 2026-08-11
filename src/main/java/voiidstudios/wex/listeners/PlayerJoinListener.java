package voiidstudios.wex.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import voiidstudios.wex.EXABoot;

public final class PlayerJoinListener implements Listener {
    private final EXABoot bootstrap;

    public PlayerJoinListener(EXABoot bootstrap) {
        this.bootstrap = bootstrap;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        if (!bootstrap.getConfigManager().isEnabled()) {
            return;
        }

        boolean isNew = bootstrap.getUserDataManager().ensurePlayer(event.getPlayer());
        if (isNew) {
            bootstrap.getMessageManager().sendPrefixed(event.getPlayer(), "Messages.gameplay.welcome");
        }
    }
}
