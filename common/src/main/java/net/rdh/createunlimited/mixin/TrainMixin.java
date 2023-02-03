package net.rdh.createunlimited.mixin;

import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.trains.TrackGraph;
import com.simibubi.create.content.logistics.trains.entity.*;
import com.simibubi.create.content.logistics.trains.entity.Carriage.DimensionalCarriageEntity;
import com.simibubi.create.content.logistics.trains.management.schedule.ScheduleRuntime;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.rdh.createunlimited.Config;
import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Train.class)
public class TrainMixin {
    @Shadow public double speed = 0;
    @Shadow public Double speedBeforeStall = null;
    @Shadow public int carriageWaitingForChunks = -1;
    @Shadow public TrackGraph graph;
    @Shadow public Navigation navigation;
    @Shadow public ScheduleRuntime runtime;
    @Shadow public Component name;
    @Shadow public TrainStatus status;
    @Shadow public List<Carriage> carriages;
    @Shadow public List<Integer> carriageSpacing;
    @Shadow public boolean derailed;
    @Shadow double[] stress;
    @Shadow public Player backwardsDriver;
    @Shadow private void updateConductors(){throw new AssertionError();}
    @Shadow private void tickPassiveSlowdown(){throw new AssertionError();}
    @Shadow private void tickDerailedSlowdown(){throw new AssertionError();}
    @Shadow private void collideWithOtherTrains(Level level, Carriage carriage){throw new AssertionError();}
    @Shadow private void updateNavigationTarget(double distance){throw new AssertionError();}

    /**
     * truly the javadoc of time
     * @author rdh
     * @reason because
     */
    @Overwrite
    public void tick(Level level) {
        //CreateUnlimited.LOGGER.info("Train tick method called");
        Create.RAILWAYS.markTracksDirty();

        if (graph == null) {
            carriages.forEach(c -> c.manageEntities(level));
            updateConductors();
            return;
        }

        updateConductors();
        runtime.tick(level);
        navigation.tick(level);

        tickPassiveSlowdown();
        if (derailed)
            tickDerailedSlowdown();

        double distance = speed;
        Carriage previousCarriage = null;
        int carriageCount = carriages.size();
        boolean stalled = false;
        double maxStress = 0;

        if (carriageWaitingForChunks != -1)
            distance = 0;

        for (int i = 0; i < carriageCount; i++) {
            Carriage carriage = carriages.get(i);
            if (previousCarriage != null) {
                int target = carriageSpacing.get(i - 1);
                double actual = target;

                TravellingPoint leadingPoint = carriage.getLeadingPoint();
                TravellingPoint trailingPoint = previousCarriage.getTrailingPoint();

                int entries = 0;
                double total = 0;

                if (leadingPoint.node1 != null && trailingPoint.node1 != null) {
                    ResourceKey<Level> d1 = leadingPoint.node1.getLocation().dimension;
                    ResourceKey<Level> d2 = trailingPoint.node1.getLocation().dimension;
                    for (boolean b : Iterate.trueAndFalse) {
                        ResourceKey<Level> d = b ? d1 : d2;
                        if (!b && d1.equals(d2))
                            continue;
                        if (!d1.equals(d2))
                            continue;

                        DimensionalCarriageEntity dimensional = carriage.getDimensionalIfPresent(d);
                        DimensionalCarriageEntity dimensional2 = previousCarriage.getDimensionalIfPresent(d);
                        if (dimensional == null || dimensional2 == null)
                            continue;

                        Vec3 leadingAnchor = dimensional.leadingAnchor();
                        Vec3 trailingAnchor = dimensional2.trailingAnchor();
                        if (leadingAnchor == null || trailingAnchor == null)
                            continue;

                        total += leadingAnchor.distanceTo(trailingAnchor);
                        entries++;
                    }
                }


                if (entries > 0)
                    actual = total / entries;

                stress[i - 1] = target - actual;
                maxStress = Math.max(maxStress, Math.abs(target - actual));
            }

            previousCarriage = carriage;

            if (carriage.stalled) {
                if (speedBeforeStall == null)
                    speedBeforeStall = speed;
                distance = 0;
                speed = 0;
                stalled = true;
            }
        }

        if (!stalled && speedBeforeStall != null) {
            speed = Mth.clamp(speedBeforeStall, -1, 1);
            speedBeforeStall = null;
        }

        // positive stress: carriages should move apart
        // negative stress: carriages should move closer

        boolean approachingStation = navigation.distanceToDestination < 5;
        double leadingModifier = approachingStation ? 0.75d : 0.5d;
        double trailingModifier = approachingStation ? 0d : 0.125d;

        boolean blocked = false;
        boolean iterateFromBack = speed < 0;

        for (int index = 0; index < carriageCount; index++) {
            int i = iterateFromBack ? carriageCount - 1 - index : index;
            double leadingStress = i == 0 ? 0 : stress[i - 1] * -(iterateFromBack ? trailingModifier : leadingModifier);
            double trailingStress =
                    i == stress.length ? 0 : stress[i] * (iterateFromBack ? leadingModifier : trailingModifier);

            Carriage carriage = carriages.get(i);

            TravellingPoint toFollowForward = i == 0 ? null
                    : carriages.get(i - 1)
                    .getTrailingPoint();

            TravellingPoint toFollowBackward = i == carriageCount - 1 ? null
                    : carriages.get(i + 1)
                    .getLeadingPoint();

            double totalStress = derailed ? 0 : leadingStress + trailingStress;

            boolean first = i == 0;
            boolean last = i == carriageCount - 1;
            int carriageType = first ? last ? 3 : 0 : last ? 2 : 1;
            double actualDistance =
                    carriage.travel(level, graph, distance + totalStress, toFollowForward, toFollowBackward, carriageType);
            blocked |= carriage.blocked;

            boolean onTwoBogeys = carriage.isOnTwoBogeys();
            maxStress = Math.max(maxStress, onTwoBogeys ? carriage.bogeySpacing - carriage.getAnchorDiff() : 0);
            maxStress = Math.max(maxStress, carriage.leadingBogey()
                    .getStress());
            if (onTwoBogeys)
                maxStress = Math.max(maxStress, carriage.trailingBogey()
                        .getStress());

            if (index == 0) {
                distance = actualDistance;
                collideWithOtherTrains(level, carriage);
                backwardsDriver = null;
                if (graph == null)
                    return;
            }
        }

        if (blocked) {
            speed = 0;
            navigation.cancelNavigation();
            runtime.tick(level);
            status.endOfTrack();

        } else if (maxStress > Config.MAX_ALLOWED_STRESS.get() && Config.MAX_ALLOWED_STRESS.get() != -1) {
            speed = 0;
            navigation.cancelNavigation();
            runtime.tick(level);
            derailed = true;
            status.highStress();

        } else if (speed != 0)
            status.trackOK();

        updateNavigationTarget(distance);
    }
}
