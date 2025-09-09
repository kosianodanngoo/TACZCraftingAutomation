package com.taczcraftingautomation.network.message;

import com.taczcraftingautomation.inventory.AutomaticSmithTableMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundUpdateItemLayout {
    private final boolean memorize;
    private final int menuId;

    public ServerboundUpdateItemLayout(boolean memorize, int menuId) {
        this.memorize = memorize;
        this.menuId = menuId;
    }

    public static void encode(ServerboundUpdateItemLayout message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.memorize);
        buf.writeVarInt(message.menuId);
    }

    public static ServerboundUpdateItemLayout decode(FriendlyByteBuf buf) {
        return new ServerboundUpdateItemLayout(buf.readBoolean(), buf.readVarInt());
    }

    public static void handle(ServerboundUpdateItemLayout message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            context.enqueueWork(() -> {
                ServerPlayer entity = context.getSender();
                if (entity == null) {
                    return;
                }
                if (entity.containerMenu.containerId == message.menuId && entity.containerMenu instanceof AutomaticSmithTableMenu menu) {
                    if(message.memorize) {
                        menu.blockEntity.memorizeItemLayout();
                    } else {
                        menu.blockEntity.forgetItemLayout();
                    }
                }
            });
        }
        context.setPacketHandled(true);
    }
}
