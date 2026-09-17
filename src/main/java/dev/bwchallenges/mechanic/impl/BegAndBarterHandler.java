/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 */
package dev.bwchallenges.mechanic.impl;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.engine.ChallengeEngine;
import dev.bwchallenges.engine.MatchSession;
import dev.bwchallenges.mechanic.ChallengeHandler;
import dev.bwchallenges.util.XMat;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public final class BegAndBarterHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.BEG_AND_BARTER;
    }

    @Override
    public boolean blockGenerator(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Material material, Item item) {
        if (challengeEngine.onOwnIsland(player, matchSession) && XMat.isResource(material)) {
            challengeEngine.deny(player, matchSession, "&cBeg & Barter: you cannot collect from your own generator.");
            challengeEngine.softBlockItem(item);
            return true;
        }
        return false;
    }

    @Override
    public boolean blockPickup(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Material material, boolean bl) {
        if (challengeEngine.onOwnIsland(player, matchSession) && XMat.isResource(material)) {
            challengeEngine.deny(player, matchSession, "&cBeg & Barter: you cannot collect from your own generator.");
            return true;
        }
        return false;
    }
}

