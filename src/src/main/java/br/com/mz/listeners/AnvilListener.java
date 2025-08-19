package br.com.mz.listeners;

import br.com.mz.AnvilManager;
import br.com.mz.Main;
import br.com.mz.menus.AnvilMenu;
import com.destroystokyo.paper.event.block.AnvilDamagedEvent;
import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class AnvilListener implements Listener {

    private final Main _plugin;
    private final AnvilManager _anvilManager;

    @Inject
    public AnvilListener(Main plugin, AnvilManager anvilManager) {
        _plugin = plugin;
        _anvilManager = anvilManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || !isAnvil(clickedBlock.getType())) {
            return;
        }

        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action == Action.RIGHT_CLICK_BLOCK) {

            if (!player.hasPermission("mz-anvil.use")) {
                player.sendMessage(Component.text("§cVocê não tem permissão para usar esta bigorna."));
                return;
            }

            event.setCancelled(true);
            _anvilManager.openMenu(player);
            return;
        }

        if (action == Action.LEFT_CLICK_BLOCK && player.getGameMode() != GameMode.CREATIVE) {

            if (!player.hasPermission("mz-anvil.repair")) {
                player.sendMessage(Component.text("§cVocê não tem permissão para reparar itens aqui."));
                return;
            }

            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            if (itemInHand.getType().isAir())
                return;

            ItemMeta meta = itemInHand.getItemMeta();

            if (meta instanceof Damageable damageableMeta) {

                if (damageableMeta.hasDamage()) {
                    damageableMeta.setDamage(0);
                    itemInHand.setItemMeta(damageableMeta);

                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.2f);
                    player.sendMessage(Component.text("§a» Sua ferramenta foi totalmente reparada!"));
                }
            }
        }

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!event.getView().title().equals(AnvilMenu.TITLE))
            return;

        int slot = event.getRawSlot();
        if (slot != 1 && slot != 3 && slot != 7 && slot < 9) {
            event.setCancelled(true);
        }

        if (slot == 1 || slot == 3 || slot > 8) {
            Bukkit.getScheduler().runTask(_plugin, () -> _anvilManager.updateResult(event.getInventory()));
        }

        if (slot == 7) {
            handleResultTake(event);
        }
    }

    @EventHandler
    public void onAnvilDamage(AnvilDamagedEvent event) {
        event.setCancelled(true);
    }

    private void handleResultTake(InventoryClickEvent event) {
        ItemStack resultItem = event.getCurrentItem();

        if (resultItem == null || resultItem.getType() == Material.AIR || resultItem.getType() == Material.BARRIER){
            event.setCancelled(true);
            return;
        }

        ItemStack item1 = event.getInventory().getItem(1);
        ItemStack item2 = event.getInventory().getItem(3);

        if (item1 == null || item2 == null)
            return;

        event.getInventory().setItem(1, null);
        event.getInventory().setItem(3, null);

        Bukkit.getScheduler().runTask(_plugin, () -> {
            _anvilManager.updateResult(event.getInventory());
        });
    }

    private boolean isAnvil(Material material) {
        return material == Material.ANVIL || material == Material.CHIPPED_ANVIL || material == Material.DAMAGED_ANVIL;
    }
}
