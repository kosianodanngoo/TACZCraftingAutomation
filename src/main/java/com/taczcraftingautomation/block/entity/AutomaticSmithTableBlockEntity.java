package com.taczcraftingautomation.block.entity;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.config.sync.SyncConfig;
import com.tacz.guns.crafting.GunSmithTableIngredient;
import com.tacz.guns.crafting.GunSmithTableRecipe;
import com.tacz.guns.init.ModRecipe;
import com.tacz.guns.item.GunSmithTableItem;
import com.tacz.guns.resource.filter.RecipeFilter;
import com.tacz.guns.resource.index.CommonBlockIndex;
import com.taczcraftingautomation.ModConfig;
import com.taczcraftingautomation.block.AutomaticSmithTableBlock;
import com.taczcraftingautomation.init.ModBlocks;
import com.taczcraftingautomation.inventory.AutomaticSmithTableMenu;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AutomaticSmithTableBlockEntity extends BlockEntity implements MenuProvider {
    public static final BlockEntityType<AutomaticSmithTableBlockEntity> TYPE = BlockEntityType.Builder.of(AutomaticSmithTableBlockEntity::new,
            ModBlocks.AUTOMATIC_SMITH_TABLE.get()
    ).build(null);

    private int energyUsage = ModConfig.AUTOMATIC_SMITH_TABLE_ENERGY_USAGE.get();

    protected final ContainerData data;

    public static final int INPUT_SLOTS = 18;
    public static final int OUTPUT_SLOTS = 3;
    public static final int TABLE_SLOTS = 1;
    public static final int TO_CRAFT_SLOTS = 1;
    public static final int INPUT_INDEX = 0;
    public static final int OUTPUT_INDEX = INPUT_INDEX+INPUT_SLOTS;
    public static final int TABLE_INDEX = OUTPUT_INDEX+OUTPUT_SLOTS;
    public static final int TO_CRAFT_INDEX = TABLE_INDEX+TABLE_SLOTS;
    public static final int CONTAINER_SIZE = INPUT_SLOTS+OUTPUT_SLOTS+TABLE_SLOTS+TO_CRAFT_SLOTS;

    private List<GunSmithTableRecipe> cashedRecipes;

    public boolean autoPush = false;
    private ItemStackHandler itemLayout = new ItemStackHandler(INPUT_SLOTS) {
        @Override
        protected void onContentsChanged(int slot){
            setChanged();
            super.onContentsChanged(slot);
        }
    };

    private final NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);

    private final ItemStackHandler itemHandler = new ItemStackHandler(items) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (INPUT_SLOTS <= slot  && slot <= TABLE_INDEX) {
                return false;
            }
            if (slot < INPUT_SLOTS) {
                ItemStack stack1 = itemLayout.getStackInSlot(slot);
                if(!stack1.isEmpty() && !ItemStack.isSameItem(stack, stack1)) {
                    return false;
                }
            }
            return super.isItemValid(slot, stack);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot < INPUT_SLOTS || TABLE_INDEX <= slot) {
                return ItemStack.EMPTY;
            }
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        protected void onContentsChanged(int slot){
            setChanged();
            super.onContentsChanged(slot);
        }
    };

    private final ItemStackHandler menuItemHandler = new ItemStackHandler(items) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (INPUT_SLOTS <= slot  && slot < TABLE_INDEX) {
                return false;
            }
            if (slot < INPUT_SLOTS) {
                ItemStack stack1 = itemLayout.getStackInSlot(slot);
                if(!stack1.isEmpty() && !ItemStack.isSameItem(stack, stack1)) {
                    return false;
                }
            }
            return super.isItemValid(slot, stack);
        }

        @Override
        protected void onContentsChanged(int slot){
            setChanged();
            super.onContentsChanged(slot);
        }
    };

    private final ItemStackHandler internalItemHandler = new ItemStackHandler(items);

    private final EnergyStorage energyStorage = new EnergyStorage(ModConfig.AUTOMATIC_SMITH_TABLE_ENERGY_CAPACITY.get());

    private LazyOptional<ItemStackHandler> optionalItemHandler;
    private LazyOptional<EnergyStorage> optionalEnergyStorage;

    public AutomaticSmithTableBlockEntity(BlockPos pos, BlockState blockState) {
        super(TYPE, pos, blockState);
        optionalItemHandler = LazyOptional.of(() -> this.itemHandler);
        optionalEnergyStorage = LazyOptional.of(() -> this.energyStorage);
        cashedRecipes = null;
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex){
                    case 0 -> AutomaticSmithTableBlockEntity.this.energyStorage.getEnergyStored();
                    case 1 -> AutomaticSmithTableBlockEntity.this.energyStorage.getMaxEnergyStored();
                    case 2 -> AutomaticSmithTableBlockEntity.this.getAutoPush() ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer((itemHandler.getSlots()));
        for (int i = 0; i < itemHandler.getSlots(); i++){
            inventory.setItem(i,itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level,this.worldPosition,inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Automatic Smith Table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new AutomaticSmithTableMenu(id, inventory, this, this.data);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return optionalItemHandler.cast();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return optionalEnergyStorage.cast();
        }
        return super.getCapability(cap, side);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        clearCashe();
        if(canProcess()){
            setChanged(pLevel,pPos,pState);
            for (GunSmithTableRecipe recipe : getRecipes()) {
                if (doCraft(recipe, false)){
                    break;
                }
            }
        }
        if(getAutoPush()) {
            Direction direction = getBlockState().getValue(AutomaticSmithTableBlock.FACING);
            pushItem(direction);
        }
    }

    private void pushItem(Direction direction) {
        IItemHandler toOut = getInventoryWithDiretion(direction);
        for (int slotIndex = OUTPUT_INDEX ; slotIndex < OUTPUT_SLOTS + OUTPUT_INDEX; slotIndex++) {
            ItemStack stackInSlot = internalItemHandler.extractItem(slotIndex, internalItemHandler.getStackInSlot(slotIndex).getCount(), true);
            if (!stackInSlot.isEmpty()) {
                ItemStack stack = ItemHandlerHelper.insertItem(toOut, stackInSlot, false);
                internalItemHandler.extractItem(slotIndex, stackInSlot.getCount() - stack.getCount(), false);
                break;
            }
        }
    }

    private IItemHandler getInventoryWithDiretion(Direction direction) {
        BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(direction));
        if (blockEntity != null) {
            return blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite()).orElse(EmptyHandler.INSTANCE);
        }

        return EmptyHandler.INSTANCE;
    }

    public boolean doCraft(GunSmithTableRecipe recipe, boolean simulate) {
        ItemStack result = recipe.getOutput();
        if (energyStorage.extractEnergy(energyUsage, true) < energyUsage) {
            return false;
        }
        if (!putItemIntoOutput(result, true)) {
            return false;
        }
        List<GunSmithTableIngredient> ingredients = recipe.getInputs();
        Int2IntArrayMap recordCount = new Int2IntArrayMap();
        for (GunSmithTableIngredient ingredient : ingredients){
            int count = 0;
            for (int slotIndex = 0; slotIndex < INPUT_SLOTS; slotIndex++) {
                ItemStack stack = internalItemHandler.getStackInSlot(slotIndex);
                int stackCount = stack.getCount();
                if (!stack.isEmpty() && ingredient.getIngredient().test(stack)) {
                    count = count + stackCount;
                    if (count <= ingredient.getCount()) {
                        recordCount.put(slotIndex, stackCount);
                    } else {
                        int remaining = count - ingredient.getCount();
                        recordCount.put(slotIndex, stackCount - remaining);
                        break;
                    }
                }
            }
            if (count < ingredient.getCount()) {
                return false;
            }
        }
        if(!simulate) {
            energyStorage.extractEnergy(energyUsage, false);
            for (int slotIndex : recordCount.keySet()) {
                internalItemHandler.extractItem(slotIndex, recordCount.get(slotIndex), false);
            }
            putItemIntoOutput(result, false);
        }
        return true;
    }

    public boolean putItemIntoOutput(ItemStack stack, boolean simulate) {
        ItemStack stack1 = stack.copy();
        for (int slotIndex = INPUT_SLOTS; slotIndex < INPUT_SLOTS + OUTPUT_SLOTS; slotIndex++) {
            stack1 = internalItemHandler.insertItem(slotIndex, stack1, simulate);
            if (stack1.isEmpty()) return true;
        }
        return false;
    }

    public boolean isRecipeSupported(GunSmithTableRecipe recipe){
        ItemStack tableStack = internalItemHandler.getStackInSlot(INPUT_SLOTS+OUTPUT_SLOTS);
        if (tableStack.getItem() instanceof GunSmithTableItem table) {
            ResourceLocation id = table.getBlockId(tableStack);
            RecipeFilter filter = TimelessAPI.getCommonBlockIndex(id).map(CommonBlockIndex::getFilter).orElse(null);
            if (!DefaultAssets.DEFAULT_BLOCK_ID.equals(id) || SyncConfig.ENABLE_TABLE_FILTER.get()) {
                if (filter != null && !filter.contains(recipe.getId())) {
                    return false;
                }
            }
            boolean flag = TimelessAPI.getCommonBlockIndex(id).map(blockIndex -> {
                return blockIndex.getData().getTabs().stream().noneMatch(tab -> tab.id().equals(recipe.getTab()));
            }).orElse(true);
            if (DefaultAssets.DEFAULT_BLOCK_ID.equals(id) && !SyncConfig.ENABLE_TABLE_FILTER.get()) {
                flag = false;
            }
            return !flag;
        }
        return false;
    }

    public boolean canProcess() {
        List<GunSmithTableRecipe> recipes = getRecipes();
        for (GunSmithTableRecipe recipe : recipes) {
            if (doCraft(recipe, true)){
                return true;
            }
        }
        return false;
    }

    public List<GunSmithTableRecipe> getRecipes() {
        if(cashedRecipes != null) {
            return cashedRecipes;
        }

        cashedRecipes = this.level.getRecipeManager().getAllRecipesFor(ModRecipe.GUN_SMITH_TABLE_CRAFTING.get()).stream().filter((recipe) -> {
            return !getToCraft().isEmpty() && ItemStack.isSameItemSameTags(getToCraft(), recipe.getOutput()) && isRecipeSupported(recipe);
        }).toList();
        return cashedRecipes;
    }

    public ItemStack getToCraft() {
        return internalItemHandler.getStackInSlot(TO_CRAFT_INDEX);
    }

    public ItemStackHandler getMenuItemHandler() {
        return menuItemHandler;
    }

    private void clearCashe() {
        cashedRecipes = null;
    }

    public boolean getAutoPush() {
        return autoPush;
    }

    public void setAutoPush(boolean flag) {
        autoPush = flag;
    }

    public ItemStackHandler getItemLayout() {
        return itemLayout;
    }

    public void forgetItemLayout() {
        for(int itemIndex = 0 ; itemIndex < itemLayout.getSlots() ; itemIndex++) {
            itemLayout.setStackInSlot(itemIndex, ItemStack.EMPTY);
        }
        setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void memorizeItemLayout() {
        for(int itemIndex = 0 ; itemIndex < itemLayout.getSlots() ; itemIndex++) {
            ItemStack stack = internalItemHandler.getStackInSlot(itemIndex).copy();
            if(!stack.isEmpty()) {
                stack.setCount(1);
                itemLayout.setStackInSlot(itemIndex, stack);
            }
        }
        setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        saveData(pTag);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        loadData(pTag);
    }

    void loadData(CompoundTag pTag) {
        energyStorage.deserializeNBT(pTag.get("energy"));
        ItemStackHandler loadedInventory = new ItemStackHandler(CONTAINER_SIZE);
        loadedInventory.deserializeNBT(pTag.getCompound("inventory"));
        for(int slotIndex = 0 ; slotIndex < internalItemHandler.getSlots() ; slotIndex++) {
            internalItemHandler.setStackInSlot(slotIndex, loadedInventory.getStackInSlot(slotIndex));
        }
        itemLayout.deserializeNBT(pTag.getCompound("itemLayout"));
        setAutoPush(pTag.getBoolean("autoPush"));
    }

    void saveData(CompoundTag pTag) {
        pTag.put("inventory", internalItemHandler.serializeNBT());
        pTag.put("energy", energyStorage.serializeNBT());
        pTag.put("itemLayout", itemLayout.serializeNBT());
        pTag.putBoolean("autoPush", getAutoPush());
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet) {
        load(packet.getTag());
    }

    @Override
    public void handleUpdateTag(CompoundTag pTag) {
        super.handleUpdateTag(pTag);
        loadData(pTag);
    }


    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveData(tag);
        return tag;
    }
}
