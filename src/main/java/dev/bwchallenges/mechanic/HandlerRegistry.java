/*
 * Decompiled with CFR 0.152.
 */
package dev.bwchallenges.mechanic;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.mechanic.ChallengeHandler;
import dev.bwchallenges.mechanic.impl.AnchorHandler;
import dev.bwchallenges.mechanic.impl.AssassinHandler;
import dev.bwchallenges.mechanic.impl.BedsAndBloodlustHandler;
import dev.bwchallenges.mechanic.impl.BegAndBarterHandler;
import dev.bwchallenges.mechanic.impl.BlockrepellentBedsHandler;
import dev.bwchallenges.mechanic.impl.BridgingForDummiesHandler;
import dev.bwchallenges.mechanic.impl.CantTouchThisHandler;
import dev.bwchallenges.mechanic.impl.CappedResourcesHandler;
import dev.bwchallenges.mechanic.impl.CollectorHandler;
import dev.bwchallenges.mechanic.impl.DefuserHandler;
import dev.bwchallenges.mechanic.impl.HalvedAndDoubledHandler;
import dev.bwchallenges.mechanic.impl.LazyMinerHandler;
import dev.bwchallenges.mechanic.impl.MarksmanHandler;
import dev.bwchallenges.mechanic.impl.MasterAssassinHandler;
import dev.bwchallenges.mechanic.impl.MidnightHandler;
import dev.bwchallenges.mechanic.impl.MinimumWageHandler;
import dev.bwchallenges.mechanic.impl.NoDreamingHandler;
import dev.bwchallenges.mechanic.impl.OldManHandler;
import dev.bwchallenges.mechanic.impl.PacifistHandler;
import dev.bwchallenges.mechanic.impl.PatriotHandler;
import dev.bwchallenges.mechanic.impl.ProtectThePresidentHandler;
import dev.bwchallenges.mechanic.impl.QuickMathsHandler;
import dev.bwchallenges.mechanic.impl.RedLightGreenLightHandler;
import dev.bwchallenges.mechanic.impl.RegularShopperHandler;
import dev.bwchallenges.mechanic.impl.RenegadeHandler;
import dev.bwchallenges.mechanic.impl.SelfishHandler;
import dev.bwchallenges.mechanic.impl.SleightOfHandHandler;
import dev.bwchallenges.mechanic.impl.SlowReflexesHandler;
import dev.bwchallenges.mechanic.impl.SocialDistancingHandler;
import dev.bwchallenges.mechanic.impl.StaminaHandler;
import dev.bwchallenges.mechanic.impl.StandingTallHandler;
import dev.bwchallenges.mechanic.impl.SwordlessHandler;
import dev.bwchallenges.mechanic.impl.ToxicRainHandler;
import dev.bwchallenges.mechanic.impl.UltimateUhcHandler;
import dev.bwchallenges.mechanic.impl.WarmongerHandler;
import dev.bwchallenges.mechanic.impl.WeightedItemsHandler;
import dev.bwchallenges.mechanic.impl.WoodworkerHandler;
import dev.bwchallenges.mechanic.impl.WoolWarriorHandler;
import java.util.EnumMap;
import java.util.Map;

public final class HandlerRegistry {
    private static final ChallengeHandler NOOP = new ChallengeHandler(){

        @Override
        public Challenge challenge() {
            return Challenge.RENEGADE;
        }
    };
    private final Map<Challenge, ChallengeHandler> handlers = new EnumMap<Challenge, ChallengeHandler>(Challenge.class);

    public HandlerRegistry() {
        this.register(new RenegadeHandler());
        this.register(new WarmongerHandler());
        this.register(new SelfishHandler());
        this.register(new MinimumWageHandler());
        this.register(new AssassinHandler());
        this.register(new RegularShopperHandler());
        this.register(new CollectorHandler());
        this.register(new WoodworkerHandler());
        this.register(new BridgingForDummiesHandler());
        this.register(new ToxicRainHandler());
        this.register(new DefuserHandler());
        this.register(new LazyMinerHandler());
        this.register(new UltimateUhcHandler());
        this.register(new SleightOfHandHandler());
        this.register(new WeightedItemsHandler());
        this.register(new SocialDistancingHandler());
        this.register(new SwordlessHandler());
        this.register(new MarksmanHandler());
        this.register(new PatriotHandler());
        this.register(new StaminaHandler());
        this.register(new OldManHandler());
        this.register(new CappedResourcesHandler());
        this.register(new RedLightGreenLightHandler());
        this.register(new SlowReflexesHandler());
        this.register(new PacifistHandler());
        this.register(new MasterAssassinHandler());
        this.register(new StandingTallHandler());
        this.register(new ProtectThePresidentHandler());
        this.register(new CantTouchThisHandler());
        this.register(new WoolWarriorHandler());
        this.register(new AnchorHandler());
        this.register(new NoDreamingHandler());
        this.register(new QuickMathsHandler());
        this.register(new BlockrepellentBedsHandler());
        this.register(new MidnightHandler());
        this.register(new BedsAndBloodlustHandler());
        this.register(new HalvedAndDoubledHandler());
        this.register(new BegAndBarterHandler());
    }

    private void register(ChallengeHandler challengeHandler) {
        this.handlers.put(challengeHandler.challenge(), challengeHandler);
    }

    public ChallengeHandler of(Challenge challenge) {
        ChallengeHandler challengeHandler = challenge == null ? null : this.handlers.get((Object)challenge);
        return challengeHandler == null ? NOOP : challengeHandler;
    }
}

