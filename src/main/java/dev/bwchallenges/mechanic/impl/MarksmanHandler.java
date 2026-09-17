/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package dev.bwchallenges.mechanic.impl;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.engine.ChallengeEngine;
import dev.bwchallenges.engine.MatchSession;
import dev.bwchallenges.mechanic.ChallengeHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class MarksmanHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.MARKSMAN;
    }

    @Override
    public boolean blockShopBuy(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, String string, String string2, Material material, String string3) {
        String string4 = challengeEngine.shopBlob(string, string2, material, string3);
        if (challengeEngine.looksLikeMelee(string4, material) || challengeEngine.looksLikeSword(string4, material) || string4.contains("stick")) {
            challengeEngine.deny(player, matchSession, "&cYou can't buy any melee weapons in this challenge!");
            return true;
        }
        return false;
    }

    @Override
    public boolean blockMelee(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Player player2, ItemStack itemStack) {
        challengeEngine.deny(player, matchSession, "&cMarksman: bows only.");
        return true;
    }
}

