package net.rdh.createunlimited.mixin.accessors;

import com.simibubi.create.content.logistics.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.content.logistics.trains.entity.TrainRelocator;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.lang.ref.WeakReference;
import java.util.UUID;

@Mixin(TrainRelocator.class)
public interface TrainRelocatorAccessor {
    @Accessor static UUID getRelocatingTrain() {
        throw new AssertionError();
    }
    @Accessor static void setRelocatingTrain(UUID relocatingTrain) {
        throw new AssertionError();
    }
    @Accessor static Vec3 getRelocatingOrigin(){
        throw new AssertionError();
    }
    @Invoker static Train invokeGetRelocating(LevelAccessor level) {
        throw new AssertionError();
    }
    @Accessor static WeakReference<CarriageContraptionEntity> getHoveredEntity() {
        throw new AssertionError();
    }
    @Accessor static void setHoveredEntity(WeakReference<CarriageContraptionEntity> hoveredEntity) {
        throw new AssertionError();
    }
    @Invoker static Boolean invokeRelocateClient(Train relocating, boolean simulate) {
        throw new AssertionError();
    }
    @Accessor static int getRelocatingEntityId() {
        throw new AssertionError();
    }

}
