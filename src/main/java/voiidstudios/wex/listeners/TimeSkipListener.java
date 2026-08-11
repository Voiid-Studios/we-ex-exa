package voiidstudios.wex.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.TimeSkipEvent;

import voiidstudios.wex.EXABoot;

public final class TimeSkipListener implements Listener {
    private final EXABoot bootstrap;

    public TimeSkipListener(EXABoot bootstrap) {
        this.bootstrap = bootstrap;
    }

    @EventHandler
    public void onTimeSkip(TimeSkipEvent event) {
        if (!bootstrap.getConfigManager().isEnabled()) {
            return;
        }

        if (event.getSkipReason() != TimeSkipEvent.SkipReason.NIGHT_SKIP) {
            return;
        }

        Bukkit.getOnlinePlayers().forEach(player ->
                bootstrap.getMessageManager().sendPrefixed(player, "Messages.gameplay.new_day"));
    }
}
