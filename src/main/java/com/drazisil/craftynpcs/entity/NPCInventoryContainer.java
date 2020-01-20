package com.drazisil.craftynpcs.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

import javax.annotation.Nullable;

public class NPCInventoryContainer extends Container {
    protected NPCInventoryContainer(@Nullable ContainerType<?> type, int id) {
        super(type, id);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerEntity) {
        return true;
    }
}
