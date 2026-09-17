/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitTask
 */
package dev.bwchallenges.storage;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.ChallengesPlugin;
import dev.bwchallenges.PlayerProfile;
import dev.bwchallenges.storage.Storage;
import java.io.File;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public final class YamlStorage
implements Storage {
    private final ChallengesPlugin plugin;
    private final File file;
    private final YamlConfiguration yaml;
    private final Map<UUID, PlayerProfile> cache = new ConcurrentHashMap<UUID, PlayerProfile>();
    private volatile boolean pendingFlush;
    private BukkitTask flushTask;

    public YamlStorage(ChallengesPlugin challengesPlugin) {
        this.plugin = challengesPlugin;
        File file = challengesPlugin.getDataFolder();
        if (!file.exists()) {
            file.mkdirs();
        }
        this.file = new File(file, "players.yml");
        this.yaml = YamlConfiguration.loadConfiguration((File)this.file);
        this.flushTask = challengesPlugin.getServer().getScheduler().runTaskTimerAsynchronously((Plugin)challengesPlugin, new Runnable(){

            @Override
            public void run() {
                if (YamlStorage.this.pendingFlush) {
                    YamlStorage.this.pendingFlush = false;
                    YamlStorage.this.flush();
                }
            }
        }, 100L, 100L);
    }

    @Override
    public PlayerProfile load(UUID uUID) {
        Object object;
        Object object22;
        PlayerProfile playerProfile = this.cache.get(uUID);
        if (playerProfile != null) {
            return playerProfile;
        }
        PlayerProfile playerProfile2 = new PlayerProfile(uUID);
        String string = uUID.toString();
        String string2 = this.yaml.getString(string + ".active");
        Challenge challenge = Challenge.byId(string2);
        EnumSet<Challenge> enumSet = EnumSet.noneOf(Challenge.class);
        for (Object object3 : this.yaml.getStringList(string + ".completed")) {
            object22 = Challenge.byId((String)object3);
            if (object22 == null) continue;
            enumSet.add((Challenge)((Object)object22));
        }
        EnumSet<Challenge> enumSet2 = EnumSet.noneOf(Challenge.class);
        for (Object object22 : this.yaml.getStringList(string + ".unlocked")) {
            object = Challenge.byId((String)object22);
            if (object == null) continue;
            enumSet2.add((Challenge)((Object)object));
        }
        int n = this.yaml.getInt(string + ".wins", 0);
        object22 = EnumSet.noneOf(Challenge.class);
        for (Object object4 : this.yaml.getStringList(string + ".claimed")) {
            Challenge challenge2 = Challenge.byId(String.valueOf(object4));
            if (challenge2 == null) continue;
            ((AbstractCollection)object22).add(challenge2);
        }
        object = new LinkedHashSet();
        for (Challenge challenge2 : this.yaml.getStringList(string + ".permissions")) {
            if (challenge2 == null || String.valueOf((Object)challenge2).trim().isEmpty()) continue;
            ((HashSet)object).add(String.valueOf((Object)challenge2).trim());
        }
        playerProfile2.applyLoaded(challenge, enumSet, (Set<Challenge>)enumSet2, n, (Set<Challenge>)object22, (Set<String>)object);
        this.cache.put(uUID, playerProfile2);
        return playerProfile2;
    }

    @Override
    public void save(PlayerProfile playerProfile) {
        this.cache.put(playerProfile.uuid(), playerProfile);
        this.write(playerProfile);
        this.pendingFlush = true;
        playerProfile.markClean();
    }

    @Override
    public void saveAsync(PlayerProfile playerProfile) {
        this.save(playerProfile);
    }

    private void write(PlayerProfile playerProfile) {
        String string = playerProfile.uuid().toString();
        this.yaml.set(string + ".active", (Object)(playerProfile.active() == null ? "none" : playerProfile.active().id()));
        if (playerProfile.name() != null) {
            this.yaml.set(string + ".name", (Object)playerProfile.name());
        }
        ArrayList<String> arrayList = new ArrayList<String>();
        for (Challenge object2 : playerProfile.completed()) {
            arrayList.add(object2.id());
        }
        this.yaml.set(string + ".completed", arrayList);
        ArrayList arrayList2 = new ArrayList();
        for (Challenge challenge : playerProfile.unlocked()) {
            arrayList2.add(challenge.id());
        }
        this.yaml.set(string + ".unlocked", (Object)arrayList2);
        this.yaml.set(string + ".wins", (Object)playerProfile.wins());
        ArrayList<String> arrayList3 = new ArrayList<String>();
        for (Challenge challenge : playerProfile.claimed()) {
            arrayList3.add(challenge.id());
        }
        this.yaml.set(string + ".claimed", arrayList3);
        this.yaml.set(string + ".permissions", new ArrayList<String>(playerProfile.grantedPermissions()));
    }

    private synchronized void flush() {
        try {
            this.yaml.save(this.file);
        }
        catch (IOException iOException) {
            this.plugin.getLogger().warning("Could not save players.yml: " + iOException.getMessage());
        }
    }

    @Override
    public void close() {
        if (this.flushTask != null) {
            this.flushTask.cancel();
        }
        for (PlayerProfile playerProfile : this.cache.values()) {
            this.write(playerProfile);
        }
        this.flush();
    }

    @Override
    public String kind() {
        return "YAML";
    }
}

