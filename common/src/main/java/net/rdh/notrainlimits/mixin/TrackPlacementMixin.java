package net.rdh.notrainlimits.mixin;

import com.simibubi.create.content.logistics.trains.track.TrackPlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.rdh.notrainlimits.Methods;
import org.spongepowered.asm.mixin.Mixin;
import com.simibubi.create.content.logistics.trains.track.TrackPlacement.PlacementInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.rdh.notrainlimits.Methods.tryConnectRef;

@Mixin(TrackPlacement.class)
public class TrackPlacementMixin {
        @Redirect(method = "clientTick", at = @At(value = "INVOKE", target = tryConnectRef))
        private static PlacementInfo redirectTryConnect(Level level, Player player, BlockPos pos2, BlockState state2, ItemStack stack, boolean girder, boolean maximiseTurn) {
            return Methods.tryConnectLoose(level, player, pos2, state2, stack, girder, maximiseTurn);
        }
}
