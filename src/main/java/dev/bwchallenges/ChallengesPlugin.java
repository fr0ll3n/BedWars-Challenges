/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandMap
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.server.PluginEnableEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package dev.bwchallenges;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.ChallengeManager;
import dev.bwchallenges.command.ChallengeCommand;
import dev.bwchallenges.engine.ChallengeEngine;
import dev.bwchallenges.gui.ChallengeMenu;
import dev.bwchallenges.gui.Items;
import dev.bwchallenges.hook.BedWars2023Hook;
import dev.bwchallenges.hook.BedWarsHook;
import dev.bwchallenges.listener.GuiListener;
import dev.bwchallenges.listener.PlayListener;
import dev.bwchallenges.reward.RewardService;
import dev.bwchallenges.storage.Storage;
import dev.bwchallenges.storage.StorageFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChallengesPlugin
extends JavaPlugin
implements Listener {
    private ChallengeManager manager;
    private ChallengeEngine engine;
    private ChallengeMenu menu;
    private Storage storage;
    private RewardService rewards;
    private BedWarsHook hook;
    private boolean arenaMode;
    private boolean proxyMode;
    private final Map<UUID, String> playerModes = new ConcurrentHashMap<UUID, String>();

    public void onEnable() {
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.storage = StorageFactory.create(this);
        this.manager = new ChallengeManager(this, this.storage);
        this.engine = new ChallengeEngine(this);
        this.rewards = new RewardService(this);
        this.menu = new ChallengeMenu(this);
        this.registerCommand();
        Bukkit.getPluginManager().registerEvents((Listener)new GuiListener(this), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new PlayListener(this), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this);
        this.hookBedWars();
        Bukkit.getScheduler().runTaskTimer((Plugin)this, new Runnable(){

            @Override
            public void run() {
                ChallengesPlugin.this.engine().tick();
            }
        }, 20L, 10L);
        this.getLogger().info("BedWars2023-Challenges v" + this.getDescription().getVersion() + " enabled. " + Challenge.values().length + " challenges. Storage=" + this.storage.kind() + " Arena=" + this.arenaMode + " Proxy=" + this.proxyMode);
    }

    public void onDisable() {
        if (this.manager != null) {
            this.manager.saveAll();
        }
        if (this.storage != null) {
            this.storage.close();
        }
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent pluginEnableEvent) {
        String string = pluginEnableEvent.getPlugin().getName();
        if ("BedWars2023".equals(string) && !this.arenaMode) {
            this.hookBedWars();
        } else if ("BWProxy2023".equals(string) && !this.proxyMode) {
            this.hookBedWars();
        }
    }

    private void hookBedWars() {
        Object object;
        Plugin plugin = Bukkit.getPluginManager().getPlugin("BedWars2023");
        Plugin plugin2 = Bukkit.getPluginManager().getPlugin("BWProxy2023");
        if (!this.arenaMode && plugin != null && plugin.isEnabled()) {
            try {
                object = new BedWars2023Hook(this);
                this.hook = object;
                ((BedWars2023Hook)object).register();
                this.arenaMode = true;
                this.getLogger().info("Hooked BedWars2023 (" + plugin.getDescription().getVersion() + "). Select a challenge with /challenge before the game starts.");
            }
            catch (Throwable throwable) {
                this.getLogger().severe("Failed to hook BedWars2023: " + String.valueOf(throwable));
                throwable.printStackTrace();
            }
        }
        if (!this.proxyMode && plugin2 != null && plugin2.isEnabled()) {
            this.proxyMode = true;
            try {
                object = Class.forName("dev.bwchallenges.hook.Proxy2023Hook");
                ((Class)object).getMethod("register", new Class[0]).invoke(((Class)object).getConstructor(ChallengesPlugin.class).newInstance(new Object[]{this}), new Object[0]);
            }
            catch (Throwable throwable) {
                this.getLogger().warning("Could not hook BWProxy2023: " + throwable.getMessage());
            }
        }
        if (!this.arenaMode && !this.proxyMode) {
            this.getLogger().warning("BedWars2023 / BWProxy2023 not found yet. Menu still works; arena rules apply once BedWars2023 is installed.");
        }
    }

    private void registerCommand() {
        final ChallengeCommand challengeCommand = new ChallengeCommand(this);
        PluginCommand pluginCommand = this.getCommand("challenge");
        if (pluginCommand != null) {
            pluginCommand.setExecutor((CommandExecutor)challengeCommand);
            pluginCommand.setTabCompleter((TabCompleter)challengeCommand);
            return;
        }
        this.getLogger().warning("Command 'challenge' missing from plugin.yml \u2014 registering via CommandMap.");
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap commandMap = (CommandMap)field.get(Bukkit.getServer());
            Command command = new Command("challenge"){

                public boolean execute(CommandSender commandSender, String string, String[] stringArray) {
                    return challengeCommand.onCommand(commandSender, this, string, stringArray);
                }

                public List<String> tabComplete(CommandSender commandSender, String string, String[] stringArray) {
                    return challengeCommand.onTabComplete(commandSender, this, string, stringArray);
                }
            };
            command.setAliases(Arrays.asList("challenges", "bwchallenge", "bwc", "bwchallenges"));
            command.setDescription("Open the Bed Wars challenges menu");
            commandMap.register("bedwars2023-challenges", command);
        }
        catch (Throwable throwable) {
            this.logSafe("Could not register /challenge via CommandMap", throwable);
        }
    }

    public ChallengeManager manager() {
        return this.manager;
    }

    public ChallengeEngine engine() {
        return this.engine;
    }

    public ChallengeMenu menu() {
        return this.menu;
    }

    public RewardService rewards() {
        return this.rewards;
    }

    public BedWarsHook hook() {
        return this.hook;
    }

    public boolean arenaMode() {
        return this.arenaMode;
    }

    public boolean proxyMode() {
        return this.proxyMode;
    }

    public String msg(String string, String string2) {
        String string3 = this.getConfig().getString(string, string2);
        return string3 == null ? string2 : string3;
    }

    public void giveLobbyItem(Player player) {
        if (!this.getConfig().getBoolean("challenge.selector.enabled", true)) {
            return;
        }
        try {
            if (this.hook != null && this.hook.isPlaying(player)) {
                return;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        int n = this.getConfig().getInt("challenge.selector.item.slot", 7);
        if (n < 0 || n > 8) {
            n = 7;
        }
        String string = this.getConfig().getString("challenge.selector.item.material", "IRON_SWORD");
        String string2 = this.getConfig().getString("challenge.selector.item.name", "&aChallenges");
        ArrayList<String> arrayList = new ArrayList<String>(this.getConfig().getStringList("challenge.selector.item.lore"));
        arrayList.add("");
        arrayList.add("&8bwchallenges-selector");
        player.getInventory().setItem(n, Items.of(string, string2, arrayList));
    }

    public void logSafe(String string, Throwable throwable) {
        this.getLogger().warning(string + (String)(throwable == null ? "" : ": " + throwable.getMessage()));
        if (throwable != null && this.getConfig().getBoolean("debug", false)) {
            throwable.printStackTrace();
        }
    }

    public void setPlayerMode(Player player, String string) {
        if (player == null) {
            return;
        }
        if (string == null || string.isEmpty()) {
            this.playerModes.remove(player.getUniqueId());
        } else {
            this.playerModes.put(player.getUniqueId(), string.toLowerCase(Locale.ROOT));
        }
    }

    public String getPlayerMode(Player player) {
        if (player == null) {
            return null;
        }
        return this.playerModes.get(player.getUniqueId());
    }

    public void clearPlayerMode(Player player) {
        if (player != null) {
            this.playerModes.remove(player.getUniqueId());
        }
    }

    public void runGoBack(Player player) {
        List list;
        if (player == null || !player.isOnline()) {
            return;
        }
        String string = this.detectPlayMode(player);
        String string2 = null;
        if (string != null && !string.isEmpty()) {
            string2 = this.getConfig().getString("challenge.menu.go-back.modes." + string, null);
        }
        if (string2 == null || string2.isEmpty()) {
            string2 = this.getConfig().getString("challenge.menu.go-back.command", "bedwars");
        }
        if ((list = this.getConfig().getStringList("challenge.menu.go-back.commands")) != null && !list.isEmpty() && (string2 == null || string2.equals("bedwars"))) {
            for (String string3 : list) {
                this.dispatchPlayerCommand(player, string3);
            }
            return;
        }
        this.dispatchPlayerCommand(player, string2);
        if (list != null) {
            for (String string4 : list) {
                if (string4 == null || string4.equals(string2)) continue;
                this.dispatchPlayerCommand(player, string4);
            }
        }
    }

    private void dispatchPlayerCommand(Player player, String string) {
        if (string == null || string.trim().isEmpty() || string.equalsIgnoreCase("none")) {
            return;
        }
        String string2 = string.trim();
        if (string2.startsWith("/")) {
            string2 = string2.substring(1);
        }
        string2 = string2.replace("{player}", player.getName());
        try {
            player.performCommand(string2);
        }
        catch (Throwable throwable) {
            this.logSafe("Go-back command failed: " + string2, throwable);
        }
    }

    private String detectPlayMode(Player player) {
        Object object;
        block11: {
            String string = this.getPlayerMode(player);
            if (string != null && !string.isEmpty()) {
                return string;
            }
            try {
                if (this.hook == null) break block11;
                object = null;
                try {
                    object = this.hook.getClass().getMethod("getArena", Player.class).invoke((Object)this.hook, player);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                if (object == null) break block11;
                for (String string2 : new String[]{"getGroup", "getArenaName", "getName", "getIdentifier"}) {
                    try {
                        Object object2 = object.getClass().getMethod(string2, new Class[0]).invoke(object, new Object[0]);
                        if (object2 == null) continue;
                        String string3 = object2.toString().toLowerCase();
                        if (string3.contains("solo") || string3.contains("1v1") || string3.equals("1")) {
                            return "solo";
                        }
                        if (string3.contains("double") || string3.contains("2v2") || string3.equals("2")) {
                            return "doubles";
                        }
                        if (string3.contains("3v3") || string3.contains("triple") || string3.contains("3v3v3v3")) {
                            return "triples";
                        }
                        if (!string3.contains("4v4") && !string3.contains("quad") && !string3.contains("4v4v4v4")) continue;
                        return "fours";
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        object = this.getConfig().getString("challenge.menu.go-back.last-mode." + String.valueOf(player.getUniqueId()), null);
        return object;
    }
}

