package net.rdh.notrainlimits.mixin;

import com.simibubi.create.content.logistics.trains.track.TrackPlacement;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TrackPlacement.class)
public interface TrackPlacementAccessor {
    @Accessor("hoveringPos") static BlockPos getHoveringPos() { throw new AssertionError(); }
    @Accessor("hoveringPos") static void setHoveringPos(BlockPos pos) { throw new AssertionError(); }
    @Accessor("hoveringMaxed") static boolean getHoveringMaxed() { throw new AssertionError(); }
    @Accessor("hoveringMaxed") static void setHoveringMaxed(boolean maxed) { throw new AssertionError(); }
    @Accessor("hoveringAngle") static int getHoveringAngle() { throw new AssertionError(); }
    @Accessor("hoveringAngle") static void setHoveringAngle(int angle) { throw new AssertionError(); }
    @Accessor("lastItem") static ItemStack getLastItem() { throw new AssertionError(); }
    @Accessor("lastItem") static void setLastItem(ItemStack item) { throw new AssertionError(); }
    @Invoker("placeTracks") static TrackPlacement.PlacementInfo invokePlaceTracks(Level level, TrackPlacement.PlacementInfo info, BlockState state1, BlockState state2,
                                                                                  BlockPos targetPos1, BlockPos targetPos2, boolean simulate) { throw new AssertionError(); }
    @Invoker("paveTracks") static void invokePaveTracks(Level level, TrackPlacement.PlacementInfo info, BlockItem blockItem, boolean simulate) { throw new AssertionError(); }
}
