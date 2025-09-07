package com.taczcraftingautomation.init;

import com.taczcraftingautomation.TACZCraftingAutomation;
import com.taczcraftingautomation.inventory.AutomaticSmithTableMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContainer {
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPE = DeferredRegister.create(ForgeRegistries.MENU_TYPES, TACZCraftingAutomation.MOD_ID);

    public static final RegistryObject<MenuType<AutomaticSmithTableMenu>> AUTOMATIC_SMITH_TABLE_MENU = CONTAINER_TYPE.register("automatic_smith_table_menu", () -> IForgeMenuType.create(AutomaticSmithTableMenu::new));
}