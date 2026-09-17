/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package dev.bwchallenges.mechanic.impl;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.engine.ChallengeEngine;
import dev.bwchallenges.engine.MatchSession;
import dev.bwchallenges.mechanic.ChallengeHandler;
import dev.bwchallenges.util.XMat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class SwordlessHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.SWORDLESS;
    }

    @Override
    public boolean blockShopBuy(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, String string, String string2, Material material, String string3) {
        if (challengeEngine.looksLikeSword(challengeEngine.shopBlob(string, string2, material, string3), material)) {
            challengeEngine.deny(player, matchSession, "&cYou can't buy swords on this challenge");
            return true;
        }
        return false;
    }

    @Override
    public boolean blockPickup(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Material material, boolean bl) {
        return XMat.isSword(material);
    }

    @Override
    public boolean blockMelee(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Player player2, ItemStack itemStack) {
        Material material;
        Material material2 = material = itemStack == null ? Material.AIR : itemStack.getType();
        if (XMat.isSword(material)) {
            challengeEngine.deny(player, matchSession, "&cSwordless: no swords.");
            return true;
        }
        return false;
    }

    @Override
    public double modifyDamage(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, double d, boolean bl) {
        ItemStack itemStack = XMat.handOf(player);
        if (itemStack != null && XMat.isAxeOrPick(itemStack.getType())) {
            return 1.0;
        }
        return d;
    }
}

