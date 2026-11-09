package dev.gogeta.ascension.form;

import dev.gogeta.ascension.GogetaAscension;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.util.Random;

public final class TransformationManager {
    public static final String TAG_PREFIX = "gogeta_form_";
    private static final ResourceLocation DAMAGE_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(GogetaAscension.MOD_ID, "form_damage");
    private static final Random RANDOM = new Random();

    private TransformationManager() {}

    public static Form getForm(Player player) {
        if (player == null) return Form.NONE;
        for (Form form : Form.values()) {
            if (form != Form.NONE && player.getTags().contains(TAG_PREFIX + form.getId())) {
                return form;
            }
        }
        return Form.NONE;
    }

    public static boolean isInForm(Player player, Form form) {
        return getForm(player) == form;
    }

    public static void setForm(Player player, Form form) {
        if (player == null) return;
        for (Form f : Form.values()) {
            if (f != Form.NONE) {
                player.removeTag(TAG_PREFIX + f.getId());
            }
        }
        if (form != Form.NONE) {
            player.addTag(TAG_PREFIX + form.getId());
        }
        applyFormBuffs(player, form);
    }

    public static void clearForm(Player player) {
        setForm(player, Form.NONE);
        // Reset flight if not in creative / spectator
        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
        // Remove damage modifier
        AttributeInstance attackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttr != null) {
            attackAttr.removeModifier(DAMAGE_MODIFIER_ID);
        }
    }

    public static void tickPlayer(Player player) {
        Form form = getForm(player);
        if (form == Form.NONE) return;

        // Maintain Flight capability for transformed players
        if (form.canFly() && !player.getAbilities().mayfly) {
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
        }

        // Reapply potion buffs periodically (every second)
        if (player.tickCount % 20 == 0) {
            applyMobEffects(player, form);
        }

        // Spawn ambient aura particles on server side (viewable by everyone)
        if (player.level() instanceof ServerLevel serverLevel && player.tickCount % 2 == 0) {
            spawnAuraParticles(serverLevel, player, form);
        }
    }

    private static void applyFormBuffs(Player player, Form form) {
        AttributeInstance attackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttr != null) {
            attackAttr.removeModifier(DAMAGE_MODIFIER_ID);
            if (form != Form.NONE && form.getDamageMultiplier() > 1.0f) {
                double bonusDamage = (form.getDamageMultiplier() - 1.0f) * 4.0;
                attackAttr.addTransientModifier(new AttributeModifier(
                    DAMAGE_MODIFIER_ID,
                    bonusDamage,
                    AttributeModifier.Operation.ADD_VALUE
                ));
            }
        }
        applyMobEffects(player, form);
    }

    private static void applyMobEffects(Player player, Form form) {
        if (form == Form.NONE) return;
        int duration = 60; // 3 seconds refreshed continuously
        if (form.getSpeedLevel() > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, form.getSpeedLevel() - 1, false, false, false));
        }
        if (form.getStrengthLevel() > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, form.getStrengthLevel() - 1, false, false, false));
        }

        // Resistance and regeneration
        switch (form) {
            case SSJ -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, 0, false, false, false));
            }
            case SSJ2 -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, 1, false, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, duration, 1, false, false, false));
            }
            case BLUE -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, 2, false, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, duration, 0, false, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, duration, 2, false, false, false));
            }
            case SS4 -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, 3, false, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, duration, 1, false, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, duration, 0, false, false, false));
            }
            case ULTRA -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, 4, false, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, duration, 2, false, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.SATURATION, duration, 0, false, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, duration, 0, false, false, false));
            }
            default -> {}
        }
    }

    private static void spawnAuraParticles(ServerLevel level, Player player, Form form) {
        double px = player.getX();
        double py = player.getY();
        double pz = player.getZ();

        int color = form.getAuraColor();
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        int count = form == Form.ULTRA ? 8 : (form == Form.BLUE || form == Form.SS4 ? 6 : 4);
        for (int i = 0; i < count; i++) {
            double ox = (RANDOM.nextDouble() - 0.5) * 1.2;
            double oy = RANDOM.nextDouble() * 2.0;
            double oz = (RANDOM.nextDouble() - 0.5) * 1.2;

            level.sendParticles(
                new DustParticleOptions(new Vector3f(r, g, b), 1.2f),
                px + ox, py + oy, pz + oz,
                1, 0, 0.15, 0, 0.05
            );
        }

        // SSJ2 and SS4 lightning sparks
        if (form == Form.SSJ2 || form == Form.SS4) {
            if (RANDOM.nextFloat() < 0.4f) {
                level.sendParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    px + (RANDOM.nextDouble() - 0.5) * 1.5,
                    py + RANDOM.nextDouble() * 2.2,
                    pz + (RANDOM.nextDouble() - 0.5) * 1.5,
                    2, 0.05, 0.05, 0.05, 0.1
                );
            }
        }

        // Ultra Instinct glowing flame/flash aura
        if (form == Form.ULTRA) {
            if (RANDOM.nextFloat() < 0.3f) {
                level.sendParticles(
                    ParticleTypes.END_ROD,
                    px + (RANDOM.nextDouble() - 0.5) * 1.2,
                    py + 0.1,
                    pz + (RANDOM.nextDouble() - 0.5) * 1.2,
                    1, 0, 0.2, 0, 0.02
                );
            }
        }
    }
}
