package net.svartmagi.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.svartmagi.Svartmagi;
import net.svartmagi.menu.GeneratorMenu;
import net.svartmagi.menu.HarvesterMenu;
import net.svartmagi.menu.ProcessingMenu;
import net.svartmagi.menu.TieredFurnaceMenu;
import net.svartmagi.menu.UpgradableChestMenu;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, Svartmagi.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<GeneratorMenu>> KULLGENERATOR =
            MENUS.register("kullgenerator", () -> IMenuTypeExtension.create(GeneratorMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<ProcessingMenu>> PROSESSERING =
            MENUS.register("prosessering", () -> IMenuTypeExtension.create(ProcessingMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<TieredFurnaceMenu>> TIER_OVN =
            MENUS.register("tier_ovn", () -> IMenuTypeExtension.create(TieredFurnaceMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<HarvesterMenu>> INNHOSTER =
            MENUS.register("innhoster", () -> IMenuTypeExtension.create(HarvesterMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<UpgradableChestMenu>> OPPGRADERBAR_KISTE =
            MENUS.register("oppgraderbar_kiste", () -> IMenuTypeExtension.create(UpgradableChestMenu::forClient));

    private ModMenus() {}
}
