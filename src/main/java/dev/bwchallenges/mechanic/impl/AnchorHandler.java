/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 */
package dev.bwchallenges.mechanic.impl;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.engine.ChallengeEngine;
import dev.bwchallenges.engine.MatchSession;
import dev.bwchallenges.mechanic.ChallengeHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class AnchorHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.ANCHOR;
    }

    @Override
    public void onMove(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Location location, Location location2) {
        challengeEngine.checkAnchorMove(player, matchSession, location, location2);
    }

    @Override
    public boolean blockConsumeGap(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
        matchSession.jumpAllowedUntil = System.currentTimeMillis() + 20000L;
        return false;
    }
}

