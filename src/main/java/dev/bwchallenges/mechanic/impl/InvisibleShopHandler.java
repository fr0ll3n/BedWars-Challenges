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

public final class InvisibleShopHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.INVISIBLE_SHOP;
    }

    @Override
    public void onStart(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
        try {
            player.sendMessage("\u00a77\u00a7lInvisible Shop \u00a77\u2014 shop items are hidden. Memorize the slots!");
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

