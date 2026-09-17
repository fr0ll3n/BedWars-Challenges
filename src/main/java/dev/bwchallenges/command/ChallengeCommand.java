/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.entity.Player
 */
package dev.bwchallenges.command;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.ChallengesPlugin;
import dev.bwchallenges.PlayerProfile;
import dev.bwchallenges.util.Sounds;
import dev.bwchallenges.util.Text;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class ChallengeCommand
implements CommandExecutor,
TabCompleter {
    private final ChallengesPlugin plugin;

    public ChallengeCommand(ChallengesPlugin challengesPlugin) {
        this.plugin = challengesPlugin;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] stringArray) {
        if (stringArray.length >= 1 && stringArray[0].equalsIgnoreCase("open") && stringArray.length >= 2) {
            Player player = Bukkit.getPlayerExact((String)stringArray[1]);
            if (player == null) {
                Text.send(commandSender, "&cPlayer not found.");
                return true;
            }
            if (stringArray.length >= 3) {
                this.plugin.setPlayerMode(player, ChallengeCommand.normalizeMode(stringArray[2]));
            }
            this.openMenu(player);
            Text.send(commandSender, "&aOpened the challenges menu for &f" + player.getName());
            return true;
        }
        if (!(commandSender instanceof Player)) {
            Text.send(commandSender, "&cPlayers only. Console can use /challenge open <player> [mode]");
            return true;
        }
        Player player = (Player)commandSender;
        if (!player.hasPermission("bwchallenges.use") && !player.isOp()) {
            Text.send((CommandSender)player, "&cNo permission.");
            return true;
        }
        if (stringArray.length == 0) {
            this.openMenu(player);
            return true;
        }
        String string2 = stringArray[0].toLowerCase(Locale.ROOT);
        String string3 = ChallengeCommand.normalizeMode(string2);
        if (string3 != null) {
            this.plugin.setPlayerMode(player, string3);
            this.openMenu(player);
            return true;
        }
        if (string2.equals("help")) {
            Text.send((CommandSender)player, Arrays.asList("&8&m------------------------------", "&e/" + string + " &7- Open the challenges menu", "&e/" + string + " menu &7- Same as above", "&e/" + string + " solo &7- Open menu; Go Back runs the Solo command", "&e/" + string + " doubles &7- Open menu; Go Back runs the Doubles command", "&e/" + string + " 3v3v3v3 &7- Open menu; Go Back runs the 3v3v3v3 command", "&e/" + string + " 4v4v4v4 &7- Open menu; Go Back runs the 4v4v4v4 command", "&e/" + string + " list &7- List all challenges", "&e/" + string + " select <id> &7- Activate a challenge", "&e/" + string + " disable &7- Clear the active challenge", "&e/" + string + " info &7- Show your active challenge", "&e/" + string + " open <player> [mode] &7- Open menu for a player", "&e/" + string + " reload &7- Reload config (admin)", "&8&m------------------------------"));
            return true;
        }
        if (string2.equals("menu") || string2.equals("open") || string2.equals("gui")) {
            this.openMenu(player);
            return true;
        }
        if (string2.equals("disable") || string2.equals("off") || string2.equals("none") || string2.equals("clear")) {
            this.plugin.manager().clearActive(player);
            Text.send((CommandSender)player, this.plugin.msg("challenge.messages.menu.deactivated", "&cDeactivated Challenge: &6{challenge}").replace("{challenge}", "NONE"));
            Sounds.deactivate(player);
            return true;
        }
        if (string2.equals("info") || string2.equals("status")) {
            Challenge challenge = this.plugin.manager().activeOf(player);
            PlayerProfile playerProfile = this.plugin.manager().profile(player);
            Text.send((CommandSender)player, "&6Active: &f" + (challenge == null ? "NONE" : challenge.displayName()));
            Text.send((CommandSender)player, "&6Completed: &f" + playerProfile.completedCount() + "&7/&f" + Challenge.values().length);
            Text.send((CommandSender)player, "&6Storage: &f" + this.plugin.manager().storage().kind());
            String string4 = this.plugin.getPlayerMode(player);
            if (string4 != null) {
                Text.send((CommandSender)player, "&6Play mode: &f" + string4);
            }
            return true;
        }
        if (string2.equals("list")) {
            PlayerProfile playerProfile = this.plugin.manager().profile(player);
            for (Challenge challenge : Challenge.values()) {
                if (!challenge.selectable()) continue;
                Text.send((CommandSender)player, (playerProfile.isCompleted(challenge) ? "&a\u2714" : (playerProfile.isUnlocked(challenge) ? "&e\u25cb" : "&8\u2716")) + " &f" + challenge.displayName() + " &8(" + challenge.id() + ")" + (playerProfile.active() == challenge ? " &a(active)" : ""));
            }
            return true;
        }
        if (string2.equals("select") || string2.equals("set") || string2.equals("activate")) {
            if (stringArray.length < 2) {
                Text.send((CommandSender)player, "&cUsage: /" + string + " select <challenge>");
                return true;
            }
            return this.select(player, stringArray[1]);
        }
        if (string2.equals("reload")) {
            if (!player.hasPermission("bwchallenges.admin")) {
                Text.send((CommandSender)player, "&cNo permission.");
                return true;
            }
            this.plugin.reloadConfig();
            if (this.plugin.rewards() != null) {
                this.plugin.rewards().reload();
            }
            Text.send((CommandSender)player, "&aReloaded config.yml and rewards.yml. Storage is still " + this.plugin.manager().storage().kind() + " until the next restart.");
            return true;
        }
        if (Challenge.byId(string2) != null) {
            return this.select(player, string2);
        }
        this.openMenu(player);
        return true;
    }

    static String normalizeMode(String string) {
        if (string == null) {
            return null;
        }
        String string2 = string.toLowerCase(Locale.ROOT).trim();
        if (string2.equals("solo") || string2.equals("1v1") || string2.equals("solos")) {
            return "solo";
        }
        if (string2.equals("doubles") || string2.equals("2v2") || string2.equals("double")) {
            return "doubles";
        }
        if (string2.equals("triples") || string2.equals("3v3") || string2.equals("3v3v3v3") || string2.equals("triple")) {
            return "triples";
        }
        if (string2.equals("fours") || string2.equals("4v4") || string2.equals("4v4v4v4") || string2.equals("quads") || string2.equals("quad")) {
            return "fours";
        }
        return null;
    }

    private boolean select(Player player, String string) {
        Challenge challenge = Challenge.byId(string);
        if (challenge == null) {
            Text.send((CommandSender)player, "&cUnknown challenge.");
            return true;
        }
        if (!this.plugin.manager().profile(player).isUnlocked(challenge)) {
            Text.send((CommandSender)player, this.plugin.msg("challenge.messages.menu.not-unlocked", "&cYou have not unlocked this challenge yet!"));
            return true;
        }
        this.plugin.manager().setActive(player, challenge);
        Text.send((CommandSender)player, this.plugin.msg("challenge.messages.menu.activated", "&aActivated Challenge: &6{challenge}").replace("{challenge}", challenge.displayName()));
        Sounds.activate(player);
        return true;
    }

    private void openMenu(Player player) {
        try {
            if (this.plugin.hook() != null && this.plugin.hook().isPlaying(player)) {
                Text.send((CommandSender)player, this.plugin.msg("challenge.messages.menu.arena-open", "&cYou can only open the menu in the lobby!"));
                return;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            this.plugin.menu().open(player);
        }
        catch (Throwable throwable) {
            this.plugin.logSafe("Failed to open challenge menu", throwable);
            Text.send((CommandSender)player, "&cCould not open the challenges menu. Check console.");
        }
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String string, String[] stringArray) {
        ArrayList<String> arrayList;
        block5: {
            block6: {
                block4: {
                    arrayList = new ArrayList<String>();
                    if (stringArray.length != 1) break block4;
                    String string2 = stringArray[0].toLowerCase(Locale.ROOT);
                    for (String string3 : Arrays.asList("menu", "solo", "doubles", "3v3v3v3", "4v4v4v4", "triples", "fours", "list", "select", "disable", "info", "help", "reload")) {
                        if (!string3.startsWith(string2)) continue;
                        arrayList.add(string3);
                    }
                    for (Challenge challenge : Challenge.values()) {
                        if (!challenge.id().startsWith(string2)) continue;
                        arrayList.add(challenge.id());
                    }
                    break block5;
                }
                if (stringArray.length != 2 || !stringArray[0].equalsIgnoreCase("select")) break block6;
                String string4 = stringArray[1].toLowerCase(Locale.ROOT);
                for (Challenge challenge : Challenge.values()) {
                    if (!challenge.id().startsWith(string4)) continue;
                    arrayList.add(challenge.id());
                }
                break block5;
            }
            if (stringArray.length != 3 || !stringArray[0].equalsIgnoreCase("open")) break block5;
            String string5 = stringArray[2].toLowerCase(Locale.ROOT);
            for (String string6 : Arrays.asList("solo", "doubles", "3v3v3v3", "4v4v4v4")) {
                if (!string6.startsWith(string5)) continue;
                arrayList.add(string6);
            }
        }
        return arrayList;
    }
}

