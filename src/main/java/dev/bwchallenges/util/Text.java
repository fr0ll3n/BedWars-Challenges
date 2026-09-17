/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package dev.bwchallenges.util;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class Text {
    private Text() {
    }

    public static String color(String string) {
        if (string == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes((char)'&', (String)string);
    }

    public static List<String> color(List<String> list) {
        ArrayList<String> arrayList = new ArrayList<String>(list.size());
        for (String string : list) {
            arrayList.add(Text.color(string));
        }
        return arrayList;
    }

    public static void send(CommandSender commandSender, String string) {
        if (string == null || string.isEmpty()) {
            return;
        }
        commandSender.sendMessage(Text.color(string));
    }

    public static void send(CommandSender commandSender, List<String> list) {
        for (String string : list) {
            Text.send(commandSender, string);
        }
    }

    public static String strip(String string) {
        return ChatColor.stripColor((String)Text.color(string));
    }
}

