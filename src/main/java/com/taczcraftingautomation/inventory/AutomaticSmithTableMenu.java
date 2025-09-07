package com.taczcraftingautomation.inventory;

import com.taczcraftingautomation.block.entity.AutomaticSmithTableBlockEntity;
import com.taczcraftingautomation.init.ModContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class AutomaticSmithTableMenu extends AbstractContainerMenu {
    public final AutomaticSmithTableBlockEntity blockEntity;
    private final ContainerData data;

    public AutomaticSmithTableMenu(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        this(pContainerId, inventory, inventory.player.level().getBlockEntity(buf.readBlockPos(), AutomaticSmithTableBlockEntity.TYPE).orElseThrow(), new SimpleContainerData(3));
    }

    public AutomaticSmithTableMenu(int id, Inventory inventory, AutomaticSmithTableBlockEntity blockEntity, ContainerData data) {
        super(ModContainer.AUTOMATIC_SMITH_TABLE_MENU.get(), id);
        checkContainerSize(inventory, AutomaticSmithTableBlockEntity.CONTAINER_SIZE);
        this.blockEntity = blockEntity;
        this.data = data;

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        for (int i = 0; i < AutomaticSmithTableBlockEntity.INPUT_SLOTS; i++) {
            addSlot(new SlotItemHandler(blockEntity.getMenuItemHandler(), i+AutomaticSmithTableBlockEntity.INPUT_INDEX, 8 + (i % INPUT_COLUMNS) * 18, 54+18*(i / INPUT_COLUMNS)));
        }
        for (int i = 0; i < AutomaticSmithTableBlockEntity.OUTPUT_SLOTS; i++) {
            addSlot(new SlotItemHandler(blockEntity.getMenuItemHandler(), i+AutomaticSmithTableBlockEntity.OUTPUT_INDEX, 152 - (i % OUTPUT_COLUMNS) * 18, 54+18*(i / OUTPUT_COLUMNS)));
        }
        addSlot(new SlotItemHandler(blockEntity.getMenuItemHandler(), AutomaticSmithTableBlockEntity.TABLE_INDEX, 8, 18));
        addSlot(new SlotItemHandler(blockEntity.getMenuItemHandler(), AutomaticSmithTableBlockEntity.TO_CRAFT_INDEX, 152, 18));

        addDataSlots(data);
    }


    private static final int PLAYER_HOTBAR_SLOTS = 9;
    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int PLAYER_INVENTORY_COLUMNS = 9;
    private static final int PLAYER_INVENTORY_SLOTS = PLAYER_INVENTORY_COLUMNS * PLAYER_INVENTORY_ROWS;
    private static final int PLAYER_SLOTS = PLAYER_HOTBAR_SLOTS + PLAYER_INVENTORY_SLOTS;
    private static final int INPUT_COLUMNS = 6;
    private static final int OUTPUT_COLUMNS = 1;

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        ItemStack stack1 = ItemStack.EMPTY;
        Slot slot = this.slots.get(i);
        if (slot.hasItem()) {
            ItemStack stack2 = slot.getItem();
            stack1 = stack2.copy();
            if (i < PLAYER_SLOTS) {
                if (!this.moveItemStackTo(stack2, PLAYER_SLOTS, this.slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack2, 0, PLAYER_SLOTS, true)) {
                return ItemStack.EMPTY;
            }

            if (stack2.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return stack1;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int i1, int i2, boolean b) {
        boolean flag = false;
        int i = i1;
        if (b) {
            i = i2 - 1;
        }

        Slot slot1;
        ItemStack itemstack;
        if (stack.isStackable()) {
            while(!stack.isEmpty()) {
                if (b) {
                    if (i < i1) {
                        break;
                    }
                } else if (i >= i2) {
                    break;
                }

                slot1 = this.slots.get(i);
                itemstack = slot1.getItem();
                if (!itemstack.isEmpty() && ItemStack.isSameItemSameTags(stack, itemstack) && slot1.mayPlace(itemstack)) {
                    long itemStackCount = itemstack.getCount();
                    long stackCount = stack.getCount();

                    long j = itemStackCount + stackCount;
                    int maxSize = stack.getMaxStackSize();
                    if (j <= maxSize) {
                        stack.setCount(0);
                        itemstack.setCount((int) j);
                        slot1.setChanged();
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        stack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot1.setChanged();
                        flag = true;
                    }
                }

                if (b) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (b) {
                i = i2 - 1;
            } else {
                i = i1;
            }

            while(true) {
                if (b) {
                    if (i < i1) {
                        break;
                    }
                } else if (i >= i2) {
                    break;
                }

                slot1 = this.slots.get(i);
                itemstack = slot1.getItem();
                if (itemstack.isEmpty() && slot1.mayPlace(stack)) {
                    if (stack.getCount() > slot1.getMaxStackSize()) {
                        slot1.setByPlayer(stack.split(slot1.getMaxStackSize()));
                    } else {
                        slot1.setByPlayer(stack.split(stack.getCount()));
                    }

                    slot1.setChanged();
                    flag = true;
                    break;
                }

                if (b) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return flag;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.isAlive();
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < PLAYER_INVENTORY_ROWS; ++i) {
            for (int l = 0; l < PLAYER_INVENTORY_COLUMNS; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 122 + i * 18));
            }
        }
    }

    public float getEnergyPercentage() {
        float currentEnergy = (float) data.get(0);
        float maxEnergy = (float) data.get(1);

        return maxEnergy == 0 ? 0 : Mth.clamp(currentEnergy/maxEnergy, 0.0f, 1.0f);
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < PLAYER_HOTBAR_SLOTS; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 180));
        }
    }
}
