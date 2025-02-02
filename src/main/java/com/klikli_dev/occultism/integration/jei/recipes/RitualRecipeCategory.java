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

package com.klikli_dev.occultism.integration.jei.recipes;

import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.occultism.Occultism;
import com.klikli_dev.occultism.crafting.recipe.RitualRecipe;
import com.klikli_dev.occultism.integration.jei.JeiRecipeTypes;
import com.klikli_dev.occultism.registry.OccultismBlocks;
import com.klikli_dev.occultism.registry.OccultismItems;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RitualRecipeCategory implements IRecipeCategory<RitualRecipe> {

    private final IDrawable background;
    private final IDrawable arrow;
    private final Component localizedName;
    private final String pentacle;
    private final ItemStack goldenSacrificialBowl = new ItemStack(OccultismBlocks.GOLDEN_SACRIFICIAL_BOWL.get());
    private final ItemStack sacrificialBowl = new ItemStack(OccultismBlocks.SACRIFICIAL_BOWL.get());
    private final int iconWidth = 16;
    private final int ritualCenterX;
    private final int ritualCenterY;
    private int recipeOutputOffsetX = 50;

    public RitualRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(168, 120); //64
        this.ritualCenterX = this.background.getWidth() / 2 - this.iconWidth / 2 - 30;
        this.ritualCenterY = this.background.getHeight() / 2 - this.iconWidth / 2 + 20;
        this.localizedName = Component.translatable(Occultism.MODID + ".jei.ritual");
        this.pentacle = I18n.get(Occultism.MODID + ".jei.pentacle");
        this.goldenSacrificialBowl.getOrCreateTag().putBoolean("RenderFull", true);
        this.sacrificialBowl.getOrCreateTag().putBoolean("RenderFull", true);
        this.arrow = guiHelper.createDrawable(
                new ResourceLocation(Occultism.MODID, "textures/gui/jei/arrow.png"), 0, 0, 64, 46);
    }

    protected int getStringCenteredMaxX(Font font, Component text, int x, int y) {
        int width = font.width(text);
        int actualX = (int) (x - width / 2.0f);
        return actualX + width;
    }

    protected void drawStringCentered(PoseStack poseStack, Font font, Component text, int x, int y) {
        font.draw(poseStack, text, (x - font.width(text) / 2.0f), y, 0);
    }

    protected void drawStringCentered(PoseStack poseStack, Font font, FormattedCharSequence text, int x, int y) {
        font.draw(poseStack, text, (x - font.width(text) / 2.0f), y, 0);
    }

    @Override
    public RecipeType<RitualRecipe> getRecipeType() {
        return JeiRecipeTypes.RITUAL;
    }

    @Override
    public Component getTitle() {
        return this.localizedName;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RitualRecipe recipe, IFocusGroup focuses) {
        this.recipeOutputOffsetX = 75;

        //draw activation item on top of bowl
        builder.addSlot(RecipeIngredientRole.INPUT, this.ritualCenterX, this.ritualCenterY - 5)
                .addIngredients(recipe.getActivationItem());

        //draw the sacrificial bowl in the center
        builder.addSlot(RecipeIngredientRole.CATALYST, this.ritualCenterX, this.ritualCenterY)
                .addItemStack(this.goldenSacrificialBowl);

        int sacrificialCircleRadius = 30;
        int sacricialBowlPaddingVertical = 20;
        int sacricialBowlPaddingHorizontal = 15;
        List<Vec3i> sacrificialBowlPosition = Stream.of(
                //first the 4 centers of each side
                new Vec3i(this.ritualCenterX, this.ritualCenterY - sacrificialCircleRadius, 0),
                new Vec3i(this.ritualCenterX + sacrificialCircleRadius, this.ritualCenterY, 0),
                new Vec3i(this.ritualCenterX, this.ritualCenterY + sacrificialCircleRadius, 0),
                new Vec3i(this.ritualCenterX - sacrificialCircleRadius, this.ritualCenterY, 0),

                //then clockwise of the enter the next 4
                new Vec3i(this.ritualCenterX + sacricialBowlPaddingHorizontal,
                        this.ritualCenterY - sacrificialCircleRadius,
                        0),
                new Vec3i(this.ritualCenterX + sacrificialCircleRadius,
                        this.ritualCenterY - sacricialBowlPaddingVertical, 0),
                new Vec3i(this.ritualCenterX - sacricialBowlPaddingHorizontal,
                        this.ritualCenterY + sacrificialCircleRadius,
                        0),
                new Vec3i(this.ritualCenterX - sacrificialCircleRadius,
                        this.ritualCenterY + sacricialBowlPaddingVertical, 0),

                //then counterclockwise of the center the last 4
                new Vec3i(this.ritualCenterX - sacricialBowlPaddingHorizontal,
                        this.ritualCenterY - sacrificialCircleRadius,
                        0),
                new Vec3i(this.ritualCenterX + sacrificialCircleRadius,
                        this.ritualCenterY + sacricialBowlPaddingVertical, 0),
                new Vec3i(this.ritualCenterX + sacricialBowlPaddingHorizontal,
                        this.ritualCenterY + sacrificialCircleRadius,
                        0),
                new Vec3i(this.ritualCenterX - sacrificialCircleRadius,
                        this.ritualCenterY - sacricialBowlPaddingVertical, 0)
        ).collect(Collectors.toList());


        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            Vec3i pos = sacrificialBowlPosition.get(i);

            builder.addSlot(RecipeIngredientRole.INPUT, pos.getX(), pos.getY() - 5)
                    .addIngredients(recipe.getIngredients().get(i));

            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, pos.getX(), pos.getY())
                    .addItemStack(this.sacrificialBowl);
        }

        //ingredients: 0: recipe output, 1: ritual dummy item

        //draw recipe output on the left
        if (recipe.getResultItem().getItem() != OccultismItems.JEI_DUMMY_NONE.get()) {
            //if we have an item output -> render it
            builder.addSlot(RecipeIngredientRole.OUTPUT, this.ritualCenterX + this.recipeOutputOffsetX, this.ritualCenterY - 5)
                    .addItemStack(recipe.getResultItem());
        } else {
            //if not, we instead render our ritual dummy item, just like in the corner
            builder.addSlot(RecipeIngredientRole.OUTPUT, this.ritualCenterX + this.recipeOutputOffsetX, this.ritualCenterY - 5)
                    .addItemStack(recipe.getRitualDummy());
        }

        //draw output golden bowl
        builder.addSlot(RecipeIngredientRole.CATALYST, this.ritualCenterX + this.recipeOutputOffsetX, this.ritualCenterY)
                .addItemStack(this.goldenSacrificialBowl);

        //draw ritual dummy item in upper left corner
        builder.addSlot(RecipeIngredientRole.OUTPUT, 0, 0)
                .addItemStack(recipe.getRitualDummy());


        //draw item to use
        if (recipe.requiresItemUse()) {

            int infotextY = 0;

            int lineHeight = Minecraft.getInstance().font.lineHeight;
            var pentacle = ModonomiconAPI.get().getMultiblock(recipe.getPentacleId());

            //simulate pentacle id rendering, to get the correct Y level to draw at
            if (pentacle != null) {
                var pentacleName = Minecraft.getInstance().font.split(Component.translatable(Util.makeDescriptionId("multiblock", pentacle.getId())), 150);

                for (var line : pentacleName) {
                    //Don't actually draw
                    //this.drawStringCentered(poseStack, Minecraft.getInstance().font,  line , infoTextX, infotextY);
                    infotextY += lineHeight;
                }
            }

            //also simulate the info rendered before the item to use for the y level.
            if (recipe.requiresSacrifice()) {
                infotextY += lineHeight;
            }

            int itemToUseY = infotextY - 5;
            int infoTextX = 94;
            int itemToUseX = this.getStringCenteredMaxX(Minecraft.getInstance().font, Component.translatable("jei.occultism.item_to_use"), infoTextX, infotextY);

            builder.addSlot(RecipeIngredientRole.CATALYST, itemToUseX, itemToUseY)
                    .addIngredients(recipe.getItemToUse());
        }
    }

    @Override
    public void draw(RitualRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack poseStack, double mouseX, double mouseY) {
        RenderSystem.enableBlend();
        this.arrow.draw(poseStack, this.ritualCenterX + this.recipeOutputOffsetX - 20, this.ritualCenterY);
        RenderSystem.disableBlend();

        int infotextY = 0;
        int infoTextX = 94;
        int lineHeight = Minecraft.getInstance().font.lineHeight;
        var pentacle = ModonomiconAPI.get().getMultiblock(recipe.getPentacleId());
        if (pentacle != null) {
            var pentacleName = Minecraft.getInstance().font.split(Component.translatable(Util.makeDescriptionId("multiblock", pentacle.getId())), 150);

            for (var line : pentacleName) {
                this.drawStringCentered(poseStack, Minecraft.getInstance().font,
                        line, infoTextX, infotextY);
                infotextY += lineHeight;
            }
        } else {
            this.drawStringCentered(poseStack, Minecraft.getInstance().font,
                    Component.translatable("jei.occultism.error.pentacle_not_loaded"), infoTextX, 0);
        }

        if (recipe.requiresSacrifice()) {
            this.drawStringCentered(poseStack, Minecraft.getInstance().font,
                    Component.translatable("jei.occultism.sacrifice", Component.translatable(recipe.getEntityToSacrificeDisplayName())), infoTextX, infotextY);
            infotextY += lineHeight;
        }

        if (recipe.requiresItemUse()) {
            this.drawStringCentered(poseStack, Minecraft.getInstance().font, Component.translatable("jei.occultism.item_to_use"), infoTextX, infotextY);
            infotextY += lineHeight;
        }

        if (recipe.getEntityToSummon() != null) {
            this.drawStringCentered(poseStack, Minecraft.getInstance().font,
                    Component.translatable("jei.occultism.summon", Component.translatable(recipe.getEntityToSummon().getDescriptionId())),
                    infoTextX, infotextY);
            infotextY += lineHeight;
        }

        if (recipe.getSpiritJobType() != null) {
            this.drawStringCentered(poseStack, Minecraft.getInstance().font,
                    Component.translatable("jei.occultism.job",
                            Component.translatable("job." + recipe.getSpiritJobType().toString().replace(":", "."))),
                    infoTextX, infotextY);
        }
    }
}
