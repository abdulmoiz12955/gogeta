package dev.gogeta.ascension.client;

import dev.gogeta.ascension.GogetaAscension;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public final class GogetaModelLayers {
    public static final ModelLayerLocation GOGETA_LAYER = new ModelLayerLocation(
        ResourceLocation.fromNamespaceAndPath(GogetaAscension.MOD_ID, "gogeta_layer"),
        "main"
    );

    private GogetaModelLayers() {}
}
