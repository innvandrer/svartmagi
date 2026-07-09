package net.svartmagi.registry;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.svartmagi.Svartmagi;
import net.svartmagi.item.RecallAmuletItem;
import net.svartmagi.item.UpgradeItem;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Svartmagi.MODID);

    // Block items
    public static final DeferredItem<?> KULLGENERATOR = ITEMS.registerSimpleBlockItem(ModBlocks.KULLGENERATOR);
    public static final DeferredItem<?> ELEKTRISK_OVN = ITEMS.registerSimpleBlockItem(ModBlocks.ELEKTRISK_OVN);
    public static final DeferredItem<?> KNUSER = ITEMS.registerSimpleBlockItem(ModBlocks.KNUSER);
    public static final DeferredItem<?> SKYGGEINFUSER = ITEMS.registerSimpleBlockItem(ModBlocks.SKYGGEINFUSER);
    public static final DeferredItem<?> INNHOSTER = ITEMS.registerSimpleBlockItem(ModBlocks.INNHOSTER);
    public static final DeferredItem<?> UTTREKKER = ITEMS.registerSimpleBlockItem(ModBlocks.UTTREKKER);
    public static final DeferredItem<?> KRAFTKABEL = ITEMS.registerSimpleBlockItem(ModBlocks.KRAFTKABEL);
    public static final DeferredItem<?> KOBBEROVN = ITEMS.registerSimpleBlockItem(ModBlocks.KOBBEROVN);
    public static final DeferredItem<?> JERNOVN = ITEMS.registerSimpleBlockItem(ModBlocks.JERNOVN);
    public static final DeferredItem<?> DIAMANTOVN = ITEMS.registerSimpleBlockItem(ModBlocks.DIAMANTOVN);
    public static final DeferredItem<?> OPPGRADERBAR_KISTE = ITEMS.registerSimpleBlockItem(ModBlocks.OPPGRADERBAR_KISTE);
    public static final DeferredItem<?> RITUALALTER = ITEMS.registerSimpleBlockItem(ModBlocks.RITUALALTER);
    public static final DeferredItem<?> PIDESTALL = ITEMS.registerSimpleBlockItem(ModBlocks.PIDESTALL);
    public static final DeferredItem<?> SKYGGEMALM = ITEMS.registerSimpleBlockItem(ModBlocks.SKYGGEMALM);
    public static final DeferredItem<?> SKYGGESTEIN = ITEMS.registerSimpleBlockItem(ModBlocks.SKYGGESTEIN);

    // Materialer
    public static final DeferredItem<Item> SKYGGESTOV = simple("skyggestov");
    public static final DeferredItem<Item> SKYGGESKAAR = simple("skyggeskaar");
    public static final DeferredItem<Item> LADET_SKYGGESKAAR = ITEMS.register("ladet_skyggeskaar",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> SKYGGEKJERNE = ITEMS.register("skyggekjerne",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final DeferredItem<Item> SKYGGESTAAL_BARRE = ITEMS.register("skyggestaal_barre",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE).fireResistant()));
    public static final DeferredItem<Item> MASKINKJERNE = simple("maskinkjerne");
    public static final DeferredItem<Item> KNUST_JERN = simple("knust_jern");
    public static final DeferredItem<Item> KNUST_GULL = simple("knust_gull");
    public static final DeferredItem<Item> KNUST_KOBBER = simple("knust_kobber");

    // Oppgraderinger
    public static final DeferredItem<UpgradeItem> FARTSOPPGRADERING = upgrade("fartsoppgradering", UpgradeItem.Kind.SPEED);
    public static final DeferredItem<UpgradeItem> PARALLELLOPPGRADERING = upgrade("parallelloppgradering", UpgradeItem.Kind.PARALLEL);
    public static final DeferredItem<UpgradeItem> KISTEOPPGRADERING_JERN = upgrade("kisteoppgradering_jern", UpgradeItem.Kind.CHEST_JERN);
    public static final DeferredItem<UpgradeItem> KISTEOPPGRADERING_GULL = upgrade("kisteoppgradering_gull", UpgradeItem.Kind.CHEST_GULL);
    public static final DeferredItem<UpgradeItem> KISTEOPPGRADERING_DIAMANT = upgrade("kisteoppgradering_diamant", UpgradeItem.Kind.CHEST_DIAMANT);
    public static final DeferredItem<UpgradeItem> STABELOPPGRADERING = upgrade("stabeloppgradering", UpgradeItem.Kind.CHEST_STACK);

    // Magiske gjenstander
    public static final DeferredItem<RecallAmuletItem> GJENKALLINGSAMULETT = ITEMS.register("gjenkallingsamulett",
            () -> new RecallAmuletItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    // Skyggestaal-verktoy og rustning
    public static final DeferredItem<SwordItem> SKYGGESTAAL_SVERD = ITEMS.register("skyggestaal_sverd",
            () -> new SwordItem(ModMaterials.SKYGGESTAAL_TIER, new Item.Properties().fireResistant()
                    .attributes(SwordItem.createAttributes(ModMaterials.SKYGGESTAAL_TIER, 3, -2.4f))));
    public static final DeferredItem<PickaxeItem> SKYGGESTAAL_HAKKE = ITEMS.register("skyggestaal_hakke",
            () -> new PickaxeItem(ModMaterials.SKYGGESTAAL_TIER, new Item.Properties().fireResistant()
                    .attributes(PickaxeItem.createAttributes(ModMaterials.SKYGGESTAAL_TIER, 1.0f, -2.8f))));
    public static final DeferredItem<AxeItem> SKYGGESTAAL_OKS = ITEMS.register("skyggestaal_oks",
            () -> new AxeItem(ModMaterials.SKYGGESTAAL_TIER, new Item.Properties().fireResistant()
                    .attributes(AxeItem.createAttributes(ModMaterials.SKYGGESTAAL_TIER, 5.0f, -3.0f))));
    public static final DeferredItem<ShovelItem> SKYGGESTAAL_SPADE = ITEMS.register("skyggestaal_spade",
            () -> new ShovelItem(ModMaterials.SKYGGESTAAL_TIER, new Item.Properties().fireResistant()
                    .attributes(ShovelItem.createAttributes(ModMaterials.SKYGGESTAAL_TIER, 1.5f, -3.0f))));
    public static final DeferredItem<HoeItem> SKYGGESTAAL_GREIP = ITEMS.register("skyggestaal_greip",
            () -> new HoeItem(ModMaterials.SKYGGESTAAL_TIER, new Item.Properties().fireResistant()
                    .attributes(HoeItem.createAttributes(ModMaterials.SKYGGESTAAL_TIER, -4.0f, 0.0f))));

    public static final DeferredItem<ArmorItem> SKYGGESTAAL_HJELM = ITEMS.register("skyggestaal_hjelm",
            () -> new ArmorItem(ModMaterials.SKYGGESTAAL, ArmorItem.Type.HELMET,
                    new Item.Properties().fireResistant().durability(ArmorItem.Type.HELMET.getDurability(42))));
    public static final DeferredItem<ArmorItem> SKYGGESTAAL_BRYNJE = ITEMS.register("skyggestaal_brynje",
            () -> new ArmorItem(ModMaterials.SKYGGESTAAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().fireResistant().durability(ArmorItem.Type.CHESTPLATE.getDurability(42))));
    public static final DeferredItem<ArmorItem> SKYGGESTAAL_BUKSER = ITEMS.register("skyggestaal_bukser",
            () -> new ArmorItem(ModMaterials.SKYGGESTAAL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().fireResistant().durability(ArmorItem.Type.LEGGINGS.getDurability(42))));
    public static final DeferredItem<ArmorItem> SKYGGESTAAL_STOVLER = ITEMS.register("skyggestaal_stovler",
            () -> new ArmorItem(ModMaterials.SKYGGESTAAL, ArmorItem.Type.BOOTS,
                    new Item.Properties().fireResistant().durability(ArmorItem.Type.BOOTS.getDurability(42))));

    public static final DeferredItem<DeferredSpawnEggItem> SKYGGEVOKTER_SPAWN_EGG = ITEMS.register("skyggevokter_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SKYGGEVOKTER, 0x14141e, 0x7a3cc8, new Item.Properties()));

    private static DeferredItem<Item> simple(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    private static DeferredItem<UpgradeItem> upgrade(String name, UpgradeItem.Kind kind) {
        return ITEMS.register(name, () -> new UpgradeItem(kind, new Item.Properties().stacksTo(16)));
    }

    private ModItems() {}
}
