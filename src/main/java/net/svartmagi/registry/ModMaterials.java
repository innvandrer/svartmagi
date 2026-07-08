package net.svartmagi.registry;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.svartmagi.Svartmagi;

public final class ModMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, Svartmagi.MODID);

    /** Skyggestaal: ett hakk over netherite. */
    public static final Tier SKYGGESTAAL_TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            2531, 11.0f, 5.0f, 18,
            () -> Ingredient.of(ModItems.SKYGGESTAAL_BARRE.get()));

    public static final Holder<ArmorMaterial> SKYGGESTAAL = ARMOR_MATERIALS.register("skyggestaal",
            () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.BOOTS, 4);
                        map.put(ArmorItem.Type.LEGGINGS, 7);
                        map.put(ArmorItem.Type.CHESTPLATE, 9);
                        map.put(ArmorItem.Type.HELMET, 4);
                        map.put(ArmorItem.Type.BODY, 12);
                    }),
                    17,
                    SoundEvents.ARMOR_EQUIP_NETHERITE,
                    (Supplier<Ingredient>) () -> Ingredient.of(ModItems.SKYGGESTAAL_BARRE.get()),
                    List.of(new ArmorMaterial.Layer(Svartmagi.id("skyggestaal"))),
                    4.0f,
                    0.15f));

    private ModMaterials() {}
}
