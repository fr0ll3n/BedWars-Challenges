/*
 * Decompiled with CFR 0.152.
 */
package dev.bwchallenges.storage;

import dev.bwchallenges.PlayerProfile;
import java.util.UUID;

public interface Storage {
    public PlayerProfile load(UUID var1);

    public void save(PlayerProfile var1);

    public void saveAsync(PlayerProfile var1);

    public void close();

    public String kind();
}

