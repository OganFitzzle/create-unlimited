package net.rdh.notrainlimits.fabric;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.rdh.notrainlimits.Config;
import net.rdh.notrainlimits.NoTrainLimits;
import net.fabricmc.api.ModInitializer;

public class NoTrainLimitsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        NoTrainLimits.init();
        NoTrainLimits.LOGGER.info(EnvExecutor.unsafeRunForDist(
                () -> () -> "{} is accessing Porting Lib on a Fabric client!",
                () -> () -> "{} is accessing Porting Lib on a Fabric server!"
                ), NoTrainLimits.NAME);
        ModLoadingContext.registerConfig(NoTrainLimits.MOD_ID, Type.COMMON,  Config.SPEC);
        Config.loadConfig(Config.SPEC, FabricLoader.getInstance().getConfigDir().resolve("notrainlimits.toml"));
    }
}
