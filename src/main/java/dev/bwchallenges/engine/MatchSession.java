/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 */
package dev.bwchallenges.engine;

import dev.bwchallenges.Challenge;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;

public final class MatchSession {
    public final UUID playerId;
    public final Challenge challenge;
    public boolean failed;
    public boolean completed;
    public long startedAt = System.currentTimeMillis();
    public long lastHitAt;
    public long lastKillAt;
    public long lastDenyAt;
    public int rainTicks;
    public boolean redLight;
    public String targetTeam = "";
    public UUID presidentId;
    public boolean bedBrokenByTeam;
    public boolean ownBedLost;
    public long staminaBoostUntil;
    public final Set<String> collectedWool = new HashSet<String>();
    public final Map<String, Integer> purchaseCounts = new HashMap<String, Integer>();
    public final Set<String> defusedBeds = new HashSet<String>();
    public Location islandCenter;
    public int islandRadius = 20;
    public String teamName = "";
    public String teamColor = "";
    public int enemyTeams = 2;
    public int mathA;
    public int mathB;
    public int mathAnswer = Integer.MIN_VALUE;
    public long mathDeadline;
    public long bloodlustUntil;
    public long jumpAllowedUntil;
    public boolean generatorsSlowed;
    public String arenaKey = "";
    public boolean woolSubmitted;
    public int[] shopOrder;
    public final Map<String, int[]> shopLayouts = new HashMap<String, int[]>();
    public boolean shopHidden;
    public long lightNextAt;
    public int lightCountdown;
    public double lastX;
    public double lastY;
    public double lastZ;
    public boolean hasLastPos;

    public MatchSession(UUID uUID, Challenge challenge) {
        this.playerId = uUID;
        this.challenge = challenge;
        this.lastKillAt = System.currentTimeMillis();
    }
}

