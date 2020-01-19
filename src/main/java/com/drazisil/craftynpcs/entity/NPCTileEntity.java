package com.drazisil.craftynpcs.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;

public class NPCTileEntity extends ChestTileEntity implements INamedContainerProvider {

    private NonNullList<ItemStack> chestContents;

    public NPCTileEntity(TileEntityType<?> typeIn) {
        super(typeIn);
    }

    public NPCTileEntity() {
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

    protected void onOpenOrClose() {
//        Block constructionBarrelBlock = this.getBlockState().getConstructionBarrelBlock();
//        if (constructionBarrelBlock instanceof ChestBlock) {
//            this.world.addBlockEvent(this.pos, constructionBarrelBlock, 1, this.numPlayersUsing);
//            this.world.notifyNeighborsOfStateChange(this.pos, constructionBarrelBlock);
//        }

    }

    public int getNumPlayersUsing(){
        return this.numPlayersUsing;
    }

    public boolean isUsableByPlayer(PlayerEntity var1) {
        return true;

    }

    protected NonNullList<ItemStack> getItems() {
        return this.chestContents;
    }

    boolean addInventorySlotContents(int index, ItemStack stack) {
        this.getItems().set(index, stack);
        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }

        this.markDirty();
        return true;
    }

}
