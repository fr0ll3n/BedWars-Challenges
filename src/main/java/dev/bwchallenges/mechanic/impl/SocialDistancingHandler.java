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

public final class SocialDistancingHandler
extends ChallengeHandler {
    @Override
    public Challenge challenge() {
        return Challenge.SOCIAL_DISTANCING;
    }

    @Override
    public boolean blockShopBuy(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, String string, String string2, Material material, String string3) {
        String string4 = challengeEngine.shopBlob(string, string2, material, string3);
        if (challengeEngine.looksLikeSword(string4, material) || challengeEngine.looksLikeMelee(string4, material) && !string4.contains("stick")) {
            challengeEngine.deny(player, matchSession, "&cYou can't buy melee weapons on this challenge");
            return true;
        }
        return false;
    }

    @Override
    public boolean blockMelee(ChallengeEngine challengeEngine, Player player, MatchSession matchSession, Player player2, ItemStack itemStack) {
        Material material;
        Material material2 = material = itemStack == null ? Material.AIR : itemStack.getType();
        if (XMat.isStick(material) || XMat.isBow(material)) {
            return false;
        }
        challengeEngine.deny(player, matchSession, "&cSocial Distancing: knockback sticks and punch bows only.");
        return true;
    }
}

