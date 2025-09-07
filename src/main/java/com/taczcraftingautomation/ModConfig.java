package com.taczcraftingautomation;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue AUTOMATIC_SMITH_TABLE_ENERGY_CAPACITY = BUILDER.comment("A magic number").defineInRange("automaticSmithTableEnergyCapacity", 10000, 0, Integer.MAX_VALUE);
    public static final ForgeConfigSpec.IntValue AUTOMATIC_SMITH_TABLE_ENERGY_USAGE = BUILDER.comment("A magic number").defineInRange("automaticSmithTableEnergyUsage", 100, 0, Integer.MAX_VALUE);

    static final ForgeConfigSpec SPEC = BUILDER.build();

}
