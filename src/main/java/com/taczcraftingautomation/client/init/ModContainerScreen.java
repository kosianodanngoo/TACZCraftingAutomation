package com.taczcraftingautomation.client.init;

import com.taczcraftingautomation.client.gui.AutomaticSmithTableScreen;
import com.taczcraftingautomation.init.ModContainer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModContainerScreen {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent evt) {
        evt.enqueueWork(() -> MenuScreens.register(ModContainer.AUTOMATIC_SMITH_TABLE_MENU.get(), AutomaticSmithTableScreen::new));
    }
}