package dev.gogeta.ascension.registry;

import dev.gogeta.ascension.GogetaAscension;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GogetaAscension.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> GOGETA_TAB =
        CREATIVE_MODE_TABS.register("gogeta_tab", () ->
            CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + GogetaAscension.MOD_ID))
                .icon(() -> new ItemStack(ModItems.ASCENSION_CHRONICLE.get()))
                .displayItems((parameters, output) -> {
                    output.accept(ModItems.ASCENSION_CHRONICLE.get());
                    output.accept(ModItems.BANKAI_BLADE.get());
                    output.accept(ModItems.STARDUST_BREAKER.get());
                    output.accept(ModItems.KAMEHAMEHA.get());
                    output.accept(ModItems.BIG_BANG_KAMEHAMEHA.get());
                    output.accept(ModItems.METEOR_COMBINATION.get());
                })
                .build()
        );

    private ModCreativeTabs() {}

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
