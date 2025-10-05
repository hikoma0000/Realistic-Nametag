package io.github.hikoma0000.realisticnametag;

import com.mojang.logging.LogUtils;
import io.github.hikoma0000.realisticnametag.config.ServerConfig;
import io.github.hikoma0000.realisticnametag.network.ModPresenceChecker;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

@Mod(RealisticNametag.MOD_ID)
public class RealisticNametag {
    public static final String MOD_ID = "realisticnametag";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String NETWORK_VERSION = "1";

    private final ModContainer modContainer;

    public RealisticNametag(ModContainer modContainer, IEventBus modEventBus) {
        this.modContainer = modContainer;
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC, "realisticnametag-server.toml");
        modEventBus.register(this);
    }

    @SubscribeEvent
    public void onRegisterPayloadHandlers(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MOD_ID)
                .versioned(NETWORK_VERSION);

        registrar.playToServer(
                ModPresenceChecker.TYPE,
                ModPresenceChecker.STREAM_CODEC,
                (payload, context) -> {
                }
        );
    }
}