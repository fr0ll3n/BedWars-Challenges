/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package dev.bwchallenges.mechanic.impl;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.engine.ChallengeEngine;
import dev.bwchallenges.engine.MatchSession;
import dev.bwchallenges.mechanic.ChallengeHandler;
import org.bukkit.entity.Player;

public final class DefuserHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.DEFUSER;
    }

    @Override
    public boolean blockBedBreak(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, String string) {
        if (string != null && !matchSession.defusedBeds.contains(string.toLowerCase())) {
            challengeEngine.defuser().open(player, string.toLowerCase());
            challengeEngine.deny(player, matchSession, "&eDefuse the bed in the menu first!");
            return true;
        }
        return false;
    }
}

