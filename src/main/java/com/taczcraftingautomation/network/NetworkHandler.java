package com.taczcraftingautomation.network;

import com.taczcraftingautomation.TACZCraftingAutomation;
import com.taczcraftingautomation.network.message.ServerboundSetAutoPush;
import com.taczcraftingautomation.network.message.ServerboundUpdateItemLayout;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class NetworkHandler {
    private static final String VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(TACZCraftingAutomation.MOD_ID, "network"),
            () -> VERSION, it -> it.equals(VERSION), it -> it.equals(VERSION));

    public static void init() {
        int id = 0;
        INSTANCE.registerMessage(id++, ServerboundSetAutoPush.class, ServerboundSetAutoPush::encode, ServerboundSetAutoPush::decode, ServerboundSetAutoPush::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(id++, ServerboundUpdateItemLayout.class, ServerboundUpdateItemLayout::encode, ServerboundUpdateItemLayout::decode, ServerboundUpdateItemLayout::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
}