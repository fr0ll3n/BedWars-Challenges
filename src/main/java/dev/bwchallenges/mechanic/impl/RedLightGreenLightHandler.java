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

public final class RedLightGreenLightHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.RED_LIGHT_GREEN_LIGHT;
    }

    @Override
    public void onTick(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, long l) {
        challengeEngine.tickRedLight(player, matchSession, l);
    }

    @Override
    public void onMove(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Location location, Location location2) {
        double d;
        double d2;
        if (!matchSession.redLight) {
            return;
        }
        double d3 = location.getX() - location2.getX();
        if (d3 * d3 + (d2 = location.getY() - location2.getY()) * d2 + (d = location.getZ() - location2.getZ()) * d > 4.0E-4) {
            challengeEngine.fail(player, "You moved on red light.");
        }
    }
}

