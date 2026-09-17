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

public final class WoolWarriorHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.WOOL_WARRIOR;
    }

    @Override
    public boolean blockShopBuy(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, String string, String string2, Material material, String string3) {
        String string4 = challengeEngine.shopBlob(string, string2, material, string3);
        if (XMat.isWool(material) || string4.contains("wool")) {
            return false;
        }
        challengeEngine.deny(player, matchSession, "&cYou can only buy wool.");
        return true;
    }

    @Override
    public boolean blockPickup(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Material material, boolean bl) {
        return bl;
    }
}

