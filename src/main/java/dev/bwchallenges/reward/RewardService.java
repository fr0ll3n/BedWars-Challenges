/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.permissions.PermissionAttachment
 *  org.bukkit.plugin.Plugin
 */
package dev.bwchallenges.reward;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.ChallengesPlugin;
import dev.bwchallenges.PlayerProfile;
import dev.bwchallenges.util.Sounds;
import dev.bwchallenges.util.Text;
import dev.bwchallenges.util.XMat;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

public final class RewardService {
    private final ChallengesPlugin plugin;
    private final Map<UUID, PermissionAttachment> attachments = new ConcurrentHashMap<UUID, PermissionAttachment>();
    private File file;
    private FileConfiguration yaml;

    public RewardService(ChallengesPlugin challengesPlugin) {
        this.plugin = challengesPlugin;
        this.load();
    }

    public void load() {
        if (!this.plugin.getDataFolder().exists()) {
            this.plugin.getDataFolder().mkdirs();
        }
        this.file = new File(this.plugin.getDataFolder(), "rewards.yml");
        if (!this.file.exists()) {
            try {
                this.plugin.saveResource("rewards.yml", false);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        this.yaml = YamlConfiguration.loadConfiguration((File)this.file);
        boolean bl = false;
        if (!this.yaml.isSet("auto-permission")) {
            this.yaml.set("auto-permission", (Object)true);
            bl = true;
        }
        if (!this.yaml.isSet("claim-item")) {
            this.yaml.set("claim-item", (Object)"GOLD_INGOT");
            bl = true;
        }
        for (Challenge challenge : Challenge.values()) {
            Object object;
            String string = "challenges." + challenge.id();
            if (!this.yaml.isConfigurationSection(string)) {
                this.yaml.createSection(string);
                bl = true;
            }
            if (!this.yaml.isSet(string + ".enabled")) {
                this.yaml.set(string + ".enabled", (Object)true);
                bl = true;
            }
            if (!this.yaml.isSet(string + ".display")) {
                object = new ArrayList<String>();
                String[] stringArray = challenge.rewards();
                if (stringArray != null) {
                    for (String string2 : stringArray) {
                        ((ArrayList)object).add(string2);
                    }
                }
                if (((ArrayList)object).isEmpty()) {
                    ((ArrayList)object).add(challenge.displayName() + " Reward");
                }
                this.yaml.set(string + ".display", object);
                bl = true;
            }
            if (!this.yaml.isSet(string + ".permissions")) {
                object = new ArrayList();
                ((ArrayList)object).add("bwchallenges.reward." + challenge.id());
                this.yaml.set(string + ".permissions", object);
                bl = true;
            }
            if (!this.yaml.isSet(string + ".commands")) {
                this.yaml.set(string + ".commands", new ArrayList());
                bl = true;
            }
            if (!this.yaml.isSet(string + ".items")) {
                this.yaml.set(string + ".items", new ArrayList());
                bl = true;
            }
            if (this.yaml.isSet(string + ".message")) continue;
            object = this.firstDisplay(challenge);
            this.yaml.set(string + ".message", (Object)("&aYou claimed the " + (String)object + " reward!"));
            bl = true;
        }
        if (bl) {
            this.saveFile();
        }
        this.plugin.getLogger().info("Loaded customizable rewards for " + Challenge.values().length + " challenges from rewards.yml");
    }

    public void reload() {
        this.load();
    }

    public List<String> displayOf(Challenge challenge) {
        List<String> list = this.stringList(challenge, "display");
        if (!list.isEmpty()) {
            return list;
        }
        String[] stringArray = challenge.rewards();
        ArrayList<String> arrayList = new ArrayList<String>();
        if (stringArray != null) {
            for (String string : stringArray) {
                arrayList.add(string);
            }
        }
        return arrayList;
    }

    public List<String> permissionsOf(Challenge challenge) {
        ArrayList<String> arrayList = new ArrayList<String>();
        Object object = this.stringList(challenge, "permissions").iterator();
        while (object.hasNext()) {
            String string = object.next();
            if (string == null || string.trim().isEmpty()) continue;
            arrayList.add(string.trim());
        }
        if (this.yaml.getBoolean("auto-permission", true) && !arrayList.contains(object = "bwchallenges.reward." + challenge.id())) {
            arrayList.add((String)object);
        }
        return arrayList;
    }

    public List<String> commandsOf(Challenge challenge) {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (String string : this.stringList(challenge, "commands")) {
            if (string == null || string.trim().isEmpty()) continue;
            arrayList.add(string.trim());
        }
        return arrayList;
    }

    public List<String> itemsOf(Challenge challenge) {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (String string : this.stringList(challenge, "items")) {
            if (string == null || string.trim().isEmpty()) continue;
            arrayList.add(string.trim());
        }
        return arrayList;
    }

    public String messageOf(Challenge challenge) {
        String string = this.getString(challenge, "message", "");
        if (string != null && !string.isEmpty()) {
            return string;
        }
        return this.plugin.getConfig().getString(this.legacyPath(challenge) + ".message", "");
    }

    public String claimItem() {
        return this.yaml.getString("claim-item", "GOLD_INGOT");
    }

    public boolean claim(Player player, Challenge challenge) {
        String string;
        String string22;
        if (player == null || challenge == null) {
            return false;
        }
        PlayerProfile playerProfile = this.plugin.manager().profile(player);
        if (!playerProfile.isCompleted(challenge)) {
            Text.send((CommandSender)player, this.plugin.msg("challenge.messages.menu.reward-not-completed", "&cComplete the challenge first!"));
            return false;
        }
        if (playerProfile.isClaimed(challenge)) {
            Text.send((CommandSender)player, this.plugin.msg("challenge.messages.menu.reward-already-claimed", "&cYou already claimed this reward."));
            return false;
        }
        if (!playerProfile.claim(challenge)) {
            return false;
        }
        for (String string22 : this.permissionsOf(challenge)) {
            playerProfile.grantPermission(string22);
        }
        this.apply(player);
        this.giveItems(player, challenge);
        for (String string22 : this.commandsOf(challenge)) {
            string = string22.replace("{player}", player.getName()).replace("{uuid}", player.getUniqueId().toString()).replace("{challenge}", challenge.id()).replace("{challenge_name}", challenge.displayName());
            try {
                Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), (String)string);
            }
            catch (Throwable throwable) {
                this.plugin.logSafe("Reward command failed: " + string, throwable);
            }
        }
        this.plugin.manager().storage().saveAsync(playerProfile);
        String string3 = this.firstDisplay(challenge);
        string22 = this.plugin.msg("challenge.messages.menu.reward-claimed", "&aClaimed &6{challenge} &areward: &e{reward}").replace("{challenge}", challenge.displayName()).replace("{reward}", string3);
        Text.send((CommandSender)player, string22);
        string = this.messageOf(challenge);
        if (string != null && !string.trim().isEmpty()) {
            Text.send((CommandSender)player, string.replace("{player}", player.getName()).replace("{challenge}", challenge.displayName()).replace("{reward}", string3));
        }
        Sounds.activate(player);
        return true;
    }

    public void apply(Player player) {
        if (player == null) {
            return;
        }
        try {
            PlayerProfile playerProfile = this.plugin.manager().profile(player);
            PermissionAttachment permissionAttachment = this.attachments.get(player.getUniqueId());
            if (permissionAttachment != null) {
                try {
                    player.removeAttachment(permissionAttachment);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            permissionAttachment = player.addAttachment((Plugin)this.plugin);
            this.attachments.put(player.getUniqueId(), permissionAttachment);
            for (String object : playerProfile.grantedPermissions()) {
                permissionAttachment.setPermission(object, true);
            }
            for (Challenge challenge : playerProfile.claimed()) {
                for (String string : this.permissionsOf(challenge)) {
                    permissionAttachment.setPermission(string, true);
                }
            }
        }
        catch (Throwable throwable) {
            this.plugin.logSafe("Could not apply challenge reward permissions", throwable);
        }
    }

    public void clear(UUID uUID) {
        this.attachments.remove(uUID);
    }

    private void giveItems(Player player, Challenge challenge) {
        for (String string : this.itemsOf(challenge)) {
            try {
                String[] stringArray = string.trim().split("[:\\s]+");
                String string2 = stringArray[0];
                int n = 1;
                if (stringArray.length > 1) {
                    try {
                        n = Integer.parseInt(stringArray[1].replaceAll("[^0-9]", ""));
                    }
                    catch (Throwable throwable) {
                        n = 1;
                    }
                }
                if (n < 1) {
                    n = 1;
                }
                ItemStack itemStack = XMat.stack(string2, "STONE");
                itemStack.setAmount(Math.min(64, n));
                player.getInventory().addItem(new ItemStack[]{itemStack});
            }
            catch (Throwable throwable) {
                this.plugin.logSafe("Invalid reward item '" + string + "' for " + challenge.id(), throwable);
            }
        }
    }

    private String firstDisplay(Challenge challenge) {
        List<String> list = this.displayOf(challenge);
        if (list == null || list.isEmpty()) {
            return challenge.displayName();
        }
        return list.get(0);
    }

    private List<String> stringList(Challenge challenge, String string) {
        List list;
        ArrayList<String> arrayList = new ArrayList<String>();
        if (this.yaml != null && (list = this.yaml.getStringList("challenges." + challenge.id() + "." + string)) != null) {
            for (Object e : list) {
                if (e == null) continue;
                arrayList.add(String.valueOf(e));
            }
        }
        if (arrayList.isEmpty() && (list = this.plugin.getConfig().getStringList(this.legacyPath(challenge) + "." + string)) != null) {
            for (Object e : list) {
                if (e == null) continue;
                arrayList.add(String.valueOf(e));
            }
        }
        return arrayList;
    }

    private String getString(Challenge challenge, String string, String string2) {
        String string3;
        if (this.yaml != null && (string3 = this.yaml.getString("challenges." + challenge.id() + "." + string, null)) != null) {
            return string3;
        }
        return this.plugin.getConfig().getString(this.legacyPath(challenge) + "." + string, string2);
    }

    private String legacyPath(Challenge challenge) {
        return "challenge.rewards.entries." + challenge.id();
    }

    private void saveFile() {
        try {
            this.yaml.save(this.file);
        }
        catch (IOException iOException) {
            this.plugin.getLogger().warning("Could not save rewards.yml: " + iOException.getMessage());
        }
    }
}

