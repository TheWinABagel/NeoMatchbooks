package de.dafuqs.matchbooks;

import com.mojang.logging.LogUtils;
import de.dafuqs.matchbooks.recipe.MatchbookRecipeTypes;
import de.dafuqs.matchbooks.recipe.matchbook.BuiltinMatchbooks;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Matchbooks.MOD_ID)
public class Matchbooks {
    public static final String MOD_ID = "matchbooks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Matchbooks() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onInitialize);
        bus.addListener(MatchbookRecipeTypes::registerSerializer);
    }


    @SubscribeEvent
    public void onInitialize(FMLCommonSetupEvent event) {
        BuiltinMatchbooks.init();
        LOGGER.info("Initialized Matchbooks");
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
