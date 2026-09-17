/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Sound
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 */
package dev.bwchallenges.util;

import dev.bwchallenges.ChallengesPlugin;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Sounds {
    private Sounds() {
    }

    private static ChallengesPlugin plugin() {
        try {
            return (ChallengesPlugin)JavaPlugin.getPlugin(ChallengesPlugin.class);
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    private static void playConfigured(Player player, String string, String ... stringArray) {
        String string2;
        Object object;
        if (player == null || !player.isOnline()) {
            return;
        }
        ChallengesPlugin challengesPlugin = Sounds.plugin();
        String string3 = null;
        float f = 1.0f;
        float f2 = 1.0f;
        if (challengesPlugin != null && ((object = challengesPlugin.getConfig()).isConfigurationSection(string2 = "sounds." + string) || object.contains(string2 + ".sound"))) {
            string3 = object.getString(string2 + ".sound", null);
            f = (float)object.getDouble(string2 + ".volume", 1.0);
            double d = object.getDouble(string2 + ".pitch", 100.0);
            f2 = d > 2.0 ? (float)(d / 100.0) : (float)d;
        }
        if (!(string3 == null || string3.isEmpty() || string3.equalsIgnoreCase("none") || string3.equalsIgnoreCase("off"))) {
            if (Sounds.tryPlay(player, string3, f, f2)) {
                return;
            }
            object = string3.toUpperCase();
            if (Sounds.tryPlay(player, (String)object, f, f2)) {
                return;
            }
            if (Sounds.tryPlay(player, "BLOCK_" + (String)object, f, f2)) {
                return;
            }
            if (Sounds.tryPlay(player, "ENTITY_" + (String)object, f, f2)) {
                return;
            }
            if (Sounds.tryPlay(player, "UI_" + (String)object, f, f2)) {
                return;
            }
            if (((String)object).startsWith("NOTE_")) {
                if (Sounds.tryPlay(player, "BLOCK_NOTE_" + ((String)object).substring(5), f, f2)) {
                    return;
                }
                if (Sounds.tryPlay(player, "NOTE_" + ((String)object).substring(5), f, f2)) {
                    return;
                }
            }
            if (((String)object).startsWith("BLOCK_NOTE_") && Sounds.tryPlay(player, "NOTE_" + ((String)object).substring(11), f, f2)) {
                return;
            }
        }
        for (String string4 : stringArray) {
            if (!Sounds.tryPlay(player, string4, f, f2)) continue;
            return;
        }
    }

    private static boolean tryPlay(Player player, String string, float f, float f2) {
        if (string == null || string.isEmpty()) {
            return false;
        }
        try {
            Sound sound = Sound.valueOf((String)string);
            player.playSound(player.getLocation(), sound, f, f2);
            return true;
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return false;
        }
        catch (Throwable throwable) {
            try {
                player.playSound(player.getLocation(), string, f, f2);
                return true;
            }
            catch (Throwable throwable2) {
                return false;
            }
        }
    }

    public static void activate(Player player) {
        Sounds.playConfigured(player, "activate", "UI_TOAST_CHALLENGE_COMPLETE", "ENTITY_PLAYER_LEVELUP", "LEVEL_UP");
    }

    public static void deactivate(Player player) {
        Sounds.playConfigured(player, "deactivate", "BLOCK_NOTE_BLOCK_BASS", "BLOCK_NOTE_BASS", "NOTE_BASS");
    }

    public static void deny(Player player) {
        Sounds.playConfigured(player, "deny", "ENTITY_VILLAGER_NO", "VILLAGER_NO", "BLOCK_ANVIL_LAND", "ANVIL_LAND");
    }

    public static void click(Player player) {
        Sounds.playConfigured(player, "click", "NOTE_PLING", "BLOCK_NOTE_PLING", "BLOCK_NOTE_BLOCK_PLING", "UI_BUTTON_CLICK", "CLICK");
    }

    public static void redLight(Player player) {
        Sounds.playConfigured(player, "red-light", "BLOCK_NOTE_BLOCK_PLING", "BLOCK_NOTE_PLING", "NOTE_PLING");
    }

    public static void greenLight(Player player) {
        Sounds.playConfigured(player, "green-light", "BLOCK_NOTE_BLOCK_CHIME", "BLOCK_NOTE_CHIME", "NOTE_PLING");
    }

    public static void complete(Player player) {
        Sounds.playConfigured(player, "complete", "UI_TOAST_CHALLENGE_COMPLETE", "ENTITY_FIREWORK_ROCKET_BLAST", "FIREWORK_BLAST", "ENTITY_PLAYER_LEVELUP", "LEVEL_UP");
    }

    public static void fail(Player player) {
        Sounds.playConfigured(player, "fail", "ENTITY_WITHER_HURT", "ENTITY_ENDER_DRAGON_GROWL", "WITHER_HURT");
    }

    public static void page(Player player) {
        Sounds.playConfigured(player, "page", "ITEM_BOOK_PAGE_TURN", "UI_BUTTON_CLICK", "CLICK", "NOTE_PLING");
    }

    public static void reward(Player player) {
        Sounds.playConfigured(player, "reward", "ENTITY_EXPERIENCE_ORB_PICKUP", "ORB_PICKUP", "ENTITY_PLAYER_LEVELUP");
    }
}

