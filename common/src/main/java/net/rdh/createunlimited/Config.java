package net.rdh.createunlimited;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final String CATEGORY_NTL = "createunlimited";

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.BooleanValue MOD_ENABLED;
    public static ForgeConfigSpec.BooleanValue VERY_ILLEGAL_DRIVING;
    public static ForgeConfigSpec.IntValue MAX_TRAIN_RELOCATING_DISTANCE;
    public static ForgeConfigSpec.IntValue MAX_ALLOWED_STRESS;
    public static ForgeConfigSpec.IntValue MAX_GLUE_RANGE;

    static {
        BUILDER.comment("Enable/Disable NTL Features").push(CATEGORY_NTL);
        MOD_ENABLED = BUILDER.comment("Allow for the placement of illegal tracks.").define("mod_enabled", true);
        VERY_ILLEGAL_DRIVING = BUILDER.comment("Allow trains to drive on \"very illegal\" tracks.").define("very_illegal_driving", true);
        MAX_TRAIN_RELOCATING_DISTANCE = BUILDER.comment("The maximum distance allowed to move trains with wrenches").defineInRange("max_train_relocating_distance", 24, 0, 128);
        MAX_ALLOWED_STRESS = BUILDER.comment("Maximum stress from couplings before train derails. Set to -1 to disable.").defineInRange("max_allowed_stress", 4, -1, 32);
        MAX_GLUE_RANGE = BUILDER.comment("Maximum range for superglue.").defineInRange("max_glue_range", 24, 0, 256);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
    public static void loadConfig(ForgeConfigSpec spec, java.nio.file.Path path) {
        CreateUnlimited.LOGGER.info("Loading NTL config!");
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        configData.load();
        spec.setConfig(configData);
    }
}