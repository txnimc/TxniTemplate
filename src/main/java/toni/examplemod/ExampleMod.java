package toni.examplemod;

import toni.examplemod.foundation.config.AllConfigs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


#if FABRIC
    import net.fabricmc.api.ClientModInitializer;
    import net.fabricmc.api.ModInitializer;
    import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
    import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
    import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
    import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
    #if AFTER_21
    import net.neoforged.fml.config.ModConfig;
    import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
    import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
    import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
    #else
    import net.minecraftforge.fml.config.ModConfig;
    import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
    #endif
#endif


#if FORGE
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

#endif


#if NEO

#endif


#if FORGE
@Mod("example_mod")
#endif
public class ExampleMod #if FABRIC implements ModInitializer, ClientModInitializer #endif
{
    public static final String MODNAME = "Example Mod";
    public static final String MODID = "example_mod";
    public static final Logger LOGGER = LogManager.getLogger(MODNAME);

    public ExampleMod() {
        #if FORGE
        var context = FMLJavaModLoadingContext.get();
        context.getModEventBus().addListener(this::commonSetup);
        context.getModEventBus().addListener(this::clientSetup);
        #endif

        AllConfigs.register((pair) -> {
            #if FORGE
            ModLoadingContext.get().registerConfig(pair.getKey(), pair.getValue().specification);
            #else
            ConfigRegistry.registerConfig(ExampleMod.MODID, pair.getKey(), pair.getValue().specification);
            #endif
        });
    }


    #if FABRIC @Override #endif
    public void onInitialize() {

    }

    #if FABRIC @Override #endif
    public void onInitializeClient() {

    }

    // Forg event stubs to call the Fabric initialize methods, and set up cloth config screen
    #if FORGE
    public void commonSetup(FMLCommonSetupEvent event) { onInitialize(); }
    public void clientSetup(FMLClientSetupEvent event) { onInitializeClient(); }
    #endif
}
