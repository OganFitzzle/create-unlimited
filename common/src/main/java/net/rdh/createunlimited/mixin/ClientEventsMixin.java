package net.rdh.createunlimited.mixin;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionHandlerClient;
import com.simibubi.create.content.logistics.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.events.ClientEvents;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.rdh.createunlimited.Config;
import net.rdh.createunlimited.mixin.accessors.TrainRelocatorAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.ref.WeakReference;
import java.util.List;

import static net.rdh.createunlimited.mixin.accessors.TrainRelocatorAccessor.*;


@Mixin(ClientEvents.class)
public class ClientEventsMixin {
    @Redirect(method = "onTick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/trains/entity/TrainRelocator;clientTick()V"))
    private static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null)
            return;
        if (player.isPassenger())
            return;
        if (mc.level == null)
            return;

        if (TrainRelocatorAccessor.getRelocatingTrain() != null) {
            Train relocating = invokeGetRelocating(mc.level);
            if (relocating == null) {
                TrainRelocatorAccessor.setRelocatingTrain(null);
                return;
            }

            Entity entity = mc.level.getEntity(getRelocatingEntityId());
            if (entity instanceof AbstractContraptionEntity ce && Math.abs(ce.getPosition(0)
                    .subtract(ce.getPosition(1))
                    .lengthSqr()) > 1 / 1024d) {
                player.displayClientMessage(Lang.translateDirect("train.cannot_relocate_moving")
                        .withStyle(ChatFormatting.RED), true);
                TrainRelocatorAccessor.setRelocatingTrain(null);
                return;
            }

            if (!AllItems.WRENCH.isIn(player.getMainHandItem())) {
                player.displayClientMessage(Lang.translateDirect("train.relocate.abort")
                        .withStyle(ChatFormatting.RED), true);
                TrainRelocatorAccessor.setRelocatingTrain(null);
                return;
            }

            if (!player.position()
                    .closerThan(getRelocatingOrigin(), Config.MAX_TRAIN_RELOCATING_DISTANCE.get())) {
                player.displayClientMessage(Lang.translateDirect("train.relocate.too_far")
                        .withStyle(ChatFormatting.RED), true);
                return;
            }

            Boolean success = invokeRelocateClient(relocating, true);
            if (success == null)
                player.displayClientMessage(Lang.translateDirect("train.relocate", relocating.name), true);
            else if (success)
                player.displayClientMessage(Lang.translateDirect("train.relocate.valid")
                        .withStyle(ChatFormatting.GREEN), true);
            else
                player.displayClientMessage(Lang.translateDirect("train.relocate.invalid")
                        .withStyle(ChatFormatting.RED), true);
            return;
        }

        Couple<Vec3> rayInputs = ContraptionHandlerClient.getRayInputs(player);
        Vec3 origin = rayInputs.getFirst();
        Vec3 target = rayInputs.getSecond();

        CarriageContraptionEntity currentEntity = getHoveredEntity().get();
        if (currentEntity != null) {
            if (ContraptionHandlerClient.rayTraceContraption(origin, target, currentEntity) != null)
                return;
            setHoveredEntity(new WeakReference<>(null));
        }

        AABB aabb = new AABB(origin, target);
        List<CarriageContraptionEntity> intersectingContraptions =
                mc.level.getEntitiesOfClass(CarriageContraptionEntity.class, aabb);

        for (CarriageContraptionEntity contraptionEntity : intersectingContraptions) {
            if (ContraptionHandlerClient.rayTraceContraption(origin, target, contraptionEntity) == null)
                continue;
            setHoveredEntity(new WeakReference<>(contraptionEntity));
        }
    }
}
