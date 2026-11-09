package dev.gogeta.ascension.client;

import dev.gogeta.ascension.GogetaAscension;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = GogetaAscension.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class GogetaAscensionClient {

    private GogetaAscensionClient() {}

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(GogetaModelLayers.GOGETA_LAYER, GogetaHairVestModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(dev.gogeta.ascension.registry.ModEntities.KI_BLAST.get(), InvisibleEntityRenderer::new);
        event.registerEntityRenderer(dev.gogeta.ascension.registry.ModEntities.GETSUGA.get(), InvisibleEntityRenderer::new);
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        for (net.minecraft.client.resources.PlayerSkin.Model skin : event.getSkins()) {
            if (event.getSkin(skin) instanceof PlayerRenderer renderer) {
                renderer.addLayer(new GogetaLayerRenderer(renderer, event.getEntityModels()));
            }
        }
    }

    @EventBusSubscriber(modid = GogetaAscension.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class GameClientEvents {
        @SubscribeEvent
        public static void onPlayerTick(PlayerTickEvent.Post event) {
            Player player = event.getEntity();
            if (player != null && player.level().isClientSide()) {
                AuraParticleHandler.clientTick(player);
            }
        }
    }
}
