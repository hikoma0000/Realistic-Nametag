package io.github.hikoma0000.realisticnametag;

import com.mojang.logging.LogUtils;
import io.github.hikoma0000.realisticnametag.config.ClientConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import io.github.hikoma0000.realisticnametag.client.NametagRenderEvents;

@SuppressWarnings("removal")
@Mod(RealisticNametag.MOD_ID)
public class RealisticNametag {
    public static final String MOD_ID = "realisticnametag";
    private static final Logger LOGGER = LogUtils.getLogger();

    public RealisticNametag() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::init);
        MinecraftForge.EVENT_BUS.register(new NametagRenderEvents());
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC, "realisticnametag-client.toml");

    }

    private void init(final FMLCommonSetupEvent event) {
    }
}