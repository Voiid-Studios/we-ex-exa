package voiidstudios.wex.commands;

import org.bukkit.command.CommandSender;

import voiidstudios.wonderevents.api.WEACommand;
import voiidstudios.wex.EXABoot;

import java.util.Collections;
import java.util.List;

public final class EXACommand implements WEACommand {
    private final EXABoot bootstrap;

    public EXACommand(EXABoot bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public String getName() {
        return "exa";
    }

    @Override
    public String getPermission() {
        return bootstrap.getPermissionsManager().node("command.main");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!bootstrap.getPermissionsManager().has(sender, getPermission())) {
            bootstrap.getMessageManager().sendPrefixed(sender, "Messages.system.no_permission");
            return true;
        }

        bootstrap.getMessageManager().sendPrefixed(sender, "Messages.command.exa.response");
        return true;
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
