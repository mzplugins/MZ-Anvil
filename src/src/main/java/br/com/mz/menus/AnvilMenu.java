package br.com.mz.menus;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class AnvilMenu {

    public static final Component TITLE = Component.text("Juntar Encantamentos");

    private static final String ARROW_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjgyYWQxYjljYjRkZDIxMjU5YzBkNzVhYTMxNWZmMzg5YzNjZWY3NTJiZTM5NDkzMzgxNjRiYWM4NGE5NmUifX19";
    private static final String PLUS_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWEyZDg5MWM2YWU5ZjZiYWEwNDBkNzM2YWI4NGQ0ODM0NGJiNmI3MGQ3ZjFhMjgwZGQxMmNiYWM0ZDc3NyJ9fX0=";

    public Inventory createMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, TITLE);

        ItemStack placeholder = createMenuItem(Material.BLACK_STAINED_GLASS_PANE, "", null);
        ItemStack arrowIcon = createCustomSkull(ARROW_TEXTURE, "");
        ItemStack plusIcon = createCustomSkull(PLUS_TEXTURE, "");
        ItemStack resultPlaceholder = getResultPlaceholder();

        inv.setItem(0, placeholder);
        inv.setItem(2, arrowIcon);
        inv.setItem(4, placeholder);
        inv.setItem(5, placeholder);
        inv.setItem(6, plusIcon.clone());
        inv.setItem(7, resultPlaceholder);
        inv.setItem(8, placeholder);

        return inv;
    }

    public ItemStack getResultPlaceholder(){
        return createMenuItem(Material.BARRIER, "§eResultado", null);
    }

    private ItemStack createMenuItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            Component componentName = LegacyComponentSerializer.legacySection().deserialize(name);
            meta.displayName(componentName);

            if (lore != null && !lore.isEmpty()) {
                List<Component> componentLore = lore.stream()
                        .map(line -> LegacyComponentSerializer.legacySection().deserialize(line))
                        .collect(Collectors.toList());

                meta.lore(componentLore);
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createCustomSkull(String base64Texture, String name) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        ProfileProperty property = new ProfileProperty("textures", base64Texture);
        profile.setProperty(property);
        meta.setPlayerProfile(profile);

        Component componentName = LegacyComponentSerializer.legacySection().deserialize(name);
        meta.displayName(componentName);

        head.setItemMeta(meta);
        return head;
    }
}
