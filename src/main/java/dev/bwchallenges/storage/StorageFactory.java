/*
 * Decompiled with CFR 0.152.
 */
package dev.bwchallenges.storage;

import dev.bwchallenges.ChallengesPlugin;
import dev.bwchallenges.storage.MySqlStorage;
import dev.bwchallenges.storage.Storage;
import dev.bwchallenges.storage.YamlStorage;

public final class StorageFactory {
    private StorageFactory() {
    }

    public static Storage create(ChallengesPlugin challengesPlugin) {
        block3: {
            if (challengesPlugin.getConfig().getBoolean("database.enabled", false)) {
                try {
                    MySqlStorage mySqlStorage = new MySqlStorage(challengesPlugin);
                    challengesPlugin.getLogger().info("Storage: MySQL (shared lobby + arena). Host=" + challengesPlugin.getConfig().getString("database.host") + " db=" + challengesPlugin.getConfig().getString("database.name"));
                    return mySqlStorage;
                }
                catch (Exception exception) {
                    challengesPlugin.getLogger().severe("MySQL storage failed, falling back to YAML: " + exception.getMessage());
                    if (!challengesPlugin.getConfig().getBoolean("debug", false)) break block3;
                    exception.printStackTrace();
                }
            }
        }
        challengesPlugin.getLogger().info("Storage: YAML (per-server). Enable database.enabled for lobby/arena sync.");
        return new YamlStorage(challengesPlugin);
    }
}

