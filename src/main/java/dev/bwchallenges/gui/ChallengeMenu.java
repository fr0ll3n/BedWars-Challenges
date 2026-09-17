/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package dev.bwchallenges.gui;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.ChallengesPlugin;
import dev.bwchallenges.PlayerProfile;
import dev.bwchallenges.gui.Items;
import dev.bwchallenges.gui.MenuHolder;
import dev.bwchallenges.util.Text;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public final class ChallengeMenu {
    public static final int SIZE = 54;
    public static final int SLOT_PREV = 45;
    public static final int SLOT_DISABLE = 48;
    public static final int SLOT_BACK = 49;
    public static final int SLOT_INFO = 50;
    public static final int SLOT_NEXT = 53;
    public static final int[] CHALLENGE_SLOTS = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
    public static final int PER_PAGE = CHALLENGE_SLOTS.length;
    private final ChallengesPlugin plugin;
    private final Map<UUID, Integer> openPages = new ConcurrentHashMap<UUID, Integer>();

    public ChallengeMenu(ChallengesPlugin challengesPlugin) {
        this.plugin = challengesPlugin;
    }

    public void open(Player player) {
        Integer n = this.openPages.get(player.getUniqueId());
        this.open(player, n == null ? 0 : n);
    }

    public void open(Player player, int n) {
        Object object2;
        ArrayList<Challenge> arrayList = new ArrayList<Challenge>();
        for (Object object2 : Challenge.values()) {
            if (!object2.selectable()) continue;
            arrayList.add((Challenge)((Object)object2));
        }
        Challenge[] challengeArray = arrayList.toArray(new Challenge[0]);
        int n2 = Math.max(1, (int)Math.ceil((double)challengeArray.length / (double)PER_PAGE));
        if (n < 0) {
            n = 0;
        }
        if (n >= n2) {
            n = n2 - 1;
        }
        PlayerProfile playerProfile = this.plugin.manager().profile(player);
        object2 = Text.color(this.plugin.msg("challenge.menu.title", "&8Bed Wars Challenges"));
        if (((String)object2).length() > 32) {
            object2 = ((String)object2).substring(0, 32);
        }
        String string = this.plugin.getPlayerMode(player);
        MenuHolder menuHolder = new MenuHolder(player.getUniqueId(), n, string);
        Inventory inventory = Bukkit.createInventory((InventoryHolder)menuHolder, (int)54, (String)object2);
        menuHolder.setInventory(inventory);
        int n3 = n * PER_PAGE;
        for (int i = 0; i < PER_PAGE; ++i) {
            int n4 = n3 + i;
            if (n4 >= challengeArray.length) continue;
            inventory.setItem(CHALLENGE_SLOTS[i], this.challengeIcon(challengeArray[n4], playerProfile));
        }
        Challenge challenge = playerProfile.active();
        String string2 = challenge == null ? this.plugin.msg("challenge.menu.item.none.name", "NONE") : challenge.displayName();
        boolean bl = challenge != null;
        inventory.setItem(48, Items.of("REDSTONE_BLOCK", (bl ? "&a" : "&c") + this.plugin.msg("challenge.menu.item.redstone.name", "Disable Active Challenge"), Items.lore("", "&6Currently Active: &a" + string2, "", bl ? this.plugin.msg("challenge.menu.item.redstone.state.active", "&eClick to Disable!") : this.plugin.msg("challenge.menu.item.redstone.state.deactivate", "&cNo Active Challenge."))));
        inventory.setItem(49, Items.of(this.plugin.msg("challenge.menu.go-back.material", "ARROW"), this.plugin.msg("challenge.menu.go-back.name", "&aGo Back"), Items.lore(this.plugin.msg("challenge.menu.go-back.lore", "&7To Play Bed Wars"))));
        inventory.setItem(50, Items.of("REDSTONE_TORCH", this.plugin.msg("challenge.menu.item.information.name", "&aChallenge Information"), this.infoLore(playerProfile, string2, challengeArray.length)));
        if (n > 0) {
            inventory.setItem(45, Items.of("ARROW", "&eLeft-click for previous page!", Items.lore("&bRight-click for first page!")));
        }
        if (n < n2 - 1) {
            inventory.setItem(53, Items.of("ARROW", "&eLeft-click for next page!", Items.lore("&bRight-click for last page!")));
        }
        this.openPages.put(player.getUniqueId(), n);
        player.openInventory(inventory);
    }

    private List<String> infoLore(PlayerProfile playerProfile, String string, int n) {
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("&7Challenges are a unique way to increase");
        arrayList.add("&7the difficulty of the game for you and your");
        arrayList.add("&7team. To unlock additional challenges, you");
        arrayList.add("&7must first complete currently unlocked");
        arrayList.add("&7challenges.");
        arrayList.add("&7Good Luck!");
        arrayList.add("");
        arrayList.add("&6Active Challenge: &a" + string);
        arrayList.add("");
        arrayList.add("&bTotal Challenge Wins: &a" + playerProfile.wins());
        arrayList.add("&bChallenges Completed: &a" + playerProfile.completedCount() + "&7/&e" + n);
        return arrayList;
    }

    public Integer pageOf(Player player) {
        return this.openPages.get(player.getUniqueId());
    }

    public boolean isViewing(Player player) {
        return this.openPages.containsKey(player.getUniqueId());
    }

    public void closed(Player player) {
        this.openPages.remove(player.getUniqueId());
    }

    public int indexOfSlot(int n) {
        for (int i = 0; i < CHALLENGE_SLOTS.length; ++i) {
            if (CHALLENGE_SLOTS[i] != n) continue;
            return i;
        }
        return -1;
    }

    public int maxPages() {
        int n = 0;
        for (Challenge challenge : Challenge.values()) {
            if (!challenge.selectable()) continue;
            ++n;
        }
        return Math.max(1, (int)Math.ceil((double)n / (double)PER_PAGE));
    }

    private ItemStack challengeIcon(Challenge challenge, PlayerProfile playerProfile) {
        if (!playerProfile.isUnlocked(challenge)) {
            return Items.lockedPane(this.plugin.msg("challenge.menu.item.locked.name", "&7&o????"), this.plugin.msg("challenge.menu.item.locked.lore", "&cChallenge Locked!"));
        }
        boolean bl = playerProfile.isCompleted(challenge);
        boolean bl2 = playerProfile.isClaimed(challenge);
        boolean bl3 = bl && !bl2;
        boolean bl4 = playerProfile.active() == challenge;
        String string = "&a" + challenge.displayName();
        boolean bl5 = bl4 || bl || bl3;
        ArrayList<String> arrayList = new ArrayList<String>();
        List<String> list = this.plugin.rewards() == null ? Arrays.asList(challenge.rewards()) : this.plugin.rewards().displayOf(challenge);
        arrayList.add(this.plugin.msg("challenge.menu.item.category", "&bChallenge Rules:"));
        int n = 1;
        for (String string2 : challenge.rules()) {
            arrayList.add("&6" + n + ". &7" + string2);
            ++n;
        }
        arrayList.add("");
        if (bl3) {
            arrayList.add("&8Wins with Challenge: &e1");
            arrayList.add("");
            arrayList.add(this.plugin.msg("challenge.menu.item.status.claim", "&eClick to claim reward!"));
            if (list != null) {
                for (String string3 : list) {
                    arrayList.add("&7- " + string3);
                }
            }
            return Items.of(challenge.material(), string, arrayList, bl5);
        }
        if (bl && bl2) {
            arrayList.add("&8Wins with Challenge: &e1");
            arrayList.add("");
            arrayList.add(this.plugin.msg("challenge.menu.item.status.rewards_claimed", "&aRewards Claimed"));
            arrayList.add("");
            arrayList.add(bl4 ? this.plugin.msg("challenge.menu.item.status.deactivated", "&cClick to Disable!") : this.plugin.msg("challenge.menu.item.status.activated", "&eClick to Activate!"));
            return Items.of(challenge.material(), string, arrayList, bl5);
        }
        arrayList.add("&cYou can disable Challenges from this");
        arrayList.add("&cmenu using the redstone on this page.");
        if (list != null && !list.isEmpty()) {
            arrayList.add("");
            arrayList.add("&6Reward:");
            for (String string4 : list) {
                arrayList.add("&7- " + string4);
            }
        }
        arrayList.add("");
        arrayList.add(bl4 ? this.plugin.msg("challenge.menu.item.status.deactivated", "&cClick to Disable!") : this.plugin.msg("challenge.menu.item.status.activated", "&eClick to Activate!"));
        return Items.of(challenge.material(), string, arrayList, bl5);
    }
}

