package net.svartmagi.registry;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.svartmagi.Svartmagi;
import net.svartmagi.magic.AltarBlock;
import net.svartmagi.magic.PedestalBlock;
import net.svartmagi.magic.PortalBlock;
import net.svartmagi.storage.UpgradableChestBlock;
import net.svartmagi.tech.CableBlock;
import net.svartmagi.tech.HarvesterBlock;
import net.svartmagi.tech.ItemMoverBlock;
import net.svartmagi.tech.MachineBlock;
import net.svartmagi.tech.TieredFurnaceBlock;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Svartmagi.MODID);

    private static BlockBehaviour.Properties machineProps() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.0f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.METAL);
    }

    // --- Tech ---
    public static final DeferredBlock<MachineBlock> KULLGENERATOR = BLOCKS.register("kullgenerator",
            () -> new MachineBlock(machineProps().lightLevel(s -> s.getValue(MachineBlock.LIT) ? 13 : 0),
                    () -> ModBlockEntities.KULLGENERATOR.get()));
    public static final DeferredBlock<MachineBlock> ELEKTRISK_OVN = BLOCKS.register("elektrisk_ovn",
            () -> new MachineBlock(machineProps().lightLevel(s -> s.getValue(MachineBlock.LIT) ? 13 : 0),
                    () -> ModBlockEntities.ELEKTRISK_OVN.get()));
    public static final DeferredBlock<MachineBlock> KNUSER = BLOCKS.register("knuser",
            () -> new MachineBlock(machineProps(), () -> ModBlockEntities.KNUSER.get()));
    public static final DeferredBlock<MachineBlock> SKYGGEINFUSER = BLOCKS.register("skyggeinfuser",
            () -> new MachineBlock(machineProps().lightLevel(s -> s.getValue(MachineBlock.LIT) ? 7 : 0),
                    () -> ModBlockEntities.SKYGGEINFUSER.get()));
    public static final DeferredBlock<HarvesterBlock> INNHOSTER = BLOCKS.register("innhoster",
            () -> new HarvesterBlock(machineProps()));
    public static final DeferredBlock<ItemMoverBlock> UTTREKKER = BLOCKS.register("uttrekker",
            () -> new ItemMoverBlock(machineProps()));
    public static final DeferredBlock<CableBlock> KRAFTKABEL = BLOCKS.register("kraftkabel",
            () -> new CableBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL).strength(2.0f).requiresCorrectToolForDrops().sound(SoundType.METAL)));

    public static final DeferredBlock<TieredFurnaceBlock> KOBBEROVN = BLOCKS.register("kobberovn",
            () -> new TieredFurnaceBlock(furnaceProps(), TieredFurnaceBlock.Tier.KOBBER));
    public static final DeferredBlock<TieredFurnaceBlock> JERNOVN = BLOCKS.register("jernovn",
            () -> new TieredFurnaceBlock(furnaceProps(), TieredFurnaceBlock.Tier.JERN));
    public static final DeferredBlock<TieredFurnaceBlock> DIAMANTOVN = BLOCKS.register("diamantovn",
            () -> new TieredFurnaceBlock(furnaceProps(), TieredFurnaceBlock.Tier.DIAMANT));

    private static BlockBehaviour.Properties furnaceProps() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(3.5f)
                .requiresCorrectToolForDrops()
                .lightLevel(s -> s.getValue(TieredFurnaceBlock.LIT) ? 13 : 0);
    }

    // --- Lagring ---
    public static final DeferredBlock<UpgradableChestBlock> OPPGRADERBAR_KISTE = BLOCKS.register("oppgraderbar_kiste",
            () -> new UpgradableChestBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD).strength(2.5f).sound(SoundType.WOOD)));

    // --- Magi ---
    public static final DeferredBlock<AltarBlock> RITUALALTER = BLOCKS.register("ritualalter",
            () -> new AltarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK).strength(4.0f, 1200.0f)
                    .requiresCorrectToolForDrops().lightLevel(s -> 7)));
    public static final DeferredBlock<PedestalBlock> PIDESTALL = BLOCKS.register("pidestall",
            () -> new PedestalBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE).strength(2.5f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<PortalBlock> SKYGGEPORTAL = BLOCKS.register("skyggeportal",
            () -> new PortalBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE).strength(-1.0f, 3600000.0f)
                    .noLootTable().noCollission().lightLevel(s -> 11)));

    // --- Verden ---
    public static final DeferredBlock<Block> SKYGGEMALM = BLOCKS.register("skyggemalm",
            () -> new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK).strength(4.5f, 6.0f)
                    .requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE)));
    public static final DeferredBlock<Block> SKYGGESTEIN = BLOCKS.register("skyggestein",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK).strength(2.0f, 8.0f)
                    .requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE)));

    private ModBlocks() {}
}
