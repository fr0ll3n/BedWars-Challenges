/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 */
package dev.bwchallenges.mechanic.impl;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.engine.ChallengeEngine;
import dev.bwchallenges.engine.MatchSession;
import dev.bwchallenges.mechanic.ChallengeHandler;
import dev.bwchallenges.util.XMat;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public final class CappedResourcesHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.CAPPED_RESOURCES;
    }

    @Override
    public boolean blockShopBuy(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, String string, String string2, Material material, String string3) {
        int n;
        String string4 = string == null || string.isEmpty() ? challengeEngine.shopBlob(string, string2, material, string3) : string.toLowerCase();
        int n2 = n = matchSession.purchaseCounts.containsKey(string4) ? matchSession.purchaseCounts.get(string4) : 0;
        if (n >= 20) {
            challengeEngine.deny(player, matchSession, "&cYou hit the limit of purchases!");
            return true;
        }
        matchSession.purchaseCounts.put(string4, n + 1);
        return false;
    }

    @Override
    public boolean blockGenerator(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Material material, Item item) {
        if (!challengeEngine.onOwnIsland(player, matchSession) && XMat.isResource(material)) {
            challengeEngine.deny(player, matchSession, "&cYou can only collect from your own generator.");
            challengeEngine.softBlockItem(item);
            return true;
        }
        return false;
    }

    @Override
    public boolean blockPickup(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Material material, boolean bl) {
        if (bl) {
            challengeEngine.deny(player, matchSession, "&cYou cannot pick up items from other players.");
            return true;
        }
        if (!challengeEngine.onOwnIsland(player, matchSession) && XMat.isResource(material)) {
            challengeEngine.deny(player, matchSession, "&cYou can only collect from your own generator.");
            return true;
        }
        return false;
    }
}

