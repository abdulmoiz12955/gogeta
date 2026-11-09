package dev.gogeta.ascension.item;

import dev.gogeta.ascension.attack.GetsugaEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;
import java.util.Random;

public class BankaiBladeItem extends SwordItem {
    private static final int CHARGE_TICKS = 100; // 5 seconds (20 ticks/sec * 5)
    private static final Random RANDOM = new Random();

    public BankaiBladeItem(Properties properties) {
        super(Tiers.NETHERITE, properties);
    }

    public static net.minecraft.world.item.component.ItemAttributeModifiers createAttributes() {
        return net.minecraft.world.item.component.ItemAttributeModifiers.builder()
            .add(
                net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE,
                new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                    BASE_ATTACK_DAMAGE_ID,
                    79.0, // 80 total HP damage (1 base player + 79)
                    net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE
                ),
                net.minecraft.world.entity.EquipmentSlotGroup.MAINHAND
            )
            .add(
                net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED,
                new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                    BASE_ATTACK_SPEED_ID,
                    -2.0,
                    net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE
                ),
                net.minecraft.world.entity.EquipmentSlotGroup.MAINHAND
            )
            .build();
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        // Can cut through virtually all Minecraft blocks instantly
        return 120.0f;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!(entity instanceof Player player)) return;

        // Having it in inventory enables super-speed flight with a silver aura
        if (!player.getAbilities().mayfly) {
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
        }

        // Silver aura around player when blade is present
        if (level.isClientSide() && player.tickCount % 2 == 0) {
            Vec3 pos = player.position();
            level.addParticle(
                new DustParticleOptions(new Vector3f(0.85f, 0.85f, 0.95f), 1.2f),
                pos.x + (RANDOM.nextDouble() - 0.5) * 1.2,
                pos.y + RANDOM.nextDouble() * 2.0,
                pos.z + (RANDOM.nextDouble() - 0.5) * 1.2,
                0, 0.1, 0
            );
        }

        // When flying with blade in inventory, provide super-speed flight
        if (player.getAbilities().flying) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30, 3, false, false, false));
        }

        // Holding the sword grants enhanced strength
        if (isSelected || player.getOffhandItem() == stack) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 4, false, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 2, false, false, false));
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int count) {
        int usedTicks = getUseDuration(stack, livingEntity) - count;

        // Charge sound & particle effects during 5-second charge
        if (livingEntity instanceof Player player) {
            Vec3 pos = player.position();

            if (level.isClientSide()) {
                // Swirling black and gold particles condensing into the blade
                for (int i = 0; i < 3; i++) {
                    Vector3f c = RANDOM.nextBoolean() ? new Vector3f(0.05f, 0.05f, 0.05f) : new Vector3f(1.0f, 0.84f, 0.0f);
                    level.addParticle(
                        new DustParticleOptions(c, 1.8f),
                        pos.x + (RANDOM.nextDouble() - 0.5) * 2.0,
                        pos.y + RANDOM.nextDouble() * 2.0,
                        pos.z + (RANDOM.nextDouble() - 0.5) * 2.0,
                        0, 0.1, 0
                    );
                }
            }

            if (usedTicks % 20 == 0 && level instanceof ServerLevel serverLevel) {
                float pitch = 0.5f + (float) usedTicks / CHARGE_TICKS * 0.8f;
                serverLevel.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BEACON_AMBIENT, SoundSource.PLAYERS, 1.5f, pitch);
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return;

        int usedTicks = getUseDuration(stack, entity) - timeLeft;

        // 5-second charge reached
        if (usedTicks >= CHARGE_TICKS) {
            if (!level.isClientSide()) {
                Vec3 look = player.getLookAngle();
                // 800 HP Getsuga attack
                GetsugaEntity getsuga = new GetsugaEntity(level, player, 800.0f);
                getsuga.setPos(player.getX() + look.x * 1.5, player.getEyeY() - 0.2 + look.y * 1.5, player.getZ() + look.z * 1.5);
                getsuga.shoot(look.x, look.y, look.z, 2.8f, 0.0f);
                level.addFreshEntity(getsuga);

                player.sendSystemMessage(Component.literal("GETSUGA TENSHO! (800 HP Damage)")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.TRIDENT_THUNDER.value(), SoundSource.PLAYERS, 3.0f, 0.8f);
                }
            }
            player.getCooldowns().addCooldown(this, 100); // 5s cooldown
        } else {
            if (!level.isClientSide()) {
                player.sendSystemMessage(Component.literal("Charge cancelled! Must charge for 5 seconds to unleash Getsuga.")
                    .withStyle(ChatFormatting.RED));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Legendary Anime Katana").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
        tooltipComponents.add(Component.literal("Normal Damage: 80 HP").withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.literal("Mining: Slices through almost any block instantly").withStyle(ChatFormatting.AQUA));
        tooltipComponents.add(Component.literal("Holding: Enhanced Strength & Resistance").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.literal("Inventory: Super-speed flight with Silver Aura").withStyle(ChatFormatting.WHITE));
        tooltipComponents.add(Component.literal("Hold Right-Click (5s): Charged Getsuga Tensho (800 HP)").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
