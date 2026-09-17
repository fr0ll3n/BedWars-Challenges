/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package dev.bwchallenges;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.ChallengesPlugin;
import dev.bwchallenges.PlayerProfile;
import dev.bwchallenges.storage.MySqlStorage;
import dev.bwchallenges.storage.Storage;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class ChallengeManager {
    private final ChallengesPlugin plugin;
    private final Storage storage;
    private final Map<UUID, PlayerProfile> online = new ConcurrentHashMap<UUID, PlayerProfile>();
    private final Map<UUID, Challenge> selected = new ConcurrentHashMap<UUID, Challenge>();

    public ChallengeManager(ChallengesPlugin challengesPlugin, Storage storage) {
        this.plugin = challengesPlugin;
        this.storage = storage;
    }

    public PlayerProfile profile(Player player) {
        PlayerProfile playerProfile = this.profile(player.getUniqueId());
        playerProfile.setName(player.getName());
        return playerProfile;
    }

    public PlayerProfile profile(UUID uUID) {
        PlayerProfile playerProfile = this.online.get(uUID);
        if (playerProfile != null && playerProfile.isLoaded()) {
            return playerProfile;
        }
        PlayerProfile playerProfile2 = this.storage.load(uUID);
        PlayerProfile playerProfile3 = this.online.putIfAbsent(uUID, playerProfile2);
        if (playerProfile3 != null && playerProfile3.isLoaded()) {
            return playerProfile3;
        }
        this.online.put(uUID, playerProfile2);
        return playerProfile2;
    }

    public void loadAsync(Player player) {
        if (player == null) {
            return;
        }
        final UUID uUID = player.getUniqueId();
        final String string = player.getName();
        PlayerProfile playerProfile = this.online.get(uUID);
        if (playerProfile != null && playerProfile.isLoaded()) {
            return;
        }
        Runnable runnable = new Runnable(){

            @Override
            public void run() {
                try {
                    final PlayerProfile playerProfile = ChallengeManager.this.storage.load(uUID);
                    playerProfile.setName(string);
                    Runnable runnable = new Runnable(){

                        @Override
                        public void run() {
                            ChallengeManager.this.applyLoaded(uUID, playerProfile);
                        }
                    };
                    try {
                        ChallengeManager.this.plugin.getServer().getScheduler().runTask((Plugin)ChallengeManager.this.plugin, runnable);
                    }
                    catch (Throwable throwable) {
                        ChallengeManager.this.applyLoaded(uUID, playerProfile);
                    }
                }
                catch (Throwable throwable) {
                    ChallengeManager.this.plugin.logSafe("Failed to load challenge profile for " + string, throwable);
                }
            }
        };
        try {
            this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, runnable);
        }
        catch (Throwable throwable) {
            try {
                this.plugin.getServer().getScheduler().scheduleAsyncDelayedTask((Plugin)this.plugin, runnable);
            }
            catch (Throwable throwable2) {
                runnable.run();
            }
        }
    }

    private void applyLoaded(UUID uUID, PlayerProfile playerProfile) {
        PlayerProfile playerProfile2 = this.online.get(uUID);
        if (playerProfile2 != null && playerProfile2.isLoaded()) {
            if (playerProfile.active() != null && playerProfile2.active() == null) {
                playerProfile2.setActive(playerProfile.active());
                this.remember(uUID, playerProfile.active());
            }
            return;
        }
        if (playerProfile.active() != null && !playerProfile.active().selectable()) {
            playerProfile.setActive(null);
        }
        this.online.put(uUID, playerProfile);
        this.remember(uUID, playerProfile.active());
    }

    public void refresh(UUID uUID) {
        PlayerProfile playerProfile = this.online.get(uUID);
        if (playerProfile != null && playerProfile.isLoaded()) {
            this.remember(uUID, playerProfile.active());
            return;
        }
        PlayerProfile playerProfile2 = this.storage.load(uUID);
        this.online.put(uUID, playerProfile2);
        this.remember(uUID, playerProfile2.active());
    }

    public void remember(UUID uUID, Challenge challenge) {
        if (uUID == null) {
            return;
        }
        if (challenge == null) {
            this.selected.remove(uUID);
        } else {
            this.selected.put(uUID, challenge);
        }
    }

    public Challenge activeOf(Player player) {
        PlayerProfile playerProfile = this.profile(player);
        if (playerProfile.isLoaded()) {
            Challenge challenge = playerProfile.active();
            this.remember(player.getUniqueId(), challenge);
            return challenge;
        }
        Challenge challenge = this.selected.get(player.getUniqueId());
        if (challenge != null) {
            return challenge;
        }
        Challenge challenge2 = playerProfile.active();
        if (challenge2 != null) {
            this.remember(player.getUniqueId(), challenge2);
        }
        return challenge2;
    }

    public boolean isActive(Player player, Challenge challenge) {
        return this.profile(player).active() == challenge;
    }

    public void setActive(Player player, Challenge challenge) {
        if (challenge != null && !challenge.selectable()) {
            challenge = null;
        }
        PlayerProfile playerProfile = this.profile(player);
        playerProfile.setName(player.getName());
        playerProfile.setActive(challenge);
        this.remember(player.getUniqueId(), challenge);
        this.storage.saveAsync(playerProfile);
        try {
            if (this.plugin.hook() != null) {
                this.plugin.hook().persistActive(player, challenge);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public void clearActive(Player player) {
        this.setActive(player, null);
    }

    public void markCompleted(Player player, Challenge challenge) {
        if (challenge == null) {
            return;
        }
        PlayerProfile playerProfile = this.profile(player);
        playerProfile.complete(challenge);
        this.storage.saveAsync(playerProfile);
        if (this.storage instanceof MySqlStorage) {
            ((MySqlStorage)this.storage).recordResult(player.getUniqueId(), challenge, true);
        }
    }

    public void markFailed(Player player, Challenge challenge) {
        if (challenge == null) {
            return;
        }
        if (this.storage instanceof MySqlStorage) {
            ((MySqlStorage)this.storage).recordResult(player.getUniqueId(), challenge, false);
        }
    }

    public void unload(UUID uUID) {
        PlayerProfile playerProfile = this.online.remove(uUID);
        if (playerProfile != null) {
            this.storage.saveAsync(playerProfile);
        }
        this.selected.remove(uUID);
    }

    public void saveAll() {
        for (PlayerProfile playerProfile : this.online.values()) {
            this.storage.save(playerProfile);
        }
        for (PlayerProfile playerProfile : Bukkit.getOnlinePlayers()) {
            PlayerProfile playerProfile2 = this.online.get(playerProfile.getUniqueId());
            if (playerProfile2 == null) continue;
            playerProfile2.setName(playerProfile.getName());
        }
    }

    public Storage storage() {
        return this.storage;
    }

    public ChallengesPlugin plugin() {
        return this.plugin;
    }
}

