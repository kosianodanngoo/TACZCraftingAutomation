package com.taczcraftingautomation.compat.jei;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.crafting.GunSmithTableRecipe;
import com.taczcraftingautomation.TACZCraftingAutomation;
import com.taczcraftingautomation.init.ModBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class TACZCraftingAutomationPlugin implements IModPlugin {
    private static final ResourceLocation UID = new ResourceLocation(TACZCraftingAutomation.MOD_ID, "jei");

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        var map = TimelessAPI.getAllCommonBlockIndex();
        for (var entry : map) {
            RecipeType<GunSmithTableRecipe> type = RecipeType.create(GunMod.MOD_ID, "gun_smith_table/" + entry.getKey().toString().replace(':', '_'), GunSmithTableRecipe.class);
            registration.addRecipeCatalyst(ModBlocks.AUTOMATIC_SMITH_TABLE.get(), type);
        }
    }

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }
}
