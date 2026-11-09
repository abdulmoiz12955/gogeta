package dev.gogeta.ascension.client;

import dev.gogeta.ascension.form.Form;
import dev.gogeta.ascension.form.TransformationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

import java.util.Random;

public final class AuraParticleHandler {
    private static final Random RANDOM = new Random();

    private AuraParticleHandler() {}

    public static void clientTick(Player player) {
        if (player == null) return;
        Level level = player.level();
        if (level == null || !level.isClientSide()) return;

        Form form = TransformationManager.getForm(player);
        if (form == Form.NONE) return;

        double px = player.getX();
        double py = player.getY();
        double pz = player.getZ();

        int color = form.getAuraColor();
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        // Rising flame aura particles
        for (int i = 0; i < 3; i++) {
            double ox = (RANDOM.nextDouble() - 0.5) * 1.0;
            double oy = RANDOM.nextDouble() * 1.8;
            double oz = (RANDOM.nextDouble() - 0.5) * 1.0;

            level.addParticle(
                new DustParticleOptions(new Vector3f(r, g, b), 1.4f),
                px + ox, py + oy, pz + oz,
                0, 0.2, 0
            );
        }

        // Form-specific extra client visual flairs
        switch (form) {
            case SSJ2 -> {
                if (RANDOM.nextFloat() < 0.35f) {
                    level.addParticle(
                        ParticleTypes.ELECTRIC_SPARK,
                        px + (RANDOM.nextDouble() - 0.5) * 1.4,
                        py + RANDOM.nextDouble() * 2.0,
                        pz + (RANDOM.nextDouble() - 0.5) * 1.4,
                        0, 0.05, 0
                    );
                }
            }
            case BLUE -> {
                if (RANDOM.nextFloat() < 0.4f) {
                    level.addParticle(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        px + (RANDOM.nextDouble() - 0.5) * 1.2,
                        py + 0.1,
                        pz + (RANDOM.nextDouble() - 0.5) * 1.2,
                        0, 0.15, 0
                    );
                }
            }
            case SS4 -> {
                if (RANDOM.nextFloat() < 0.4f) {
                    level.addParticle(
                        ParticleTypes.FLAME,
                        px + (RANDOM.nextDouble() - 0.5) * 1.2,
                        py + 0.1,
                        pz + (RANDOM.nextDouble() - 0.5) * 1.2,
                        0, 0.15, 0
                    );
                }
            }
            case ULTRA -> {
                // Ultra Instinct ethereal aura shimmer
                for (int i = 0; i < 2; i++) {
                    level.addParticle(
                        ParticleTypes.END_ROD,
                        px + (RANDOM.nextDouble() - 0.5) * 1.4,
                        py + RANDOM.nextDouble() * 2.0,
                        pz + (RANDOM.nextDouble() - 0.5) * 1.4,
                        (RANDOM.nextDouble() - 0.5) * 0.04,
                        0.1,
                        (RANDOM.nextDouble() - 0.5) * 0.04
                    );
                }
            }
            default -> {}
        }
    }
}
