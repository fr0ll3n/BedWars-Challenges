/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.WeatherType
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.util.Vector
 */
package dev.bwchallenges.engine;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.ChallengesPlugin;
import dev.bwchallenges.api.event.ChallengeCompleteEvent;
import dev.bwchallenges.engine.MatchSession;
import dev.bwchallenges.gui.DefuserMenu;
import dev.bwchallenges.gui.MenuHolder;
import dev.bwchallenges.hook.BedWarsHook;
import dev.bwchallenges.mechanic.HandlerRegistry;
import dev.bwchallenges.mechanic.WoolColors;
import dev.bwchallenges.util.Sounds;
import dev.bwchallenges.util.Text;
import dev.bwchallenges.util.XMat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public final class ChallengeEngine {
    private final ChallengesPlugin plugin;
    private final Map<UUID, MatchSession> sessions = new ConcurrentHashMap<UUID, MatchSession>();
    private final Set<UUID> announcedPlayers = ConcurrentHashMap.newKeySet();
    private final DefuserMenu defuserMenu;
    private final HandlerRegistry handlers;

    public ChallengeEngine(ChallengesPlugin challengesPlugin) {
        this.plugin = challengesPlugin;
        this.defuserMenu = new DefuserMenu(challengesPlugin);
        this.handlers = new HandlerRegistry();
    }

    public HandlerRegistry handlers() {
        return this.handlers;
    }

    public BedWarsHook pluginHook() {
        return this.plugin.hook();
    }

    public DefuserMenu defuser() {
        return this.defuserMenu;
    }

    public MatchSession session(Player player) {
        return this.sessions.get(player.getUniqueId());
    }

    public MatchSession session(UUID uUID) {
        return this.sessions.get(uUID);
    }

    public void start(Player player, Challenge challenge, Location location, int n, String string, String string2, int n2) {
        if (challenge == null || !challenge.selectable()) {
            this.sessions.remove(player.getUniqueId());
            return;
        }
        MatchSession matchSession = new MatchSession(player.getUniqueId(), challenge);
        matchSession.islandCenter = location;
        matchSession.islandRadius = Math.max(8, n);
        matchSession.teamName = string == null ? "" : string;
        matchSession.teamColor = string2 == null ? "" : string2;
        matchSession.enemyTeams = Math.max(1, n2);
        try {
            matchSession.lastX = player.getLocation().getX();
            matchSession.lastY = player.getLocation().getY();
            matchSession.lastZ = player.getLocation().getZ();
            matchSession.hasLastPos = true;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this.sessions.put(player.getUniqueId(), matchSession);
        this.applyStartEffects(player, matchSession);
        this.handlers.of(challenge).onStart(this, player, matchSession);
    }

    public void clear(UUID uUID) {
        this.sessions.remove(uUID);
        if (uUID != null) {
            this.announcedPlayers.remove(uUID);
        }
    }

    public void fail(Player player, String string) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed || matchSession.completed) {
            return;
        }
        matchSession.failed = true;
        this.plugin.manager().markFailed(player, matchSession.challenge);
        Text.send((CommandSender)player, "&c&lCHALLENGE FAILED! &7" + matchSession.challenge.displayName());
        if (string != null && !string.isEmpty()) {
            Text.send((CommandSender)player, "&7" + string);
        }
        player.sendMessage(Text.color("&cYou will not receive completion credit."));
    }

    public void succeed(Player player) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed || matchSession.completed) {
            return;
        }
        if (!this.passesWinConditions(matchSession)) {
            this.fail(player, "Win conditions for this challenge were not met.");
            return;
        }
        matchSession.completed = true;
        this.plugin.manager().markCompleted(player, matchSession.challenge);
        Bukkit.getPluginManager().callEvent((Event)new ChallengeCompleteEvent(player, matchSession.challenge));
        Text.send((CommandSender)player, "&a&lCHALLENGE COMPLETE! &6" + matchSession.challenge.displayName());
        Text.send((CommandSender)player, "&7A new challenge has been unlocked.");
    }

    private boolean passesWinConditions(MatchSession matchSession) {
        switch (matchSession.challenge) {
            case CANT_TOUCH_THIS: 
            case BEDS_AND_BLOODLUST: {
                return matchSession.bedBrokenByTeam;
            }
            case COLLECTOR: {
                return matchSession.woolSubmitted && matchSession.collectedWool.size() >= this.collectorNeeded(matchSession);
            }
        }
        return true;
    }

    public boolean isRestricting(Player player) {
        MatchSession matchSession = this.session(player);
        return matchSession != null && !matchSession.failed && player.getGameMode() != GameMode.SPECTATOR;
    }

    public Challenge active(Player player) {
        MatchSession matchSession = this.session(player);
        return matchSession == null ? null : matchSession.challenge;
    }

    private void applyStartEffects(Player player, MatchSession matchSession) {
        switch (matchSession.challenge) {
            case LAZY_MINER: {
                this.safePotion(player, "SLOW_DIGGING", 72000, 0);
                break;
            }
            case SLEIGHT_OF_HAND: {
                this.applySleightPanes(player);
                break;
            }
            case TOXIC_RAIN: {
                try {
                    player.setPlayerWeather(WeatherType.DOWNFALL);
                }
                catch (Throwable throwable) {}
                break;
            }
            case HALVED_AND_DOUBLED: {
                this.setMaxHealth(player, 10.0);
            }
        }
    }

    public void tick() {
        long l = System.currentTimeMillis();
        for (MatchSession matchSession : this.sessions.values()) {
            Player player;
            if (matchSession.failed || matchSession.completed || !matchSession.challenge.ticking() || (player = Bukkit.getPlayer((UUID)matchSession.playerId)) == null || !player.isOnline() || player.getGameMode() == GameMode.SPECTATOR) continue;
            this.tickPlayer(player, matchSession, l);
        }
    }

    private void tickPlayer(Player player, MatchSession matchSession, long l) {
        this.handlers.of(matchSession.challenge).onTick(this, player, matchSession, l);
        switch (matchSession.challenge) {
            case TOXIC_RAIN: {
                this.tickToxicRain(player, matchSession);
                break;
            }
            case SLEIGHT_OF_HAND: {
                this.applySleightPanes(player);
                if (player.getInventory().getHeldItemSlot() == 0) break;
                player.getInventory().setHeldItemSlot(0);
                break;
            }
            case WEIGHTED_ITEMS: {
                int n = this.inventoryWeight(player);
                int n2 = Math.max(0, n / 12);
                if (n2 <= 0) {
                    player.removePotionEffect(PotionEffectType.SLOW);
                    break;
                }
                this.safePotion(player, "SLOW", 80, Math.min(9, n2 - 1));
                break;
            }
            case RED_LIGHT_GREEN_LIGHT: {
                this.tickRedLight(player, matchSession, l);
                break;
            }
            case STAMINA: {
                int n = player.getFoodLevel();
                if (l < matchSession.staminaBoostUntil) {
                    if (n >= 20 || !(Math.random() < 0.5)) break;
                    player.setFoodLevel(n + 1);
                    break;
                }
                if (player.isSprinting()) {
                    player.setFoodLevel(Math.max(0, n - 1));
                    break;
                }
                if (n >= 20 || !(Math.random() < 0.25)) break;
                player.setFoodLevel(n + 1);
                break;
            }
            case LAZY_MINER: {
                int n = 0;
                try {
                    PotionEffectType potionEffectType = PotionEffectType.getByName((String)"SLOW_DIGGING");
                    if (potionEffectType != null && player.hasPotionEffect(potionEffectType)) {
                        n = player.getPotionEffect(potionEffectType) != null ? player.getPotionEffect(potionEffectType).getAmplifier() : 0;
                    }
                }
                catch (Throwable throwable) {
                    n = 0;
                }
                if (n >= 1) {
                    this.safePotion(player, "SLOW_DIGGING", 80, 4);
                    break;
                }
                this.safePotion(player, "SLOW_DIGGING", 80, 0);
                break;
            }
            case QUICK_MATHS: {
                if (matchSession.mathAnswer != Integer.MIN_VALUE && l > matchSession.mathDeadline) {
                    this.fail(player, "You did not answer the math question in time.");
                    break;
                }
                if (matchSession.mathAnswer != Integer.MIN_VALUE || l - matchSession.startedAt <= 20000L || !(Math.random() < 0.04)) break;
                this.askMath(player, matchSession, l);
                break;
            }
            case MIDNIGHT: {
                if (this.onOwnIsland(player, matchSession)) {
                    player.removePotionEffect(PotionEffectType.BLINDNESS);
                    this.safePotion(player, "NIGHT_VISION", 80, 0);
                    break;
                }
                this.safePotion(player, "BLINDNESS", 80, 0);
                break;
            }
            case BEDS_AND_BLOODLUST: {
                if (matchSession.bloodlustUntil <= 0L || l <= matchSession.bloodlustUntil) break;
                this.fail(player, "You did not get a kill within 90 seconds of breaking a bed.");
            }
        }
    }

    public void tickToxicRain(Player player, MatchSession matchSession) {
        try {
            player.setPlayerWeather(WeatherType.DOWNFALL);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        boolean bl = player.getLocation().getBlock().getLightFromSky() > 10 && player.getWorld().getHighestBlockYAt(player.getLocation()) <= player.getLocation().getBlockY() + 1;
        boolean bl2 = bl;
        if (!bl) {
            matchSession.rainTicks = Math.max(0, matchSession.rainTicks - 3);
            return;
        }
        ++matchSession.rainTicks;
        int n = Math.max(8, 50 - Math.min(42, matchSession.rainTicks / 8));
        if (matchSession.rainTicks % n == 0) {
            this.drainOneItem(player);
        }
    }

    public void tickRedLight(Player player, MatchSession matchSession, long l) {
        if (matchSession.lightNextAt <= 0L) {
            matchSession.redLight = false;
            matchSession.lightCountdown = 0;
            matchSession.lightNextAt = l + 8000L + (long)ThreadLocalRandom.current().nextInt(7000);
            this.sendTitle(player, "&a&lGREEN LIGHT!", "");
            return;
        }
        if (l < matchSession.lightNextAt) {
            return;
        }
        if (!matchSession.redLight && matchSession.lightCountdown == 0) {
            matchSession.lightCountdown = 3;
            matchSession.lightNextAt = l + 1000L;
            this.sendTitle(player, "&e&l3", "&7Red light incoming");
            return;
        }
        if (!matchSession.redLight && matchSession.lightCountdown > 1) {
            --matchSession.lightCountdown;
            matchSession.lightNextAt = l + 1000L;
            this.sendTitle(player, "&e&l" + matchSession.lightCountdown, "&7Red light incoming");
            return;
        }
        if (!matchSession.redLight) {
            matchSession.redLight = true;
            matchSession.lightCountdown = 0;
            matchSession.lightNextAt = l + 3000L + (long)ThreadLocalRandom.current().nextInt(2000);
            this.sendTitle(player, "&c&lRED LIGHT!", "&7Don't move!");
            Text.send((CommandSender)player, "&c&lRED LIGHT! &7Don't move.");
            return;
        }
        matchSession.redLight = false;
        matchSession.lightNextAt = l + 8000L + (long)ThreadLocalRandom.current().nextInt(7000);
        this.sendTitle(player, "&a&lGREEN LIGHT!", "");
        Text.send((CommandSender)player, "&a&lGREEN LIGHT!");
    }

    private void askMath(Player player, MatchSession matchSession, long l) {
        matchSession.mathA = ThreadLocalRandom.current().nextInt(2, 13);
        matchSession.mathB = ThreadLocalRandom.current().nextInt(2, 13);
        boolean bl = ThreadLocalRandom.current().nextBoolean();
        if (bl) {
            matchSession.mathAnswer = matchSession.mathA + matchSession.mathB;
            Text.send((CommandSender)player, "&e&lQUICK MATHS! &7What is &f" + matchSession.mathA + " + " + matchSession.mathB + "&7?");
        } else {
            matchSession.mathAnswer = matchSession.mathA * matchSession.mathB;
            Text.send((CommandSender)player, "&e&lQUICK MATHS! &7What is &f" + matchSession.mathA + " x " + matchSession.mathB + "&7?");
        }
        matchSession.mathDeadline = l + 10000L;
        Text.send((CommandSender)player, "&7Type the answer in chat. You have 10 seconds.");
    }

    public void onChat(Player player, String string) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed || matchSession.challenge != Challenge.QUICK_MATHS) {
            return;
        }
        if (matchSession.mathAnswer == Integer.MIN_VALUE) {
            return;
        }
        String string2 = string == null ? "" : string.trim();
        try {
            int n = Integer.parseInt(string2.replace(",", ""));
            if (n == matchSession.mathAnswer) {
                Text.send((CommandSender)player, "&aCorrect!");
                Sounds.activate(player);
                matchSession.mathAnswer = Integer.MIN_VALUE;
            } else {
                this.fail(player, "Wrong answer.");
            }
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
    }

    public boolean onOwnIsland(Player player, MatchSession matchSession) {
        if (matchSession.islandCenter == null) {
            return true;
        }
        Location location = player.getLocation();
        if (location.getWorld() != matchSession.islandCenter.getWorld()) {
            return true;
        }
        double d = matchSession.islandRadius;
        return location.distanceSquared(matchSession.islandCenter) <= d * d;
    }

    public boolean blockShopBuy(Player player, String string, Material material) {
        return this.blockShopBuy(player, string, "", material, "");
    }

    public boolean blockShopBuy(Player player, String string, String string2, Material material, String string3) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed) {
            return false;
        }
        if (this.handlers.of(matchSession.challenge).blockShopBuy(this, player, matchSession, string, string2, material, string3)) {
            return true;
        }
        if (matchSession == null || matchSession.failed) {
            return false;
        }
        String string4 = ((string == null ? "" : string) + " " + (string2 == null ? "" : string2) + " " + (material == null ? "" : material.name()) + " " + (string3 == null ? "" : string3)).toLowerCase();
        switch (matchSession.challenge) {
            case LAZY_MINER: {
                if (!this.looksLikeTntOrFireball(string4, material)) break;
                this.deny(player, matchSession, "&cYou cannot buy TNT or fireballs during this challenge.");
                return true;
            }
            case WARMONGER: {
                if (!this.looksLikeUtility(string4, material)) break;
                this.deny(player, matchSession, "&cYou cannot purchase utilities during this challenge.");
                return true;
            }
            case WOODWORKER: {
                if (this.looksLikeWoodItem(string4, material)) break;
                this.deny(player, matchSession, "&cYou can only buy items made of wood.");
                return true;
            }
            case BRIDGING_FOR_DUMMIES: {
                if (XMat.isSponge(material) || string4.contains("sponge")) break;
                this.deny(player, matchSession, "&cYou can only buy sponge block in this challenge");
                return true;
            }
            case SWORDLESS: {
                if (!this.looksLikeSword(string4, material)) break;
                this.deny(player, matchSession, "&cYou can't buy swords on this challenge");
                return true;
            }
            case SOCIAL_DISTANCING: {
                if (this.looksLikeSword(string4, material) || this.looksLikeMelee(string4, material) && !string4.contains("stick")) {
                    this.deny(player, matchSession, "&cYou can't buy melee weapons on this challenge");
                    return true;
                }
                if (string4.contains("bow") && string4.contains("punch") || string4.contains("knockback") && string4.contains("bow") || string4.contains("bow1") || string4.contains("power-1") || string4.contains("power_1") || !string4.contains("bow") || !string4.contains("power-2") && !string4.contains("power-3") && !string4.contains("power2") && !string4.contains("power3")) break;
                this.deny(player, matchSession, "&cYou can only buy a punch bow or Power I bow.");
                return true;
            }
            case MARKSMAN: {
                if (!this.looksLikeMelee(string4, material) && !this.looksLikeSword(string4, material) && !string4.contains("stick")) break;
                this.deny(player, matchSession, "&cYou can't buy any melee weapons in this challenge!");
                return true;
            }
            case PACIFIST: {
                if (!this.looksLikeMelee(string4, material) && !this.looksLikeSword(string4, material) && !string4.contains("bow") && !string4.contains("arrow") && !string4.contains("stick")) break;
                this.deny(player, matchSession, "&cYou can't buy bows or melee weapons in this challenge!");
                return true;
            }
            case WOOL_WARRIOR: {
                if (XMat.isWool(material) || string4.contains("wool")) break;
                this.deny(player, matchSession, "&cYou can only buy wool.");
                return true;
            }
            case CAPPED_RESOURCES: {
                String string5 = string == null || string.isEmpty() ? string4 : string.toLowerCase();
                int n = matchSession.purchaseCounts.containsKey(string5) ? matchSession.purchaseCounts.get(string5) : 0;
                int n2 = n;
                if (n >= 20) {
                    this.deny(player, matchSession, "&cYou hit the limit of purchases!");
                    return true;
                }
                matchSession.purchaseCounts.put(string5, n + 1);
                break;
            }
        }
        return false;
    }

    public boolean looksLikeUtility(String string, Material material) {
        if (XMat.isUtility(material) || XMat.isUtilityUse(material)) {
            return true;
        }
        return string.contains("utility") || string.contains("tnt") || string.contains("fireball") || string.contains("fire-charge") || string.contains("egg") || string.contains("bridge") || string.contains("pearl") || string.contains("water") || string.contains("milk") || string.contains("golden apple") || string.contains("golden-apple") || string.contains("gapple") || string.contains("tower") || string.contains("popup") || string.contains("pop-up") || string.contains("bedbug") || string.contains("bed-bug") || string.contains("golem") || string.contains("dream") || string.contains("sponge") || string.contains("silverfish") || string.contains("compact");
    }

    public boolean looksLikeTntOrFireball(String string, Material material) {
        String string2 = material == null ? "" : material.name();
        return string2.contains("TNT") || string2.contains("FIRE") || string.contains("tnt") || string.contains("fireball") || string.contains("fire-charge");
    }

    public boolean looksLikeWoodItem(String string, Material material) {
        if (XMat.isWood(material)) {
            return true;
        }
        if (string.contains("wool") || string.contains("diamond") || string.contains("iron") || string.contains("gold") || string.contains("chain") || string.contains("obsidian") || string.contains("end stone") || string.contains("endstone") || string.contains("sandstone") || string.contains("terracotta") || string.contains("glass") || string.contains("tnt") || string.contains("sword") && !string.contains("wood")) {
            return false;
        }
        return string.contains("wood") || string.contains("oak") || string.contains("spruce") || string.contains("birch") || string.contains("jungle") || string.contains("plank") || string.contains("log") || string.contains("stick") || string.contains("ladder") || string.contains("bow") && !string.contains("arrow");
    }

    public boolean looksLikeSword(String string, Material material) {
        return XMat.isSword(material) || string.contains("sword");
    }

    public boolean looksLikeMelee(String string, Material material) {
        if (this.looksLikeSword(string, material) || XMat.isStick(material)) {
            return true;
        }
        return string.contains("melee") || string.contains("stick") || string.contains("knockback") && !string.contains("bow");
    }

    public boolean blockUpgrade(Player player) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed) {
            return false;
        }
        if (this.handlers.of(matchSession.challenge).blockUpgrade(this, player, matchSession)) {
            return true;
        }
        if (matchSession.challenge == Challenge.RENEGADE) {
            this.deny(player, matchSession, this.plugin.msg("challenge.messages.challenges.renegade.open_upgrade", "&cYou cannot use upgrades and traps during the &6Renegade &cchallenge"));
            return true;
        }
        return false;
    }

    public boolean blockShopOpen(Player player) {
        return false;
    }

    public boolean blockGenerator(Player player, Material material, Item item) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed) {
            return false;
        }
        if (this.handlers.of(matchSession.challenge).blockGenerator(this, player, matchSession, material, item)) {
            return true;
        }
        if (matchSession.challenge == Challenge.RENEGADE && XMat.isDiamond(material)) {
            this.deny(player, matchSession, "&cYou cannot pick up diamonds during &6Renegade&c.");
            this.softBlockItem(item);
            return true;
        }
        if (matchSession.challenge == Challenge.BEG_AND_BARTER && this.onOwnIsland(player, matchSession) && XMat.isResource(material)) {
            this.deny(player, matchSession, "&cBeg & Barter: you cannot collect from your own generator.");
            this.softBlockItem(item);
            return true;
        }
        if ((matchSession.challenge == Challenge.PATRIOT || matchSession.challenge == Challenge.CAPPED_RESOURCES) && !this.onOwnIsland(player, matchSession) && XMat.isResource(material)) {
            this.deny(player, matchSession, "&cYou can only collect from your own generator.");
            this.softBlockItem(item);
            return true;
        }
        return false;
    }

    public void softBlockItem(Item item) {
        if (item == null || item.isDead()) {
            return;
        }
        try {
            if (item.getPickupDelay() < 25) {
                item.setPickupDelay(30);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public boolean blockDrop(Player player) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed) {
            return false;
        }
        if (this.handlers.of(matchSession.challenge).blockDrop(this, player, matchSession)) {
            return true;
        }
        if (matchSession.challenge == Challenge.SELFISH) {
            this.deny(player, matchSession, this.plugin.msg("challenge.messages.challenges.selfish.drop_item", "&cYou cannot drop anything during the &6Selfish &cchallenge"));
            return true;
        }
        return false;
    }

    public boolean blockRealChest(Player player) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed) {
            return false;
        }
        if (matchSession.challenge == Challenge.SELFISH) {
            this.deny(player, matchSession, this.plugin.msg("challenge.messages.challenges.selfish.open_chests", "&cThis chest is locked during the &6Selfish &cchallenge"));
            return true;
        }
        return false;
    }

    public boolean blockRealChest(Player player, boolean bl) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed) {
            return false;
        }
        if (this.handlers.of(matchSession.challenge).blockChest(this, player, matchSession, bl)) {
            return true;
        }
        if (matchSession.challenge == Challenge.SELFISH) {
            this.deny(player, matchSession, this.plugin.msg("challenge.messages.challenges.selfish.open_chests", "&cThis chest is locked during the &6Selfish &cchallenge"));
            return true;
        }
        if (matchSession.challenge == Challenge.WOODWORKER && bl) {
            this.deny(player, matchSession, "&cWoodworker: your Ender Chest isn't made of wood.");
            return true;
        }
        return false;
    }

    public boolean blockPickup(Player player, Material material, boolean bl) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed) {
            return false;
        }
        if (this.handlers.of(matchSession.challenge).blockPickup(this, player, matchSession, material, bl)) {
            return true;
        }
        switch (matchSession.challenge) {
            case WOODWORKER: {
                return material != null && !XMat.isWood(material);
            }
            case BRIDGING_FOR_DUMMIES: {
                return XMat.isBlockItem(material);
            }
            case SWORDLESS: {
                return XMat.isSword(material);
            }
            case CAPPED_RESOURCES: 
            case PATRIOT: {
                if (bl) {
                    this.deny(player, matchSession, "&cYou cannot pick up items from other players.");
                    return true;
                }
                if (!this.onOwnIsland(player, matchSession) && XMat.isResource(material)) {
                    this.deny(player, matchSession, "&cYou can only collect from your own generator.");
                    return true;
                }
                return false;
            }
            case BEG_AND_BARTER: {
                if (this.onOwnIsland(player, matchSession) && XMat.isResource(material)) {
                    this.deny(player, matchSession, "&cBeg & Barter: you cannot collect from your own generator.");
                    return true;
                }
                return false;
            }
            case WOOL_WARRIOR: {
                return bl;
            }
            case COLLECTOR: {
                return false;
            }
            case RENEGADE: {
                if (XMat.isDiamond(material)) {
                    this.deny(player, matchSession, "&cYou cannot pick up diamonds during &6Renegade&c.");
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public boolean blockPlace(Player player, Block block) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed || block == null) {
            return false;
        }
        if (this.handlers.of(matchSession.challenge).blockPlace(this, player, matchSession, block)) {
            return true;
        }
        Material material = block.getType();
        if ((matchSession.challenge == Challenge.WARMONGER || matchSession.challenge == Challenge.LAZY_MINER) && (material.name().contains("TNT") || XMat.isSponge(material))) {
            this.deny(player, matchSession, "&cYou cannot use utilities during this challenge.");
            return true;
        }
        if (matchSession.challenge == Challenge.STAMINA && player.getFoodLevel() <= 0) {
            this.deny(player, matchSession, "&cNot enough stamina to place blocks.");
            return true;
        }
        if (matchSession.challenge == Challenge.BRIDGING_FOR_DUMMIES && !XMat.isSponge(material) && !material.name().contains("LADDER")) {
            this.deny(player, matchSession, "&cYou can only place sponge.");
            return true;
        }
        if (matchSession.challenge == Challenge.BLOCKREPELLENT_BEDS && this.nearBed(block)) {
            this.deny(player, matchSession, "&cBlockrepellent Beds: you cannot place blocks next to a bed.");
            return true;
        }
        return false;
    }

    public boolean nearBed(Block block) {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -2; k <= 2; ++k) {
                    if (!XMat.isBed(block.getRelative(i, j, k).getType())) continue;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean blockMelee(Player player, Player player2, ItemStack itemStack) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed) {
            return false;
        }
        if (this.handlers.of(matchSession.challenge).blockMelee(this, player, matchSession, player2, itemStack)) {
            return true;
        }
        Material material = itemStack == null ? Material.AIR : itemStack.getType();
        long l = System.currentTimeMillis();
        switch (matchSession.challenge) {
            case MARKSMAN: {
                this.deny(player, matchSession, "&cMarksman: bows only.");
                return true;
            }
            case PACIFIST: {
                this.deny(player, matchSession, "&cPacifist: utilities only.");
                return true;
            }
            case SOCIAL_DISTANCING: {
                if (XMat.isStick(material) || XMat.isBow(material)) break;
                this.deny(player, matchSession, "&cSocial Distancing: knockback sticks and punch bows only.");
                return true;
            }
            case SWORDLESS: {
                if (!XMat.isSword(material)) break;
                this.deny(player, matchSession, "&cSwordless: no swords.");
                return true;
            }
            case SLOW_REFLEXES: {
                if (l - matchSession.lastHitAt < 2000L) {
                    this.deny(player, matchSession, "&cSlow Reflexes: wait 2 seconds between hits.");
                    return true;
                }
                matchSession.lastHitAt = l;
                break;
            }
            case STAMINA: {
                if (player.getFoodLevel() <= 0) {
                    this.deny(player, matchSession, "&cNot enough stamina to hit.");
                    return true;
                }
                player.setFoodLevel(Math.max(0, player.getFoodLevel() - 1));
                break;
            }
            case MASTER_ASSASSIN: {
                String string;
                if (matchSession.targetTeam == null || matchSession.targetTeam.isEmpty() || this.plugin.hook() == null || (string = this.plugin.hook().teamName(player2)) == null || string.isEmpty() || string.equalsIgnoreCase(matchSession.targetTeam)) break;
                this.deny(player, matchSession, "&cMaster Assassin: you may only attack your target team.");
                return true;
            }
        }
        return false;
    }

    public boolean blockBow(Player player) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed) {
            return false;
        }
        if (this.handlers.of(matchSession.challenge).blockBow(this, player, matchSession)) {
            return true;
        }
        if (matchSession.challenge == Challenge.PACIFIST) {
            this.deny(player, matchSession, "&cPacifist: no bows.");
            return true;
        }
        return false;
    }

    public void onDamaged(Player player, String string) {
        this.onDamaged(player, string, 1.0);
    }

    public void onDamaged(Player player, String string, double d) {
        if (player == null) {
            return;
        }
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed || matchSession.completed || matchSession.challenge == null) {
            return;
        }
        try {
            this.handlers.of(matchSession.challenge).onDamaged(this, player, matchSession, string, d);
        }
        catch (Throwable throwable) {
            this.plugin.logSafe("Challenge onDamaged handler failed", throwable);
        }
        if (d <= 0.0) {
            return;
        }
        if (matchSession.challenge == Challenge.CANT_TOUCH_THIS) {
            if (string != null && string.toUpperCase().contains("VOID")) {
                this.fail(player, "You fell into the void.");
            } else {
                this.fail(player, "You took damage.");
            }
        }
    }

    public void onMove(Player player, Location location, Location location2) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed) {
            return;
        }
        this.handlers.of(matchSession.challenge).onMove(this, player, matchSession, location, location2);
    }

    private boolean hasJumpBoost(Player player) {
        try {
            PotionEffectType potionEffectType = PotionEffectType.getByName((String)"JUMP") != null ? PotionEffectType.getByName((String)"JUMP") : PotionEffectType.getByName((String)"JUMP_BOOST");
            return potionEffectType != null && player.hasPotionEffect(potionEffectType);
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    private boolean canClimb(Block block) {
        if (block == null) {
            return false;
        }
        String string = block.getType().name();
        return string.contains("LADDER") || string.contains("VINE") || string.contains("WATER") || string.contains("STAIR") || string.contains("SLAB") || string.contains("SCAFFOLD");
    }

    public void onSprint(Player player) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed) {
            return;
        }
        this.handlers.of(matchSession.challenge).onSprint(this, player, matchSession);
        if (matchSession.challenge == Challenge.OLD_MAN) {
            this.fail(player, "You sprinted.");
        }
    }

    public void onSneak(Player player) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed) {
            return;
        }
        this.handlers.of(matchSession.challenge).onSneak(this, player, matchSession);
        if (matchSession.challenge == Challenge.STANDING_TALL) {
            this.fail(player, "You crouched.");
        }
    }

    public void onKill(Player player) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed) {
            return;
        }
        this.handlers.of(matchSession.challenge).onKill(this, player, matchSession);
        matchSession.lastKillAt = System.currentTimeMillis();
        if (matchSession.challenge == Challenge.BEDS_AND_BLOODLUST) {
            matchSession.bloodlustUntil = 0L;
        }
    }

    public void onNonFinalKill(Player player) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed) {
            return;
        }
        this.handlers.of(matchSession.challenge).onNonFinalKill(this, player, matchSession);
        if (matchSession.challenge == Challenge.NO_DREAMING) {
            this.fail(player, "No Dreaming: only final kills are allowed.");
        }
    }

    public void onPresidentDeath(Player player) {
        if (player == null) {
            return;
        }
        MatchSession matchSession = this.session(player);
        if (matchSession != null && matchSession.challenge == Challenge.NO_DREAMING && !matchSession.failed) {
            this.fail(player, "You died.");
        }
        for (MatchSession matchSession2 : this.sessions.values()) {
            if (matchSession2.challenge != Challenge.PROTECT_THE_PRESIDENT || matchSession2.presidentId == null || !matchSession2.presidentId.equals(player.getUniqueId()) || matchSession2.failed) continue;
            Player player2 = Bukkit.getPlayer((UUID)matchSession2.playerId);
            if (player2 != null) {
                this.fail(player2, "The President died.");
                continue;
            }
            matchSession2.failed = true;
        }
    }

    public void onBedBrokenBy(Player player) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed) {
            return;
        }
        this.handlers.of(matchSession.challenge).onBedBrokenBy(this, player, matchSession);
        matchSession.bedBrokenByTeam = true;
        if (matchSession.challenge == Challenge.BEDS_AND_BLOODLUST) {
            matchSession.bloodlustUntil = System.currentTimeMillis() + 90000L;
            Text.send((CommandSender)player, "&eBeds & Bloodlust: get a kill within 90 seconds!");
        }
    }

    public void onOwnBedLost(Player player) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed) {
            return;
        }
        matchSession.ownBedLost = true;
    }

    public boolean blockBedBreak(Player player, String string) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed) {
            return false;
        }
        if (this.handlers.of(matchSession.challenge).blockBedBreak(this, player, matchSession, string)) {
            return true;
        }
        if (!(matchSession.challenge != Challenge.ASSASSIN && matchSession.challenge != Challenge.MASTER_ASSASSIN || matchSession.targetTeam == null || matchSession.targetTeam.isEmpty() || string == null || string.equalsIgnoreCase(matchSession.targetTeam))) {
            this.deny(player, matchSession, "&cAssassin: that is not your target team.");
            return true;
        }
        if (matchSession.challenge == Challenge.DEFUSER && string != null && !matchSession.defusedBeds.contains(string.toLowerCase())) {
            this.defuserMenu.open(player, string.toLowerCase());
            this.deny(player, matchSession, "&eDefuse the bed in the menu first!");
            return true;
        }
        if (matchSession.challenge == Challenge.PROTECT_THE_PRESIDENT && matchSession.presidentId != null && !matchSession.presidentId.equals(player.getUniqueId())) {
            this.deny(player, matchSession, "&cOnly the President can break beds.");
            return true;
        }
        return false;
    }

    public void applyKnockback(Player player, Player player2) {
        MatchSession matchSession = this.session(player2);
        if (matchSession == null) {
            return;
        }
        if (matchSession.challenge == Challenge.SOCIAL_DISTANCING) {
            Vector vector = player.getLocation().toVector().subtract(player2.getLocation().toVector());
            if (vector.lengthSquared() < 1.0E-4) {
                vector = player2.getLocation().getDirection();
            }
            vector = vector.normalize().multiply(1.6);
            vector.setY(0.45);
            player.setVelocity(vector);
        }
    }

    public double modifyDamage(Player player, double d) {
        return this.modifyDamage(player, d, true);
    }

    public double modifyDamage(Player player, double d, boolean bl) {
        ItemStack itemStack;
        MatchSession matchSession = this.session(player);
        if (matchSession == null) {
            return d;
        }
        if (bl && matchSession.challenge == Challenge.HALVED_AND_DOUBLED) {
            return d * 2.0;
        }
        if (matchSession.challenge == Challenge.SWORDLESS && (itemStack = XMat.handOf(player)) != null && XMat.isAxeOrPick(itemStack.getType())) {
            return 1.0;
        }
        return d;
    }

    public double modifyIncoming(Player player, double d) {
        return d;
    }

    public boolean blockRegen(Player player, boolean bl) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed) {
            return false;
        }
        return matchSession.challenge == Challenge.ULTIMATE_UHC && bl;
    }

    public boolean blockConsumeGap(Player player) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null) {
            return false;
        }
        if (matchSession.challenge == Challenge.STAMINA) {
            matchSession.staminaBoostUntil = System.currentTimeMillis() + 10000L;
            return false;
        }
        if (matchSession.challenge == Challenge.ANCHOR) {
            matchSession.jumpAllowedUntil = System.currentTimeMillis() + 20000L;
            return false;
        }
        return false;
    }

    public boolean stripGappleEffects(Player player) {
        MatchSession matchSession = this.session(player);
        return matchSession != null && matchSession.challenge == Challenge.ULTIMATE_UHC;
    }

    public void resetShopperGear(Player player) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.challenge != Challenge.REGULAR_SHOPPER) {
            return;
        }
        this.resetShopperShopTiers(player);
        ItemStack[] itemStackArray = player.getInventory().getArmorContents();
        if (itemStackArray != null) {
            for (int i = 0; i < itemStackArray.length; ++i) {
                if (itemStackArray[i] == null || !this.isPurchasedArmor(itemStackArray[i].getType())) continue;
                itemStackArray[i] = null;
            }
            player.getInventory().setArmorContents(itemStackArray);
        }
        ItemStack[] itemStackArray2 = player.getInventory().getContents();
        for (int i = 0; i < itemStackArray2.length; ++i) {
            Material material;
            ItemStack itemStack = itemStackArray2[i];
            if (itemStack == null || !XMat.isTool(material = itemStack.getType()) && !this.isPurchasedSword(material)) continue;
            itemStackArray2[i] = null;
        }
        player.getInventory().setContents(itemStackArray2);
        try {
            if (this.plugin.hook() != null) {
                this.plugin.hook().restoreStarterKit(player);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            player.updateInventory();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private boolean isPurchasedArmor(Material material) {
        if (material == null) {
            return false;
        }
        String string = material.name();
        if (!(string.contains("HELMET") || string.contains("CHESTPLATE") || string.contains("LEGGINGS") || string.contains("BOOTS"))) {
            return false;
        }
        return string.contains("IRON") || string.contains("DIAMOND") || string.contains("GOLD") || string.contains("CHAIN") || string.contains("NETHERITE");
    }

    private boolean isPurchasedSword(Material material) {
        if (!XMat.isSword(material)) {
            return false;
        }
        String string = material.name();
        return !string.contains("WOOD");
    }

    private boolean isShopperResetIdentifier(String string) {
        if (string == null || string.isEmpty()) {
            return false;
        }
        String string2 = string.toLowerCase();
        return string2.contains("pickaxe") || string2.contains("axe") || string2.contains("shears") || string2.contains("shovel") || string2.contains("spade") || string2.contains("sword") || string2.contains("armor") || string2.contains("helmet") || string2.contains("chest") || string2.contains("legging") || string2.contains("boot") || string2.contains("tool");
    }

    private void resetShopperShopTiers(Player player) {
        try {
            Class<?> clazz = Class.forName("com.tomkeuper.bedwars.shop.ShopCache");
            Object object = clazz.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            if (object == null) {
                return;
            }
            Object object2 = clazz.getMethod("getShopCache", UUID.class).invoke(object, player.getUniqueId());
            if (object2 == null) {
                return;
            }
            Object object3 = clazz.getMethod("getCachedItems", new Class[0]).invoke(object2, new Object[0]);
            if (!(object3 instanceof List)) {
                return;
            }
            List list = (List)object3;
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                Object e = iterator.next();
                if (e == null) continue;
                Object object4 = null;
                try {
                    object4 = e.getClass().getMethod("getCc", new Class[0]).invoke(e, new Object[0]);
                }
                catch (Throwable throwable) {
                    continue;
                }
                if (object4 == null) continue;
                String string = "";
                try {
                    string = String.valueOf(object4.getClass().getMethod("getIdentifier", new Class[0]).invoke(object4, new Object[0]));
                }
                catch (Throwable throwable) {
                    string = "";
                }
                if (!this.isShopperResetIdentifier(string)) continue;
                iterator.remove();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public void stripArmor(Player player) {
        player.getInventory().setArmorContents(new ItemStack[4]);
    }

    public void deny(Player player, MatchSession matchSession, String string) {
        long l = System.currentTimeMillis();
        if (l - matchSession.lastDenyAt < 1200L) {
            return;
        }
        matchSession.lastDenyAt = l;
        Text.send((CommandSender)player, string.replace("{challenge}", matchSession.challenge.displayName()));
        Sounds.deny(player);
    }

    private void drainOneItem(Player player) {
        ItemStack[] itemStackArray = player.getInventory().getContents();
        for (int i = itemStackArray.length - 1; i >= 0; --i) {
            ItemStack itemStack = itemStackArray[i];
            if (itemStack == null || itemStack.getType() == Material.AIR || this.isRainProtected(itemStack.getType())) continue;
            int n = itemStack.getAmount();
            if (n <= 1) {
                player.getInventory().setItem(i, null);
            } else {
                itemStack.setAmount(n - 1);
            }
            return;
        }
    }

    private boolean isRainProtected(Material material) {
        if (material == null) {
            return true;
        }
        String string = material.name();
        return XMat.isSword(material) || XMat.isArmor(material) || XMat.isTool(material) || string.contains("SHEARS") || string.equals("COMPASS") || string.contains("RED_STAINED") || string.contains("STAINED_GLASS_PANE");
    }

    private int inventoryWeight(Player player) {
        int n = 0;
        ItemStack[] itemStackArray = player.getInventory().getContents();
        ItemStack[] itemStackArray2 = player.getInventory().getArmorContents();
        ArrayList<Object> arrayList = new ArrayList<Object>();
        if (itemStackArray != null) {
            for (ItemStack n2 : itemStackArray) {
                arrayList.add(n2);
            }
        }
        if (itemStackArray2 != null) {
            for (ItemStack itemStack : itemStackArray2) {
                arrayList.add(itemStack);
            }
        }
        for (ItemStack itemStack : arrayList) {
            if (itemStack == null) continue;
            String string = itemStack.getType().name();
            int n2 = 1;
            if (string.contains("OBSIDIAN") || string.contains("ANVIL") || string.contains("DIAMOND_BLOCK")) {
                n2 = 8;
            } else if (string.contains("TNT") || string.contains("ENDER") || string.contains("DIAMOND")) {
                n2 = 4;
            } else if (string.contains("IRON") || string.contains("GOLD") || string.contains("SWORD") || string.contains("CHESTPLATE")) {
                n2 = 2;
            }
            n += n2 * itemStack.getAmount();
        }
        return n;
    }

    private boolean isUtilityId(String string) {
        return string.contains("utility") || string.contains("tnt") || string.contains("fireball") || string.contains("egg") || string.contains("bridge") || string.contains("pearl") || string.contains("water") || string.contains("milk") || string.contains("apple") || string.contains("tower") || string.contains("popup") || string.contains("bedbug") || string.contains("golem") || string.contains("dream") || string.contains("sponge") || string.contains("chest") || string.contains("silverfish");
    }

    public void safePotion(Player player, String string, int n, int n2) {
        try {
            PotionEffectType potionEffectType = PotionEffectType.getByName((String)string);
            if (potionEffectType == null) {
                return;
            }
            player.addPotionEffect(new PotionEffect(potionEffectType, n, n2, true, false), true);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public void setMaxHealth(Player player, double d) {
        try {
            player.setMaxHealth(d);
            player.setHealth(Math.min(d, player.getHealth()));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public boolean blockUtilityUse(Player player, Material material) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed || material == null) {
            return false;
        }
        if (this.handlers.of(matchSession.challenge).blockUtilityUse(this, player, matchSession, material)) {
            return true;
        }
        if ((matchSession.challenge == Challenge.WARMONGER || matchSession.challenge == Challenge.LAZY_MINER) && XMat.isUtilityUse(material)) {
            if (matchSession.challenge == Challenge.LAZY_MINER && !material.name().contains("TNT") && !material.name().contains("FIRE")) {
                return false;
            }
            this.deny(player, matchSession, "&cYou cannot use utilities during this challenge.");
            return true;
        }
        return false;
    }

    public boolean blockBreakAction(Player player) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed) {
            return false;
        }
        if (this.handlers.of(matchSession.challenge).blockBreakAction(this, player, matchSession)) {
            return true;
        }
        if (matchSession.challenge == Challenge.STAMINA) {
            if (player.getFoodLevel() <= 0) {
                this.deny(player, matchSession, "&cNot enough stamina to break blocks.");
                return true;
            }
            player.setFoodLevel(Math.max(0, player.getFoodLevel() - 1));
        }
        return false;
    }

    public void noteCollectorWool(Player player, ItemStack itemStack) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed || matchSession.challenge != Challenge.COLLECTOR) {
            return;
        }
        if (!WoolColors.isWool(itemStack)) {
            return;
        }
        String string = WoolColors.key(itemStack);
        if (string.isEmpty() || !matchSession.collectedWool.add(string)) {
            return;
        }
        int n = this.collectorNeeded(matchSession);
        Text.send((CommandSender)player, "&aYou collected " + string + " wool &f(" + matchSession.collectedWool.size() + "/" + n + ")");
        if (matchSession.collectedWool.size() >= n) {
            matchSession.woolSubmitted = true;
        }
    }

    public void submitCollectorWool(Player player) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed || matchSession.challenge != Challenge.COLLECTOR) {
            return;
        }
        boolean bl = false;
        ItemStack[] itemStackArray = player.getInventory().getContents();
        if (itemStackArray != null) {
            for (ItemStack itemStack : itemStackArray) {
                String string;
                if (!WoolColors.isWool(itemStack) || (string = WoolColors.key(itemStack)).isEmpty() || !matchSession.collectedWool.add(string)) continue;
                bl = true;
                Text.send((CommandSender)player, "&aYou collected " + string + " wool &f(" + matchSession.collectedWool.size() + "/" + this.collectorNeeded(matchSession) + ")");
            }
        }
        int n = this.collectorNeeded(matchSession);
        if (matchSession.collectedWool.size() >= n) {
            if (!matchSession.woolSubmitted) {
                Text.send((CommandSender)player, "&aAll wool colors turned in at the shop! &7(" + matchSession.collectedWool.size() + "/" + n + ")");
            }
            matchSession.woolSubmitted = true;
        } else if (bl) {
            Text.send((CommandSender)player, "&eShopkeeper received wool &f" + matchSession.collectedWool.size() + "&7/" + n);
        }
    }

    private int collectorNeeded(MatchSession matchSession) {
        return Math.max(2, matchSession.enemyTeams + 1);
    }

    public void applySleightPanes(Player player) {
        ItemStack itemStack = XMat.stack("RED_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", "THIN_GLASS");
        try {
            itemStack.setDurability((short)14);
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setDisplayName(Text.color("&cLocked"));
                itemStack.setItemMeta(itemMeta);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        for (int i = 1; i <= 8; ++i) {
            ItemStack itemStack2 = player.getInventory().getItem(i);
            if (itemStack2 != null && itemStack2.getType() != Material.AIR && itemStack2.hasItemMeta() && itemStack2.getItemMeta().hasDisplayName() && Text.strip(itemStack2.getItemMeta().getDisplayName()).equalsIgnoreCase("Locked")) continue;
            if (itemStack2 != null && itemStack2.getType() != Material.AIR) {
                player.getInventory().addItem(new ItemStack[]{itemStack2});
            }
            player.getInventory().setItem(i, itemStack.clone());
        }
        player.getInventory().setHeldItemSlot(0);
    }

    public boolean blockHotbarChange(Player player, int n) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed) {
            return false;
        }
        if (this.handlers.of(matchSession.challenge).blockHotbarChange(this, player, matchSession, n)) {
            return true;
        }
        if (matchSession.challenge == Challenge.SLEIGHT_OF_HAND && n != 0) {
            player.getInventory().setHeldItemSlot(0);
            this.deny(player, matchSession, "&cSleight of Hand: only slot 1 can be used.");
            return true;
        }
        return false;
    }

    private int[] createShopLayout(ItemStack[] itemStackArray) {
        int[] nArray = new int[itemStackArray.length];
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for (int i = 0; i < itemStackArray.length; ++i) {
            String string;
            nArray[i] = i;
            ItemStack itemStack = itemStackArray[i];
            if (i < 9 || itemStack == null || itemStack.getType() == Material.AIR || this.isShopControlItem(itemStack) || this.isHiddenShopItem(itemStack) || (string = itemStack.getType().name()).contains("ARROW") || string.contains("BARRIER")) continue;
            arrayList.add(i);
        }
        ArrayList arrayList2 = new ArrayList(arrayList);
        Collections.shuffle(arrayList2);
        for (int i = 0; i < arrayList.size(); ++i) {
            nArray[((Integer)arrayList.get((int)i)).intValue()] = (Integer)arrayList2.get(i);
        }
        return nArray;
    }

    private void sendTitle(Player player, String string, String string2) {
        try {
            player.sendTitle(Text.color(string), Text.color(string2), 5, 40, 10);
        }
        catch (Throwable throwable) {
            try {
                player.sendTitle(Text.color(string), Text.color(string2));
            }
            catch (Throwable throwable2) {
                // empty catch block
            }
        }
    }

    public void scheduleHideShop(final Player player) {
        if (player == null) {
            return;
        }
        this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, new Runnable(){

            @Override
            public void run() {
                ChallengeEngine.this.hideShop(player);
            }
        }, 1L);
        this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, new Runnable(){

            @Override
            public void run() {
                ChallengeEngine.this.hideShop(player);
            }
        }, 3L);
        this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, new Runnable(){

            @Override
            public void run() {
                ChallengeEngine.this.hideShop(player);
            }
        }, 5L);
        this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, new Runnable(){

            @Override
            public void run() {
                ChallengeEngine.this.hideShop(player);
            }
        }, 8L);
    }

    public void hideShop(Player player) {
        MatchSession matchSession = this.session(player);
        if (matchSession == null || matchSession.failed || matchSession.challenge != Challenge.INVISIBLE_SHOP) {
            return;
        }
        try {
            ItemStack itemStack;
            int n;
            int n2;
            if (player.getOpenInventory() == null) {
                return;
            }
            Inventory inventory = player.getOpenInventory().getTopInventory();
            if (inventory == null || !this.isItemShopInventory(player, inventory)) {
                return;
            }
            ItemStack[] itemStackArray = inventory.getContents();
            if (itemStackArray == null) {
                return;
            }
            String string = "";
            try {
                string = player.getOpenInventory().getTitle();
            }
            catch (Throwable throwable) {
                string = "";
            }
            String string2 = Text.strip(string == null ? "" : string).toLowerCase() + ":" + itemStackArray.length;
            int[] nArray = matchSession.shopLayouts.get(string2);
            if (nArray == null || nArray.length != itemStackArray.length) {
                nArray = this.createShopLayout(itemStackArray);
                matchSession.shopLayouts.put(string2, nArray);
            }
            ItemStack[] itemStackArray2 = new ItemStack[itemStackArray.length];
            for (n2 = 0; n2 < itemStackArray.length; ++n2) {
                n = nArray[n2];
                if (n < 0 || n >= itemStackArray.length) {
                    n = n2;
                }
                itemStackArray2[n2] = itemStackArray[n];
            }
            n2 = 0;
            for (n = 0; n < itemStackArray2.length; ++n) {
                itemStack = itemStackArray2[n];
                if (itemStack == null || itemStack.getType() == Material.AIR || this.isHiddenShopItem(itemStack) || this.isShopControlItem(itemStack)) continue;
                itemStackArray2[n] = this.hiddenShopItem();
                n2 = 1;
            }
            for (n = 0; n < Math.min(9, itemStackArray2.length); ++n) {
                itemStack = itemStackArray2[n];
                if (itemStack == null || itemStack.getType() == Material.AIR || itemStack.getType().name().contains("ARROW")) continue;
                itemStackArray2[n] = this.categoryBarrier();
                n2 = 1;
            }
            if (n2 != 0) {
                inventory.setContents(itemStackArray2);
                matchSession.shopHidden = true;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private boolean isItemShopInventory(Player player, Inventory inventory) {
        if (inventory.getHolder() instanceof MenuHolder) {
            return false;
        }
        if (inventory.getHolder() instanceof Player) {
            return false;
        }
        String string = inventory.getHolder() == null ? "" : inventory.getHolder().getClass().getName().toLowerCase();
        String string2 = string;
        if (string.contains("chest") || string.contains("barrel") || string.contains("ender")) {
            return false;
        }
        int n = inventory.getSize();
        if (n < 27) {
            return false;
        }
        String string3 = "";
        try {
            string3 = player.getOpenInventory().getTitle();
        }
        catch (Throwable throwable) {
            string3 = "";
        }
        String string4 = string3 == null ? "" : Text.strip(string3).toLowerCase();
        String string5 = string4;
        if (string4.contains("challenge")) {
            return false;
        }
        if (string4.contains("shop") || string4.contains("quick buy") || string4.contains("quickbuy") || string4.contains("item shop") || string4.contains("upgrade") || string4.contains("trap") || string4.contains("blocks") || string4.contains("melee") || string4.contains("armor") || string4.contains("tools") || string4.contains("ranged") || string4.contains("potion") || string4.contains("utilit") || string4.contains("buy")) {
            return true;
        }
        try {
            if (this.plugin.hook() != null && this.plugin.hook().isPlaying(player)) {
                return n == 45 || n == 54;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return false;
    }

    private boolean isShopControlItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return false;
        }
        String string = itemStack.getType().name();
        if (string.contains("COMPASS") || string.contains("COMPARATOR") || string.contains("REDSTONE_COMPARATOR") || string.contains("ARROW")) {
            return true;
        }
        try {
            if (itemStack.hasItemMeta()) {
                String string2;
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta != null && itemMeta.hasDisplayName() && ((string2 = Text.strip(itemMeta.getDisplayName()).toLowerCase()).contains("hotbar") || string2.contains("compass") || string2.contains("manager"))) {
                    return true;
                }
                if (itemMeta != null && itemMeta.hasLore()) {
                    for (String string3 : itemMeta.getLore()) {
                        String string4 = Text.strip(string3).toLowerCase();
                        if (!string4.contains("hotbar") && !string4.contains("manage your hotbar") && !string4.contains("edit hotbar")) continue;
                        return true;
                    }
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return false;
    }

    private boolean isHiddenShopItem(ItemStack itemStack) {
        try {
            if (!itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()) {
                return false;
            }
            String string = Text.strip(itemStack.getItemMeta().getDisplayName()).toLowerCase();
            return string.contains("???") || string.equals("unknown") || string.contains("hidden item");
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    private ItemStack categoryBarrier() {
        ItemStack itemStack = XMat.stack("BARRIER", "RED_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", "REDSTONE_BLOCK");
        try {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setDisplayName(Text.color("&c????"));
                itemStack.setItemMeta(itemMeta);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return itemStack;
    }

    private ItemStack hiddenShopItem() {
        ItemStack itemStack = XMat.stack("GRAY_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", "THIN_GLASS", "GLASS_PANE", "BARRIER");
        try {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setDisplayName(Text.color("&aUnknown"));
                ArrayList<String> arrayList = new ArrayList<String>();
                arrayList.add(Text.color("&7Cost: &f??"));
                arrayList.add(Text.color("&8????"));
                arrayList.add("");
                arrayList.add(Text.color("&eClick to purchase!"));
                itemMeta.setLore(arrayList);
                itemStack.setItemMeta(itemMeta);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return itemStack;
    }

    public void announce(Player player, Challenge challenge) {
        if (player == null || challenge == null || !challenge.selectable()) {
            return;
        }
        try {
            if (this.plugin.hook() != null && this.plugin.hook().isPlaying(player)) {
                return;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (!this.announcedPlayers.add(player.getUniqueId())) {
            return;
        }
        Text.send((CommandSender)player, this.plugin.getConfig().getStringList("challenge.messages.arena-top"));
        Text.send((CommandSender)player, "                                      &6&l" + challenge.displayName());
        Text.send((CommandSender)player, "&bChallenge Rules:");
        int n = 1;
        for (String string : challenge.rules()) {
            Text.send((CommandSender)player, "&6" + n + ". &7" + string);
            ++n;
        }
        Text.send((CommandSender)player, this.plugin.getConfig().getStringList("challenge.messages.arena-bottom"));
    }

    public String shopBlob(String string, String string2, Material material, String string3) {
        return ((string == null ? "" : string) + " " + (string2 == null ? "" : string2) + " " + (material == null ? "" : material.name()) + " " + (string3 == null ? "" : string3)).toLowerCase();
    }

    public void startToxicRain(Player player) {
        try {
            player.setPlayerWeather(WeatherType.DOWNFALL);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public void tickLazyMiner(Player player) {
        int n = 0;
        try {
            PotionEffectType potionEffectType = PotionEffectType.getByName((String)"SLOW_DIGGING");
            if (potionEffectType != null && player.hasPotionEffect(potionEffectType) && player.getPotionEffect(potionEffectType) != null) {
                n = player.getPotionEffect(potionEffectType).getAmplifier();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this.safePotion(player, "SLOW_DIGGING", 80, n >= 1 ? 4 : 0);
    }

    public void tickWeightedItems(Player player) {
        int n = this.inventoryWeight(player);
        int n2 = Math.max(0, n / 12);
        if (n2 <= 0) {
            try {
                player.removePotionEffect(PotionEffectType.SLOW);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            return;
        }
        this.safePotion(player, "SLOW", 80, Math.min(9, n2 - 1));
    }

    public void tickStamina(Player player, MatchSession matchSession, long l) {
        int n = player.getFoodLevel();
        if (l < matchSession.staminaBoostUntil) {
            if (n < 20 && Math.random() < 0.5) {
                player.setFoodLevel(n + 1);
            }
            return;
        }
        if (player.isSprinting()) {
            player.setFoodLevel(Math.max(0, n - 1));
            return;
        }
        if (n < 20 && Math.random() < 0.25) {
            player.setFoodLevel(n + 1);
        }
    }

    public void tickQuickMaths(Player player, MatchSession matchSession, long l) {
        this.askMathIfNeeded(player, matchSession, l);
    }

    public void handleMathAnswer(Player player, MatchSession matchSession, String string) {
        this.onChat(player, string);
    }

    public void tickMidnight(Player player, MatchSession matchSession) {
        if (this.onOwnIsland(player, matchSession)) {
            try {
                player.removePotionEffect(PotionEffectType.BLINDNESS);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            this.safePotion(player, "NIGHT_VISION", 80, 0);
        } else {
            this.safePotion(player, "BLINDNESS", 80, 0);
        }
    }

    public void checkPatriotMove(Player player, MatchSession matchSession, Location location) {
        Block block = location.clone().subtract(0.0, 0.1, 0.0).getBlock();
        String string = block.getType() == null ? "AIR" : block.getType().name();
        String string2 = string;
        if ("AIR".equals(string) || "VOID_AIR".equals(string) || "CAVE_AIR".equals(string)) {
            return;
        }
        if (this.plugin.hook() == null || !this.plugin.hook().isPlacedBlock(player, block)) {
            return;
        }
        boolean bl = this.plugin.hook().isTeamColoredBlock(player, block) || this.plugin.hook().isOwnPlacedBlock(player, block);
        boolean bl2 = bl;
        if (!bl) {
            this.fail(player, "You walked on an enemy block.");
        }
    }

    public void checkAnchorMove(Player player, MatchSession matchSession, Location location, Location location2) {
        boolean bl = System.currentTimeMillis() < matchSession.jumpAllowedUntil || this.hasJumpBoost(player);
        boolean bl2 = bl;
        if (!bl && location2.getY() > location.getY() + 0.34 && !this.canClimb(location.getBlock()) && !this.canClimb(location2.getBlock())) {
            Location location3 = location.clone();
            location3.setYaw(location2.getYaw());
            location3.setPitch(location2.getPitch());
            player.teleport(location3);
            this.deny(player, matchSession, "&cAnchor: you cannot jump.");
        }
    }

    public void askMathIfNeeded(Player player, MatchSession matchSession, long l) {
        this.askMath(player, matchSession, l);
    }
}

