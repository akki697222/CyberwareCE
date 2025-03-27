package flaxbeard.cyberware.common;

import flaxbeard.cyberware.Cyberware;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.*;

import java.util.Arrays;
import java.util.List;

@EventBusSubscriber(modid = Cyberware.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class CyberwareConfig {
    private static final Builder BUILDER = new Builder();

    private static final String C_MOBS = "Mobs";
    private static final String C_ESSENCE = "Essence";
    private static final String C_MACHINES = "Machines";
    private static final String C_OTHER = "Other";
    private static final String C_HUD = "HUD";
    private static final String C_GAMERULES = "Gamerules";
    private static final String C_INTEGRATION = "Integration";

    public static final ConfigValue<Integer> ESSENCE;
    public static final ConfigValue<Integer> CRITICAL_ESSENCE;
    public static final DoubleValue ENGINEERING_CHANCE;
    public static final DoubleValue SCANNER_CHANCE;
    public static final DoubleValue SCANNER_CHANCE_ADDL;
    public static final ConfigValue<Integer> SCANNER_TIME;
    public static final BooleanValue MOBS_ENABLE_CYBER_ZOMBIES;
    public static final ConfigValue<Integer> MOBS_CYBER_ZOMBIE_WEIGHT;
    public static final DoubleValue MOBS_CYBER_ZOMBIE_DROP_RARITY;
    public static final BooleanValue ENABLE_FLOAT;
    public static final DoubleValue HUDLENS_FLOAT;
    public static final BooleanValue SURGERY_CRAFTING;
    public static final ConfigValue<List<? extends String>> DEFAULT_STARTING_ITEMS;
    public static final BooleanValue DEFAULT_DROP;
    public static final DoubleValue DROP_CHANCE;
    public static final ConfigValue<String> FIST_MINING_TOOL_NAME;
    public static final BooleanValue INT_ENDER_IO;

    static {
        // Essence
        BUILDER.push(C_ESSENCE);
        ESSENCE = BUILDER
                .comment("Maximum Essence")
                .defineInRange("maxEssence", 100, 0, Integer.MAX_VALUE);
        CRITICAL_ESSENCE = BUILDER
                .comment("Critical Essence value, where rejection begins")
                .defineInRange("criticalEssence", 25, 0, Integer.MAX_VALUE);
        BUILDER.pop();

        // Machines
        BUILDER.push(C_MACHINES);
        ENGINEERING_CHANCE = BUILDER
                .comment("Chance of blueprint from Engineering Table")
                .defineInRange("engineeringChance", 15F, 0F, 100F);
        SCANNER_CHANCE = BUILDER
                .comment("Chance of blueprint from Scanner")
                .defineInRange("scannerChance", 10F, 0F, 50F);
        SCANNER_CHANCE_ADDL = BUILDER
                .comment("Additive chance for Scanner per extra item")
                .defineInRange("scannerChanceAddl", 10F, 0F, 100F);
        SCANNER_TIME = BUILDER
                .comment("Ticks taken per Scanner operation (24000 = one Minecraft day)")
                .defineInRange("scannerTime", 24000, 0, Integer.MAX_VALUE);
        BUILDER.pop();

        // Mobs
        BUILDER.push(C_MOBS);
        MOBS_ENABLE_CYBER_ZOMBIES = BUILDER
                .comment("Enable CyberZombies")
                .define("cyberZombiesEnabled", true);
        MOBS_CYBER_ZOMBIE_WEIGHT = BUILDER
                .comment("CyberZombies spawning weight (Vanilla Zombie = 100)")
                .defineInRange("cyberZombieWeight", 15, 0, Integer.MAX_VALUE);
        MOBS_CYBER_ZOMBIE_DROP_RARITY = BUILDER
                .comment("Percent chance a CyberZombie drops a cyberware")
                .defineInRange("cyberZombieDropRarity", 50.0F, 0F, 100F);
        BUILDER.pop();

        // HUD
        BUILDER.push(C_HUD);
        ENABLE_FLOAT = BUILDER
                .comment("Enable hudlens and hudjack float (Experimental)")
                .define("enableFloat", false);
        HUDLENS_FLOAT = BUILDER
                .comment("Amount hudlens HUD will 'float' with movement")
                .defineInRange("hudlensFloat", 0.1F, 0F, 100F);
        BUILDER.pop();

        // Other
        BUILDER.push(C_OTHER);
        SURGERY_CRAFTING = BUILDER
                .comment("Enable crafting recipe for Robosurgeon")
                .define("surgeryCrafting", false);
        FIST_MINING_TOOL_NAME = BUILDER
                .comment("Registry name of the mining tool equivalent to the reinforced fist")
                .define("fistMiningToolName", "minecraft:iron_pickaxe");
        BUILDER.pop();

        // Gamerules
        BUILDER.push(C_GAMERULES);
        DEFAULT_DROP = BUILDER
                .comment("Default for gamerule cyberware_dropCyberware")
                .define("defaultDrop", false);
        DROP_CHANCE = BUILDER
                .comment("Chance of successful drop if dropCyberware is enabled")
                .defineInRange("dropChance", 100F, 0F, 100F);
        BUILDER.pop();

        // Integration
        BUILDER.push(C_INTEGRATION);
        INT_ENDER_IO = BUILDER
                .comment("Enable EnderIO Integration if the mod is loaded")
                .define("enderIOIntegration", true);
        BUILDER.pop();

        // Starting Items (example for one slot, expand as needed)
        BUILDER.push("Defaults");
        DEFAULT_STARTING_ITEMS = BUILDER
                .comment("Default augments for slots (format: 'id amount metadata')")
                .defineList("defaultStartingItems", Arrays.asList("cyberware:bodypart_eyes"), s -> s instanceof String);
        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static int essence;
    public static int criticalEssence;
    public static double engineeringChance;
    public static double scannerChance;
    public static double scannerChanceAddl;
    public static int scannerTime;
    public static boolean mobsEnableCyberZombies;
    public static int mobsCyberZombieWeight;
    public static double mobsCyberZombieDropRarity;
    public static boolean enableFloat;
    public static double hudlensFloat;
    public static boolean surgeryCrafting;
    public static List<? extends String> defaultStartingItems;
    public static boolean defaultDrop;
    public static double dropChance;
    public static String fistMiningToolName;
    public static boolean intEnderIo;

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SPEC) {
            loadConfigValues();
        }
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            loadConfigValues();
        }
    }

    private static void loadConfigValues() {
        essence = ESSENCE.get();
        criticalEssence = CRITICAL_ESSENCE.get();
        engineeringChance = ENGINEERING_CHANCE.get();
        scannerChance = SCANNER_CHANCE.get();
        scannerChanceAddl = SCANNER_CHANCE_ADDL.get();
        scannerTime = SCANNER_TIME.get();
        mobsEnableCyberZombies = MOBS_ENABLE_CYBER_ZOMBIES.get();
        mobsCyberZombieWeight = MOBS_CYBER_ZOMBIE_WEIGHT.get();
        mobsCyberZombieDropRarity = MOBS_CYBER_ZOMBIE_DROP_RARITY.get();
        enableFloat = ENABLE_FLOAT.get();
        hudlensFloat = HUDLENS_FLOAT.get();
        surgeryCrafting = SURGERY_CRAFTING.get();
        defaultStartingItems = DEFAULT_STARTING_ITEMS.get();
        defaultDrop = DEFAULT_DROP.get();
        dropChance = DROP_CHANCE.get();
        fistMiningToolName = FIST_MINING_TOOL_NAME.get();
        intEnderIo = INT_ENDER_IO.get();
    }
}
