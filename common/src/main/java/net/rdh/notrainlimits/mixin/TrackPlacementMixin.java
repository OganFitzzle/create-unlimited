package net.rdh.notrainlimits.mixin;

import net.rdh.notrainlimits.NoTrainLimits;
import org.spongepowered.asm.mixin.Mixin;
import com.simibubi.create.content.logistics.trains.track.TrackPlacement;
import com.simibubi.create.content.logistics.trains.track.TrackPlacement.PlacementInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TrackPlacement.class)
public class TrackPlacementMixin {

    private static final String[]
            messagesToIgnore = {"perpendicular", "ascending_s_curve", "too_sharp", "too_steep", "turn_90"},
            crashMessages = {"slope_turn", "opposing_slopes", "leave_slope_ascending", "leave_slope_descending"};

    @Redirect(method = "tryConnect", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/trains/track/TrackPlacement$PlacementInfo;withMessage(Ljava/lang/String;)Lcom/simibubi/create/content/logistics/trains/track/TrackPlacement$PlacementInfo;"))
    private static PlacementInfo messageChecker(PlacementInfo instance, String message) {
        if(willCrash(message))
            return instance.withMessage(message).tooJumbly();
        if(contains(message)) {
            ((PlacementInfoMixin)instance).setValid(true);
            return instance;
        }
        return instance.withMessage(message);
    }

    @Redirect(method = "tryConnect", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/trains/track/TrackPlacement$PlacementInfo;tooJumbly()Lcom/simibubi/create/content/logistics/trains/track/TrackPlacement$PlacementInfo;"))
    private static PlacementInfo unJumblier(PlacementInfo instance) {
        if(contains(((PlacementInfoMixin)instance).getMessage()))
            return instance;
        return instance.tooJumbly();
    }

    private static boolean contains(String message) {
        if(message == null) return false;
        for(String s : messagesToIgnore)
            if(message.equals(s) || s.equals("track." + message)) return true;
        return false;
    }

    private static boolean willCrash(String message) {
        if(message == null) return false;
        for(String s : crashMessages)
            if(message.equals(s) || s.equals("track." + message)) return true;
        return false;
    }
}