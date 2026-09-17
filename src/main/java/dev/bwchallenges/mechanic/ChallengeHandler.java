/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package dev.bwchallenges.mechanic;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.engine.ChallengeEngine;
import dev.bwchallenges.engine.MatchSession;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class ChallengeHandler {
    public abstract Challenge challenge();

    public void onStart(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
    }

    public void onTick(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, long l) {
    }

    public boolean blockShopBuy(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, String string, String string2, Material material, String string3) {
        return false;
    }

    public boolean blockUpgrade(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
        return false;
    }

    public boolean blockGenerator(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Material material, Item item) {
        return false;
    }

    public boolean blockDrop(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
        return false;
    }

    public boolean blockChest(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, boolean bl) {
        return false;
    }

    public boolean blockPickup(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Material material, boolean bl) {
        return false;
    }

    public boolean blockPlace(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Block block) {
        return false;
    }

    public boolean blockMelee(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Player player2, ItemStack itemStack) {
        return false;
    }

    public boolean blockBow(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
        return false;
    }

    public void onDamaged(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, String string, double d) {
    }

    public void onMove(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Location location, Location location2) {
    }

    public void onSprint(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
    }

    public void onSneak(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
    }

    public void onKill(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
    }

    public void onNonFinalKill(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
    }

    public void onPresidentDeath(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
    }

    public void onBedBrokenBy(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
    }

    public void onOwnBedLost(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
    }

    public boolean blockBedBreak(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, String string) {
        return false;
    }

    public double modifyDamage(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, double d, boolean bl) {
        return d;
    }

    public boolean blockRegen(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, boolean bl) {
        return false;
    }

    public boolean blockConsumeGap(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
        return false;
    }

    public boolean stripGapple(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
        return false;
    }

    public boolean blockUtilityUse(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Material material) {
        return false;
    }

    public boolean blockBreakAction(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
        return false;
    }

    public boolean blockHotbarChange(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, int n) {
        return false;
    }

    public boolean passesWin(ChallengeEngine challengeEngine, MatchSession matchSession) {
        return true;
    }

    public void onChat(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, String string) {
    }
}

