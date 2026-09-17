/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityPickupItemEvent
 */
package dev.bwchallenges.listener;

import dev.bwchallenges.listener.PlayListener;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public final class PickupListenerModern
implements Listener {
    private final PlayListener parent;

    public PickupListenerModern(PlayListener playListener) {
        this.parent = playListener;
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onPickup(EntityPickupItemEvent entityPickupItemEvent) {
        if (!(entityPickupItemEvent.getEntity() instanceof Player)) {
            return;
        }
        this.parent.handlePickup((Player)entityPickupItemEvent.getEntity(), entityPickupItemEvent.getItem(), (Cancellable)entityPickupItemEvent);
    }
}

