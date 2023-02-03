package net.rdh.createunlimited;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class ExpectPlatformThingy {
    @ExpectPlatform
    public static String platformName() {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }
}
