package net.rdh.notrainlimits.mixin;

import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.events.InputEvents;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.rdh.notrainlimits.Config;
import net.rdh.notrainlimits.NoTrainLimits;
import net.rdh.notrainlimits.mixin.accessors.TrainRelocatorAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.simibubi.create.content.logistics.trains.entity.TrainRelocator.*;

@Mixin(InputEvents.class)
public class InputEventsMixin {
    @Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/trains/entity/TrainRelocator;onClicked()Z"))
    private static boolean onClicked() {
        if (TrainRelocatorAccessor.getRelocatingTrain() == null)
            return false;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null)
            return false;
        if (player.isSpectator())
            return false;

        if (!player.position()
                .closerThan(TrainRelocatorAccessor.getRelocatingOrigin(), Config.WRENCH_VALUE.get()) || player.isSteppingCarefully()) {
            NoTrainLimits.LOGGER.info("Aborting train relocation");
            TrainRelocatorAccessor.setRelocatingTrain(null);
            player.displayClientMessage(Lang.translateDirect("train.relocate.abort")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }

        if (player.isPassenger())
            return false;
        if (mc.level == null)
            return false;
        Train relocating = TrainRelocatorAccessor.invokeGetRelocating(mc.level);
        if (relocating != null) {
            Boolean relocate = relocateClient(relocating, false);
            if (relocate != null && relocate)
                TrainRelocatorAccessor.setRelocatingTrain(null);
            return relocate != null; // cancel
        }
        return false;
    }
}
