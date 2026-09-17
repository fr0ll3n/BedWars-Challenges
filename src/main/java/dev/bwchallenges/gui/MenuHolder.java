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

public final class MenuHolder
implements InventoryHolder {
    private Inventory inventory;
    private int page;
    private final UUID owner;
    private final String mode;

    public MenuHolder(UUID uUID, int n) {
        this(uUID, n, null);
    }

    public MenuHolder(UUID uUID, int n, String string) {
        this.owner = uUID;
        this.page = n;
        this.mode = string;
    }

    public int page() {
        return this.page;
    }

    public UUID owner() {
        return this.owner;
    }

    public String mode() {
        return this.mode;
    }

    public void setPage(int n) {
        this.page = n;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}

