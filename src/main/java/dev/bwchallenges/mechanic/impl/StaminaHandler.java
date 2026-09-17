/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package dev.bwchallenges.mechanic.impl;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.engine.ChallengeEngine;
import dev.bwchallenges.engine.MatchSession;
import dev.bwchallenges.mechanic.ChallengeHandler;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class StaminaHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.STAMINA;
    }

    @Override
    public void onTick(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, long l) {
        challengeEngine.tickStamina(player, matchSession, l);
    }

    @Override
    public boolean blockPlace(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Block block) {
        if (player.getFoodLevel() <= 0) {
            challengeEngine.deny(player, matchSession, "&cNot enough stamina to place blocks.");
            return true;
        }
        return false;
    }

    @Override
    public boolean blockMelee(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Player player2, ItemStack itemStack) {
        if (player.getFoodLevel() <= 0) {
            challengeEngine.deny(player, matchSession, "&cNot enough stamina to hit.");
            return true;
        }
        player.setFoodLevel(Math.max(0, player.getFoodLevel() - 1));
        return false;
    }

    @Override
    public boolean blockBreakAction(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
        if (player.getFoodLevel() <= 0) {
            challengeEngine.deny(player, matchSession, "&cNot enough stamina to break blocks.");
            return true;
        }
        player.setFoodLevel(Math.max(0, player.getFoodLevel() - 1));
        return false;
    }

    @Override
    public boolean blockConsumeGap(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
        matchSession.staminaBoostUntil = System.currentTimeMillis() + 10000L;
        return false;
    }
}

