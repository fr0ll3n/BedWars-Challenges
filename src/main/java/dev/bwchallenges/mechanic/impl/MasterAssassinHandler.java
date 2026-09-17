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

public final class MasterAssassinHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.MASTER_ASSASSIN;
    }

    @Override
    public boolean blockBedBreak(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, String string) {
        if (matchSession.targetTeam != null && !matchSession.targetTeam.isEmpty() && string != null && !string.equalsIgnoreCase(matchSession.targetTeam)) {
            challengeEngine.deny(player, matchSession, "&cAssassin: that is not your target team.");
            return true;
        }
        return false;
    }

    @Override
    public boolean blockMelee(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Player player2, ItemStack itemStack) {
        if (matchSession.targetTeam == null || matchSession.targetTeam.isEmpty() || challengeEngine.pluginHook() == null) {
            return false;
        }
        String string = challengeEngine.pluginHook().teamName(player2);
        if (string != null && !string.isEmpty() && !string.equalsIgnoreCase(matchSession.targetTeam)) {
            challengeEngine.deny(player, matchSession, "&cMaster Assassin: you may only attack your target team.");
            return true;
        }
        return false;
    }
}

