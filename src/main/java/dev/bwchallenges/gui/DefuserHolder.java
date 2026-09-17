/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 */
package dev.bwchallenges.gui;

import java.util.UUID;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class DefuserHolder
implements InventoryHolder {
    private Inventory inventory;
    private final UUID owner;
    private final String teamKey;
    public int progress;

    public DefuserHolder(UUID uUID, String string) {
        this.owner = uUID;
        this.teamKey = string == null ? "" : string.toLowerCase();
    }

    public UUID owner() {
        return this.owner;
    }

    public String teamKey() {
        return this.teamKey;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}

