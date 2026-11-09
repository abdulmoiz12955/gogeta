package dev.gogeta.ascension.item;

import dev.gogeta.ascension.form.Form;
import dev.gogeta.ascension.form.TransformationManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class AscensionChronicleItem extends Item {
    public AscensionChronicleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Activate Gogeta Base Transformation
            TransformationManager.setForm(serverPlayer, Form.BASE);

            // Display Title & Subtitle on screen
            Component title = Component.literal("GOGETA ASCENSION")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
            Component subtitle = Component.literal("Transformed into the Ultimate Fusion Warrior!")
                .withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC);

            serverPlayer.connection.send(new ClientboundSetTitlesAnimationPacket(10, 70, 20));
            serverPlayer.connection.send(new ClientboundSetTitleTextPacket(title));
            serverPlayer.connection.send(new ClientboundSetSubtitleTextPacket(subtitle));

            // Dramatic Sound & Visual Effects
            ServerLevel serverLevel = serverPlayer.serverLevel();
            serverLevel.playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 2.0f, 1.0f);
            serverLevel.playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1.5f, 1.5f);

            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, serverPlayer.getX(), serverPlayer.getY() + 1.0, serverPlayer.getZ(), 3, 0.5, 0.5, 0.5, 0.0);
            serverLevel.sendParticles(ParticleTypes.FLASH, serverPlayer.getX(), serverPlayer.getY() + 1.0, serverPlayer.getZ(), 2, 0.0, 0.0, 0.0, 0.0);

            serverPlayer.sendSystemMessage(Component.literal("You have awakened the fusion! Gogeta vest, hair, and sash applied.")
                .withStyle(ChatFormatting.YELLOW));
            serverPlayer.sendSystemMessage(Component.literal("Progression: Slay hostile mobs to unlock further Saiyan transformations!")
                .withStyle(ChatFormatting.GREEN));
        }

        player.getCooldowns().addCooldown(this, 40);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("The Sacred Fusion Tome").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        tooltipComponents.add(Component.literal("Right-click to transform into Gogeta with 3D model & hair.").withStyle(ChatFormatting.AQUA));
        tooltipComponents.add(Component.literal("Defeat bosses & mobs to unlock SSJ, SSJ2, Blue, SS4, and Ultra Instinct!").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
