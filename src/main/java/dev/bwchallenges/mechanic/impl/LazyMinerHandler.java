/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 */
package dev.bwchallenges.mechanic.impl;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.engine.ChallengeEngine;
import dev.bwchallenges.engine.MatchSession;
import dev.bwchallenges.mechanic.ChallengeHandler;
import dev.bwchallenges.util.XMat;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public final class LazyMinerHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.LAZY_MINER;
    }

    @Override
    public void onStart(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
        challengeEngine.safePotion(player, "SLOW_DIGGING", 72000, 0);
    }

    @Override
    public void onTick(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, long l) {
        challengeEngine.tickLazyMiner(player);
    }

    @Override
    public boolean blockShopBuy(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, String string, String string2, Material material, String string3) {
        if (challengeEngine.looksLikeTntOrFireball(challengeEngine.shopBlob(string, string2, material, string3), material)) {
            challengeEngine.deny(player, matchSession, "&cYou cannot buy TNT or fireballs during this challenge.");
            return true;
        }
        return false;
    }

    @Override
    public boolean blockPlace(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Block block) {
        Material material = block.getType();
        if (material.name().contains("TNT") || XMat.isSponge(material)) {
            challengeEngine.deny(player, matchSession, "&cYou cannot use TNT or fireballs during this challenge.");
            return true;
        }
        return false;
    }

    @Override
    public boolean blockUtilityUse(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Material material) {
        if (material != null && (material.name().contains("TNT") || material.name().contains("FIRE"))) {
            challengeEngine.deny(player, matchSession, "&cYou cannot use TNT or fireballs during this challenge.");
            return true;
        }
        return false;
    }
}

