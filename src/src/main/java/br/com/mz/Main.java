package br.com.mz;

import br.com.mz.listeners.AnvilListener;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private Injector _injector;

    @Override
    public void onEnable() {
        _injector = Guice.createInjector(new PluginModule(this));

        PluginManager pm = getServer().getPluginManager();

        AnvilListener anvilListener = _injector.getInstance(AnvilListener.class);

        pm.registerEvents(anvilListener, this);

        getLogger().info("Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled.");
    }
}
