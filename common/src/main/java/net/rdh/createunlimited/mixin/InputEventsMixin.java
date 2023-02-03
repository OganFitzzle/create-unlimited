package net.rdh.createunlimited.mixin;

import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.events.InputEvents;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionResult;
import net.rdh.createunlimited.Config;
import net.rdh.createunlimited.CreateUnlimited;
import net.rdh.createunlimited.mixin.accessors.TrainRelocatorAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.simibubi.create.content.logistics.trains.entity.TrainRelocator.*;

@Mixin(InputEvents.class)
public class InputEventsMixin {
    @Redirect(method = "onStartUseItem", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/trains/entity/TrainRelocator;onClicked()Lnet/minecraft/world/InteractionResult;"))
    private static InteractionResult onClicked() {
        if (TrainRelocatorAccessor.getRelocatingTrain() == null)
            return InteractionResult.PASS;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null)
            return InteractionResult.PASS;
        if (player.isSpectator())
            return InteractionResult.PASS;

        if (!player.position()
                .closerThan(TrainRelocatorAccessor.getRelocatingOrigin(), Config.WRENCH_VALUE.get()) || player.isSteppingCarefully()) {
            CreateUnlimited.LOGGER.info("wrench gotten, value is " + Config.WRENCH_VALUE.get());
            TrainRelocatorAccessor.setRelocatingTrain(null);
            player.displayClientMessage(Lang.translateDirect("train.relocate.abort")
                    .withStyle(ChatFormatting.RED), true);
            return InteractionResult.PASS;
        }

        if (player.isPassenger())
            return InteractionResult.PASS;
        if (mc.level == null)
            return InteractionResult.PASS;
        Train relocating = TrainRelocatorAccessor.invokeGetRelocating(mc.level);
        if (relocating != null) {
            Boolean relocate = relocateClient(relocating, false);
            if (relocate != null && relocate)
                TrainRelocatorAccessor.setRelocatingTrain(null);
            if (relocate != null)
                return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
