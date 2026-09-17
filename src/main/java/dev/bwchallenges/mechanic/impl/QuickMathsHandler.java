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

public final class QuickMathsHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.QUICK_MATHS;
    }

    @Override
    public void onTick(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, long l) {
        challengeEngine.tickQuickMaths(player, matchSession, l);
    }

    @Override
    public void onChat(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, String string) {
        challengeEngine.handleMathAnswer(player, matchSession, string);
    }
}

