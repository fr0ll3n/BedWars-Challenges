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

public final class ProtectThePresidentHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.PROTECT_THE_PRESIDENT;
    }

    @Override
    public boolean blockBedBreak(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, String string) {
        if (matchSession.presidentId != null && !matchSession.presidentId.equals(player.getUniqueId())) {
            challengeEngine.deny(player, matchSession, "&cOnly the President can break beds.");
            return true;
        }
        return false;
    }

    @Override
    public void onPresidentDeath(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
        if (matchSession.presidentId != null && matchSession.presidentId.equals(player.getUniqueId())) {
            challengeEngine.fail(player, "The President died.");
        }
    }
}

