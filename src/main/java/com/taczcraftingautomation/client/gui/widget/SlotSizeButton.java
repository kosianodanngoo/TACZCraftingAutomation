package com.taczcraftingautomation.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.taczcraftingautomation.TACZCraftingAutomation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public abstract class SlotSizeButton extends ExtendedButton {
    public static final ResourceLocation TEXTURE = new ResourceLocation(TACZCraftingAutomation.MOD_ID, "textures/gui/widgets.png");

    public SlotSizeButton(int x, int y, OnPress pressable) {
        super(x, y, 16, 16, Component.empty(), pressable);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            int k = !this.active ? 0 : (this.isHoveredOrFocused() ? 2 : 1);
            graphics.blit(TEXTURE, this.getX(), this.getY(), k * 16, 0, this.width, this.height);
            graphics.blit(TEXTURE, this.getX(), this.getY(), getTexturePosition().getX(), getTexturePosition().getY(), this.width, this.height);
        }
    }

    public abstract Vec3i getTexturePosition();
}
