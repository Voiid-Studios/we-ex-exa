package voiidstudios.wex.core.managers;

import voiidstudios.wex.EXABoot;
import voiidstudios.wex.commands.EXACommand;

public final class CommandManager {
    private final EXABoot bootstrap;

    public CommandManager(EXABoot bootstrap) {
        this.bootstrap = bootstrap;
    }

    public void registerDefaults() {
        bootstrap.getFeatureContext().registerCommand(new EXACommand(bootstrap));
    }

    public EXABoot getBootstrap() {
        return bootstrap;
    }
}
