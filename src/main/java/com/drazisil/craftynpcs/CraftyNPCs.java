package com.drazisil.craftynpcs;

import com.drazisil.craftynpcs.entity.EntityCraftyNPC;
import com.drazisil.craftynpcs.item.CraftyNPCEgg;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("craftynpcs")
public class CraftyNPCs {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String MODID = "craftynpcs";
    public static final DeferredRegister<EntityType<?>> ENTITIES = new DeferredRegister<>(ForgeRegistries.ENTITIES, MODID);

    public static EntityType<EntityCraftyNPC> ENTITY_CRAFTY_NPC;

    public CraftyNPCs() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the
    // contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }

        @SubscribeEvent
        public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {

//            DeferredRegister<EntityType<?>> ENTITIES = new DeferredRegister<>(ForgeRegistries.ENTITIES, MODID);

            assert ENTITY_CRAFTY_NPC == null;
            event.getRegistry().registerAll(ENTITY_CRAFTY_NPC.setRegistryName("crafty_npc"));

        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {

//            private static final DeferredRegister<EntityType<?>> ENTITIES = new DeferredRegister<>(ForgeRegistries.ENTITIES, MODID);
//
//            public static final RegistryObject<EntityType<EntityCraftyNPC>> CRAFTY_NPC = ENTITIES.register("crafty_npc", () -> EntityType.Builder
//                    .create((EntityType.IFactory<EntityCraftyNPC>) EntityCraftyNPC::new, EntityClassification.MISC)
//                    .build("crafty_npc"));

            ENTITY_CRAFTY_NPC = EntityType.Builder.create(EntityCraftyNPC::new, EntityClassification.CREATURE)
                    .build("crafty_npc");

            Item.Properties spawnEggProps = new Item.Properties().group(ItemGroup.MISC);
            SpawnEggItem spawnEgg = new CraftyNPCEgg(ENTITY_CRAFTY_NPC, 0x4c3e30, 0xf0f0f, spawnEggProps);

            event.getRegistry().registerAll(spawnEgg.setRegistryName("crafty_npc_egg"));


        }
    }
}
