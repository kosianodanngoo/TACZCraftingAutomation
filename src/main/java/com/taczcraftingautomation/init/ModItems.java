package com.taczcraftingautomation.init;

import com.taczcraftingautomation.TACZCraftingAutomation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TACZCraftingAutomation.MOD_ID);

    public static RegistryObject<Item> AUTOMATIC_SMITH_TABLE = ITEMS.register("automatic_smith_table", () -> new BlockItem(ModBlocks.AUTOMATIC_SMITH_TABLE.get(), new Item.Properties()));
}
