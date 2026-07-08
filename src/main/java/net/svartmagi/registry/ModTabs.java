package net.svartmagi.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.svartmagi.Svartmagi;

public final class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Svartmagi.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SVARTMAGI =
            TABS.register("svartmagi", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.svartmagi"))
                    .icon(() -> new ItemStack(ModItems.SKYGGEKJERNE.get()))
                    .displayItems((parameters, output) -> ModItems.ITEMS.getEntries()
                            .forEach(entry -> output.accept(entry.get())))
                    .build());

    private ModTabs() {}
}
