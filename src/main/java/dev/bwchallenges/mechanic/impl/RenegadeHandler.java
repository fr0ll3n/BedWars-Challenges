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

public final class RenegadeHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.RENEGADE;
    }

    @Override
    public boolean blockUpgrade(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
        challengeEngine.deny(player, matchSession, "&cYou cannot use upgrades and traps during the &6Renegade &cchallenge");
        return true;
    }

    @Override
    public boolean blockGenerator(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Material material, Item item) {
        if (XMat.isDiamond(material)) {
            challengeEngine.deny(player, matchSession, "&cYou cannot pick up diamonds during &6Renegade&c.");
            challengeEngine.softBlockItem(item);
            return true;
        }
        return false;
    }

    @Override
    public boolean blockPickup(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Material material, boolean bl) {
        if (XMat.isDiamond(material)) {
            challengeEngine.deny(player, matchSession, "&cYou cannot pick up diamonds during &6Renegade&c.");
            return true;
        }
        return false;
    }
}

