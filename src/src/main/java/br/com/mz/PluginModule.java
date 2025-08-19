package br.com.mz;

import br.com.mz.menus.AnvilMenu;
import com.google.inject.AbstractModule;

public class PluginModule extends AbstractModule {
    private final Main _plugin;

    public PluginModule(Main plugin) {
        _plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(Main.class).toInstance(_plugin);

        bind(AnvilManager.class);
        bind(AnvilMenu.class);
        bind(EnchantmentMerger.class);
    }
}
