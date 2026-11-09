package dev.gogeta.ascension;

import com.mojang.brigadier.arguments.StringArgumentType;
import dev.gogeta.ascension.attack.AttackType;
import dev.gogeta.ascension.form.Form;
import dev.gogeta.ascension.form.ProgressionHandler;
import dev.gogeta.ascension.form.TransformationManager;
import dev.gogeta.ascension.form.UltraInstinctHandler;
import dev.gogeta.ascension.registry.ModCreativeTabs;
import dev.gogeta.ascension.registry.ModEntities;
import dev.gogeta.ascension.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@Mod(GogetaAscension.MOD_ID)
public final class GogetaAscension {
    public static final String MOD_ID = "gogeta_ascension";
    public static final String FIRST_JOIN_TAG = "gogeta_received_starter_book";

    public static final GameRules.Key<GameRules.BooleanValue> RULE_TERRAIN_DESTRUCTION =
        GameRules.register("gogetaTerrainDestruction", GameRules.Category.MISC, GameRules.BooleanValue.create(true));

    public GogetaAscension(IEventBus modEventBus) {
        ModEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (!player.getTags().contains(FIRST_JOIN_TAG)) {
                player.addTag(FIRST_JOIN_TAG);
                ItemStack book = new ItemStack(ModItems.ASCENSION_CHRONICLE.get());
                if (!player.getInventory().add(book)) {
                    player.drop(book, false);
                }
                player.sendSystemMessage(Component.literal(
                    "You received the Ascension Chronicle! Right-click it to awaken Gogeta."
                ).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            ProgressionHandler.onMobKilled(player, event.getEntity());
        }
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("gogeta")
            .then(Commands.literal("form")
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(ctx -> {
                        if (!(ctx.getSource().getEntity() instanceof ServerPlayer player)) {
                            ctx.getSource().sendFailure(Component.literal("This command must be used by a player."));
                            return 0;
                        }
                        String requested = StringArgumentType.getString(ctx, "name").toLowerCase();
                        Form form = Form.fromId(requested);
                        if (form == Form.NONE && !requested.equals("none")) {
                            player.sendSystemMessage(Component.literal(
                                "Available forms: base, ssj, ssj2, blue, ss4, ultra"
                            ).withStyle(ChatFormatting.YELLOW));
                            return 0;
                        }
                        TransformationManager.setForm(player, form);
                        player.sendSystemMessage(Component.literal(
                            "Transformation activated: " + form.getDisplayName() + " (" + (int)form.getDamageMultiplier() + "x Power)"
                        ).withStyle(form.getChatColor(), ChatFormatting.BOLD));
                        return 1;
                    })))
            .then(Commands.literal("status")
                .executes(ctx -> {
                    if (!(ctx.getSource().getEntity() instanceof ServerPlayer player)) return 0;
                    Form current = TransformationManager.getForm(player);
                    player.sendSystemMessage(Component.literal(
                        "Current form: " + current.getDisplayName() + " [Multiplier: " + current.getDamageMultiplier() + "x]"
                    ).withStyle(current.getChatColor(), ChatFormatting.BOLD));
                    return 1;
                }))
            .then(Commands.literal("clear")
                .executes(ctx -> {
                    if (!(ctx.getSource().getEntity() instanceof ServerPlayer player)) return 0;
                    TransformationManager.clearForm(player);
                    player.sendSystemMessage(Component.literal("Transformation cleared.")
                        .withStyle(ChatFormatting.GRAY));
                    return 1;
                }))
            .then(Commands.literal("attack")
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(ctx -> {
                        if (!(ctx.getSource().getEntity() instanceof ServerPlayer player)) return 0;
                        String attackName = StringArgumentType.getString(ctx, "name").toLowerCase();
                        AttackType attack = AttackType.fromId(attackName);
                        if (attack == null) {
                            player.sendSystemMessage(Component.literal(
                                "Available attacks: stardust_breaker, kamehameha, big_bang_kamehameha, meteor_combination"
                            ).withStyle(ChatFormatting.YELLOW));
                            return 0;
                        }
                        attack.execute(player);
                        return 1;
                    })))
        );
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player != null && !player.level().isClientSide()) {
            TransformationManager.tickPlayer(player);
        }
    }

    @SubscribeEvent
    public void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            // Ultra Instinct auto-dodge check
            if (UltraInstinctHandler.handleIncomingDamage(player, event.getSource(), event.getAmount())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent.Pre event) {
        // Ultra Instinct heavy damage resistance (if dodge didn't trigger, take 90% reduced damage)
        if (event.getEntity() instanceof Player player) {
            if (TransformationManager.isInForm(player, Form.ULTRA)) {
                event.setNewDamage(event.getNewDamage() * 0.1f);
            }
        }

    }
}
