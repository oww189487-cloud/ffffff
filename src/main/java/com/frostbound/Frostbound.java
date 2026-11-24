package com.frostbound;

import com.frostbound.client.ClientSetup;
import com.frostbound.command.ModCommands;
import com.frostbound.network.Network;
import com.frostbound.registry.ModArmorMaterials;
import com.frostbound.registry.ModAttachments;
import com.frostbound.registry.ModBlockEntities;
import com.frostbound.registry.ModBlocks;
import com.frostbound.registry.ModCreativeTabs;
import com.frostbound.registry.ModItems;
import com.frostbound.temperature.TemperatureManager;
import com.frostbound.world.WorldFreeze;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Frostbound.MODID)
public class Frostbound {
    public static final String MODID = "frostbound";

    public Frostbound(IEventBus modBus, ModContainer container) {
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModArmorMaterials.MATERIALS.register(modBus);
        ModAttachments.ATTACHMENTS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);

        modBus.addListener(Network::register);

        NeoForge.EVENT_BUS.addListener(TemperatureManager::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(WorldFreeze::onLevelTick);
        NeoForge.EVENT_BUS.addListener(ModCommands::register);

        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientSetup.init(modBus);
        }
    }
}
