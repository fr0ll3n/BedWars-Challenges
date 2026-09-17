/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package dev.bwchallenges.mechanic.impl;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.engine.ChallengeEngine;
import dev.bwchallenges.engine.MatchSession;
import dev.bwchallenges.mechanic.ChallengeHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class SlowReflexesHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.SLOW_REFLEXES;
    }

    @Override
    public boolean blockMelee(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Player player2, ItemStack itemStack) {
        long l = System.currentTimeMillis();
        if (l - matchSession.lastHitAt < 2000L) {
            challengeEngine.deny(player, matchSession, "&cSlow Reflexes: wait 2 seconds between hits.");
            return true;
        }
        matchSession.lastHitAt = l;
        return false;
    }
}

