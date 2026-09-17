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

public final class AssassinHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.ASSASSIN;
    }

    @Override
    public boolean blockBedBreak(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, String string) {
        if (matchSession.targetTeam != null && !matchSession.targetTeam.isEmpty() && string != null && !string.equalsIgnoreCase(matchSession.targetTeam)) {
            challengeEngine.deny(player, matchSession, "&cAssassin: that is not your target team.");
            return true;
        }
        return false;
    }
}

