/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package dev.bwchallenges.mechanic;

import org.bukkit.inventory.ItemStack;

public final class WoolColors {
    private static final String[] DATA = new String[]{"White", "Orange", "Magenta", "Light Blue", "Yellow", "Lime", "Pink", "Gray", "Light Gray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black"};

    private WoolColors() {
    }

    public static boolean isWool(ItemStack itemStack) {
        return itemStack != null && itemStack.getType() != null && itemStack.getType().name().contains("WOOL");
    }

    public static String key(ItemStack itemStack) {
        if (!WoolColors.isWool(itemStack)) {
            return "";
        }
        String string = itemStack.getType().name();
        if (!string.equals("WOOL") && string.contains("_")) {
            return WoolColors.pretty(string.replace("_WOOL", "").replace("WOOL", ""));
        }
        try {
            int n = itemStack.getDurability() & 0xF;
            return DATA[n];
        }
        catch (Throwable throwable) {
            return "White";
        }
    }

    public static String pretty(String string) {
        if (string == null || string.isEmpty()) {
            return "White";
        }
        String[] stringArray = string.toLowerCase().replace('_', ' ').trim().split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        for (String string2 : stringArray) {
            if (string2.isEmpty()) continue;
            if (stringBuilder.length() > 0) {
                stringBuilder.append(' ');
            }
            stringBuilder.append(Character.toUpperCase(string2.charAt(0)));
            if (string2.length() <= 1) continue;
            stringBuilder.append(string2.substring(1));
        }
        return stringBuilder.length() == 0 ? "White" : stringBuilder.toString();
    }
}

