/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tomkeuper.bedwars.api.BedWars
 *  com.tomkeuper.bedwars.api.addon.Addon
 *  com.tomkeuper.bedwars.api.arena.GameState
 *  com.tomkeuper.bedwars.api.arena.IArena
 *  com.tomkeuper.bedwars.api.arena.generator.GeneratorType
 *  com.tomkeuper.bedwars.api.arena.generator.IGenerator
 *  com.tomkeuper.bedwars.api.arena.shop.ICategoryContent
 *  com.tomkeuper.bedwars.api.arena.shop.IContentTier
 *  com.tomkeuper.bedwars.api.arena.team.ITeam
 *  com.tomkeuper.bedwars.api.arena.team.TeamColor
 *  com.tomkeuper.bedwars.api.database.IDatabase
 *  com.tomkeuper.bedwars.api.events.gameplay.GameEndEvent
 *  com.tomkeuper.bedwars.api.events.gameplay.GameStateChangeEvent
 *  com.tomkeuper.bedwars.api.events.gameplay.TeamAssignEvent
 *  com.tomkeuper.bedwars.api.events.player.PlayerBedBreakEvent
 *  com.tomkeuper.bedwars.api.events.player.PlayerFirstSpawnEvent
 *  com.tomkeuper.bedwars.api.events.player.PlayerGeneratorCollectEvent
 *  com.tomkeuper.bedwars.api.events.player.PlayerJoinArenaEvent
 *  com.tomkeuper.bedwars.api.events.player.PlayerKillEvent
 *  com.tomkeuper.bedwars.api.events.player.PlayerLeaveArenaEvent
 *  com.tomkeuper.bedwars.api.events.player.PlayerReSpawnEvent
 *  com.tomkeuper.bedwars.api.events.shop.ShopBuyEvent
 *  com.tomkeuper.bedwars.api.events.shop.ShopOpenEvent
 *  com.tomkeuper.bedwars.api.events.upgrades.UpgradeBuyEvent
 *  com.tomkeuper.bedwars.api.items.handlers.IPermanentItem
 *  com.tomkeuper.bedwars.api.items.handlers.IPermanentItemHandler
 *  com.tomkeuper.bedwars.api.items.handlers.PermanentItemHandler
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package dev.bwchallenges.hook;

import com.tomkeuper.bedwars.api.BedWars;
import com.tomkeuper.bedwars.api.addon.Addon;
import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.generator.GeneratorType;
import com.tomkeuper.bedwars.api.arena.generator.IGenerator;
import com.tomkeuper.bedwars.api.arena.shop.ICategoryContent;
import com.tomkeuper.bedwars.api.arena.shop.IContentTier;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.arena.team.TeamColor;
import com.tomkeuper.bedwars.api.database.IDatabase;
import com.tomkeuper.bedwars.api.events.gameplay.GameEndEvent;
import com.tomkeuper.bedwars.api.events.gameplay.GameStateChangeEvent;
import com.tomkeuper.bedwars.api.events.gameplay.TeamAssignEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerBedBreakEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerFirstSpawnEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerGeneratorCollectEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerJoinArenaEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerKillEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerLeaveArenaEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerReSpawnEvent;
import com.tomkeuper.bedwars.api.events.shop.ShopBuyEvent;
import com.tomkeuper.bedwars.api.events.shop.ShopOpenEvent;
import com.tomkeuper.bedwars.api.events.upgrades.UpgradeBuyEvent;
import com.tomkeuper.bedwars.api.items.handlers.IPermanentItem;
import com.tomkeuper.bedwars.api.items.handlers.IPermanentItemHandler;
import com.tomkeuper.bedwars.api.items.handlers.PermanentItemHandler;
import dev.bwchallenges.Challenge;
import dev.bwchallenges.ChallengesPlugin;
import dev.bwchallenges.PlayerProfile;
import dev.bwchallenges.engine.MatchSession;
import dev.bwchallenges.hook.BedWarsHook;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public final class BedWars2023Hook
implements BedWarsHook,
Listener {
    private final ChallengesPlugin plugin;
    private BedWars api;
    private final Map waitingAnnounced = new ConcurrentHashMap();

    public BedWars2023Hook(ChallengesPlugin challengesPlugin) {
        this.plugin = challengesPlugin;
    }

    @Override
    public void register() {
        this.api = this.findApi();
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this.plugin);
        try {
            if (this.api != null) {
                this.api.getAddonsUtil().registerAddon((Addon)new ChallengesAddon());
                this.registerLobbyHandler();
            }
        }
        catch (Throwable throwable) {
            this.plugin.getLogger().warning("Could not register as BedWars2023 addon: " + throwable.getMessage());
        }
        this.plugin.getLogger().info("Hooked BedWars2023.");
    }

    private BedWars findApi() {
        Object object;
        try {
            object = Bukkit.getServicesManager().getRegistration(BedWars.class);
            if (object != null && object.getProvider() != null) {
                return (BedWars)object.getProvider();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            object = Class.forName("com.tomkeuper.bedwars.BedWars");
            Object object2 = ((Class)object).getMethod("getAPI", new Class[0]).invoke(null, new Object[0]);
            if (object2 instanceof BedWars) {
                return (BedWars)object2;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    private BedWars api() {
        if (this.api == null) {
            this.api = this.findApi();
        }
        return this.api;
    }

    @Override
    public boolean isInArena(Player player) {
        BedWars bedWars = this.api();
        if (bedWars == null) {
            return false;
        }
        try {
            return bedWars.getArenaUtil().getArenaByPlayer(player) != null;
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    @Override
    public boolean isPlaying(Player player) {
        BedWars bedWars = this.api();
        if (bedWars == null) {
            return false;
        }
        try {
            IArena iArena = bedWars.getArenaUtil().getArenaByPlayer(player);
            return iArena != null && iArena.getStatus() == GameState.playing && iArena.isPlayer(player);
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    @Override
    public boolean isOwnPlacedBlock(Player player, Block block) {
        BedWars bedWars = this.api();
        if (bedWars == null || block == null) {
            return true;
        }
        try {
            IArena iArena = bedWars.getArenaUtil().getArenaByPlayer(player);
            if (iArena == null) {
                return true;
            }
            ITeam iTeam = iArena.getTeam(player);
            if (iTeam != null && iTeam.getColor() != null) {
                String string = iTeam.getColor().name();
                String string2 = block.getType().name();
                if ((string2.contains("WOOL") || string2.contains("TERRACOTTA") || string2.contains("CONCRETE")) && string2.contains(string)) {
                    return true;
                }
            }
            return iArena.isBlockPlaced(block);
        }
        catch (Throwable throwable) {
            return true;
        }
    }

    @Override
    public String teamName(Player player) {
        BedWars bedWars = this.api();
        if (bedWars == null) {
            return "";
        }
        try {
            IArena iArena = bedWars.getArenaUtil().getArenaByPlayer(player);
            if (iArena == null) {
                return "";
            }
            ITeam iTeam = iArena.getTeam(player);
            return iTeam == null ? "" : iTeam.getName();
        }
        catch (Throwable throwable) {
            return "";
        }
    }

    @Override
    public String arenaKey(Player player) {
        BedWars bedWars = this.api();
        if (bedWars == null || player == null) {
            return "";
        }
        try {
            IArena iArena = bedWars.getArenaUtil().getArenaByPlayer(player);
            return iArena == null ? "" : String.valueOf(iArena.hashCode());
        }
        catch (Throwable throwable) {
            return "";
        }
    }

    @Override
    public boolean isTeamColoredBlock(Player player, Block block) {
        BedWars bedWars = this.api();
        if (bedWars == null || block == null || player == null) {
            return false;
        }
        try {
            IArena iArena = bedWars.getArenaUtil().getArenaByPlayer(player);
            if (iArena == null) {
                return false;
            }
            ITeam iTeam = iArena.getTeam(player);
            if (iTeam == null || iTeam.getColor() == null) {
                return false;
            }
            TeamColor teamColor = iTeam.getColor();
            String string = teamColor.name();
            String string2 = block.getType().name();
            if (teamColor.woolMaterial() != null && block.getType() == teamColor.woolMaterial()) {
                return true;
            }
            if (string2.contains("WOOL")) {
                try {
                    if (block.getData() == teamColor.itemByte()) {
                        return true;
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            return (string2.contains("WOOL") || string2.contains("TERRACOTTA") || string2.contains("CONCRETE") || string2.contains("GLASS")) && string2.contains(string);
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    @Override
    public boolean isPlacedBlock(Player player, Block block) {
        BedWars bedWars = this.api();
        if (bedWars == null || block == null || player == null) {
            return false;
        }
        try {
            IArena iArena = bedWars.getArenaUtil().getArenaByPlayer(player);
            return iArena != null && iArena.isBlockPlaced(block);
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    @Override
    public void restoreStarterKit(Player player) {
        BedWars bedWars = this.api();
        if (bedWars == null || player == null) {
            return;
        }
        try {
            IArena iArena = bedWars.getArenaUtil().getArenaByPlayer(player);
            if (iArena == null) {
                return;
            }
            ITeam iTeam = iArena.getTeam(player);
            if (iTeam == null) {
                return;
            }
            try {
                iTeam.sendArmor(player);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                iTeam.defaultSword(player, true);
            }
            catch (Throwable throwable) {}
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    @Override
    public String teamNameAt(Player player, Block block) {
        BedWars bedWars = this.api();
        if (bedWars == null || block == null) {
            return "";
        }
        try {
            IArena iArena = bedWars.getArenaUtil().getArenaByPlayer(player);
            if (iArena == null) {
                return "";
            }
            for (ITeam iTeam : iArena.getTeams()) {
                Location location = iTeam.getBed();
                if (location == null || location.getWorld() != block.getWorld()) continue;
                if (location.getBlockX() == block.getX() && location.getBlockY() == block.getY() && location.getBlockZ() == block.getZ()) {
                    return iTeam.getName();
                }
                if (Math.abs(location.getBlockX() - block.getX()) > 1 || location.getBlockY() != block.getY() || Math.abs(location.getBlockZ() - block.getZ()) > 1) continue;
                return iTeam.getName();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return "";
    }

    @Override
    public void persistActive(Player player, Challenge challenge) {
        try {
            BedWars bedWars = this.api();
            if (bedWars == null || player == null) {
                return;
            }
            IDatabase iDatabase = bedWars.getRemoteDatabase();
            if (iDatabase == null) {
                return;
            }
            String string = challenge == null ? "none" : challenge.id();
            iDatabase.getClass().getMethod("saveCustomStat", String.class, UUID.class, Object.class, String.class).invoke((Object)iDatabase, "bwch_active", player.getUniqueId(), string, "VARCHAR");
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    @Override
    public Challenge readPersistedActive(Player player) {
        try {
            BedWars bedWars = this.api();
            if (bedWars == null || player == null) {
                return null;
            }
            IDatabase iDatabase = bedWars.getRemoteDatabase();
            if (iDatabase == null) {
                return null;
            }
            Object object = iDatabase.getClass().getMethod("getCustomStat", String.class, UUID.class).invoke((Object)iDatabase, "bwch_active", player.getUniqueId());
            if (object == null) {
                return null;
            }
            String string = String.valueOf(object);
            if (string.isEmpty() || string.equalsIgnoreCase("none") || string.equals("null")) {
                return null;
            }
            return Challenge.byId(string);
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    private Challenge resolveChallenge(Player player) {
        Challenge challenge;
        PlayerProfile playerProfile = this.plugin.manager().profile(player);
        if (playerProfile.isLoaded() && (challenge = playerProfile.active()) != null) {
            this.plugin.manager().remember(player.getUniqueId(), challenge);
            return challenge;
        }
        challenge = this.plugin.manager().activeOf(player);
        if (challenge != null) {
            return challenge;
        }
        challenge = this.readPersistedActive(player);
        if (challenge != null) {
            this.plugin.manager().remember(player.getUniqueId(), challenge);
            playerProfile.setActive(challenge);
            try {
                this.plugin.manager().storage().saveAsync(playerProfile);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            return challenge;
        }
        if (playerProfile.isLoaded()) {
            this.plugin.manager().remember(player.getUniqueId(), null);
        }
        return null;
    }

    public void startForPlayer(Player player, IArena iArena, boolean bl) {
        if (player == null || iArena == null) {
            return;
        }
        try {
            if (iArena.isSpectator(player)) {
                return;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        MatchSession matchSession = this.plugin.engine().session(player);
        if (matchSession != null && !matchSession.failed && !matchSession.completed) {
            return;
        }
        Challenge challenge = this.resolveChallenge(player);
        if (challenge == null) {
            return;
        }
        ITeam iTeam = iArena.getTeam(player);
        Location location = iTeam != null && iTeam.getSpawn() != null ? iTeam.getSpawn() : player.getLocation();
        int n = 22;
        try {
            n = Math.max(12, iArena.getIslandRadius());
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        int n2 = 1;
        try {
            n2 = Math.max(1, iArena.getTeams().size() - 1);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        String string = iTeam != null ? iTeam.getName() : "";
        String string2 = iTeam != null && iTeam.getColor() != null ? iTeam.getColor().name() : "";
        this.plugin.engine().start(player, challenge, location, n, string, string2, n2);
        this.applySpecialStart(player, iArena, challenge, iTeam);
        this.plugin.engine().announce(player, challenge);
        this.plugin.getLogger().info("Activated " + challenge.displayName() + " for " + player.getName());
    }

    public void startArena(IArena iArena) {
        if (iArena == null) {
            return;
        }
        for (Player player : iArena.getPlayers()) {
            this.startForPlayer(player, iArena, true);
        }
        this.assignAssassinTargets(iArena);
        this.assignPresidents(iArena);
    }

    @EventHandler
    public void onJoinArena(PlayerJoinArenaEvent playerJoinArenaEvent) {
        if (playerJoinArenaEvent.isSpectator()) {
            return;
        }
        Player player = playerJoinArenaEvent.getPlayer();
        this.plugin.manager().refresh(player.getUniqueId());
        this.plugin.giveLobbyItem(player);
        this.plugin.manager().loadAsync(player);
        this.scheduleWaitingAnnounce(player, 12L);
        this.scheduleWaitingAnnounce(player, 40L);
    }

    private void announceWaitingLobby(Player player) {
        Player player2 = player;
        try {
            this.plugin.manager().refresh(player2.getUniqueId());
            Challenge challenge = this.resolveChallenge(player2);
            if (challenge == null) {
                this.plugin.getLogger().info(new StringBuffer().append("Waiting-lobby announce: no active challenge for ").append(player2.getName()).toString());
                return;
            }
            Long l = (Long)this.waitingAnnounced.get(player2.getUniqueId());
            long l2 = System.currentTimeMillis();
            if (l != null && l2 - l < 8000L) {
                return;
            }
            this.waitingAnnounced.put(player2.getUniqueId(), l2);
            this.plugin.engine().announce(player2, challenge);
            this.plugin.getLogger().info(new StringBuffer().append("Waiting-lobby announce: ").append(challenge.displayName()).append(" for ").append(player2.getName()).toString());
        }
        catch (Throwable throwable) {
            this.plugin.getLogger().warning(new StringBuffer().append("Waiting-lobby announce failed for ").append(player2.getName()).append(": ").append(throwable.getMessage()).toString());
            throwable.printStackTrace();
        }
    }

    @EventHandler
    public void onState(GameStateChangeEvent gameStateChangeEvent) {
        IArena iArena;
        GameState gameState = gameStateChangeEvent.getNewState();
        if (gameState == GameState.starting && (iArena = gameStateChangeEvent.getArena()) != null) {
            for (Player player : iArena.getPlayers()) {
                this.announceWaitingLobby(player);
            }
        }
        if (gameState != GameState.playing) {
            return;
        }
        final IArena iArena2 = gameStateChangeEvent.getArena();
        if (iArena2 == null) {
            return;
        }
        this.startArena(iArena2);
        this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, new Runnable(){

            @Override
            public void run() {
                BedWars2023Hook.this.startArena(iArena2);
            }
        }, 20L);
    }

    @EventHandler
    public void onFirstSpawn(PlayerFirstSpawnEvent playerFirstSpawnEvent) {
        this.startForPlayer(playerFirstSpawnEvent.getPlayer(), playerFirstSpawnEvent.getArena(), false);
        IArena iArena = playerFirstSpawnEvent.getArena();
        if (iArena != null) {
            this.assignAssassinTargets(iArena);
            this.assignPresidents(iArena);
        }
    }

    @EventHandler
    public void onTeamAssign(TeamAssignEvent teamAssignEvent) {
        this.startForPlayer(teamAssignEvent.getPlayer(), teamAssignEvent.getArena(), false);
    }

    private void applySpecialStart(Player player, IArena iArena, Challenge challenge, ITeam iTeam) {
        MatchSession matchSession;
        if (challenge == Challenge.HALVED_AND_DOUBLED) {
            try {
                player.setMaxHealth(10.0);
                player.setHealth(10.0);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (!(challenge != Challenge.MINIMUM_WAGE || (matchSession = this.plugin.engine().session(player)) != null && matchSession.generatorsSlowed)) {
            this.slowIslandGenerators(iTeam);
            if (matchSession != null) {
                matchSession.generatorsSlowed = true;
            }
        }
    }

    private void slowIslandGenerators(ITeam iTeam) {
        if (iTeam == null) {
            return;
        }
        try {
            for (IGenerator iGenerator : iTeam.getGenerators()) {
                GeneratorType generatorType;
                if (iGenerator == null || (generatorType = iGenerator.getType()) != GeneratorType.IRON && generatorType != GeneratorType.GOLD) continue;
                iGenerator.setDelay(iGenerator.getDelay() * 1.5);
            }
        }
        catch (Throwable throwable) {
            this.plugin.getLogger().warning("Could not slow island generators: " + throwable.getMessage());
        }
    }

    private void assignAssassinTargets(IArena iArena) {
        List list = iArena.getTeams();
        if (list == null || list.size() < 2) {
            return;
        }
        for (Player player : iArena.getPlayers()) {
            Object object;
            MatchSession matchSession = this.plugin.engine().session(player);
            if (matchSession == null || matchSession.challenge != Challenge.ASSASSIN && matchSession.challenge != Challenge.MASTER_ASSASSIN) continue;
            ITeam iTeam = iArena.getTeam(player);
            ITeam iTeam2 = null;
            ITeam iTeam3 = null;
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                object = iterator.next();
                ITeam iTeam4 = (ITeam)object;
                if (iTeam != null && iTeam4.getName().equals(iTeam.getName()) || this.teamEliminated(iTeam4)) continue;
                if (matchSession.targetTeam != null && iTeam4.getName().equalsIgnoreCase(matchSession.targetTeam)) {
                    iTeam2 = iTeam4;
                    continue;
                }
                if (iTeam3 != null) continue;
                iTeam3 = iTeam4;
            }
            Object object2 = iterator = iTeam2 != null ? iTeam2 : iTeam3;
            if (iterator == null) continue;
            object = iterator.getName();
            boolean bl = matchSession.targetTeam != null && matchSession.targetTeam.equalsIgnoreCase((String)object);
            matchSession.targetTeam = object;
            String string = matchSession.teamName = iTeam == null ? "" : iTeam.getName();
            if (bl) continue;
            player.sendMessage("\u00a76Target team: \u00a7e" + iterator.getName());
        }
    }

    private boolean teamEliminated(ITeam iTeam) {
        if (iTeam == null) {
            return true;
        }
        try {
            if (!iTeam.isBedDestroyed()) {
                return false;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (iTeam.getMembers() == null || iTeam.getMembers().isEmpty()) {
            return true;
        }
        for (Player player : iTeam.getMembers()) {
            if (player == null || !player.isOnline()) continue;
            try {
                if (player.getGameMode() == GameMode.SPECTATOR) continue;
                return false;
            }
            catch (Throwable throwable) {
                return false;
            }
        }
        return true;
    }

    private void assignPresidents(IArena iArena) {
        for (ITeam iTeam : iArena.getTeams()) {
            if (iTeam.getMembers() == null || iTeam.getMembers().isEmpty()) continue;
            Player player = (Player)iTeam.getMembers().get(new Random().nextInt(iTeam.getMembers().size()));
            for (Player player2 : iTeam.getMembers()) {
                MatchSession matchSession = this.plugin.engine().session(player2);
                if (matchSession == null || matchSession.challenge != Challenge.PROTECT_THE_PRESIDENT || matchSession.presidentId != null && matchSession.presidentId.equals(player.getUniqueId())) continue;
                matchSession.presidentId = player.getUniqueId();
                player2.sendMessage("\u00a76President: \u00a7e" + player.getName());
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerLeaveArenaEvent playerLeaveArenaEvent) {
        try {
            if (playerLeaveArenaEvent.getPlayer() != null) {
                this.waitingAnnounced.remove(playerLeaveArenaEvent.getPlayer().getUniqueId());
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this.plugin.engine().clear(playerLeaveArenaEvent.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onEnd(GameEndEvent gameEndEvent) {
        IArena iArena;
        List list = gameEndEvent.getWinners();
        if (list != null) {
            for (Object e : list) {
                Player player;
                UUID uUID = e instanceof UUID ? (UUID)e : null;
                if (uUID == null || (player = Bukkit.getPlayer((UUID)uUID)) == null) continue;
                this.plugin.engine().succeed(player);
            }
        }
        if ((iArena = gameEndEvent.getArena()) != null) {
            try {
                for (Object e : iArena.getPlayers()) {
                    this.plugin.engine().clear(((Player)e).getUniqueId());
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                for (Object e : iArena.getSpectators()) {
                    this.plugin.engine().clear(((Player)e).getUniqueId());
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onShopBuy(ShopBuyEvent shopBuyEvent) {
        Material material;
        String string;
        String string2;
        String string3;
        Player player;
        block12: {
            player = shopBuyEvent.getBuyer();
            ICategoryContent iCategoryContent = shopBuyEvent.getCategoryContent();
            string3 = "";
            string2 = "";
            string = "";
            material = Material.AIR;
            try {
                if (iCategoryContent == null) break block12;
                string3 = String.valueOf(iCategoryContent.getIdentifier());
                try {
                    string2 = String.valueOf(iCategoryContent.getCategoryIdentifier());
                }
                catch (Throwable throwable) {
                    string2 = "";
                }
                try {
                    if (iCategoryContent.getItemStack(player) != null) {
                        material = iCategoryContent.getItemStack(player).getType();
                        if (iCategoryContent.getItemStack(player).hasItemMeta() && iCategoryContent.getItemStack(player).getItemMeta().hasDisplayName()) {
                            string = iCategoryContent.getItemStack(player).getItemMeta().getDisplayName();
                        }
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                try {
                    if (!(iCategoryContent.getContentTiers() == null || iCategoryContent.getContentTiers().isEmpty() || ((IContentTier)iCategoryContent.getContentTiers().get(0)).getItemStack() == null || material != Material.AIR && material != null)) {
                        material = ((IContentTier)iCategoryContent.getContentTiers().get(0)).getItemStack().getType();
                    }
                }
                catch (Throwable throwable) {}
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (this.plugin.engine().blockShopBuy(player, string3, string2, material, string)) {
            shopBuyEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onShopOpen(ShopOpenEvent shopOpenEvent) {
        MatchSession matchSession = this.plugin.engine().session(shopOpenEvent.getPlayer());
        if (matchSession == null) {
            return;
        }
        if (matchSession.challenge == Challenge.INVISIBLE_SHOP) {
            shopOpenEvent.getPlayer().sendMessage("\u00a77Shop items are hidden. Remember the slots.");
            this.plugin.engine().scheduleHideShop(shopOpenEvent.getPlayer());
        }
        if (matchSession.challenge == Challenge.COLLECTOR) {
            this.plugin.engine().submitCollectorWool(shopOpenEvent.getPlayer());
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onUpgrade(UpgradeBuyEvent upgradeBuyEvent) {
        if (this.plugin.engine().blockUpgrade(upgradeBuyEvent.getPlayer())) {
            upgradeBuyEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onGen(PlayerGeneratorCollectEvent playerGeneratorCollectEvent) {
        ItemStack itemStack = playerGeneratorCollectEvent.getItemStack();
        Material material = itemStack == null ? Material.AIR : itemStack.getType();
        Material material2 = material;
        if (this.plugin.engine().blockGenerator(playerGeneratorCollectEvent.getPlayer(), material, playerGeneratorCollectEvent.getItem())) {
            playerGeneratorCollectEvent.setCancelled(true);
            try {
                Item item = playerGeneratorCollectEvent.getItem();
                this.plugin.engine().softBlockItem(item);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onBed(PlayerBedBreakEvent playerBedBreakEvent) {
        Player player = playerBedBreakEvent.getPlayer();
        ITeam iTeam = playerBedBreakEvent.getVictimTeam();
        ITeam iTeam2 = playerBedBreakEvent.getPlayerTeam();
        MatchSession matchSession = this.plugin.engine().session(player);
        if (matchSession != null && (matchSession.challenge == Challenge.ASSASSIN || matchSession.challenge == Challenge.MASTER_ASSASSIN)) {
            final IArena iArena = playerBedBreakEvent.getArena();
            if (matchSession.targetTeam != null && iTeam != null && !iTeam.getName().equalsIgnoreCase(matchSession.targetTeam)) {
                this.plugin.engine().fail(player, "You broke the wrong team's bed.");
            } else if (iArena != null) {
                this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, new Runnable(){

                    @Override
                    public void run() {
                        BedWars2023Hook.this.assignAssassinTargets(iArena);
                    }
                }, 10L);
            }
        }
        this.plugin.engine().onBedBrokenBy(player);
        if (iTeam2 != null && iTeam2.getMembers() != null) {
            for (Player player2 : iTeam2.getMembers()) {
                this.plugin.engine().onBedBrokenBy(player2);
            }
        }
        if (iTeam != null && iTeam.getMembers() != null) {
            for (Player player2 : iTeam.getMembers()) {
                this.plugin.engine().onOwnBedLost(player2);
            }
        }
    }

    @EventHandler
    public void onKill(PlayerKillEvent playerKillEvent) {
        Player player = playerKillEvent.getKiller();
        Player player2 = playerKillEvent.getVictim();
        if (player != null) {
            this.plugin.engine().onKill(player);
            if (playerKillEvent.getCause() != null && !playerKillEvent.getCause().isFinalKill()) {
                this.plugin.engine().onNonFinalKill(player);
            }
            try {
                if (playerKillEvent.getArena() != null) {
                    this.assignAssassinTargets(playerKillEvent.getArena());
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (player2 != null) {
            this.plugin.engine().onPresidentDeath(player2);
        }
    }

    @EventHandler
    public void onRespawn(PlayerReSpawnEvent playerReSpawnEvent) {
        final Player player = playerReSpawnEvent.getPlayer();
        MatchSession matchSession = this.plugin.engine().session(player);
        if (matchSession == null) {
            return;
        }
        if (matchSession.challenge == Challenge.REGULAR_SHOPPER) {
            this.plugin.engine().resetShopperGear(player);
            this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, new Runnable(){

                @Override
                public void run() {
                    BedWars2023Hook.this.plugin.engine().resetShopperGear(player);
                }
            }, 5L);
        }
        if (matchSession.challenge == Challenge.HALVED_AND_DOUBLED) {
            try {
                player.setMaxHealth(10.0);
                player.setHealth(10.0);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (matchSession.challenge == Challenge.LAZY_MINER || matchSession.challenge == Challenge.MIDNIGHT) {
            this.plugin.engine().start(player, matchSession.challenge, matchSession.islandCenter, matchSession.islandRadius, matchSession.teamName, matchSession.teamColor, matchSession.enemyTeams);
        }
    }

    private void registerLobbyHandler() {
        try {
            String[] stringArray;
            for (String string : stringArray = new String[]{"bwchallenges-menu", "challenge", "challenges", "bwchallenges"}) {
                this.api.getItemUtil().registerItemHandler((IPermanentItemHandler)new PermanentItemHandler(string, (Plugin)this.plugin, this.api){

                    public void handleUse(Player player, IArena iArena, IPermanentItem iPermanentItem) {
                        BedWars2023Hook.this.plugin.menu().open(player);
                    }
                });
            }
        }
        catch (Throwable throwable) {
            this.plugin.getLogger().info("Lobby item handler not registered (optional): " + throwable.getMessage());
        }
    }

    private void scheduleWaitingAnnounce(final Player player, long l) {
        this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, new Runnable(){

            @Override
            public void run() {
                if (!player.isOnline()) {
                    return;
                }
                BedWars2023Hook.this.plugin.giveLobbyItem(player);
                BedWars2023Hook.this.announceWaitingLobby(player);
            }
        }, l);
    }

    private final class ChallengesAddon
    extends Addon {
        private ChallengesAddon() {
        }

        public String getAuthor() {
            return "Grok";
        }

        public Plugin getPlugin() {
            return BedWars2023Hook.this.plugin;
        }

        public String getVersion() {
            return BedWars2023Hook.this.plugin.getDescription().getVersion();
        }

        public String getName() {
            return "BedWars2023-Challenges";
        }

        public String getDescription() {
            return "Hypixel-style Bed Wars challenges";
        }

        public void load() {
        }

        public void unload() {
        }
    }
}

