/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tomkeuper.bedwars.proxy.api.BedWars
 *  com.tomkeuper.bedwars.proxy.api.addon.Addon
 *  com.tomkeuper.bedwars.proxy.api.event.PlayerArenaJoinEvent
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.RegisteredServiceProvider
 */
package dev.bwchallenges.hook;

import com.tomkeuper.bedwars.proxy.api.BedWars;
import com.tomkeuper.bedwars.proxy.api.addon.Addon;
import com.tomkeuper.bedwars.proxy.api.event.PlayerArenaJoinEvent;
import dev.bwchallenges.ChallengesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class Proxy2023Hook
implements Listener {
    private final ChallengesPlugin plugin;
    private BedWars api;

    public Proxy2023Hook(ChallengesPlugin challengesPlugin) {
        this.plugin = challengesPlugin;
    }

    public void register() {
        this.api = this.findApi();
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this.plugin);
        try {
            if (this.api != null) {
                this.api.getAddonsUtil().registerAddon((Addon)new ProxyAddon());
            }
        }
        catch (Throwable throwable) {
            this.plugin.getLogger().warning("Could not register as BWProxy2023 addon: " + throwable.getMessage());
        }
        this.plugin.getLogger().info("Hooked BWProxy2023 (lobby menu). Enable MySQL so arena servers see the selected challenge.");
    }

    private BedWars findApi() {
        try {
            RegisteredServiceProvider registeredServiceProvider = Bukkit.getServicesManager().getRegistration(BedWars.class);
            if (registeredServiceProvider != null) {
                return (BedWars)registeredServiceProvider.getProvider();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    @EventHandler
    public void onArenaJoin(PlayerArenaJoinEvent playerArenaJoinEvent) {
        Player player = playerArenaJoinEvent.getPlayer();
        if (player == null) {
            return;
        }
        this.plugin.manager().profile(player);
    }

    private final class ProxyAddon
    extends Addon {
        private ProxyAddon() {
        }

        public String getAuthor() {
            return "Grok";
        }

        public Plugin getPlugin() {
            return Proxy2023Hook.this.plugin;
        }

        public String getVersion() {
            return Proxy2023Hook.this.plugin.getDescription().getVersion();
        }

        public String getName() {
            return "BedWars2023-Challenges";
        }

        public String getDescription() {
            return "Lobby challenge selector for BWProxy2023";
        }

        public void load() {
        }

        public void unload() {
        }
    }
}

