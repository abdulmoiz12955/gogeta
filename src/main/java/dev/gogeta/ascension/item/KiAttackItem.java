package dev.gogeta.ascension.item;

import dev.gogeta.ascension.attack.AttackType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class KiAttackItem extends Item {
    private final AttackType attackType;

    public KiAttackItem(AttackType attackType, Properties properties) {
        super(properties);
        this.attackType = attackType;
    }

    public AttackType getAttackType() {
        return attackType;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            attackType.execute(player);
        }
        player.getCooldowns().addCooldown(this, 30); // 1.5s cooldown
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Signature Attack: " + attackType.getDisplayName()).withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.literal("Base Damage: " + (int)attackType.getBaseDamage() + " HP").withStyle(ChatFormatting.RED));
        tooltipComponents.add(Component.literal("Right-click to unleash. Scales with active Gogeta transformation!").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
