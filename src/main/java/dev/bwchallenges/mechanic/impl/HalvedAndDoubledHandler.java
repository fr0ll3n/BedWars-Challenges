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

public final class HalvedAndDoubledHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.HALVED_AND_DOUBLED;
    }

    @Override
    public void onStart(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
        challengeEngine.setMaxHealth(player, 10.0);
    }

    @Override
    public double modifyDamage(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, double d, boolean bl) {
        return bl ? d * 2.0 : d;
    }
}

