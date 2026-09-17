/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Chest
 *  org.bukkit.block.DoubleChest
 *  org.bukkit.entity.Arrow
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityRegainHealthEvent
 *  org.bukkit.event.entity.EntityRegainHealthEvent$RegainReason
 *  org.bukkit.event.entity.EntityShootBowEvent
 *  org.bukkit.event.entity.FoodLevelChangeEvent
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryOpenEvent
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.event.player.PlayerDropItemEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerItemConsumeEvent
 *  org.bukkit.event.player.PlayerItemHeldEvent
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.event.player.PlayerPickupItemEvent
 *  org.bukkit.event.player.PlayerToggleSneakEvent
 *  org.bukkit.event.player.PlayerToggleSprintEvent
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.potion.PotionEffectType
 */
package dev.bwchallenges.listener;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.ChallengesPlugin;
import dev.bwchallenges.engine.ChallengeEngine;
import dev.bwchallenges.engine.MatchSession;
import dev.bwchallenges.gui.DefuserHolder;
import dev.bwchallenges.gui.MenuHolder;
import dev.bwchallenges.listener.PickupListenerModern;
import dev.bwchallenges.util.XMat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

public final class PlayListener
implements Listener {
    private final ChallengesPlugin plugin;

    public PlayListener(ChallengesPlugin challengesPlugin) {
        this.plugin = challengesPlugin;
        try {
            Class.forName("org.bukkit.event.entity.EntityPickupItemEvent");
            Bukkit.getPluginManager().registerEvents((Listener)new PickupListenerModern(this), (Plugin)challengesPlugin);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public ChallengeEngine engine() {
        return this.plugin.engine();
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onDrop(PlayerDropItemEvent playerDropItemEvent) {
        if (this.engine().blockDrop(playerDropItemEvent.getPlayer())) {
            playerDropItemEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onPickupLegacy(PlayerPickupItemEvent playerPickupItemEvent) {
        this.handlePickup(playerPickupItemEvent.getPlayer(), playerPickupItemEvent.getItem(), (Cancellable)playerPickupItemEvent);
    }

    public void handlePickup(Player player, Item item, Cancellable cancellable) {
        ItemStack itemStack = item == null ? null : item.getItemStack();
        Material material = itemStack == null ? Material.AIR : itemStack.getType();
        boolean bl = item != null && item.getPickupDelay() > 0 && !this.looksLikeGeneratorItem(itemStack);
        this.engine().noteCollectorWool(player, itemStack);
        if (this.engine().blockPickup(player, material, bl) || this.engine().blockGenerator(player, material, item)) {
            cancellable.setCancelled(true);
            this.engine().softBlockItem(item);
        }
    }

    private boolean looksLikeGeneratorItem(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()) {
            return false;
        }
        return itemStack.getItemMeta().getDisplayName().toLowerCase().contains("custom");
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onChest(InventoryOpenEvent inventoryOpenEvent) {
        if (!(inventoryOpenEvent.getPlayer() instanceof Player) || inventoryOpenEvent.getInventory() == null) {
            return;
        }
        Player player = (Player)inventoryOpenEvent.getPlayer();
        if (inventoryOpenEvent.getInventory().getHolder() instanceof MenuHolder) {
            return;
        }
        this.engine().scheduleHideShop(player);
        this.engine().submitCollectorWool(player);
        boolean bl = (inventoryOpenEvent.getInventory().getType() == null ? "" : inventoryOpenEvent.getInventory().getType().name()).contains("ENDER");
        if ((bl || this.isRealWorldChest(inventoryOpenEvent.getInventory().getHolder())) && this.engine().blockRealChest(player, bl)) {
            inventoryOpenEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onDefuserClick(InventoryClickEvent inventoryClickEvent) {
        if (!(inventoryClickEvent.getWhoClicked() instanceof Player) || inventoryClickEvent.getView() == null || inventoryClickEvent.getView().getTopInventory() == null || !(inventoryClickEvent.getView().getTopInventory().getHolder() instanceof DefuserHolder)) {
            return;
        }
        inventoryClickEvent.setCancelled(true);
        Player player = (Player)inventoryClickEvent.getWhoClicked();
        DefuserHolder defuserHolder = (DefuserHolder)inventoryClickEvent.getView().getTopInventory().getHolder();
        if (inventoryClickEvent.getClickedInventory() != inventoryClickEvent.getView().getTopInventory()) {
            return;
        }
        this.engine().defuser().click(player, defuserHolder, inventoryClickEvent.getCurrentItem());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onShopClick(InventoryClickEvent inventoryClickEvent) {
        if (!(inventoryClickEvent.getWhoClicked() instanceof Player)) {
            return;
        }
        this.engine().scheduleHideShop((Player)inventoryClickEvent.getWhoClicked());
    }

    private boolean isRealWorldChest(InventoryHolder inventoryHolder) {
        if (inventoryHolder == null || inventoryHolder instanceof Player) {
            return false;
        }
        if (inventoryHolder instanceof Chest || inventoryHolder instanceof DoubleChest) {
            return true;
        }
        String string = inventoryHolder.getClass().getName().toLowerCase();
        if (string.contains("enderchest") || string.endsWith(".enderchest")) {
            return true;
        }
        if (string.contains("shop") || string.contains("upgrade") || string.contains("menu") || string.contains("npc") || string.contains("citizen") || string.contains("villager")) {
            return false;
        }
        try {
            if (inventoryHolder instanceof BlockState) {
                String string2 = ((BlockState)inventoryHolder).getType().name();
                return string2.contains("CHEST") || string2.contains("BARREL") || string2.contains("ENDER");
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return false;
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onPlace(BlockPlaceEvent blockPlaceEvent) {
        if (this.engine().blockPlace(blockPlaceEvent.getPlayer(), blockPlaceEvent.getBlockPlaced())) {
            blockPlaceEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBedBreak(BlockBreakEvent blockBreakEvent) {
        if (this.engine().blockBreakAction(blockBreakEvent.getPlayer())) {
            blockBreakEvent.setCancelled(true);
            return;
        }
        Block block = blockBreakEvent.getBlock();
        if (block == null || !XMat.isBed(block.getType())) {
            return;
        }
        Player player = blockBreakEvent.getPlayer();
        if (this.engine().blockBedBreak(player, this.plugin.hook() == null ? "" : this.plugin.hook().teamNameAt(player, block))) {
            blockBreakEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onDamageBy(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        MatchSession matchSession;
        if (!(entityDamageByEntityEvent.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player)entityDamageByEntityEvent.getEntity();
        Player player2 = this.damagerOf(entityDamageByEntityEvent.getDamager());
        if (player2 == null || player2.equals(player)) {
            return;
        }
        ItemStack itemStack = player2.getItemInHand();
        boolean bl = entityDamageByEntityEvent.getDamager() instanceof Projectile;
        if (!bl && this.engine().blockMelee(player2, player, itemStack)) {
            entityDamageByEntityEvent.setCancelled(true);
            return;
        }
        if (bl && (matchSession = this.engine().session(player2)) != null) {
            boolean bl2 = this.isArrowProjectile(entityDamageByEntityEvent.getDamager());
            if (matchSession.challenge == Challenge.PACIFIST && this.engine().blockBow(player2) && bl2) {
                entityDamageByEntityEvent.setCancelled(true);
                return;
            }
            if (matchSession.challenge == Challenge.SOCIAL_DISTANCING && !bl2) {
                entityDamageByEntityEvent.setCancelled(true);
                return;
            }
            if (matchSession.challenge == Challenge.MARKSMAN && !bl2) {
                entityDamageByEntityEvent.setCancelled(true);
                return;
            }
        }
        entityDamageByEntityEvent.setDamage(this.engine().modifyDamage(player2, entityDamageByEntityEvent.getDamage(), !bl));
        entityDamageByEntityEvent.setDamage(this.engine().modifyIncoming(player, entityDamageByEntityEvent.getDamage()));
        this.engine().applyKnockback(player, player2);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onDamaged(EntityDamageEvent entityDamageEvent) {
        if (!(entityDamageEvent.getEntity() instanceof Player)) {
            return;
        }
        this.engine().onDamaged((Player)entityDamageEvent.getEntity(), entityDamageEvent.getCause() == null ? "" : entityDamageEvent.getCause().name(), entityDamageEvent.getFinalDamage());
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBow(EntityShootBowEvent entityShootBowEvent) {
        if (entityShootBowEvent.getEntity() instanceof Player && this.engine().blockBow((Player)entityShootBowEvent.getEntity())) {
            entityShootBowEvent.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onMove(PlayerMoveEvent playerMoveEvent) {
        if (playerMoveEvent.getFrom() == null || playerMoveEvent.getTo() == null) {
            return;
        }
        this.engine().onMove(playerMoveEvent.getPlayer(), playerMoveEvent.getFrom(), playerMoveEvent.getTo());
    }

    @EventHandler
    public void onSprint(PlayerToggleSprintEvent playerToggleSprintEvent) {
        if (playerToggleSprintEvent.isSprinting()) {
            this.engine().onSprint(playerToggleSprintEvent.getPlayer());
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent playerToggleSneakEvent) {
        if (playerToggleSneakEvent.isSneaking()) {
            this.engine().onSneak(playerToggleSneakEvent.getPlayer());
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onRegen(EntityRegainHealthEvent entityRegainHealthEvent) {
        if (!(entityRegainHealthEvent.getEntity() instanceof Player)) {
            return;
        }
        if (this.engine().blockRegen((Player)entityRegainHealthEvent.getEntity(), entityRegainHealthEvent.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED || entityRegainHealthEvent.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN)) {
            entityRegainHealthEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEat(PlayerItemConsumeEvent playerItemConsumeEvent) {
        ItemStack itemStack = playerItemConsumeEvent.getItem();
        if (itemStack == null || !itemStack.getType().name().contains("GOLDEN_APPLE")) {
            return;
        }
        if (this.engine().blockConsumeGap(playerItemConsumeEvent.getPlayer())) {
            playerItemConsumeEvent.setCancelled(true);
        } else if (this.engine().stripGappleEffects(playerItemConsumeEvent.getPlayer())) {
            final Player player = playerItemConsumeEvent.getPlayer();
            this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, new Runnable(){

                @Override
                public void run() {
                    try {
                        player.removePotionEffect(PotionEffectType.REGENERATION);
                        player.removePotionEffect(PotionEffectType.ABSORPTION);
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                }
            }, 1L);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent playerDeathEvent) {
        this.engine().onPresidentDeath(playerDeathEvent.getEntity());
    }

    @EventHandler(ignoreCancelled=true)
    public void onFood(FoodLevelChangeEvent foodLevelChangeEvent) {
    }

    @EventHandler(ignoreCancelled=true)
    public void onChat(AsyncPlayerChatEvent asyncPlayerChatEvent) {
        final Player player = asyncPlayerChatEvent.getPlayer();
        MatchSession matchSession = this.engine().session(player);
        if (matchSession == null || matchSession.challenge != Challenge.QUICK_MATHS || matchSession.mathAnswer == Integer.MIN_VALUE) {
            return;
        }
        final String string = asyncPlayerChatEvent.getMessage();
        asyncPlayerChatEvent.setCancelled(true);
        this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, new Runnable(){

            @Override
            public void run() {
                PlayListener.access$000((PlayListener)PlayListener.this).onChat(player, string);
            }
        });
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onInteract(PlayerInteractEvent playerInteractEvent) {
        if (playerInteractEvent.getItem() != null && this.engine().blockUtilityUse(playerInteractEvent.getPlayer(), playerInteractEvent.getItem().getType())) {
            playerInteractEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onHeld(PlayerItemHeldEvent playerItemHeldEvent) {
        if (this.engine().blockHotbarChange(playerItemHeldEvent.getPlayer(), playerItemHeldEvent.getNewSlot())) {
            playerItemHeldEvent.setCancelled(true);
        }
    }

    private boolean isArrowProjectile(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (entity instanceof Arrow) {
            return true;
        }
        String string = entity.getClass().getSimpleName();
        return string.contains("Arrow") || string.contains("arrow");
    }

    private Player damagerOf(Entity entity) {
        if (entity instanceof Player) {
            return (Player)entity;
        }
        if (entity instanceof Projectile && ((Projectile)entity).getShooter() instanceof Player) {
            return (Player)((Projectile)entity).getShooter();
        }
        return null;
    }
}

