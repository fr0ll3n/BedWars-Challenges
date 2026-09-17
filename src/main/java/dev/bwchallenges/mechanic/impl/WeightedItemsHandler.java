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

public final class WeightedItemsHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.WEIGHTED_ITEMS;
    }

    @Override
    public void onTick(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, long l) {
        challengeEngine.tickWeightedItems(player);
    }
}

