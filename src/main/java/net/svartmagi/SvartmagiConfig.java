package net.svartmagi;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Server-side config (synced to clients, stored per world in serverconfig/).
 * All values are read live, so admins can tweak the file and it hot-reloads
 * without a world restart.
 */
public final class SvartmagiConfig {
    public static final ModConfigSpec SPEC;

    // Pilar-toggles
    public static final ModConfigSpec.BooleanValue TECH_ENABLED;
    public static final ModConfigSpec.BooleanValue MAGIC_ENABLED;
    public static final ModConfigSpec.BooleanValue STORAGE_ENABLED;
    public static final ModConfigSpec.BooleanValue VEINMINE_ENABLED;
    public static final ModConfigSpec.BooleanValue COMMANDS_ENABLED;

    // Tech
    public static final ModConfigSpec.IntValue GENERATOR_FE_PER_TICK;
    public static final ModConfigSpec.IntValue MACHINE_FE_PER_TICK;
    public static final ModConfigSpec.IntValue MACHINE_CAPACITY;
    public static final ModConfigSpec.IntValue HARVESTER_RADIUS;
    public static final ModConfigSpec.IntValue HARVESTER_FE_PER_OPERATION;
    public static final ModConfigSpec.IntValue HARVESTER_INTERVAL_TICKS;
    public static final ModConfigSpec.IntValue PIPE_TRANSFER_AMOUNT;
    public static final ModConfigSpec.IntValue PIPE_INTERVAL_TICKS;
    public static final ModConfigSpec.IntValue CABLE_CAPACITY;
    public static final ModConfigSpec.IntValue CABLE_TRANSFER_PER_TICK;

    // Veinmine
    public static final ModConfigSpec.IntValue VEINMINE_MAX_BLOCKS;
    public static final ModConfigSpec.DoubleValue VEINMINE_EXHAUSTION_PER_BLOCK;
    public static final ModConfigSpec.BooleanValue VEINMINE_REQUIRE_TOOL;

    // Kommandoer
    public static final ModConfigSpec.IntValue HOME_LIMIT;
    public static final ModConfigSpec.IntValue RTP_RADIUS;
    public static final ModConfigSpec.IntValue RTP_COOLDOWN_SECONDS;
    public static final ModConfigSpec.IntValue TPA_TIMEOUT_SECONDS;

    // Lagring
    public static final ModConfigSpec.IntValue CHEST_MAX_STACK_UPGRADES;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();

        b.push("pillars");
        TECH_ENABLED = b.comment("Aktiver tech-pilaren (maskiner, kraft, roer)").define("techEnabled", true);
        MAGIC_ENABLED = b.comment("Aktiver magi-pilaren (ritualer, portal, boss)").define("magicEnabled", true);
        STORAGE_ENABLED = b.comment("Aktiver lagrings-pilaren (oppgraderbare kister)").define("storageEnabled", true);
        VEINMINE_ENABLED = b.comment("Aktiver veinmining (aaregraving)").define("veinmineEnabled", true);
        COMMANDS_ENABLED = b.comment("Aktiver QoL-kommandoer (/tpa, /home, /rtp, ...)").define("commandsEnabled", true);
        b.pop();

        b.push("tech");
        GENERATOR_FE_PER_TICK = b.comment("FE generert per tick av kullgeneratoren")
                .defineInRange("generatorFePerTick", 40, 1, 100000);
        MACHINE_FE_PER_TICK = b.comment("FE brukt per tick av maskiner")
                .defineInRange("machineFePerTick", 20, 1, 100000);
        MACHINE_CAPACITY = b.comment("Intern FE-buffer i maskiner")
                .defineInRange("machineCapacity", 20000, 1000, 10000000);
        HARVESTER_RADIUS = b.comment("Radius (i blokker) innhoesteren jobber i")
                .defineInRange("harvesterRadius", 4, 1, 8);
        HARVESTER_FE_PER_OPERATION = b.comment("FE per innhoestings-operasjon")
                .defineInRange("harvesterFePerOperation", 200, 0, 100000);
        HARVESTER_INTERVAL_TICKS = b.comment("Ticks mellom hver innhoestings-operasjon")
                .defineInRange("harvesterIntervalTicks", 20, 5, 1200);
        PIPE_TRANSFER_AMOUNT = b.comment("Antall items et roer flytter per operasjon")
                .defineInRange("pipeTransferAmount", 8, 1, 64);
        PIPE_INTERVAL_TICKS = b.comment("Ticks mellom hver roer-operasjon")
                .defineInRange("pipeIntervalTicks", 10, 1, 200);
        CABLE_CAPACITY = b.comment("Intern FE-buffer i kraftkabler")
                .defineInRange("cableCapacity", 4000, 100, 1000000);
        CABLE_TRANSFER_PER_TICK = b.comment("Maks FE en kraftkabel flytter videre per tick")
                .defineInRange("cableTransferPerTick", 400, 10, 100000);
        b.pop();

        b.push("veinmine");
        VEINMINE_MAX_BLOCKS = b.comment("Maks antall blokker per veinmine")
                .defineInRange("maxBlocks", 64, 2, 512);
        VEINMINE_EXHAUSTION_PER_BLOCK = b.comment("Ekstra sult-kostnad per blokk")
                .defineInRange("exhaustionPerBlock", 0.1, 0.0, 5.0);
        VEINMINE_REQUIRE_TOOL = b.comment("Krev riktig verktoey for veinmining")
                .define("requireCorrectTool", true);
        b.pop();

        b.push("commands");
        HOME_LIMIT = b.comment("Maks antall homes per spiller").defineInRange("homeLimit", 3, 1, 64);
        RTP_RADIUS = b.comment("Maks avstand fra world spawn for /rtp").defineInRange("rtpRadius", 5000, 100, 1000000);
        RTP_COOLDOWN_SECONDS = b.comment("Cooldown for /rtp i sekunder").defineInRange("rtpCooldownSeconds", 60, 0, 86400);
        TPA_TIMEOUT_SECONDS = b.comment("Hvor lenge en /tpa-foresporsel er gyldig").defineInRange("tpaTimeoutSeconds", 60, 5, 3600);
        b.pop();

        b.push("storage");
        CHEST_MAX_STACK_UPGRADES = b.comment("Maks antall stabeloppgraderinger per kiste (hver dobler stack-storrelsen)")
                .defineInRange("maxStackUpgrades", 2, 0, 3);
        b.pop();

        SPEC = b.build();
    }

    private SvartmagiConfig() {}
}
