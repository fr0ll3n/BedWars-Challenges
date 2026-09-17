/*
 * Decompiled with CFR 0.152.
 */
package dev.bwchallenges;

public enum Challenge {
    RENEGADE("renegade", "Renegade", "DIAMOND", 0, false, "Team upgrades and traps are disabled", "You cannot pick up diamonds"),
    WARMONGER("warmonger", "Warmonger", "TNT", 0, false, "You cannot purchase nor use any utilities to aid you in battle"),
    SELFISH("selfish", "Selfish", "GOLD_INGOT", 0, false, "You are too selfish to drop any items to other players", "Your team chest and Ender Chest are locked"),
    MINIMUM_WAGE("minimum_wage", "Minimum Wage", "IRON_INGOT", 1, false, "Your island resource generator is 2x slower"),
    ASSASSIN("assassin", "Assassin", "IRON_SWORD", 1, false, "You can only break the bed of your assigned target", "No other beds can be broken until you eliminate your target team", "A new target is assigned after you wipe the current one"),
    REGULAR_SHOPPER("regular_shopper", "Regular Shopper", "EMERALD", 1, false, "Purchased item upgrades are removed upon death", "Purchased tool upgrades reset upon death"),
    COLLECTOR("collector", "Collector", "WHITE_WOOL", 2, false, "Collect all wool colors and return them to your shopkeeper", "You must win the game after turning in every color"),
    WOODWORKER("woodworker", "Woodworker", "OAK_PLANKS", 2, false, "You can only purchase and pick up items made of wood", "You cannot use your Ender Chest \u2014 it isn't made of wood"),
    BRIDGING_FOR_DUMMIES("bridging_for_dummies", "Bridging for Dummies", "SPONGE", 2, false, "You cannot purchase any blocks other than sponge", "You cannot pick up any blocks"),
    TOXIC_RAIN("toxic_rain", "Toxic Rain", "WATER_BUCKET", 2, true, "Toxic rain falls for the whole game", "Standing in the rain drains items from your inventory", "The longer you stay in the rain, the faster items drain"),
    DEFUSER("defuser", "Defuser", "SHEARS", 2, false, "Every enemy bed must be defused before it can be broken", "A defuse lasts 2 minutes"),
    LAZY_MINER("lazy_miner", "Lazy Miner", "IRON_PICKAXE", 2, true, "You have permanent Mining Fatigue", "Mining Fatigue traps hit even harder", "TNT and fireballs are disabled"),
    ULTIMATE_UHC("ultimate_uhc", "Ultimate UHC", "GOLDEN_APPLE", 2, false, "Natural health regeneration is disabled", "Golden Apples do not restore health"),
    SLEIGHT_OF_HAND("sleight_of_hand", "Sleight of Hand", "STICK", 3, true, "You only have 1 available hotbar slot"),
    WEIGHTED_ITEMS("weighted_items", "Weighted Items", "ANVIL", 3, true, "Each item has a unique weight", "Carrying heavy items makes you move slower"),
    SOCIAL_DISTANCING("social_distancing", "Social Distancing", "BOW", 3, false, "You can only use Knockback Sticks and Punch Bows to attack players"),
    SWORDLESS("swordless", "Swordless", "WOODEN_SWORD", 3, false, "You cannot purchase any swords from the shop", "You cannot pick up any swords", "Axes and pickaxes deal only 1 damage to players"),
    MARKSMAN("marksman", "Marksman", "ARROW", 3, false, "You cannot hit any players with melee weapons, including your fist", "You can only attack players using a bow"),
    PATRIOT("patriot", "Patriot", "WHITE_WOOL", 3, false, "You can only walk on blocks your team has placed", "You cannot pick up items from enemy players", "You only receive resources from your own generator", "Walking on enemy blocks fails the challenge"),
    STAMINA("stamina", "Stamina", "COOKED_BEEF", 3, true, "Running, hitting and breaking blocks consume stamina", "Your hunger bar is your stamina", "Golden Apples double recharge for 10 seconds"),
    OLD_MAN("old_man", "Old Man", "LEATHER_BOOTS", 4, false, "You cannot sprint during the whole game", "If you sprint, the challenge will be failed"),
    CAPPED_RESOURCES("capped_resources", "Capped Resources", "GOLD_INGOT", 4, false, "All shop items have a limited purchase cap for your team", "You cannot pick up items from enemy players", "You can only pick up resources from your own generator"),
    RED_LIGHT_GREEN_LIGHT("red_light_green_light", "Red Light Green Light", "REDSTONE", 4, true, "At random intervals the light switches between red and green", "Moving on red fails the challenge"),
    SLOW_REFLEXES("slow_reflexes", "Slow Reflexes", "COBWEB", 4, false, "You can only hit enemies once every 2 seconds"),
    PACIFIST("pacifist", "Pacifist", "SNOWBALL", 4, false, "You cannot hit players with melee weapons, including your fist", "You cannot use bows to deal damage", "You can only use utilities to deal damage"),
    MASTER_ASSASSIN("master_assassin", "Master Assassin", "DIAMOND_SWORD", 4, false, "You can only break the bed of your assigned target", "You can only damage players of your target team", "A new target is assigned after you wipe the current one"),
    STANDING_TALL("standing_tall", "Standing Tall", "OAK_FENCE", 4, false, "You cannot sneak during the challenge", "If you crouch, the challenge will be failed"),
    PROTECT_THE_PRESIDENT("protect_the_president", "Protect the President", "GOLDEN_HELMET", 4, false, "One teammate is selected as the President", "Only the President may break enemy beds", "If the President dies, the challenge is failed"),
    CANT_TOUCH_THIS("cant_touch_this", "Can't Touch This", "RED_BED", 4, false, "You must not take any damage for the whole game", "Any damage or void fall fails the challenge", "Your team must break at least one bed to complete it"),
    WOOL_WARRIOR("wool_warrior", "Wool Warrior", "WHITE_WOOL", 5, false, "You can only purchase wool from the shop", "You cannot pick up items dropped by other players"),
    ANCHOR("anchor", "Anchor", "ANVIL", 5, false, "You cannot jump", "Jump Boost potions temporarily allow a normal jump"),
    NO_DREAMING("no_dreaming", "No Dreaming", "BARRIER", 5, false, "You may only land final kills \u2014 no kills while the enemy still has a bed", "Dying once fails the challenge"),
    QUICK_MATHS("quick_maths", "Quick Maths", "PAPER", 5, true, "Math questions appear in chat during the game", "You have 10 seconds to answer each one or the challenge fails"),
    BLOCKREPELLENT_BEDS("blockrepellent_beds", "Blockrepellent Beds", "RED_BED", 5, false, "You cannot place blocks next to any bed"),
    MIDNIGHT("midnight", "Midnight", "ENDER_PEARL", 5, true, "The world stays dark for you", "You have Darkness away from your own island"),
    BEDS_AND_BLOODLUST("beds_and_bloodlust", "Beds & Bloodlust", "BLAZE_POWDER", 5, true, "After you break a bed you must get a kill within 90 seconds", "Your team must break at least one bed"),
    HALVED_AND_DOUBLED("halved_and_doubled", "Halved & Doubled", "GOLDEN_APPLE", 5, false, "Your maximum health is halved", "Your melee damage is doubled"),
    BEG_AND_BARTER("beg_and_barter", "Beg & Barter", "EMERALD", 5, false, "You cannot collect from your own island generator", "You must gather resources from mid or enemy islands"),
    INVISIBLE_SHOP("invisible_shop", "Invisible Shop", "GLASS", 1, false, "Shop items are hidden and shuffled", "Category buttons are replaced with barriers", "Memorize the slots \u2014 nothing is labeled");

    private final String id;
    private final String displayName;
    private final String material;
    private final int tier;
    private final boolean ticking;
    private final String[] rules;

    private Challenge(String string2, String string3, String string4, int n2, boolean bl, String ... stringArray) {
        this.id = string2;
        this.displayName = string3;
        this.material = string4;
        this.tier = n2;
        this.ticking = bl;
        this.rules = stringArray;
    }

    public String id() {
        return this.id;
    }

    public String displayName() {
        return this.displayName;
    }

    public String material() {
        return this.material;
    }

    public int tier() {
        return this.tier;
    }

    public boolean ticking() {
        return this.ticking;
    }

    public boolean selectable() {
        switch (this) {
            case ANCHOR: 
            case NO_DREAMING: 
            case QUICK_MATHS: 
            case BLOCKREPELLENT_BEDS: 
            case MIDNIGHT: 
            case BEDS_AND_BLOODLUST: 
            case HALVED_AND_DOUBLED: 
            case BEG_AND_BARTER: 
            case INVISIBLE_SHOP: {
                return false;
            }
        }
        return true;
    }

    public String[] rules() {
        return this.rules;
    }

    public String[] rewards() {
        String string = this.name();
        if ("RENEGADE".equals(string)) {
            return new String[]{"Cat Death Cry"};
        }
        if ("WARMONGER".equals(string)) {
            return new String[]{"Warrior Shopkeeper Skin"};
        }
        if ("SELFISH".equals(string)) {
            return new String[]{"Bite Projectile Trail"};
        }
        if ("MINIMUM_WAGE".equals(string)) {
            return new String[]{"Its Raining Gold Final Kill Effect"};
        }
        if ("ASSASSIN".equals(string)) {
            return new String[]{"Assassin Island Topper"};
        }
        if ("REGULAR_SHOPPER".equals(string)) {
            return new String[]{"Shopping Cart Island Topper"};
        }
        if ("COLLECTOR".equals(string)) {
            return new String[]{"Collector's Chest Island Topper"};
        }
        if ("WOODWORKER".equals(string)) {
            return new String[]{"Lumberjack Shopkeeper Skin"};
        }
        if ("BRIDGING_FOR_DUMMIES".equals(string)) {
            return new String[]{"Bridging for Dummies Kill Messages"};
        }
        if ("TOXIC_RAIN".equals(string)) {
            return new String[]{"Toxic Rain Island Topper"};
        }
        if ("DEFUSER".equals(string)) {
            return new String[]{"Defuser Shopkeeper Skin"};
        }
        if ("LAZY_MINER".equals(string)) {
            return new String[]{"Lazy Miner Island Topper"};
        }
        if ("ULTIMATE_UHC".equals(string)) {
            return new String[]{"Heartbleed Island Topper"};
        }
        if ("SLEIGHT_OF_HAND".equals(string)) {
            return new String[]{"Magic Bunny Island Topper"};
        }
        if ("WEIGHTED_ITEMS".equals(string)) {
            return new String[]{"Anvil Smash Final Kill Effect"};
        }
        if ("SOCIAL_DISTANCING".equals(string)) {
            return new String[]{"Social Distance Kill Messages"};
        }
        if ("SWORDLESS".equals(string)) {
            return new String[]{"The End Projectile Trail"};
        }
        if ("MARKSMAN".equals(string)) {
            return new String[]{"Ballista Island Topper"};
        }
        if ("PATRIOT".equals(string)) {
            return new String[]{"Patriotic Eagle Shopkeeper Skin"};
        }
        if ("STAMINA".equals(string)) {
            return new String[]{"Cake Walk Victory Dance"};
        }
        if ("OLD_MAN".equals(string)) {
            return new String[]{"Old Man Kill Messages"};
        }
        if ("CAPPED_RESOURCES".equals(string)) {
            return new String[]{"Merchant Shopkeeper Skin"};
        }
        if ("RED_LIGHT_GREEN_LIGHT".equals(string)) {
            return new String[]{"Traffic Light Island Topper"};
        }
        if ("SLOW_REFLEXES".equals(string)) {
            return new String[]{"Guardian Death Cry"};
        }
        if ("PACIFIST".equals(string)) {
            return new String[]{"Aura Victory Dance"};
        }
        if ("MASTER_ASSASSIN".equals(string)) {
            return new String[]{"Assassin's Blade Island Topper"};
        }
        if ("STANDING_TALL".equals(string)) {
            return new String[]{"Portal Projectile Trail"};
        }
        if ("PROTECT_THE_PRESIDENT".equals(string)) {
            return new String[]{"President Sloth Shopkeeper Skin", "Presidential Goons Island Topper"};
        }
        if ("CANT_TOUCH_THIS".equals(string)) {
            return new String[]{"Can't Touch This Kill Messages"};
        }
        if ("WOOL_WARRIOR".equals(string)) {
            return new String[]{"Woolnado Victory Dance"};
        }
        if ("ANCHOR".equals(string)) {
            return new String[]{"Anvil Ascension Victory Dance"};
        }
        if ("NO_DREAMING".equals(string)) {
            return new String[]{"Lucid Kill Messages"};
        }
        if ("QUICK_MATHS".equals(string)) {
            return new String[]{"Quick Maths Island Topper"};
        }
        if ("BLOCKREPELLENT_BEDS".equals(string)) {
            return new String[]{"Repellent Bed Island Topper"};
        }
        if ("MIDNIGHT".equals(string)) {
            return new String[]{"Midnight Projectile Trail"};
        }
        if ("BEDS_AND_BLOODLUST".equals(string)) {
            return new String[]{"Bloodlust Final Kill Effect"};
        }
        if ("HALVED_AND_DOUBLED".equals(string)) {
            return new String[]{"Halved & Doubled Spray"};
        }
        if ("BEG_AND_BARTER".equals(string)) {
            return new String[]{"Barter Shopkeeper Skin"};
        }
        if ("INVISIBLE_SHOP".equals(string)) {
            return new String[]{"Invisible Shopkeeper Skin"};
        }
        return new String[0];
    }

    public static Challenge byId(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }
        for (Challenge challenge : Challenge.values()) {
            if (!challenge.id.equalsIgnoreCase(string) && !challenge.name().equalsIgnoreCase(string)) continue;
            return challenge;
        }
        return null;
    }

    public static Challenge[] starters() {
        return new Challenge[]{RENEGADE, WARMONGER, SELFISH};
    }
}

