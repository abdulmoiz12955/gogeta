package dev.gogeta.ascension.registry;

import dev.gogeta.ascension.GogetaAscension;
import dev.gogeta.ascension.attack.GetsugaEntity;
import dev.gogeta.ascension.attack.KiBlastEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
        DeferredRegister.create(Registries.ENTITY_TYPE, GogetaAscension.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<KiBlastEntity>> KI_BLAST =
        ENTITIES.register("ki_blast", () ->
            EntityType.Builder.<KiBlastEntity>of(KiBlastEntity::new, MobCategory.MISC)
                .sized(0.8F, 0.8F)
                .clientTrackingRange(64)
                .updateInterval(1)
                .build("ki_blast")
        );

    public static final DeferredHolder<EntityType<?>, EntityType<GetsugaEntity>> GETSUGA =
        ENTITIES.register("getsuga", () ->
            EntityType.Builder.<GetsugaEntity>of(GetsugaEntity::new, MobCategory.MISC)
                .sized(2.0F, 1.0F)
                .clientTrackingRange(64)
                .updateInterval(1)
                .build("getsuga")
        );

    private ModEntities() {}

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
