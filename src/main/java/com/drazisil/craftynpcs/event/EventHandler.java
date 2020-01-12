package com.drazisil.craftynpcs.event;

import com.drazisil.craftynpcs.entity.NPCEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandler {

    @SubscribeEvent
    public static void onPlayerEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!event.getWorld().isRemote) {
            if (event.getTarget() instanceof NPCEntity) {
                PlayerEntity player = event.getPlayer();
                NPCEntity targetEntity = (NPCEntity) event.getTarget();

                if (targetEntity.getEquipmentInventory().getNumPlayersUsing() == 0) {
                    player.openContainer(targetEntity.getEquipmentInventory());
                    MinecraftServer moo = player.getServer();
                }


            }

        }

    }
}
