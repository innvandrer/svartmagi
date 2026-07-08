package net.svartmagi.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.svartmagi.Svartmagi;
import net.svartmagi.entity.SkyggevokterEntity;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Svartmagi.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<SkyggevokterEntity>> SKYGGEVOKTER =
            ENTITY_TYPES.register("skyggevokter", () -> EntityType.Builder
                    .of(SkyggevokterEntity::new, MobCategory.MONSTER)
                    .sized(0.9f, 2.6f)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .build("skyggevokter"));

    private ModEntities() {}
}
