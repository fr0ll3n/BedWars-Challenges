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

public final class CantTouchThisHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.CANT_TOUCH_THIS;
    }

    @Override
    public void onDamaged(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, String string, double d) {
        if (d <= 0.0) {
            return;
        }
        if (string != null && string.toUpperCase().contains("VOID")) {
            challengeEngine.fail(player, "You fell into the void.");
        } else {
            challengeEngine.fail(player, "You took damage.");
        }
    }

    @Override
    public void onMove(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Location location, Location location2) {
        if (location2.getY() < 0.0) {
            challengeEngine.fail(player, "You fell into the void.");
        }
    }

    @Override
    public boolean passesWin(ChallengeEngine challengeEngine, MatchSession matchSession) {
        return matchSession.bedBrokenByTeam;
    }
}

