/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 */
package dev.bwchallenges.gui;

import dev.bwchallenges.ChallengesPlugin;
import dev.bwchallenges.engine.MatchSession;
import dev.bwchallenges.gui.DefuserHolder;
import dev.bwchallenges.util.Text;
import dev.bwchallenges.util.XMat;
import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public final class DefuserMenu {
    private final ChallengesPlugin plugin;

    public DefuserMenu(ChallengesPlugin challengesPlugin) {
        this.plugin = challengesPlugin;
    }

    public void open(Player player, String string) {
        int n;
        Object object;
        DefuserHolder defuserHolder = new DefuserHolder(player.getUniqueId(), string);
        Inventory inventory = Bukkit.createInventory((InventoryHolder)defuserHolder, (int)27, (String)Text.color("&cDefuse the Bed"));
        defuserHolder.setInventory(inventory);
        ItemStack itemStack = this.pane("LIME_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", (short)5, "&aCut this wire");
        ItemStack itemStack2 = this.pane("RED_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", (short)14, "&cWrong wire");
        ItemStack itemStack3 = XMat.stack("SHEARS", "IRON_INGOT");
        try {
            object = itemStack3.getItemMeta();
            if (object != null) {
                object.setDisplayName(Text.color("&eDefuser"));
                ArrayList<String> arrayList = new ArrayList<String>();
                arrayList.add(Text.color("&7Click the &agreen wires &7in any order."));
                arrayList.add(Text.color("&7There are &f3 &7correct wires."));
                arrayList.add(Text.color("&cWrong wire resets the defuse."));
                object.setLore(arrayList);
                itemStack3.setItemMeta((ItemMeta)object);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        inventory.setItem(4, itemStack3);
        object = new ArrayList();
        for (n = 9; n <= 17; ++n) {
            ((ArrayList)object).add(n);
        }
        Collections.shuffle(object);
        inventory.setItem(((Integer)((ArrayList)object).get(0)).intValue(), itemStack);
        inventory.setItem(((Integer)((ArrayList)object).get(1)).intValue(), itemStack.clone());
        inventory.setItem(((Integer)((ArrayList)object).get(2)).intValue(), itemStack.clone());
        for (n = 3; n < ((ArrayList)object).size(); ++n) {
            inventory.setItem(((Integer)((ArrayList)object).get(n)).intValue(), itemStack2.clone());
        }
        player.openInventory(inventory);
    }

    public void click(Player player, DefuserHolder defuserHolder, ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }
        String string = "";
        try {
            if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
                string = Text.strip(itemStack.getItemMeta().getDisplayName()).toLowerCase();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (string.contains("wrong")) {
            defuserHolder.progress = 0;
            Text.send((CommandSender)player, "&cWrong wire! Defuse reset.");
            this.open(player, defuserHolder.teamKey());
            return;
        }
        if (!string.contains("cut this")) {
            return;
        }
        ++defuserHolder.progress;
        Text.send((CommandSender)player, "&aWire cut &f" + defuserHolder.progress + "&7/3");
        if (defuserHolder.progress < 3) {
            return;
        }
        final MatchSession matchSession = this.plugin.engine().session(player);
        if (matchSession == null) {
            player.closeInventory();
            return;
        }
        final String string2 = defuserHolder.teamKey();
        matchSession.defusedBeds.add(string2);
        player.closeInventory();
        Text.send((CommandSender)player, "&aBed defused! &7You have 2 minutes to break it.");
        this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, new Runnable(){

            @Override
            public void run() {
                MatchSession matchSession2 = DefuserMenu.this.plugin.engine().session(matchSession.playerId);
                if (matchSession2 != null) {
                    matchSession2.defusedBeds.remove(string2);
                }
            }
        }, 2400L);
    }

    private ItemStack pane(String string, String string2, short s, String string3) {
        ItemStack itemStack = XMat.stack(string, string2, "THIN_GLASS");
        try {
            itemStack.setDurability(s);
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setDisplayName(Text.color(string3));
                itemStack.setItemMeta(itemMeta);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return itemStack;
    }
}

