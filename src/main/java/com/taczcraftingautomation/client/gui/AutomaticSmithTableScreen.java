package com.taczcraftingautomation.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.tacz.guns.inventory.GunSmithTableMenu;
import com.taczcraftingautomation.TACZCraftingAutomation;
import com.taczcraftingautomation.inventory.AutomaticSmithTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AutomaticSmithTableScreen extends AbstractContainerScreen<AutomaticSmithTableMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(TACZCraftingAutomation.MOD_ID,"textures/gui/automatic_smith_table.png");


    public AutomaticSmithTableScreen(AutomaticSmithTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 256;
        this.imageHeight = 256;
        this.inventoryLabelY = 110;
    }
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);
        RenderSystem.setShaderTexture(0,TEXTURE);
        int x = (width - this.imageWidth) / 2;
        int y = (height - this.imageHeight) / 2;

        guiGraphics.blit(TEXTURE,x,y,0,0,this.imageWidth,this.imageHeight,this.imageWidth,this.imageHeight);

        guiGraphics.fill(x + 118, y + 106 - Math.round(52 * menu.getEnergyPercentage()), x + 120, y + 106, 0xFFFF0000);
    }

    @Override
    public void init(){
        super.init();

    }
}
