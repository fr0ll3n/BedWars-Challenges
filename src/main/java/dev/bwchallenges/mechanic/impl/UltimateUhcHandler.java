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

public final class UltimateUhcHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.ULTIMATE_UHC;
    }

    @Override
    public boolean blockRegen(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, boolean bl) {
        return bl;
    }

    @Override
    public boolean stripGapple(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
        return true;
    }
}

