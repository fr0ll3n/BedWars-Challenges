/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package dev.bwchallenges.mechanic.impl;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.engine.ChallengeEngine;
import dev.bwchallenges.engine.MatchSession;
import dev.bwchallenges.mechanic.ChallengeHandler;
import dev.bwchallenges.util.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class BedsAndBloodlustHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.BEDS_AND_BLOODLUST;
    }

    @Override
    public void onTick(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, long l) {
        if (matchSession.bloodlustUntil > 0L && l > matchSession.bloodlustUntil) {
            challengeEngine.fail(player, "You did not get a kill within 90 seconds of breaking a bed.");
        }
    }

    @Override
    public void onBedBrokenBy(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
        matchSession.bloodlustUntil = System.currentTimeMillis() + 90000L;
        Text.send((CommandSender)player, "&eBeds & Bloodlust: get a kill within 90 seconds!");
    }

    @Override
    public void onKill(ChallengeEngine challengeEngine, Player player, MatchSession matchSession) {
        matchSession.bloodlustUntil = 0L;
    }

    @Override
    public boolean passesWin(ChallengeEngine challengeEngine, MatchSession matchSession) {
        return matchSession.bedBrokenByTeam;
    }
}

