package com.japaricraft.japaricraftmod.render;


import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelKouteiPengin extends ModelBiped{
    public ModelRenderer bipedLeftArmwear;
    public ModelRenderer bipedRightArmwear;
    public ModelRenderer bipedLeftLegwear;
    public ModelRenderer bipedRightLegwear;
    public ModelRenderer bipedBodyWear;


    public ModelKouteiPengin()
    {
        this(0.0F);
    }
    protected ModelKouteiPengin(float modelSize) {

        super(modelSize, 0.0F, 64, 64);
        {this.bipedLeftArm = new ModelRenderer(this, 32, 48);
            this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
            this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
            this.bipedLeftLeg = new ModelRenderer(this, 16, 48);
            this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
            this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
            this.bipedBodyWear = new ModelRenderer(this, 16, 32);
            this.bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize + 0.25F);
            this.bipedBodyWear.setRotationPoint(0.0F, 0.0F, 0.0F);
        }
    }



    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        // 描画
        bipedLeftArm.render(f5);
        bipedRightArm.render(f5);
        bipedHead.render(f5);
        bipedBody.render(f5);
        bipedBodyWear.render(f5);
        bipedRightLeg.render(f5);
        bipedLeftLeg.render(f5);
        bipedHeadwear.render(f5);


    }
    @Override
    public void postRenderArm(float scale, EnumHandSide side)
    {

        float f = side == EnumHandSide.RIGHT ? 1.0F : -1.0F;
        ModelRenderer modelrenderer = this.getArmForSide(side);
        modelrenderer.rotationPointX += f;
        modelrenderer.postRender(scale);
        modelrenderer.rotationPointX -= f;

    }

}

