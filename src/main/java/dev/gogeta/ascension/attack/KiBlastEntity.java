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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;
import java.util.Random;

public class KiBlastEntity extends ThrowableProjectile {
    private static final EntityDataAccessor<String> ATTACK_TYPE =
        SynchedEntityData.defineId(KiBlastEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> DAMAGE =
        SynchedEntityData.defineId(KiBlastEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RADIUS =
        SynchedEntityData.defineId(KiBlastEntity.class, EntityDataSerializers.FLOAT);

    private static final Random RANDOM = new Random();
    private static final Vector3f[] RAINBOW_COLORS = new Vector3f[] {
        new Vector3f(1.0f, 0.0f, 0.0f), // Red
        new Vector3f(1.0f, 0.5f, 0.0f), // Orange
        new Vector3f(1.0f, 1.0f, 0.0f), // Yellow
        new Vector3f(0.0f, 1.0f, 0.0f), // Green
        new Vector3f(0.0f, 0.8f, 1.0f), // Cyan
        new Vector3f(0.0f, 0.0f, 1.0f), // Blue
        new Vector3f(0.8f, 0.0f, 1.0f)  // Violet
    };

    public KiBlastEntity(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public KiBlastEntity(Level level, LivingEntity shooter, AttackType type, float damage, float radius) {
        super(ModEntities.KI_BLAST.get(), shooter, level);
        this.setNoGravity(true);
        this.setAttackType(type);
        this.setDamage(damage);
        this.setRadius(radius);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ATTACK_TYPE, AttackType.KAMEHAMEHA.getId());
        builder.define(DAMAGE, 50.0f);
        builder.define(RADIUS, 3.0f);
    }

    public AttackType getAttackType() {
        return AttackType.fromId(this.entityData.get(ATTACK_TYPE));
    }

    public void setAttackType(AttackType type) {
        this.entityData.set(ATTACK_TYPE, type.getId());
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public float getRadius() {
        return this.entityData.get(RADIUS);
    }

    public void setRadius(float radius) {
        this.entityData.set(RADIUS, radius);
    }

    @Override
    public void tick() {
        super.tick();

        // Trail particles
        AttackType type = getAttackType();
        Vec3 pos = position();

        if (this.level().isClientSide()) {
            spawnClientTrail(type, pos);
        } else {
            // Despawn after 100 ticks (5 seconds) to avoid floating forever
            if (this.tickCount > 100) {
                this.discard();
            }
        }
    }

    private void spawnClientTrail(AttackType type, Vec3 pos) {
        if (type == AttackType.STARDUST_BREAKER) {
            // Genuinely rainbow-coloured energy particles
            for (Vector3f color : RAINBOW_COLORS) {
                this.level().addParticle(
                    new DustParticleOptions(color, 1.5f),
                    pos.x + (RANDOM.nextDouble() - 0.5) * 0.4,
                    pos.y + (RANDOM.nextDouble() - 0.5) * 0.4,
                    pos.z + (RANDOM.nextDouble() - 0.5) * 0.4,
                    0, 0, 0
                );
            }
            this.level().addParticle(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, 0, 0, 0);
        } else if (type == AttackType.BIG_BANG_KAMEHAMEHA) {
            // Golden and blue fusion energy
            this.level().addParticle(new DustParticleOptions(new Vector3f(1.0f, 0.85f, 0.0f), 2.0f), pos.x, pos.y, pos.z, 0, 0, 0);
            this.level().addParticle(new DustParticleOptions(new Vector3f(0.0f, 0.8f, 1.0f), 2.0f), pos.x, pos.y, pos.z, 0, 0, 0);
            this.level().addParticle(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 0, 0, 0);
        } else {
            // Kamehameha: Bright cyan/blue beam
            this.level().addParticle(new DustParticleOptions(new Vector3f(0.1f, 0.9f, 1.0f), 1.6f), pos.x, pos.y, pos.z, 0, 0, 0);
            this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, pos.x, pos.y, pos.z, 0, 0, 0);
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
        AttackType type = getAttackType();
        float damage = getDamage();
        float radius = getRadius();

        // Area of Effect Damage
        AABB aabb = new AABB(pos.x - radius * 1.5, pos.y - radius * 1.5, pos.z - radius * 1.5,
                             pos.x + radius * 1.5, pos.y + radius * 1.5, pos.z + radius * 1.5);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, aabb);
        Entity owner = getOwner();

        for (LivingEntity target : targets) {
            if (target != owner) {
                target.hurt(level.damageSources().indirectMagic(this, owner), damage);
                // Knockback
                Vec3 dir = target.position().subtract(pos).normalize();
                target.push(dir.x * 2.0, 0.6, dir.z * 2.0);
            }
        }

        // Sound & Visuals
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.WEATHER, 3.0f, 0.8f);

            if (type == AttackType.STARDUST_BREAKER) {
                // Expanding rainbow sphere & fireworks
                for (int i = 0; i < 70; i++) {
                    Vector3f c = RAINBOW_COLORS[i % RAINBOW_COLORS.length];
                    double vx = (RANDOM.nextDouble() - 0.5) * 1.5;
                    double vy = (RANDOM.nextDouble() - 0.5) * 1.5;
                    double vz = (RANDOM.nextDouble() - 0.5) * 1.5;
                    serverLevel.sendParticles(new DustParticleOptions(c, 2.5f), pos.x, pos.y, pos.z, 1, vx, vy, vz, 0.3);
                }
                serverLevel.sendParticles(ParticleTypes.FLASH, pos.x, pos.y, pos.z, 3, 0, 0, 0, 0);
            } else if (type == AttackType.BIG_BANG_KAMEHAMEHA) {
                serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, pos.x, pos.y, pos.z, 5, 1.0, 1.0, 1.0, 0.1);
            } else {
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y, pos.z, 4, 0.5, 0.5, 0.5, 0.1);
            }

            // Block & Terrain Destruction Option
            boolean canDestroyTerrain = serverLevel.getGameRules().getBoolean(GogetaAscension.RULE_TERRAIN_DESTRUCTION);
            if (canDestroyTerrain && radius > 0.5f) {
                destroyTerrain(serverLevel, pos, (int) Math.min(radius, 8.0f));
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
        tag.putString("AttackType", entityData.get(ATTACK_TYPE));
        tag.putFloat("Damage", entityData.get(DAMAGE));
        tag.putFloat("Radius", entityData.get(RADIUS));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("AttackType")) entityData.set(ATTACK_TYPE, tag.getString("AttackType"));
        if (tag.contains("Damage")) entityData.set(DAMAGE, tag.getFloat("Damage"));
        if (tag.contains("Radius")) entityData.set(RADIUS, tag.getFloat("Radius"));
    }
}
