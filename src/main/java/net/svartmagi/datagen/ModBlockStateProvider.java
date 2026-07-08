package net.svartmagi.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.svartmagi.Svartmagi;
import net.svartmagi.registry.ModBlocks;
import net.svartmagi.tech.ItemMoverBlock;
import net.svartmagi.tech.MachineBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Svartmagi.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        machine(ModBlocks.KULLGENERATOR, "kullgenerator");
        machine(ModBlocks.ELEKTRISK_OVN, "elektrisk_ovn");
        machine(ModBlocks.KNUSER, "knuser");
        machine(ModBlocks.SKYGGEINFUSER, "skyggeinfuser");
        machine(ModBlocks.INNHOSTER, "innhoster");
        machine(ModBlocks.KOBBEROVN, "kobberovn");
        machine(ModBlocks.JERNOVN, "jernovn");
        machine(ModBlocks.DIAMANTOVN, "diamantovn");

        // Uttrekker: retningsbasert kube
        ModelFile moverModel = models().cube("uttrekker",
                        modLoc("block/uttrekker_side"), modLoc("block/uttrekker_front"),
                        modLoc("block/uttrekker_front"), modLoc("block/uttrekker_side"),
                        modLoc("block/uttrekker_side"), modLoc("block/uttrekker_side"))
                .texture("particle", modLoc("block/uttrekker_side"));
        getVariantBuilder(ModBlocks.UTTREKKER.get()).forAllStates(state -> {
            var dir = state.getValue(ItemMoverBlock.FACING);
            int rotX = switch (dir) {
                case DOWN -> 90;
                case UP -> 270;
                default -> 0;
            };
            int rotY = switch (dir) {
                case SOUTH -> 180;
                case WEST -> 270;
                case EAST -> 90;
                default -> 0;
            };
            return net.neoforged.neoforge.client.model.generators.ConfiguredModel.builder()
                    .modelFile(moverModel).rotationX(rotX).rotationY(rotY).build();
        });
        simpleBlockItem(ModBlocks.UTTREKKER.get(), moverModel);

        simpleBlockWithItem(ModBlocks.OPPGRADERBAR_KISTE.get(),
                cubeAll(ModBlocks.OPPGRADERBAR_KISTE.get()));
        simpleBlockWithItem(ModBlocks.SKYGGEMALM.get(), cubeAll(ModBlocks.SKYGGEMALM.get()));
        simpleBlockWithItem(ModBlocks.SKYGGESTEIN.get(), cubeAll(ModBlocks.SKYGGESTEIN.get()));
        simpleBlockWithItem(ModBlocks.SKYGGEPORTAL.get(), cubeAll(ModBlocks.SKYGGEPORTAL.get()));

        // Alter og pidestall: enkle former
        ModelFile altar = models().cubeBottomTop("ritualalter",
                modLoc("block/ritualalter_side"), modLoc("block/ritualalter_bottom"), modLoc("block/ritualalter_top"));
        simpleBlockWithItem(ModBlocks.RITUALALTER.get(), altar);

        ModelFile pedestal = models().cubeColumn("pidestall",
                modLoc("block/pidestall_side"), modLoc("block/pidestall_top"));
        simpleBlockWithItem(ModBlocks.PIDESTALL.get(), pedestal);
    }

    /** Maskin: orienterbar kube med egen front, LIT-variant med lys front. */
    private void machine(DeferredBlock<? extends Block> block, String name) {
        ModelFile off = models().orientable(name,
                modLoc("block/" + name + "_side"), modLoc("block/" + name + "_front"),
                modLoc("block/" + name + "_top"));
        ModelFile on = models().orientable(name + "_on",
                modLoc("block/" + name + "_side"), modLoc("block/" + name + "_front_on"),
                modLoc("block/" + name + "_top"));
        getVariantBuilder(block.get()).forAllStates(state -> {
            boolean lit = state.hasProperty(MachineBlock.LIT) && state.getValue(MachineBlock.LIT);
            int rotY = switch (state.getValue(MachineBlock.FACING)) {
                case SOUTH -> 180;
                case WEST -> 270;
                case EAST -> 90;
                default -> 0;
            };
            return net.neoforged.neoforge.client.model.generators.ConfiguredModel.builder()
                    .modelFile(lit ? on : off).rotationY(rotY).build();
        });
        simpleBlockItem(block.get(), off);
    }
}
