package com.taczcraftingautomation.init;

import com.taczcraftingautomation.TACZCraftingAutomation;
import com.taczcraftingautomation.block.AutomaticSmithTableBlock;
import com.taczcraftingautomation.block.entity.AutomaticSmithTableBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TACZCraftingAutomation.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TACZCraftingAutomation.MOD_ID);

    public static RegistryObject<Block> AUTOMATIC_SMITH_TABLE = BLOCKS.register("automatic_smith_table", AutomaticSmithTableBlock::new);

    public static RegistryObject<BlockEntityType<AutomaticSmithTableBlockEntity>> AUTOMATIC_SMITH_TABLE_BE = TILE_ENTITIES.register("automatic_smith_table", () -> AutomaticSmithTableBlockEntity.TYPE);

}
