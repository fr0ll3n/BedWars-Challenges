/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 */
package dev.bwchallenges.api;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.ChallengesPlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChallengesAPI {
    private ChallengesAPI() {
    }

    public static ChallengesPlugin plugin() {
        return (ChallengesPlugin)JavaPlugin.getPlugin(ChallengesPlugin.class);
    }

    public static void openMenu(Player player) {
        ChallengesAPI.plugin().menu().open(player);
    }

    public static Challenge active(Player player) {
        return ChallengesAPI.plugin().manager().activeOf(player);
    }
}

