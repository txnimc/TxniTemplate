package toni.examplemod.foundation.data;

#if FABRIC
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import toni.examplemod.ExampleMod;
import toni.examplemod.foundation.config.AllConfigs;

import java.util.concurrent.CompletableFuture;

public class ConfigLangDatagen extends FabricLanguageProvider {

    #if AFTER_21_1
    protected ConfigLangDatagen(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }
    #else
    protected ConfigLangDatagen(FabricDataOutput dataOutput) {
        super(dataOutput);
    }
    #endif

    @Override
    public void generateTranslations(#if AFTER_21_1 HolderLookup.Provider registryLookup, #endif TranslationBuilder translationBuilder) {
        AllConfigs.generateTranslations(translationBuilder);
    }

    @Override
    public String getName() {
        return "ExampleMod Data Gen";
    }
}
#endif