package net.rdh.notrainlimits.forge;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.rdh.notrainlimits.Config;
import net.rdh.notrainlimits.NoTrainLimits;
import net.minecraftforge.fml.common.Mod;

@Mod(NoTrainLimits.MOD_ID)
public class NoTrainLimitsForge {
    public NoTrainLimitsForge() {
        initConfig();
        NoTrainLimits.init();
    }
    private static void initConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        Config.loadConfig(Config.SPEC, FMLPaths.CONFIGDIR.get().resolve("notrainlimits.toml"));
    }
}