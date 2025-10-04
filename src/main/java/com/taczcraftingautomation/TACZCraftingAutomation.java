package com.taczcraftingautomation;

import com.mojang.logging.LogUtils;
import com.tacz.guns.init.ModCreativeTabs;
import com.taczcraftingautomation.init.ModBlocks;
import com.taczcraftingautomation.init.ModContainer;
import com.taczcraftingautomation.init.ModItems;
import com.taczcraftingautomation.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TACZCraftingAutomation.MOD_ID)
public class TACZCraftingAutomation {

    public static final String MOD_ID = "tacz_crafting_automation";
    private static final Logger LOGGER = LogUtils.getLogger();


    public TACZCraftingAutomation() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.TILE_ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModContainer.CONTAINER_TYPE.register(modEventBus);
        NetworkHandler.init();

        modEventBus.addListener(this::buildContents);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ModConfig.SPEC);
    }

    public void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == ModCreativeTabs.OTHER_TAB.getKey()) {
            event.accept(ModBlocks.AUTOMATIC_SMITH_TABLE);
        }
    }
}
