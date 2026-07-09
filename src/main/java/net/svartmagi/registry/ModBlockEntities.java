package net.svartmagi.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.svartmagi.Svartmagi;
import net.svartmagi.magic.AltarBlockEntity;
import net.svartmagi.magic.PedestalBlockEntity;
import net.svartmagi.storage.UpgradableChestBlockEntity;
import net.svartmagi.tech.CableBlockEntity;
import net.svartmagi.tech.CrusherBlockEntity;
import net.svartmagi.tech.ElectricFurnaceBlockEntity;
import net.svartmagi.tech.GeneratorBlockEntity;
import net.svartmagi.tech.HarvesterBlockEntity;
import net.svartmagi.tech.InfuserBlockEntity;
import net.svartmagi.tech.ItemMoverBlockEntity;
import net.svartmagi.tech.TieredFurnaceBlockEntity;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Svartmagi.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeneratorBlockEntity>> KULLGENERATOR =
            BLOCK_ENTITIES.register("kullgenerator", () -> BlockEntityType.Builder
                    .of(GeneratorBlockEntity::new, ModBlocks.KULLGENERATOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ElectricFurnaceBlockEntity>> ELEKTRISK_OVN =
            BLOCK_ENTITIES.register("elektrisk_ovn", () -> BlockEntityType.Builder
                    .of(ElectricFurnaceBlockEntity::new, ModBlocks.ELEKTRISK_OVN.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrusherBlockEntity>> KNUSER =
            BLOCK_ENTITIES.register("knuser", () -> BlockEntityType.Builder
                    .of(CrusherBlockEntity::new, ModBlocks.KNUSER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InfuserBlockEntity>> SKYGGEINFUSER =
            BLOCK_ENTITIES.register("skyggeinfuser", () -> BlockEntityType.Builder
                    .of(InfuserBlockEntity::new, ModBlocks.SKYGGEINFUSER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HarvesterBlockEntity>> INNHOSTER =
            BLOCK_ENTITIES.register("innhoster", () -> BlockEntityType.Builder
                    .of(HarvesterBlockEntity::new, ModBlocks.INNHOSTER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemMoverBlockEntity>> UTTREKKER =
            BLOCK_ENTITIES.register("uttrekker", () -> BlockEntityType.Builder
                    .of(ItemMoverBlockEntity::new, ModBlocks.UTTREKKER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableBlockEntity>> KRAFTKABEL =
            BLOCK_ENTITIES.register("kraftkabel", () -> BlockEntityType.Builder
                    .of(CableBlockEntity::new, ModBlocks.KRAFTKABEL.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TieredFurnaceBlockEntity>> TIER_OVN =
            BLOCK_ENTITIES.register("tier_ovn", () -> BlockEntityType.Builder
                    .of(TieredFurnaceBlockEntity::new,
                            ModBlocks.KOBBEROVN.get(), ModBlocks.JERNOVN.get(), ModBlocks.DIAMANTOVN.get())
                    .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<UpgradableChestBlockEntity>> OPPGRADERBAR_KISTE =
            BLOCK_ENTITIES.register("oppgraderbar_kiste", () -> BlockEntityType.Builder
                    .of(UpgradableChestBlockEntity::new, ModBlocks.OPPGRADERBAR_KISTE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AltarBlockEntity>> RITUALALTER =
            BLOCK_ENTITIES.register("ritualalter", () -> BlockEntityType.Builder
                    .of(AltarBlockEntity::new, ModBlocks.RITUALALTER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PedestalBlockEntity>> PIDESTALL =
            BLOCK_ENTITIES.register("pidestall", () -> BlockEntityType.Builder
                    .of(PedestalBlockEntity::new, ModBlocks.PIDESTALL.get()).build(null));

    private ModBlockEntities() {}
}
