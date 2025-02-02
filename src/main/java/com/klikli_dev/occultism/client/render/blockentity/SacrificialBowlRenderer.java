/*
 * MIT License
 *
 * Copyright 2020 klikli-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.klikli_dev.occultism.client.render.blockentity;

import com.klikli_dev.occultism.common.block.SpiritAttunedCrystalBlock;
import com.klikli_dev.occultism.common.blockentity.SacrificialBowlBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class SacrificialBowlRenderer implements BlockEntityRenderer<SacrificialBowlBlockEntity> {

    //region Initialization
    public SacrificialBowlRenderer(BlockEntityRendererProvider.Context context) {

    }
    //endregion Initialization

    //region Overrides

    //region Static Methods
    public static float getScale(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem itemBlock) {
            if (itemBlock.getBlock() instanceof SpiritAttunedCrystalBlock)
                return 3.0f;
        }
        return 1.0f;
    }
    //endregion Overrides

    @Override
    public void render(SacrificialBowlBlockEntity blockEntity, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        blockEntity.itemStackHandler.ifPresent(handler -> {
            ItemStack stack = handler.getStackInSlot(0);
            long time = blockEntity.getLevel().getGameTime();
            poseStack.pushPose();

            //slowly bob up and down following a sine
            double offset = Math.sin((time - blockEntity.lastChangeTime + partialTicks) / 16) * 0.5f + 0.5f; // * 0.5f + 0.5f;  move sine between 0.0-1.0
            offset = offset / 4.0f; //reduce amplitude
            poseStack.translate(0.5, 0.6 + offset, 0.5);

            //use system time to become independent of game time
            long systemTime = System.currentTimeMillis();
            //rotate item slowly around y axis
            float angle = (systemTime / 16) % 360;
            poseStack.mulPose(Vector3f.YP.rotationDegrees(angle));

            //Fixed scale
            float scale = getScale(stack) * 0.5f;
            poseStack.scale(scale, scale, scale);

            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            BakedModel model = itemRenderer.getModel(stack, blockEntity.getLevel(), null, 0);
            itemRenderer.render(stack, ItemTransforms.TransformType.FIXED, true, poseStack, buffer,
                    combinedLight, combinedOverlay, model);

            poseStack.popPose();
        });
    }
    //endregion Static Methods
}
