package com.drazisil.craftynpcs.entity;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.NonNullList;

public class NPCInventory extends ChestTileEntity implements INamedContainerProvider {

    private NonNullList<ItemStack> chestContents;

//    public NPCInventory(TileEntityType<?> typeIn) {
//        super(typeIn);
//    }

    NPCInventory() {
        this.chestContents = NonNullList.withSize(27, ItemStack.EMPTY);
    }

    public ItemStack getItemStackInSlot(int slotId) {
        return this.getItems().get(slotId);
    }

    public void openInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            if (this.numPlayersUsing < 0) {
                this.numPlayersUsing = 0;
            }

            ++this.numPlayersUsing;
            this.onOpenOrClose();
        }

    }

    public void closeInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            --this.numPlayersUsing;
            this.markDirty();
            this.onOpenOrClose();
        }

    }

    @Override
    protected void onOpenOrClose() {}

    public int getNumPlayersUsing(){
        return this.numPlayersUsing;
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity var1) {
        return true;

    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.chestContents;
    }

    protected int getNextEmptySlot() {
        for (int i = 0; i < this.chestContents.size(); i++) {
            if (this.chestContents.get(i) == ItemStack.EMPTY) {
                return i;
            }
        }
        return -1;
    }

    boolean addInventorySlotContents(int index, ItemStack stack) {
        this.getItems().set(index, stack);
        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }

        this.markDirty();
        return true;
    }

    public boolean addItemStackToInventory(ItemStack itemStackIn) {
        return this.add(-1, itemStackIn);
    }

    public boolean add(int p_191971_1_, ItemStack p_191971_2_) {
        if (p_191971_2_.isEmpty()) {
            return false;
        } else {
            try {
                if (p_191971_2_.isDamaged()) {
                    if (p_191971_1_ == -1) {
                        p_191971_1_ = this.getFirstEmptyStack();
                    }

                    if (p_191971_1_ >= 0) {
                        this.chestContents.set(p_191971_1_, p_191971_2_.copy());
                        ((ItemStack)this.chestContents.get(p_191971_1_)).setAnimationsToGo(5);
                        p_191971_2_.setCount(0);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    int i;
                    do {
                        i = p_191971_2_.getCount();
                        if (p_191971_1_ == -1) {
                            p_191971_2_.setCount(this.storePartialItemStack(p_191971_2_));
                        } else {
                            p_191971_2_.setCount(this.addResource(p_191971_1_, p_191971_2_));
                        }
                    } while(!p_191971_2_.isEmpty() && p_191971_2_.getCount() < i);

                    return p_191971_2_.getCount() < i;
                }
            } catch (Throwable var6) {
                CrashReport crashreport = CrashReport.makeCrashReport(var6, "Adding item to inventory");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
                crashreportcategory.addDetail("Registry Name", () -> {
                    return String.valueOf(p_191971_2_.getItem().getRegistryName());
                });
                crashreportcategory.addDetail("Item Class", () -> {
                    return p_191971_2_.getItem().getClass().getName();
                });
                crashreportcategory.addDetail("Item ID", Item.getIdFromItem(p_191971_2_.getItem()));
                crashreportcategory.addDetail("Item data", p_191971_2_.getDamage());
                crashreportcategory.addDetail("Item name", () -> {
                    return p_191971_2_.getDisplayName().getString();
                });
                throw new ReportedException(crashreport);
            }
        }
    }

    private int storePartialItemStack(ItemStack itemStackIn) {
        int i = this.storeItemStack(itemStackIn);
        if (i == -1) {
            i = this.getFirstEmptyStack();
        }

        return i == -1 ? itemStackIn.getCount() : this.addResource(i, itemStackIn);
    }

    public int getFirstEmptyStack() {
        for(int i = 0; i < this.chestContents.size(); ++i) {
            if (((ItemStack)this.chestContents.get(i)).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    private int addResource(int p_191973_1_, ItemStack p_191973_2_) {
        Item item = p_191973_2_.getItem();
        int i = p_191973_2_.getCount();
        ItemStack itemstack = this.getStackInSlot(p_191973_1_);
        if (itemstack.isEmpty()) {
            itemstack = p_191973_2_.copy();
            itemstack.setCount(0);
            if (p_191973_2_.hasTag()) {
                itemstack.setTag(p_191973_2_.getTag().copy());
            }

            this.setInventorySlotContents(p_191973_1_, itemstack);
        }

        int j = i;
        if (i > itemstack.getMaxStackSize() - itemstack.getCount()) {
            j = itemstack.getMaxStackSize() - itemstack.getCount();
        }

        if (j > this.getInventoryStackLimit() - itemstack.getCount()) {
            j = this.getInventoryStackLimit() - itemstack.getCount();
        }

        if (j == 0) {
            return i;
        } else {
            i -= j;
            itemstack.grow(j);
            itemstack.setAnimationsToGo(5);
            return i;
        }
    }

    public int storeItemStack(ItemStack itemStackIn) {
        for(int i = 0; i < this.chestContents.size(); ++i) {
            if (this.canMergeStacks((ItemStack)this.chestContents.get(i), itemStackIn)) {
                return i;
            }
        }

        return -1;
    }

    private boolean canMergeStacks(ItemStack stack1, ItemStack stack2) {
        return !stack1.isEmpty() && this.stackEqualExact(stack1, stack2) && stack1.isStackable() && stack1.getCount() < stack1.getMaxStackSize() && stack1.getCount() < this.getInventoryStackLimit();
    }

    private boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }


}
