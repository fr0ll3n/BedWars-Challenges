/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package dev.bwchallenges.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class XMat {
    private XMat() {
    }

    public static Material of(String ... stringArray) {
        for (String string : stringArray) {
            if (string == null) continue;
            try {
                Material material = Material.valueOf((String)string.toUpperCase());
                if (material == null) continue;
                return material;
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        return Material.STONE;
    }

    public static ItemStack stack(String ... stringArray) {
        return new ItemStack(XMat.of(stringArray), 1);
    }

    public static boolean named(Material material, String string) {
        return material != null && material.name().contains(string);
    }

    public static boolean isSword(Material material) {
        if (material == null) {
            return false;
        }
        String string = material.name();
        return string.endsWith("_SWORD") || string.equals("WOOD_SWORD") || string.equals("IRON_SWORD") || string.equals("DIAMOND_SWORD") || string.equals("GOLD_SWORD") || string.equals("STONE_SWORD") || string.equals("NETHERITE_SWORD") || string.equals("GOLDEN_SWORD") || string.equals("WOODEN_SWORD");
    }

    public static boolean isBow(Material material) {
        if (material == null) {
            return false;
        }
        String string = material.name();
        return string.equals("BOW") || string.equals("CROSSBOW");
    }

    public static boolean isAxeOrPick(Material material) {
        if (material == null) {
            return false;
        }
        String string = material.name();
        return string.endsWith("_AXE") || string.endsWith("_PICKAXE") || string.equals("WOOD_AXE") || string.equals("WOOD_PICKAXE") || string.equals("IRON_AXE") || string.equals("IRON_PICKAXE") || string.equals("STONE_AXE") || string.equals("STONE_PICKAXE") || string.equals("DIAMOND_AXE") || string.equals("DIAMOND_PICKAXE") || string.equals("GOLD_AXE") || string.equals("GOLD_PICKAXE");
    }

    public static boolean isWood(Material material) {
        if (material == null) {
            return false;
        }
        String string = material.name();
        if (string.contains("ENDER_CHEST") || string.contains("TRAPPED_CHEST") || string.equals("CHEST")) {
            return false;
        }
        if (string.contains("IRON_DOOR") || string.contains("IRON_TRAPDOOR")) {
            return false;
        }
        return string.contains("WOOD") || string.contains("LOG") || string.contains("PLANK") || string.contains("STICK") || string.equals("BOW") || string.contains("FENCE") || string.contains("SIGN") || string.equals("LADDER") || string.contains("SAPLING") || string.contains("SLAB") && string.contains("OAK") || string.contains("STAIR") && (string.contains("OAK") || string.contains("WOOD"));
    }

    public static boolean isUtilityUse(Material material) {
        if (material == null) {
            return false;
        }
        String string = material.name();
        return string.contains("TNT") || string.contains("FIREBALL") || string.contains("FIRE_CHARGE") || string.equals("EGG") || string.contains("SNOW") || string.contains("WATER_BUCKET") || string.contains("LAVA_BUCKET") || string.contains("ENDER_PEARL") || string.contains("GOLDEN_APPLE") || string.contains("MILK") || string.contains("SPONGE") || string.contains("SPAWN_EGG") || string.contains("BUCKET") || string.contains("POTION") || string.contains("SILVERFISH");
    }

    public static boolean isResource(Material material) {
        if (material == null) {
            return false;
        }
        String string = material.name();
        return string.equals("IRON_INGOT") || string.equals("GOLD_INGOT") || string.equals("DIAMOND") || string.equals("EMERALD") || string.equals("IRON") || string.equals("GOLD");
    }

    public static ItemStack handOf(Player player) {
        try {
            return player.getInventory().getItemInMainHand();
        }
        catch (Throwable throwable) {
            return player.getItemInHand();
        }
    }

    public static boolean isBlockItem(Material material) {
        if (material == null) {
            return false;
        }
        if (material.isBlock()) {
            return true;
        }
        String string = material.name();
        return string.contains("WOOL") || string.contains("TERRACOTTA") || string.contains("GLASS") || string.contains("CONCRETE") || string.equals("SPONGE") || string.contains("SANDSTONE") || string.contains("END_STONE") || string.contains("LADDER") || string.contains("PLANKS") || string.contains("WOOD");
    }

    public static boolean isDiamond(Material material) {
        return material != null && material.name().equals("DIAMOND");
    }

    public static boolean isUtility(Material material) {
        if (material == null) {
            return false;
        }
        String string = material.name();
        return string.contains("TNT") || string.contains("FIREBALL") || string.contains("FIRE_CHARGE") || string.contains("EGG") || string.contains("SNOW") || string.contains("WATER") || string.contains("LAVA") || string.contains("PEARL") || string.contains("APPLE") || string.contains("MILK") || string.contains("SPONGE") || string.contains("CHEST") || string.contains("SILVERFISH") || string.contains("SPAWN_EGG") || string.contains("GOLEM") || string.contains("BUCKET") || string.contains("POTION") || string.contains("TOWER");
    }

    public static boolean isArmor(Material material) {
        if (material == null) {
            return false;
        }
        String string = material.name();
        return string.endsWith("_HELMET") || string.endsWith("_CHESTPLATE") || string.endsWith("_LEGGINGS") || string.endsWith("_BOOTS");
    }

    public static boolean isTool(Material material) {
        if (material == null) {
            return false;
        }
        String string = material.name();
        return string.endsWith("_PICKAXE") || string.endsWith("_AXE") || string.endsWith("_SHOVEL") || string.endsWith("_HOE") || string.contains("SHEARS") || string.equals("WOOD_SPADE") || string.equals("IRON_SPADE") || string.equals("STONE_SPADE") || string.equals("DIAMOND_SPADE") || string.equals("GOLD_SPADE");
    }

    public static boolean isBed(Material material) {
        if (material == null) {
            return false;
        }
        String string = material.name();
        return string.equals("BED") || string.equals("BED_BLOCK") || string.endsWith("_BED");
    }

    public static boolean isWool(Material material) {
        return material != null && material.name().contains("WOOL");
    }

    public static boolean isStick(Material material) {
        return material != null && material.name().equals("STICK");
    }

    public static boolean isSponge(Material material) {
        return material != null && material.name().contains("SPONGE");
    }
}

