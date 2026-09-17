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

public final class SelfishHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.SELFISH;
    }

    @Override
    public boolean blockDrop(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
        challengeEngine.deny(player, matchSession, "&cYou cannot drop anything during the &6Selfish &cchallenge");
        return true;
    }

    @Override
    public boolean blockChest(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, boolean bl) {
        challengeEngine.deny(player, matchSession, "&cThis chest is locked during the &6Selfish &cchallenge");
        return true;
    }
}

