package dev.gogeta.ascension.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

/**
 * 3D Model representing Gogeta's DBS: Broly outfit and hair:
 * - Spiky Super Saiyan / Gogeta hairstyle with single front bang
 * - Black vest with padded orange/yellow neck and shoulder rolls
 * - Blue waist sash / belt
 * - Black/blue wristbands
 * - White baggy trousers layer
 */
public class GogetaHairVestModel<T extends LivingEntity> extends EntityModel<T> {
    public final ModelPart head;
    public final ModelPart body;
    public final ModelPart rightArm;
    public final ModelPart leftArm;
    public final ModelPart rightLeg;
    public final ModelPart leftLeg;

    public GogetaHairVestModel(ModelPart root) {
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Head & Spiky Hair
        PartDefinition head = root.addOrReplaceChild("head",
            CubeListBuilder.create()
                // Base head hair layer overlay
                .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F))
                // Front prominent Gogeta bang (falls on forehead)
                .texOffs(32, 0).addBox(-1.5F, -9.0F, -5.2F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                // Top massive spiky hair block
                .texOffs(32, 7).addBox(-4.5F, -13.0F, -4.5F, 9.0F, 5.0F, 9.0F, new CubeDeformation(0.0F))
                // Rear spiky hair volume
                .texOffs(0, 32).addBox(-4.0F, -11.0F, 2.5F, 8.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.ZERO
        );

        // Body & Vest
        PartDefinition body = root.addOrReplaceChild("body",
            CubeListBuilder.create()
                // Black vest base fitting over chest
                .texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F))
                // Yellow/Orange padded neck roll / shoulder pads
                .texOffs(24, 16).addBox(-5.0F, -1.0F, -3.0F, 10.0F, 4.0F, 6.0F, new CubeDeformation(0.2F))
                // Blue waist sash
                .texOffs(24, 26).addBox(-4.5F, 9.0F, -2.5F, 9.0F, 3.0F, 5.0F, new CubeDeformation(0.3F)),
            PartPose.ZERO
        );

        // Right Arm & Wristband
        PartDefinition rightArm = root.addOrReplaceChild("right_arm",
            CubeListBuilder.create()
                // Black/blue wristband near cuff
                .texOffs(40, 34).addBox(-3.0F, 6.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.25F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
        );

        // Left Arm & Wristband
        PartDefinition leftArm = root.addOrReplaceChild("left_arm",
            CubeListBuilder.create()
                // Black/blue wristband near cuff
                .texOffs(40, 34).addBox(-1.0F, 6.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.25F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
        );

        // Right Leg & White Baggy Trousers
        PartDefinition rightLeg = root.addOrReplaceChild("right_leg",
            CubeListBuilder.create()
                .texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
        );

        // Left Leg & White Baggy Trousers
        PartDefinition leftLeg = root.addOrReplaceChild("left_leg",
            CubeListBuilder.create()
                .texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
        );

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Rotations are synced directly from the parent player model in the layer renderer
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        head.render(poseStack, buffer, packedLight, packedOverlay, color);
        body.render(poseStack, buffer, packedLight, packedOverlay, color);
        rightArm.render(poseStack, buffer, packedLight, packedOverlay, color);
        leftArm.render(poseStack, buffer, packedLight, packedOverlay, color);
        rightLeg.render(poseStack, buffer, packedLight, packedOverlay, color);
        leftLeg.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
