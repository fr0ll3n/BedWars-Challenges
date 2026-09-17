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

public final class SleightOfHandHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.SLEIGHT_OF_HAND;
    }

    @Override
    public void onStart(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
        challengeEngine.applySleightPanes(player);
    }

    @Override
    public void onTick(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, long l) {
        challengeEngine.applySleightPanes(player);
    }

    @Override
    public boolean blockHotbarChange(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, int n) {
        if (n != 0) {
            player.getInventory().setHeldItemSlot(0);
            challengeEngine.deny(player, matchSession, "&cSleight of Hand: only slot 1 can be used.");
            return true;
        }
        return false;
    }
}

