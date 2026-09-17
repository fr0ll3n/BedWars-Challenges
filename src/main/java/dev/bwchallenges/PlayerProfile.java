/*
 * Decompiled with CFR 0.152.
 */
package dev.bwchallenges;

import dev.bwchallenges.Challenge;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public final class PlayerProfile {
    private final UUID uuid;
    private volatile String name;
    private volatile Challenge active;
    private final EnumSet<Challenge> completed = EnumSet.noneOf(Challenge.class);
    private final EnumSet<Challenge> unlocked = EnumSet.noneOf(Challenge.class);
    private final EnumSet<Challenge> claimed = EnumSet.noneOf(Challenge.class);
    private final Set<String> grantedPerms = new LinkedHashSet<String>();
    private volatile int wins;
    private volatile boolean dirty;
    private volatile boolean loaded;

    public PlayerProfile(UUID uUID) {
        this.uuid = uUID;
        this.loaded = false;
        this.rebuildUnlocks();
    }

    public UUID uuid() {
        return this.uuid;
    }

    public String name() {
        return this.name;
    }

    public void setName(String string) {
        this.name = string;
    }

    public Challenge active() {
        return this.active;
    }

    public void setActive(Challenge challenge) {
        this.active = challenge;
        this.dirty = true;
    }

    public Set<Challenge> completed() {
        return Collections.unmodifiableSet(this.completed);
    }

    public Set<Challenge> unlocked() {
        return Collections.unmodifiableSet(this.unlocked);
    }

    public Set<Challenge> claimed() {
        return Collections.unmodifiableSet(this.claimed);
    }

    public Set<String> grantedPermissions() {
        return Collections.unmodifiableSet(this.grantedPerms);
    }

    public boolean isCompleted(Challenge challenge) {
        return challenge != null && this.completed.contains((Object)challenge);
    }

    public boolean isUnlocked(Challenge challenge) {
        return challenge != null && this.unlocked.contains((Object)challenge);
    }

    public boolean isClaimed(Challenge challenge) {
        return challenge != null && this.claimed.contains((Object)challenge);
    }

    public boolean hasUnclaimedReward(Challenge challenge) {
        return this.isCompleted(challenge) && !this.isClaimed(challenge);
    }

    public void unlock(Challenge challenge) {
        if (challenge != null && this.unlocked.add(challenge)) {
            this.dirty = true;
        }
    }

    public void complete(Challenge challenge) {
        if (challenge == null) {
            return;
        }
        ++this.wins;
        this.completed.add(challenge);
        this.unlocked.add(challenge);
        this.rebuildUnlocks();
        this.dirty = true;
    }

    public boolean claim(Challenge challenge) {
        if (challenge == null || !this.completed.contains((Object)challenge)) {
            return false;
        }
        if (!this.claimed.add(challenge)) {
            return false;
        }
        this.dirty = true;
        return true;
    }

    public void grantPermission(String string) {
        if (string == null || string.trim().isEmpty()) {
            return;
        }
        if (this.grantedPerms.add(string.trim())) {
            this.dirty = true;
        }
    }

    public int wins() {
        return this.wins;
    }

    public void setWins(int n) {
        this.wins = Math.max(0, n);
    }

    public void rebuildUnlocks() {
        this.unlocked.clear();
        ArrayList<Challenge> arrayList = new ArrayList<Challenge>();
        for (Challenge challenge : Challenge.values()) {
            if (!challenge.selectable()) continue;
            arrayList.add(challenge);
        }
        if (arrayList.isEmpty()) {
            return;
        }
        int n = Math.min(3, arrayList.size());
        int n2 = 0;
        for (Challenge challenge : this.completed) {
            if (challenge == null || !challenge.selectable()) continue;
            ++n2;
        }
        int n3 = Math.min(arrayList.size(), Math.max(n, n2 + 3));
        for (int i = 0; i < n3; ++i) {
            this.unlocked.add((Challenge)((Object)arrayList.get(i)));
        }
    }

    public int completedCount() {
        return this.completed.size();
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void markClean() {
        this.dirty = false;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public void markLoaded() {
        this.loaded = true;
    }

    public void applyLoaded(Challenge challenge, Set<Challenge> set, Set<Challenge> set2) {
        this.applyLoaded(challenge, set, set2, 0, null, null);
    }

    public void applyLoaded(Challenge challenge, Set<Challenge> set, Set<Challenge> set2, int n) {
        this.applyLoaded(challenge, set, set2, n, null, null);
    }

    public void applyLoaded(Challenge challenge, Set<Challenge> set, Set<Challenge> set2, int n, Set<Challenge> set3, Set<String> set4) {
        this.active = challenge;
        this.wins = Math.max(0, n);
        this.completed.clear();
        if (set != null) {
            this.completed.addAll(set);
        }
        this.claimed.clear();
        if (set3 != null) {
            this.claimed.addAll(set3);
        }
        this.grantedPerms.clear();
        if (set4 != null) {
            this.grantedPerms.addAll(set4);
        }
        this.rebuildUnlocks();
        this.loaded = true;
        this.dirty = false;
    }
}

