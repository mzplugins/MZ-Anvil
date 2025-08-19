package br.com.mz;

import br.com.mz.menus.AnvilMenu;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Singleton
public class AnvilManager {
    private final AnvilMenu _AnvilMenu;
    private final EnchantmentMerger _enchantmentMerger;

    @Inject
    public AnvilManager(AnvilMenu anvilMenu, EnchantmentMerger enchantmentMerger) {
        _AnvilMenu = anvilMenu;
        _enchantmentMerger = enchantmentMerger;
    }

    public void openMenu(Player p) {
        p.openInventory(_AnvilMenu.createMenu(p));
    }

    public void updateResult(Inventory inventory) {
        ItemStack item1 = inventory.getItem(1);
        ItemStack item2 = inventory.getItem(3);

        ItemStack result = _enchantmentMerger.merge(item1, item2);


        if(result != null) {
            inventory.setItem(7, result);
        } else {
            var resultPlaceholder = _AnvilMenu.getResultPlaceholder();
            inventory.setItem(7, resultPlaceholder);
        }
    }
}
