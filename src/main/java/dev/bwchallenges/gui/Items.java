/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package dev.bwchallenges.gui;

import dev.bwchallenges.util.Text;
import dev.bwchallenges.util.XMat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class Items {
    public static final String LOBBY_MARK = "bwchallenges-selector";

    private Items() {
    }

    public static ItemStack of(String string, String string2, List<String> list) {
        return Items.of(string, string2, list, false);
    }

    public static ItemStack of(String string, String string2, List<String> list, boolean bl) {
        ItemStack itemStack = Items.meta(XMat.stack(string, Items.fallback(string), "STONE"), string2, list);
        return bl ? Items.glow(itemStack) : itemStack;
    }

    public static ItemStack of(Material material, String string, String ... stringArray) {
        return Items.meta(new ItemStack(material == null ? Material.STONE : material, 1), string, stringArray == null ? null : Arrays.asList(stringArray));
    }

    public static ItemStack glow(ItemStack itemStack) {
        if (itemStack == null) {
            return itemStack;
        }
        try {
            Enchantment[] enchantmentArray;
            Enchantment enchantment = Enchantment.getByName((String)"DURABILITY");
            if (enchantment == null) {
                enchantment = Enchantment.getByName((String)"UNBREAKING");
            }
            if (enchantment == null && (enchantmentArray = Enchantment.values()) != null && enchantmentArray.length > 0) {
                enchantment = enchantmentArray[0];
            }
            if (enchantment != null) {
                itemStack.addUnsafeEnchantment(enchantment, 1);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return itemStack;
    }

    public static ItemStack lockedPane(String string, String string2) {
        Material material = XMat.of("GRAY_DYE", "INK_SACK", "COAL");
        ItemStack itemStack = new ItemStack(material, 1);
        try {
            if (material.name().equals("INK_SACK") || material.name().equals("INK_SAC")) {
                itemStack.setDurability((short)8);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return Items.meta(itemStack, string, Items.lore("", string2));
    }

    public static ItemStack pane() {
        return null;
    }

    public static List<String> lore(String ... stringArray) {
        ArrayList<String> arrayList = new ArrayList<String>();
        if (stringArray != null) {
            for (String string : stringArray) {
                arrayList.add(string);
            }
        }
        return arrayList;
    }

    public static boolean isLobbySelector(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return false;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        }
        if (itemMeta.hasLore()) {
            for (Object e : itemMeta.getLore()) {
                if (!Text.strip(String.valueOf(e)).equalsIgnoreCase(LOBBY_MARK)) continue;
                return true;
            }
        }
        if (itemMeta.hasDisplayName()) {
            String string = Text.strip(itemMeta.getDisplayName()).toLowerCase();
            return string.equals("challenges") || string.equals("challenge");
        }
        return false;
    }

    private static ItemStack meta(ItemStack itemStack, String string, List<String> list) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(Text.color(string));
            if (list != null) {
                itemMeta.setLore(Text.color(list));
            }
            try {
                for (ItemFlag itemFlag : ItemFlag.values()) {
                    itemMeta.addItemFlags(new ItemFlag[]{itemFlag});
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    private static String fallback(String string) {
        if (string == null) {
            return "STONE";
        }
        String string2 = string.toUpperCase();
        if (string2.equals("WOOD")) {
            return "OAK_PLANKS";
        }
        if (string2.equals("WOOD_SWORD")) {
            return "WOODEN_SWORD";
        }
        if (string2.equals("SNOW_BALL")) {
            return "SNOWBALL";
        }
        if (string2.equals("WEB")) {
            return "COBWEB";
        }
        if (string2.equals("FENCE")) {
            return "OAK_FENCE";
        }
        if (string2.equals("FENCE_GATE")) {
            return "OAK_FENCE_GATE";
        }
        if (string2.equals("GOLDEN_HELMET")) {
            return "GOLD_HELMET";
        }
        if (string2.equals("BED")) {
            return "RED_BED";
        }
        if (string2.equals("FIREBALL")) {
            return "FIRE_CHARGE";
        }
        if (string2.equals("WOOL")) {
            return "WHITE_WOOL";
        }
        if (string2.equals("REDSTONE_TORCH")) {
            return "REDSTONE_TORCH_ON";
        }
        if (string2.equals("REDSTONE_TORCH_ON")) {
            return "REDSTONE_TORCH";
        }
        return string;
    }
}

