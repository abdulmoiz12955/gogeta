package dev.gogeta.ascension.attack;

import dev.gogeta.ascension.form.Form;
import dev.gogeta.ascension.form.TransformationManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class MeteorCombinationHandler {
    private MeteorCombinationHandler() {}

    public static void execute(Player player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) return;

        Form form = TransformationManager.getForm(player);
        float multiplier = form.getDamageMultiplier();
        float damage = 100.0f * multiplier;

        Vec3 look = player.getLookAngle();
        Vec3 start = player.position();
        Vec3 dashTarget = start.add(look.scale(8.0));

        // Dash forward
        player.teleportTo(dashTarget.x, dashTarget.y, dashTarget.z);
        player.setDeltaMovement(look.scale(1.5));

        // Swift slash sound & sweep particles
        serverLevel.playSound(null, dashTarget.x, dashTarget.y, dashTarget.z, SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 2.0f, 1.2f);
        serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, dashTarget.x, dashTarget.y + 1.0, dashTarget.z, 5, 0.5, 0.5, 0.5, 0.1);
        serverLevel.sendParticles(ParticleTypes.CRIT, dashTarget.x, dashTarget.y + 1.0, dashTarget.z, 20, 0.8, 0.8, 0.8, 0.2);

        // Hit enemies along dash path
        AABB hitBox = new AABB(
            Math.min(start.x, dashTarget.x) - 2, Math.min(start.y, dashTarget.y) - 1, Math.min(start.z, dashTarget.z) - 2,
            Math.max(start.x, dashTarget.x) + 2, Math.max(start.y, dashTarget.y) + 3, Math.max(start.z, dashTarget.z) + 2
        );

        List<LivingEntity> enemies = serverLevel.getEntitiesOfClass(LivingEntity.class, hitBox, e -> e != player && e.isAlive());
        for (LivingEntity enemy : enemies) {
            enemy.hurt(serverLevel.damageSources().playerAttack(player), damage);
            enemy.push(look.x * 2.5, 0.8, look.z * 2.5);
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, enemy.getX(), enemy.getY() + 1.0, enemy.getZ(), 1, 0, 0, 0, 0);
        }
    }
}
