/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.player.PlayerEvent
 */
package dev.bwchallenges.api.event;

import dev.bwchallenges.Challenge;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class ChallengeCompleteEvent
extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Challenge challenge;

    public ChallengeCompleteEvent(Player player, Challenge challenge) {
        super(player);
        this.challenge = challenge;
    }

    public Challenge getChallenge() {
        return this.challenge;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

