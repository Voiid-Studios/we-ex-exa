package voiidstudios.wex;

import voiidstudios.wonderevents.api.WEABootstrap;
import voiidstudios.wonderevents.core.bootstrap.WonderFeatureContext;
import voiidstudios.wex.core.managers.CommandManager;
import voiidstudios.wex.core.managers.ConfigManager;
import voiidstudios.wex.core.managers.ListenerManager;
import voiidstudios.wex.core.managers.MessageManager;
import voiidstudios.wex.core.managers.PermissionsManager;
import voiidstudios.wex.core.managers.UserDataManager;

public final class EXABoot extends WEABootstrap {
    private ConfigManager configManager;
    private MessageManager messageManager;
    private UserDataManager userDataManager;
    private PermissionsManager permissionsManager;
    private ListenerManager listenerManager;
    private CommandManager commandManager;

    @Override
    public void onLoad(WonderFeatureContext context) {
        this.configManager = new ConfigManager(this, context);
        this.configManager.bootstrap();

        this.userDataManager = new UserDataManager(this, context);
        this.userDataManager.bootstrap();

        this.messageManager = new MessageManager(this, context, configManager);
        this.permissionsManager = new PermissionsManager(this);
        this.listenerManager = new ListenerManager(this);
        this.commandManager = new CommandManager(this);

        listenerManager.registerDefaults();
        commandManager.registerDefaults();

        getLogger().success("Hello! I'm EXA, the example expansion bundled with WonderEvents.");
        getLogger().passiveInfo("I'm purely a usage example: feel free to delete my jar from the expansions/ folder at any time, WonderEvents will keep running just fine without me :)");
    }

    @Override
    public void onEnable() {
        getLogger().success("EXA enabled correctly.");
    }

    @Override
    public void onReload() {
        if (configManager != null) {
            configManager.reload();
        }
        if (userDataManager != null) {
            userDataManager.reload();
        }
        if (messageManager != null) {
            messageManager.reload();
        }
        getLogger().success("EXA reloaded correctly.");
    }

    @Override
    public void onDisable() {
        getLogger().passiveInfo("EXA disabled.");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public UserDataManager getUserDataManager() {
        return userDataManager;
    }

    public PermissionsManager getPermissionsManager() {
        return permissionsManager;
    }

    public ListenerManager getListenerManager() {
        return listenerManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
