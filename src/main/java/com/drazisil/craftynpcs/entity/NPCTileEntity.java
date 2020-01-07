package com.drazisil.craftynpcs.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntityType;

public class NPCTileEntity extends ChestTileEntity implements INamedContainerProvider {


    public NPCTileEntity(TileEntityType<?> typeIn) {
        super(typeIn);
    }

    public NPCTileEntity() {
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
        System.out.println(this.getItems().toString());
        if (!player.isSpectator()) {
            --this.numPlayersUsing;
            this.markDirty();
            this.onOpenOrClose();
        }

    }

    protected void onOpenOrClose() {
//        Block block = this.getBlockState().getBlock();
//        if (block instanceof ChestBlock) {
//            this.world.addBlockEvent(this.pos, block, 1, this.numPlayersUsing);
//            this.world.notifyNeighborsOfStateChange(this.pos, block);
//        }

    }

    public int getNumPlayersUsing(){
        return this.numPlayersUsing;
    }

    public boolean isUsableByPlayer(PlayerEntity var1) {
        return true;

    }

}
