package net.rdh.notrainlimits.mixin;

import com.simibubi.create.content.logistics.trains.TrackEdge;
import net.rdh.notrainlimits.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TrackEdge.class)
public class TrackEdgeMixin {
    @Inject(method = "canTravelTo", at = @At("RETURN"), cancellable = true, remap = false)
    private void canTravelTo(TrackEdge other, CallbackInfoReturnable<Boolean> cir) {
        if(Config.VERY_ILLEGAL.get())
            cir.setReturnValue(true);
    }
}
