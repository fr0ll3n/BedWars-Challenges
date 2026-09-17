/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 */
package dev.bwchallenges.hook;

import dev.bwchallenges.Challenge;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface BedWarsHook {
    public boolean isInArena(Player var1);

    public boolean isPlaying(Player var1);

    public boolean isOwnPlacedBlock(Player var1, Block var2);

    public String teamName(Player var1);

    public String teamNameAt(Player var1, Block var2);

    public void register();

    default public void persistActive(Player player, Challenge challenge) {
    }

    default public Challenge readPersistedActive(Player player) {
        return null;
    }

    default public String arenaKey(Player player) {
        return "";
    }

    default public boolean isTeamColoredBlock(Player player, Block block) {
        return this.isOwnPlacedBlock(player, block);
    }

    default public boolean isPlacedBlock(Player player, Block block) {
        return false;
    }

    default public void restoreStarterKit(Player player) {
    }
}

