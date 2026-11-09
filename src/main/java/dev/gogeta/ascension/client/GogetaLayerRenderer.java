package dev.gogeta.ascension.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.gogeta.ascension.GogetaAscension;
import dev.gogeta.ascension.form.Form;
import dev.gogeta.ascension.form.TransformationManager;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class GogetaLayerRenderer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public static final ResourceLocation TEXTURE_BASE =
        ResourceLocation.fromNamespaceAndPath(GogetaAscension.MOD_ID, "textures/entity/gogeta_base.png");
    public static final ResourceLocation TEXTURE_SSJ =
        ResourceLocation.fromNamespaceAndPath(GogetaAscension.MOD_ID, "textures/entity/gogeta_ssj.png");
    public static final ResourceLocation TEXTURE_SSJ2 =
        ResourceLocation.fromNamespaceAndPath(GogetaAscension.MOD_ID, "textures/entity/gogeta_ssj2.png");
    public static final ResourceLocation TEXTURE_BLUE =
        ResourceLocation.fromNamespaceAndPath(GogetaAscension.MOD_ID, "textures/entity/gogeta_blue.png");
    public static final ResourceLocation TEXTURE_SS4 =
        ResourceLocation.fromNamespaceAndPath(GogetaAscension.MOD_ID, "textures/entity/gogeta_ss4.png");
    public static final ResourceLocation TEXTURE_ULTRA =
        ResourceLocation.fromNamespaceAndPath(GogetaAscension.MOD_ID, "textures/entity/gogeta_ultra.png");

    private final GogetaHairVestModel<AbstractClientPlayer> model;

    public GogetaLayerRenderer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new GogetaHairVestModel<>(modelSet.bakeLayer(GogetaModelLayers.GOGETA_LAYER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        Form form = TransformationManager.getForm(player);
        if (form == Form.NONE) {
            return;
        }

        ResourceLocation texture = getTextureForForm(form);
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));

        // Sync model part transforms to player animation
        PlayerModel<AbstractClientPlayer> parent = this.getParentModel();
        copyTransform(parent.head, this.model.head);
        copyTransform(parent.body, this.model.body);
        copyTransform(parent.rightArm, this.model.rightArm);
        copyTransform(parent.leftArm, this.model.leftArm);
        copyTransform(parent.rightLeg, this.model.rightLeg);
        copyTransform(parent.leftLeg, this.model.leftLeg);

        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
    }

    private static void copyTransform(net.minecraft.client.model.geom.ModelPart from, net.minecraft.client.model.geom.ModelPart to) {
        to.x = from.x;
        to.y = from.y;
        to.z = from.z;
        to.xRot = from.xRot;
        to.yRot = from.yRot;
        to.zRot = from.zRot;
    }

    private ResourceLocation getTextureForForm(Form form) {
        return switch (form) {
            case BASE -> TEXTURE_BASE;
            case SSJ -> TEXTURE_SSJ;
            case SSJ2 -> TEXTURE_SSJ2;
            case BLUE -> TEXTURE_BLUE;
            case SS4 -> TEXTURE_SS4;
            case ULTRA -> TEXTURE_ULTRA;
            default -> TEXTURE_BASE;
        };
    }
}
