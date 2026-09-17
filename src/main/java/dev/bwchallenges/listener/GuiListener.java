/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.plugin.Plugin
 */
package dev.bwchallenges.listener;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.ChallengesPlugin;
import dev.bwchallenges.PlayerProfile;
import dev.bwchallenges.gui.ChallengeMenu;
import dev.bwchallenges.gui.Items;
import dev.bwchallenges.gui.MenuHolder;
import dev.bwchallenges.util.Sounds;
import dev.bwchallenges.util.Text;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public final class GuiListener
implements Listener {
    private final ChallengesPlugin plugin;

    public GuiListener(ChallengesPlugin challengesPlugin) {
        this.plugin = challengesPlugin;
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent playerJoinEvent) {
        this.plugin.manager().loadAsync(playerJoinEvent.getPlayer());
        this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, new Runnable(){

            @Override
            public void run() {
                if (playerJoinEvent.getPlayer().isOnline()) {
                    GuiListener.this.plugin.manager().profile(playerJoinEvent.getPlayer());
                    GuiListener.this.plugin.giveLobbyItem(playerJoinEvent.getPlayer());
                    if (GuiListener.this.plugin.rewards() != null) {
                        GuiListener.this.plugin.rewards().apply(playerJoinEvent.getPlayer());
                    }
                }
            }
        }, 20L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent playerQuitEvent) {
        this.plugin.manager().unload(playerQuitEvent.getPlayer().getUniqueId());
        this.plugin.engine().clear(playerQuitEvent.getPlayer().getUniqueId());
        this.plugin.menu().closed(playerQuitEvent.getPlayer());
        this.plugin.clearPlayerMode(playerQuitEvent.getPlayer());
        if (this.plugin.rewards() != null) {
            this.plugin.rewards().clear(playerQuitEvent.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent inventoryClickEvent) {
        boolean bl;
        boolean bl2;
        if (!(inventoryClickEvent.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player)inventoryClickEvent.getWhoClicked();
        Inventory inventory = inventoryClickEvent.getView().getTopInventory();
        boolean bl3 = bl2 = inventory != null && inventory.getHolder() instanceof MenuHolder;
        if (!bl2 && this.plugin.menu().isViewing(player) && inventoryClickEvent.getView().getTopInventory() != null && inventoryClickEvent.getView().getTopInventory().getSize() == 54) {
            bl2 = true;
        }
        if (!bl2) {
            return;
        }
        inventoryClickEvent.setCancelled(true);
        if (inventoryClickEvent.getClickedInventory() == null || inventoryClickEvent.getClickedInventory() != inventory) {
            return;
        }
        int n = 0;
        String string = this.plugin.getPlayerMode(player);
        if (inventory.getHolder() instanceof MenuHolder) {
            MenuHolder menuHolder = (MenuHolder)inventory.getHolder();
            n = menuHolder.page();
            if (menuHolder.mode() != null && !menuHolder.mode().isEmpty()) {
                string = menuHolder.mode();
                this.plugin.setPlayerMode(player, string);
            }
        } else if (this.plugin.menu().pageOf(player) != null) {
            n = this.plugin.menu().pageOf(player);
        }
        int n2 = inventoryClickEvent.getRawSlot();
        boolean bl4 = bl = inventoryClickEvent.getClick() == ClickType.RIGHT || inventoryClickEvent.getClick() == ClickType.SHIFT_RIGHT;
        if (n2 == 49) {
            player.closeInventory();
            if (string != null) {
                this.plugin.setPlayerMode(player, string);
            }
            this.plugin.runGoBack(player);
            return;
        }
        if (n2 == 50) {
            return;
        }
        if (n2 == 45) {
            this.plugin.menu().open(player, bl ? 0 : n - 1);
            return;
        }
        if (n2 == 53) {
            this.plugin.menu().open(player, bl ? this.plugin.menu().maxPages() - 1 : n + 1);
            return;
        }
        if (n2 == 48) {
            this.plugin.manager().clearActive(player);
            Text.send((CommandSender)player, this.plugin.msg("challenge.messages.menu.deactivated", "&cDeactivated Challenge: &6{challenge}").replace("{challenge}", "NONE"));
            Sounds.deactivate(player);
            this.plugin.menu().open(player, n);
            return;
        }
        int n3 = this.plugin.menu().indexOfSlot(n2);
        if (n3 < 0) {
            return;
        }
        ArrayList<Challenge> arrayList = new ArrayList<Challenge>();
        for (Challenge challenge : Challenge.values()) {
            if (!challenge.selectable()) continue;
            arrayList.add(challenge);
        }
        int n4 = n * ChallengeMenu.PER_PAGE + n3;
        if (n4 < 0 || n4 >= arrayList.size()) {
            return;
        }
        Challenge challenge = (Challenge)((Object)arrayList.get(n4));
        PlayerProfile playerProfile = this.plugin.manager().profile(player);
        if (!playerProfile.isUnlocked(challenge)) {
            Text.send((CommandSender)player, this.plugin.msg("challenge.messages.menu.not-unlocked", "&cYou have not unlocked this challenge yet!"));
            Sounds.deny(player);
            return;
        }
        if (playerProfile.hasUnclaimedReward(challenge) && this.plugin.rewards() != null) {
            this.plugin.rewards().claim(player, challenge);
            this.plugin.menu().open(player, n);
            return;
        }
        if (playerProfile.active() == challenge) {
            this.plugin.manager().clearActive(player);
            Text.send((CommandSender)player, this.plugin.msg("challenge.messages.menu.deactivated", "&cDeactivated Challenge: &6{challenge}").replace("{challenge}", challenge.displayName()));
            Sounds.deactivate(player);
        } else {
            this.plugin.manager().setActive(player, challenge);
            Text.send((CommandSender)player, this.plugin.msg("challenge.messages.menu.activated", "&aActivated Challenge: &6{challenge}").replace("{challenge}", challenge.displayName()));
            Sounds.activate(player);
        }
        this.plugin.menu().open(player, n);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent inventoryDragEvent) {
        Inventory inventory = inventoryDragEvent.getView().getTopInventory();
        if (inventory != null && inventory.getHolder() instanceof MenuHolder) {
            inventoryDragEvent.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent inventoryCloseEvent) {
        if (inventoryCloseEvent.getPlayer() instanceof Player && inventoryCloseEvent.getInventory().getHolder() instanceof MenuHolder) {
            this.plugin.menu().closed((Player)inventoryCloseEvent.getPlayer());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent playerInteractEvent) {
        if (!Items.isLobbySelector(playerInteractEvent.getItem())) {
            return;
        }
        playerInteractEvent.setCancelled(true);
        try {
            if (this.plugin.hook() != null && this.plugin.hook().isPlaying(playerInteractEvent.getPlayer())) {
                Text.send((CommandSender)playerInteractEvent.getPlayer(), this.plugin.msg("challenge.messages.menu.arena-open", "&cYou can only open the menu in the lobby!"));
            } else {
                this.plugin.menu().open(playerInteractEvent.getPlayer());
            }
        }
        catch (Throwable throwable) {
            this.plugin.logSafe("Lobby item failed to open menu", throwable);
            Text.send((CommandSender)playerInteractEvent.getPlayer(), "&cCould not open the challenges menu.");
        }
    }
}

