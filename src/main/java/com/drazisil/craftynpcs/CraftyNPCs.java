package com.drazisil.craftynpcs;

import com.drazisil.craftynpcs.client.NPCEntityRender;
import com.drazisil.craftynpcs.entity.NPCEntity;
import com.drazisil.craftynpcs.entity.NPCManager;
import com.drazisil.craftynpcs.event.EventHandler;
import com.drazisil.craftynpcs.item.CraftyNPCEgg;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
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

    public static final String MODID = "craftynpcs";
    public static final DeferredRegister<EntityType<?>> ENTITIES = new DeferredRegister<>(ForgeRegistries.ENTITIES, MODID);

    private static EntityType<NPCEntity> NPC_ENTITY_TYPE;

    public static ConstructionBarrelBlock getBlock() {
        return block;
    }

    public static void setBlock(ConstructionBarrelBlock block) {
        CraftyNPCs.block = block;
    }

    private static ConstructionBarrelBlock block;

    public NPCManager getNpcManager() {
        return npcManager;
    }

    private final NPCManager npcManager = new NPCManager();

    public static CraftyNPCs getInstance() {
        return instance;
    }

    private static void setInstance(CraftyNPCs instance) {
        CraftyNPCs.instance = instance;
    }

    private static CraftyNPCs instance;

    public CraftyNPCs() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        MinecraftForge.EVENT_BUS.register(EventHandler.class);
    }

    private void setup(final FMLCommonSetupEvent event) {

        setInstance(this);

        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);

        LOGGER.info("Registering Model..");


        RenderingRegistry.registerEntityRenderingHandler(NPCEntity.class, NPCEntityRender::new);
        LOGGER.info("Model registered");
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
    private static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {

            // register a new block here
            LOGGER.info("HELLO from Register Block");
            ConstructionBarrelBlock constructionBarrelBlock = new ConstructionBarrelBlock(Block.Properties.create(Material.ROCK, MaterialColor.STONE).hardnessAndResistance(1.5F, 6.0F));
            constructionBarrelBlock.setRegistryName(
                    new ResourceLocation(CraftyNPCs.MODID + ":construction_barrel"));

            setBlock(constructionBarrelBlock);
            blockRegistryEvent.getRegistry().registerAll(constructionBarrelBlock);
        }

        @SubscribeEvent
        public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {

            if (NPC_ENTITY_TYPE == null) {
                LOGGER.error("Error in registerEntities: entityType is null");
                throw new NullPointerException();
            }
            event.getRegistry().registerAll(NPC_ENTITY_TYPE.setRegistryName("crafty_npc"));

        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {

            NPC_ENTITY_TYPE = EntityType.Builder.create(NPCEntity::new, EntityClassification.CREATURE)
                    .build("crafty_npc");

            Item.Properties spawnEggProps = new Item.Properties().group(ItemGroup.MISC);
            Item.Properties constructionBarrelProps = new Item.Properties().group(ItemGroup.BUILDING_BLOCKS);
            SpawnEggItem spawnEgg = new CraftyNPCEgg(NPC_ENTITY_TYPE, 0x4c3e30, 0xf0f0f, spawnEggProps);
            BlockItem constrictionBarrelItem = new BlockItem(getBlock(), constructionBarrelProps);

            event.getRegistry().registerAll(spawnEgg.setRegistryName("crafty_npc_egg"));
            event.getRegistry().registerAll(constrictionBarrelItem.setRegistryName(CraftyNPCs.MODID + ":construction_barrel"));


        }

    }

}
