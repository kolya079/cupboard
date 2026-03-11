package com.cupboard;

import com.cupboard.config.CommonConfiguration;
import com.cupboard.config.CupboardConfig;
import com.cupboard.event.EventHandler;
import com.sun.management.HotSpotDiagnosticMXBean;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.management.ManagementFactory;
import java.util.Random;

import static com.cupboard.Cupboard.MOD_ID;

@Mod(MOD_ID)
public class Cupboard
{
    public static final String                              MOD_ID = "cupboard";
    public static final Logger                              LOGGER = LogManager.getLogger();
    public static       CupboardConfig<CommonConfiguration> config = new CupboardConfig<>(MOD_ID, new CommonConfiguration());
    public static       Random                              rand   = new Random();

    public static boolean IN_DEV = !FMLEnvironment.production;
    public static boolean IS_CLIENT_OR_INTEGRATED = FMLEnvironment.dist == Dist.CLIENT;

    public Cupboard(IEventBus modEventBus, ModContainer modContainer)
    {
        if (Cupboard.config.getCommonConfig().forceHeapDumpOnOOM)
        {
            try
            {
                HotSpotDiagnosticMXBean bean = ManagementFactory.newPlatformMXBeanProxy(
                  ManagementFactory.getPlatformMBeanServer(),
                  "com.sun.management:type=HotSpotDiagnostic",
                  HotSpotDiagnosticMXBean.class);

                bean.setVMOption("HeapDumpOnOutOfMemoryError", "true");
                bean.setVMOption("HeapDumpPath", FMLPaths.GAMEDIR.get().toString());
            }
            catch (Exception e)
            {
                LOGGER.error("Failed to enable heapdump option: ", e);
            }
        }

        NeoForge.EVENT_BUS.register(EventHandler.class);
        modEventBus.addListener(this::clientSetup);
    }

    public void clientSetup(FMLClientSetupEvent event)
    {
        // Side safe client event handler
        CupboardClient.onInitializeClient(event);
    }
}
