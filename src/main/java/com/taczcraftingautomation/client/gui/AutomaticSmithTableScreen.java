package com.taczcraftingautomation.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.taczcraftingautomation.TACZCraftingAutomation;
import com.taczcraftingautomation.client.gui.widget.SlotSizeButton;
import com.taczcraftingautomation.inventory.AutomaticSmithTableMenu;
import com.taczcraftingautomation.network.NetworkHandler;
import com.taczcraftingautomation.network.message.ServerboundSetAutoPush;
import com.taczcraftingautomation.network.message.ServerboundUpdateItemLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class AutomaticSmithTableScreen extends AbstractContainerScreen<AutomaticSmithTableMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(TACZCraftingAutomation.MOD_ID,"textures/gui/automatic_smith_table.png");


    private AutoPushButton autoPushButton;
    private MemorizeButton memorizeButton;
    private ForgetButton forgetButton;

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
        renderItemLayout(guiGraphics);
    }

    @Override
    public void render(GuiGraphics graphics, int i, int i1, float v) {
        this.renderBackground(graphics);
        super.render(graphics, i, i1, v);
        this.renderTooltip(graphics, i, i1);
    }

    private void renderItemLayout(GuiGraphics graphics) {
        com.mojang.blaze3d.platform.Lighting.setupFor3DItems();
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(leftPos, topPos, 100.0F);

        ItemStackHandler itemLayout = menu.getItemLayout();
        GlStateManager._enableDepthTest();
        GlStateManager._disableBlend();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, 0.25f);

        for (int indexSlot = 0; indexSlot < itemLayout.getSlots(); indexSlot++) {
            ItemStack stack = itemLayout.getStackInSlot(indexSlot);
            if (!stack.isEmpty()) {
                Slot slot = menu.getSlot(AutomaticSmithTableMenu.PLAYER_SLOTS + indexSlot);
                if (!slot.hasItem()) {
                    graphics.renderItem(stack, slot.x, slot.y, slot.x * slot.y * 31);
                    graphics.renderItemDecorations(Minecraft.getInstance().font, stack, slot.x, slot.y, null);
                }
            }
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();

        matrixStack.popPose();
    }

    @Override
    public void init(){
        int x = (width - this.imageWidth) / 2;
        int y = (height - this.imageHeight) / 2;
        super.init();

        addRenderableWidget(autoPushButton = new AutoPushButton(x + 152, y + 36));
        addRenderableWidget(memorizeButton = new MemorizeButton(x + 8, y + 36));
        addRenderableWidget(forgetButton = new ForgetButton(x + 26, y + 36));

        autoPushButton.setTooltip(Tooltip.create(Component.translatable("gui.tacz_crafting_automation.auto_smith_table.toggle_auto_push")));
        memorizeButton.setTooltip(Tooltip.create(Component.translatable("gui.tacz_crafting_automation.auto_smith_table.memorize")));
        forgetButton.setTooltip(Tooltip.create(Component.translatable("gui.tacz_crafting_automation.auto_smith_table.forget")));
    }

    private class AutoPushButton extends SlotSizeButton {
        private static final Vec3i TEXTURE_POS = new Vec3i(64, 0, 0);
        private static final Vec3i TEXTURE_POS_TOGGLED = new Vec3i(48, 0, 0);

        AutoPushButton(int x, int y) {
            super(x, y, (button) -> {
                NetworkHandler.INSTANCE.sendToServer(new ServerboundSetAutoPush(!menu.getAutoPush(), menu.containerId));
            });
        }

        @Override
        public Vec3i getTexturePosition() {
            return menu.getAutoPush() ? TEXTURE_POS_TOGGLED : TEXTURE_POS;
        }
    }

    private class MemorizeButton extends SlotSizeButton {
        private static final Vec3i TEXTURE_POS = new Vec3i(80, 0, 0);

        MemorizeButton(int x, int y) {
            super(x, y, (button) -> {
                NetworkHandler.INSTANCE.sendToServer(new ServerboundUpdateItemLayout(true, menu.containerId));
            });
        }

        @Override
        public Vec3i getTexturePosition() {
            return TEXTURE_POS;
        }
    }

    private class ForgetButton extends SlotSizeButton {
        private static final Vec3i TEXTURE_POS = new Vec3i(96, 0, 0);

        ForgetButton(int x, int y) {
            super(x, y, (button) -> {
                NetworkHandler.INSTANCE.sendToServer(new ServerboundUpdateItemLayout(false, menu.containerId));
            });
        }

        @Override
        public Vec3i getTexturePosition() {
            return TEXTURE_POS;
        }
    }
}
