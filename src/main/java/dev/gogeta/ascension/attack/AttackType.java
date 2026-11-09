package dev.gogeta.ascension.attack;

import dev.gogeta.ascension.form.Form;
import dev.gogeta.ascension.form.TransformationManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public enum AttackType {
    STARDUST_BREAKER("stardust_breaker", "Stardust Breaker", 150.0f, 5.0f),
    KAMEHAMEHA("kamehameha", "Kamehameha", 60.0f, 3.0f),
    BIG_BANG_KAMEHAMEHA("big_bang_kamehameha", "Big Bang Kamehameha", 220.0f, 7.0f),
    METEOR_COMBINATION("meteor_combination", "Meteor Combination", 100.0f, 0.0f);

    private final String id;
    private final String displayName;
    private final float baseDamage;
    private final float baseExplosionRadius;

    AttackType(String id, String displayName, float baseDamage, float baseExplosionRadius) {
        this.id = id;
        this.displayName = displayName;
        this.baseDamage = baseDamage;
        this.baseExplosionRadius = baseExplosionRadius;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public float getBaseDamage() {
        return baseDamage;
    }

    public float getBaseExplosionRadius() {
        return baseExplosionRadius;
    }

    public static AttackType fromId(String id) {
        if (id == null) return null;
        for (AttackType type : values()) {
            if (type.id.equalsIgnoreCase(id) || type.name().equalsIgnoreCase(id)) {
                return type;
            }
        }
        return null;
    }

    public void execute(Player player) {
        Form form = TransformationManager.getForm(player);
        float multiplier = form.getDamageMultiplier();
        float finalDamage = baseDamage * multiplier;
        float finalRadius = baseExplosionRadius * (1.0f + (multiplier - 1.0f) * 0.05f); // scales with form

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.literal(
                "Unleashing " + displayName + "! (Power: " + (int) finalDamage + " DMG)"
            ).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        }

        if (this == METEOR_COMBINATION) {
            MeteorCombinationHandler.execute(player);
        } else {
            // Launch Ki Blast entity
            Vec3 look = player.getLookAngle();
            KiBlastEntity kiBlast = new KiBlastEntity(
                player.level(),
                player,
                this,
                finalDamage,
                finalRadius
            );
            kiBlast.setPos(player.getX() + look.x * 1.5, player.getEyeY() - 0.2 + look.y * 1.5, player.getZ() + look.z * 1.5);
            kiBlast.shoot(look.x, look.y, look.z, 2.2f, 0.0f);
            player.level().addFreshEntity(kiBlast);
        }
    }
}
