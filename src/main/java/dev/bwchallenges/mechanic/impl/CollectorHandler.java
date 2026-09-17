/*
 * Decompiled with CFR 0.152.
 */
package dev.bwchallenges.mechanic.impl;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.engine.ChallengeEngine;
import dev.bwchallenges.engine.MatchSession;
import dev.bwchallenges.mechanic.ChallengeHandler;

public final class CollectorHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.COLLECTOR;
    }

    @Override
    public boolean passesWin(ChallengeEngine challengeEngine, MatchSession matchSession) {
        return matchSession.woolSubmitted && matchSession.collectedWool.size() >= Math.max(2, matchSession.enemyTeams + 1);
    }
}

