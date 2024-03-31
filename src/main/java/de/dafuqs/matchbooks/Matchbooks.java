package de.dafuqs.matchbooks;

import com.mojang.logging.LogUtils;
import de.dafuqs.matchbooks.recipe.MatchbookRecipeTypes;
import de.dafuqs.matchbooks.recipe.matchbook.BuiltinMatchbooks;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class Matchbooks implements ModInitializer {
    public static final String MOD_ID = "matchbooks";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        BuiltinMatchbooks.init();
        MatchbookRecipeTypes.init();
        LOGGER.info("Initialized Matchbooks");
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
