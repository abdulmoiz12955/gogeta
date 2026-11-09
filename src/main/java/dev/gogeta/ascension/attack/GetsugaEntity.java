package dev.gogeta.ascension.attack;

import dev.gogeta.ascension.GogetaAscension;
import dev.gogeta.ascension.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;
import java.util.Random;

public class GetsugaEntity extends ThrowableProjectile {
    private static final EntityDataAccessor<Float> DAMAGE =
        SynchedEntityData.defineId(GetsugaEntity.class, EntityDataSerializers.FLOAT);
    private static final Random RANDOM = new Random();

    public GetsugaEntity(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public GetsugaEntity(Level level, LivingEntity shooter, float damage) {
        super(ModEntities.GETSUGA.get(), shooter, level);
        this.setNoGravity(true);
        this.setDamage(damage);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DAMAGE, 800.0f);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 pos = position();
        // Black and Gold projectile particles
        if (this.level().isClientSide()) {
            for (int i = 0; i < 4; i++) {
                // Black ki core
                this.level().addParticle(
                    new DustParticleOptions(new Vector3f(0.05f, 0.05f, 0.05f), 2.0f),
                    pos.x + (RANDOM.nextDouble() - 0.5) * 1.5,
                    pos.y + (RANDOM.nextDouble() - 0.5) * 0.8,
                    pos.z + (RANDOM.nextDouble() - 0.5) * 1.5,
                    0, 0, 0
                );
                // Gold edge energy
                this.level().addParticle(
                    new DustParticleOptions(new Vector3f(1.0f, 0.84f, 0.0f), 1.8f),
                    pos.x + (RANDOM.nextDouble() - 0.5) * 1.8,
                    pos.y + (RANDOM.nextDouble() - 0.5) * 1.0,
                    pos.z + (RANDOM.nextDouble() - 0.5) * 1.8,
                    0, 0, 0
                );
            }
            this.level().addParticle(ParticleTypes.CRIT, pos.x, pos.y, pos.z, 0, 0, 0);
        } else {
            if (this.tickCount > 100) {
                this.discard();
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide()) {
            explode();
            this.discard();
        }
    }

    private void explode() {
        Level level = this.level();
        Vec3 pos = position();
        float damage = getDamage();
        float radius = 8.0f; // Large impact crater

        AABB aabb = new AABB(pos.x - radius, pos.y - radius, pos.z - radius,
                             pos.x + radius, pos.y + radius, pos.z + radius);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, aabb);
        Entity owner = getOwner();

        for (LivingEntity target : targets) {
            if (target != owner) {
                target.hurt(level.damageSources().indirectMagic(this, owner), damage);
                Vec3 dir = target.position().subtract(pos).normalize();
                target.push(dir.x * 3.0, 1.2, dir.z * 3.0);
            }
        }

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.WEATHER, 4.0f, 0.6f);
            serverLevel.playSound(null, pos.x, pos.y, pos.z, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 3.0f, 1.2f);

            // Black and gold shockwave particles
            for (int i = 0; i < 80; i++) {
                Vector3f c = (i % 2 == 0) ? new Vector3f(0.05f, 0.05f, 0.05f) : new Vector3f(1.0f, 0.84f, 0.0f);
                double vx = (RANDOM.nextDouble() - 0.5) * 2.0;
                double vy = (RANDOM.nextDouble() - 0.5) * 2.0;
                double vz = (RANDOM.nextDouble() - 0.5) * 2.0;
                serverLevel.sendParticles(new DustParticleOptions(c, 2.5f), pos.x, pos.y, pos.z, 1, vx, vy, vz, 0.4);
            }
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, pos.x, pos.y, pos.z, 5, 1.5, 1.5, 1.5, 0.1);

            // Large crater
            boolean canDestroyTerrain = serverLevel.getGameRules().getBoolean(GogetaAscension.RULE_TERRAIN_DESTRUCTION);
            if (canDestroyTerrain) {
                destroyTerrain(serverLevel, pos, 6);
            }
        }
    }

    private void destroyTerrain(ServerLevel level, Vec3 center, int r) {
        int cx = (int) center.x;
        int cy = (int) center.y;
        int cz = (int) center.z;

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    if (x * x + y * y + z * z <= r * r) {
                        BlockPos bpos = new BlockPos(cx + x, cy + y, cz + z);
                        if (!level.getBlockState(bpos).is(Blocks.BEDROCK)) {
                            level.destroyBlock(bpos, false);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Damage", entityData.get(DAMAGE));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Damage")) entityData.set(DAMAGE, tag.getFloat("Damage"));
    }
}
