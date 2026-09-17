/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 */
package dev.bwchallenges.mechanic.impl;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.engine.ChallengeEngine;
import dev.bwchallenges.engine.MatchSession;
import dev.bwchallenges.mechanic.ChallengeHandler;
import dev.bwchallenges.util.XMat;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class WoodworkerHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.WOODWORKER;
    }

    @Override
    public boolean blockShopBuy(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, String string, String string2, Material material, String string3) {
        if (!challengeEngine.looksLikeWoodItem(challengeEngine.shopBlob(string, string2, material, string3), material)) {
            challengeEngine.deny(player, matchSession, "&cYou can only buy items made of wood.");
            return true;
        }
        return false;
    }

    @Override
    public boolean blockChest(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, boolean bl) {
        if (bl) {
            challengeEngine.deny(player, matchSession, "&cWoodworker: your Ender Chest isn't made of wood.");
            return true;
        }
        return false;
    }

    @Override
    public boolean blockPickup(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Material material, boolean bl) {
        return material != null && !XMat.isWood(material);
    }
}

