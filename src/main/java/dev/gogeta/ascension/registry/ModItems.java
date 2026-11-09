package dev.gogeta.ascension.registry;

import dev.gogeta.ascension.GogetaAscension;
import dev.gogeta.ascension.attack.AttackType;
import dev.gogeta.ascension.item.AscensionChronicleItem;
import dev.gogeta.ascension.item.BankaiBladeItem;
import dev.gogeta.ascension.item.KiAttackItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(Registries.ITEM, GogetaAscension.MOD_ID);

    public static final DeferredHolder<Item, AscensionChronicleItem> ASCENSION_CHRONICLE =
        ITEMS.register("ascension_chronicle", () ->
            new AscensionChronicleItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC))
        );

    public static final DeferredHolder<Item, BankaiBladeItem> BANKAI_BLADE =
        ITEMS.register("bankai_blade", () ->
            new BankaiBladeItem(
                new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
                    .durability(5000)
                    .attributes(BankaiBladeItem.createAttributes())
            )
        );

    public static final DeferredHolder<Item, KiAttackItem> STARDUST_BREAKER =
        ITEMS.register("stardust_breaker_scroll", () ->
            new KiAttackItem(AttackType.STARDUST_BREAKER, new Item.Properties().stacksTo(1).rarity(Rarity.RARE))
        );

    public static final DeferredHolder<Item, KiAttackItem> KAMEHAMEHA =
        ITEMS.register("kamehameha_scroll", () ->
            new KiAttackItem(AttackType.KAMEHAMEHA, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON))
        );

    public static final DeferredHolder<Item, KiAttackItem> BIG_BANG_KAMEHAMEHA =
        ITEMS.register("big_bang_kamehameha_scroll", () ->
            new KiAttackItem(AttackType.BIG_BANG_KAMEHAMEHA, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC))
        );

    public static final DeferredHolder<Item, KiAttackItem> METEOR_COMBINATION =
        ITEMS.register("meteor_combination_scroll", () ->
            new KiAttackItem(AttackType.METEOR_COMBINATION, new Item.Properties().stacksTo(1).rarity(Rarity.RARE))
        );

    private ModItems() {}

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
