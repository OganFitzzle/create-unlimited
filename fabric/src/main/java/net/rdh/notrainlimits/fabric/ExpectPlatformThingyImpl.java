package net.rdh.notrainlimits.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class ExpectPlatformThingyImpl {
	public static String platformName() {
		return FabricLoader.getInstance().isModLoaded("quilt_loader") ? "Quilt" : "Fabric";
	}
}
