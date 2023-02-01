package net.rdh.notrainlimits;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final String CATEGORY_NTL = "notrainlimits";

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.BooleanValue MOD_ENABLED;
    public static ForgeConfigSpec.BooleanValue VERY_ILLEGAL;

    static {
        BUILDER.comment("Enable/Disable NTL Features").push(CATEGORY_NTL);
        MOD_ENABLED = BUILDER.comment("Allow for the placement of illegal tracks.").define("mod_enabled", true);
        VERY_ILLEGAL = BUILDER.comment("Allow trains to drive on \"very illegal\" tracks.").define("very_illegal", true);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
    public static void loadConfig(ForgeConfigSpec spec, java.nio.file.Path path) {
        NoTrainLimits.LOGGER.info("Loading NTL config!");
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        configData.load();
        spec.setConfig(configData);
    }
}