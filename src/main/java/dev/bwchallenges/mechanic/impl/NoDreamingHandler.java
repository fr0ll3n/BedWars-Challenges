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

public final class NoDreamingHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.NO_DREAMING;
    }

    @Override
    public void onNonFinalKill(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
        challengeEngine.fail(player, "You got a non-final kill.");
    }

    @Override
    public void onOwnBedLost(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
    }

    @Override
    public void onPresidentDeath(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
    }
}

