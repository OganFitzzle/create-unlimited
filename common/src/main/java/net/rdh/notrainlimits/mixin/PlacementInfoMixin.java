package net.rdh.notrainlimits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import com.simibubi.create.content.logistics.trains.track.TrackPlacement.PlacementInfo;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * A simple mixin to get the message of a PlacementInfo object.
 */
@Mixin(PlacementInfo.class)
public interface PlacementInfoMixin {
    @Accessor
    String getMessage();
}
