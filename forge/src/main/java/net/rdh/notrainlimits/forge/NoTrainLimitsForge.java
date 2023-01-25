package net.rdh.notrainlimits.forge;

import net.rdh.notrainlimits.NoTrainLimits;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NoTrainLimits.MOD_ID)
public class NoTrainLimitsForge {
    public NoTrainLimitsForge() {
        // registrate must be given the mod event bus on forge before registration
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        NoTrainLimits.init();
    }
}
