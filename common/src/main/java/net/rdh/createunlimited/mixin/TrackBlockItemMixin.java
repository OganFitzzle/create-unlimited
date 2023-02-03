package net.rdh.createunlimited.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.rdh.createunlimited.Methods;
import org.spongepowered.asm.mixin.Mixin;
import com.simibubi.create.content.logistics.trains.track.TrackPlacement.PlacementInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.simibubi.create.content.logistics.trains.track.TrackBlockItem;
import static net.rdh.createunlimited.Methods.tryConnectRef;

@Mixin(TrackBlockItem.class)
public class TrackBlockItemMixin {
    @Redirect(method = "useOn", at = @At(value = "INVOKE", target = tryConnectRef))
    private PlacementInfo redirectUseOn(Level level, Player player, BlockPos pos2, BlockState state2, ItemStack stack, boolean girder, boolean maximiseTurn) {
        return Methods.tryConnectLoose(level, player, pos2, state2, stack, girder, maximiseTurn);
    }
}