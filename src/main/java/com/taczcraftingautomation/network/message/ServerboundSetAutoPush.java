package com.taczcraftingautomation.network.message;

import com.taczcraftingautomation.inventory.AutomaticSmithTableMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundSetAutoPush {
    private final boolean autoPush;
    private final int menuId;

    public ServerboundSetAutoPush(boolean autoPush, int menuId) {
        this.autoPush = autoPush;
        this.menuId = menuId;
    }

    public static void encode(ServerboundSetAutoPush message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.autoPush);
        buf.writeVarInt(message.menuId);
    }

    public static ServerboundSetAutoPush decode(FriendlyByteBuf buf) {
        return new ServerboundSetAutoPush(buf.readBoolean(), buf.readVarInt());
    }

    public static void handle(ServerboundSetAutoPush message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            context.enqueueWork(() -> {
                ServerPlayer entity = context.getSender();
                if (entity == null) {
                    return;
                }
                if (entity.containerMenu.containerId == message.menuId && entity.containerMenu instanceof AutomaticSmithTableMenu menu) {
                    menu.blockEntity.setAutoPush(message.autoPush);
                }
            });
        }
        context.setPacketHandled(true);
    }

}