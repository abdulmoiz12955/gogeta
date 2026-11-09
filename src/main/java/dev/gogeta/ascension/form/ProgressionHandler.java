package dev.gogeta.ascension.form;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.warden.Warden;
import org.joml.Vector3f;

public final class ProgressionHandler {
    public static final String UNLOCK_TAG_PREFIX = "gogeta_unlocked_";

    private ProgressionHandler() {}

    public static boolean isFormUnlocked(ServerPlayer player, Form form) {
        if (form == Form.NONE || form == Form.BASE) return true;
        return player.getTags().contains(UNLOCK_TAG_PREFIX + form.getId());
    }

    public static void unlockForm(ServerPlayer player, Form form, String mobDefeated) {
        if (isFormUnlocked(player, form)) return;

        player.addTag(UNLOCK_TAG_PREFIX + form.getId());

        // Automatically ascend to newly unlocked form!
        TransformationManager.setForm(player, form);

        // Display Epic Unlock Title
        Component title = Component.literal("FORM AWAKENED!")
            .withStyle(form.getChatColor(), ChatFormatting.BOLD);
        Component subtitle = Component.literal("Unlocked " + form.getDisplayName() + " by defeating " + mobDefeated + "!")
            .withStyle(ChatFormatting.GOLD);

        player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 70, 20));
        player.connection.send(new ClientboundSetTitleTextPacket(title));
        player.connection.send(new ClientboundSetSubtitleTextPacket(subtitle));

        ServerLevel level = player.serverLevel();
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 2.5f, 1.0f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENDER_DRAGON_GROWL, SoundSource.PLAYERS, 1.5f, 1.2f);

        int c = form.getAuraColor();
        float r = ((c >> 16) & 0xFF) / 255.0f;
        float g = ((c >> 8) & 0xFF) / 255.0f;
        float b = (c & 0xFF) / 255.0f;
        level.sendParticles(new DustParticleOptions(new Vector3f(r, g, b), 2.5f), player.getX(), player.getY() + 1.0, player.getZ(), 50, 1.0, 1.0, 1.0, 0.2);
        level.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 1.0, player.getZ(), 3, 0.0, 0.0, 0.0, 0.0);
    }

    public static void onMobKilled(ServerPlayer player, LivingEntity killed) {
        if (killed instanceof Zombie) {
            unlockForm(player, Form.SSJ, "Zombie");
        } else if (killed instanceof EnderMan || killed instanceof WitherSkeleton) {
            unlockForm(player, Form.SSJ2, killed.getType().getDescription().getString());
        } else if (killed instanceof EnderDragon) {
            unlockForm(player, Form.BLUE, "Ender Dragon");
        } else if (killed instanceof WitherBoss) {
            unlockForm(player, Form.SS4, "The Wither");
        } else if (killed instanceof Warden) {
            unlockForm(player, Form.ULTRA, "The Warden");
        }
    }
}
