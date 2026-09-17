/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 */
package dev.bwchallenges.mechanic.impl;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.engine.ChallengeEngine;
import dev.bwchallenges.engine.MatchSession;
import dev.bwchallenges.mechanic.ChallengeHandler;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public final class BlockrepellentBedsHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.BLOCKREPELLENT_BEDS;
    }

    @Override
    public boolean blockPlace(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Block block) {
        if (challengeEngine.nearBed(block)) {
            challengeEngine.deny(player, matchSession, "&cBlockrepellent Beds: you cannot place blocks next to a bed.");
            return true;
        }
        return false;
    }
}

