package br.com.mz;

import com.google.inject.Singleton;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class EnchantmentMerger {

    public ItemStack merge(ItemStack item1, ItemStack item2) {

        if (item1 == null || item1.getType().isAir() || item2 == null || item2.getType().isAir())
            return null;

        if (item1.getType() != item2.getType()) {
            return null;
        }

        if (!Enchantment.UNBREAKING.canEnchantItem(item1)) {
            return null;
        }

        ItemStack result = item1.clone();
        ItemMeta resultMeta = result.getItemMeta();

        if (resultMeta == null)
            return null;

        if (resultMeta instanceof Damageable) {
            ((Damageable) resultMeta).setDamage(0);
        }

        Map<Enchantment, Integer> item1Enchants = getAllEnchantments(item1);
        Map<Enchantment, Integer> item2Enchants = getAllEnchantments(item2);
        Map<Enchantment, Integer> finalEnchants = new HashMap<>(item1Enchants);

        for (Map.Entry<Enchantment, Integer> entry : item2Enchants.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level2 = entry.getValue();

            if (finalEnchants.containsKey(enchantment)) {
                int level1 = finalEnchants.get(enchantment);
                if (level1 == level2) {
                    finalEnchants.put(enchantment, level1 + 1);
                } else {
                    finalEnchants.put(enchantment, Math.max(level1, level2));
                }
            } else {
                finalEnchants.put(enchantment, level2);
            }
        }

        boolean hasSilkTouch = finalEnchants.containsKey(Enchantment.SILK_TOUCH);
        boolean hasFortune = finalEnchants.containsKey(Enchantment.FORTUNE);

        if (hasSilkTouch && hasFortune) {
            int silkLevel = finalEnchants.get(Enchantment.SILK_TOUCH);
            int fortuneLevel = finalEnchants.get(Enchantment.FORTUNE);

            if (fortuneLevel > silkLevel) {
                finalEnchants.remove(Enchantment.SILK_TOUCH);
            } else if (silkLevel > fortuneLevel) {
                finalEnchants.remove(Enchantment.FORTUNE);
            } else {
                if (item1Enchants.containsKey(Enchantment.FORTUNE)) {
                    finalEnchants.remove(Enchantment.SILK_TOUCH);
                } else {
                    finalEnchants.remove(Enchantment.FORTUNE);
                }
            }
        }

        clearAllEnchantments(resultMeta);
        applyEnchantments(resultMeta, finalEnchants);

        result.setItemMeta(resultMeta);
        return result;
    }

    private Map<Enchantment, Integer> getAllEnchantments(ItemStack item) {

        if (item == null)
            return new HashMap<>();

        Map<Enchantment, Integer> enchantments = new HashMap<>(item.getEnchantments());

        if (item.getItemMeta() instanceof EnchantmentStorageMeta bookMeta) {
            enchantments.putAll(bookMeta.getStoredEnchants());
        }

        return enchantments;
    }

    private void clearAllEnchantments(ItemMeta meta) {
        meta.getEnchants().keySet().forEach(meta::removeEnchant);

        if (meta instanceof EnchantmentStorageMeta bookMeta) {
            bookMeta.getStoredEnchants().keySet().forEach(bookMeta::removeStoredEnchant);
        }
    }

    private void applyEnchantments(ItemMeta meta, Map<Enchantment, Integer> enchantments) {
        if (meta instanceof EnchantmentStorageMeta bookMeta) {
            enchantments.forEach((enchantment, level) -> bookMeta.addStoredEnchant(enchantment, level, true));
        } else {
            enchantments.forEach((enchantment, level) -> meta.addEnchant(enchantment, level, true));
        }
    }

}
