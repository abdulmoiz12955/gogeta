package dev.gogeta.ascension.form;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Random;

public final class UltraInstinctHandler {
    private static final Random RANDOM = new Random();

    private UltraInstinctHandler() {}

    /**
     * Handles incoming damage for players in Ultra Instinct form.
     * Returns true if damage was evaded / cancelled.
     */
    public static boolean handleIncomingDamage(Player player, DamageSource source, float amount) {
        if (!TransformationManager.isInForm(player, Form.ULTRA)) {
            return false;
        }

        // UI can evade almost all direct attacks, except void / kill command / out of world
        if (source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(DamageTypes.GENERIC_KILL)) {
            return false;
        }

        // Automatic Dodge: 95% chance to completely dodge and teleport aside
        if (RANDOM.nextFloat() < 0.95f) {
            dodgeAndTeleport(player);
            return true;
        } else {
            // Even if not completely dodged, high damage resistance: absorbs 90% of damage
            return false; // will be scaled/reduced by LivingDamageEvent
        }
    }

    private static void dodgeAndTeleport(Player player) {
        if (player.level() instanceof ServerLevel serverLevel) {
            Vec3 currentPos = player.position();
            // Teleport a short distance away (afterimage effect)
            double angle = RANDOM.nextDouble() * 2 * Math.PI;
            double distance = 2.5 + RANDOM.nextDouble() * 2.0;
            double targetX = currentPos.x + Math.cos(angle) * distance;
            double targetZ = currentPos.z + Math.sin(angle) * distance;
            double targetY = currentPos.y;

            // Spawn afterimage silver/white particles at old position
            serverLevel.sendParticles(
                new DustParticleOptions(new Vector3f(0.88f, 0.92f, 1.0f), 1.5f),
                currentPos.x, currentPos.y + 1.0, currentPos.z,
                25, 0.4, 0.8, 0.4, 0.05
            );
            serverLevel.sendParticles(
                ParticleTypes.FLASH,
                currentPos.x, currentPos.y + 1.0, currentPos.z,
                1, 0.0, 0.0, 0.0, 0.0
            );

            // Play swift dodge sound
            serverLevel.playSound(
                null, currentPos.x, currentPos.y, currentPos.z,
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS,
                1.0f, 1.8f
            );
            serverLevel.playSound(
                null, currentPos.x, currentPos.y, currentPos.z,
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS,
                0.8f, 1.6f
            );

            // Execute teleport if safe
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.teleportTo(targetX, targetY, targetZ);
            } else {
                player.setPos(targetX, targetY, targetZ);
            }

            // Spawn particles at new arrival position
            serverLevel.sendParticles(
                ParticleTypes.FIREWORK,
                targetX, targetY + 0.5, targetZ,
                8, 0.2, 0.5, 0.2, 0.02
            );
        }
    }
}
