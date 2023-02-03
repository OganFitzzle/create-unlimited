package net.rdh.createunlimited.mixin;

import com.google.common.base.Objects;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.contraptions.components.structureMovement.glue.SuperGlueEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.glue.SuperGlueSelectionHandler;
import com.simibubi.create.content.contraptions.components.structureMovement.glue.SuperGlueSelectionHelper;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.RaycastHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.rdh.createunlimited.Config;
import net.rdh.createunlimited.mixin.accessors.SuperGlueSectionHandlerAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * overly long class name
 */
@Mixin(SuperGlueSelectionHandler.class)
public class SuperGlueSectionHandlerMixin {
    private static final int PASSIVE = 0x4D9162;
    private static final int HIGHLIGHT = 0x68c586;
    private static final int FAIL = 0xc5b548;
    /**
     * @author rdh
     * @reason because
     */
    @Overwrite(remap = false)
    public void tick() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        BlockPos hovered = null;
        ItemStack stack = player.getMainHandItem();

        if (!((SuperGlueSectionHandlerAccessor)this).invokeIsGlue(stack)) {
            if (((SuperGlueSectionHandlerAccessor)this).getFirstPos() != null)
                ((SuperGlueSectionHandlerAccessor)this).discard();
            return;
        }

        if (((SuperGlueSectionHandlerAccessor)this).getClusterCooldown() > 0) {
            if (((SuperGlueSectionHandlerAccessor)this).getClusterCooldown() == 25)
                player.displayClientMessage(Components.immutableEmpty(), true);
            CreateClient.OUTLINER.keep(((SuperGlueSectionHandlerAccessor)this).getClusterOutlineSlot());
            ((SuperGlueSectionHandlerAccessor)this).setClusterCooldown(((SuperGlueSectionHandlerAccessor)this).getClusterCooldown() - 1);
        }

        AABB scanArea = player.getBoundingBox()
                .inflate(32, 16, 32);

        List<SuperGlueEntity> glueNearby = mc.level.getEntitiesOfClass(SuperGlueEntity.class, scanArea);

        ((SuperGlueSectionHandlerAccessor)this).setSelected(null);
        if (((SuperGlueSectionHandlerAccessor)this).getFirstPos() == null) {
            double range = ReachEntityAttributes.getReachDistance(player, mc.gameMode.getPickRange()) + 1;
            Vec3 traceOrigin = RaycastHelper.getTraceOrigin(player);
            Vec3 traceTarget = RaycastHelper.getTraceTarget(player, range, traceOrigin);

            double bestDistance = Double.MAX_VALUE;
            for (SuperGlueEntity glueEntity : glueNearby) {
                Optional<Vec3> clip = glueEntity.getBoundingBox()
                        .clip(traceOrigin, traceTarget);
                if (clip.isEmpty())
                    continue;
                Vec3 vec3 = clip.get();
                double distanceToSqr = vec3.distanceToSqr(traceOrigin);
                if (distanceToSqr > bestDistance)
                    continue;
                ((SuperGlueSectionHandlerAccessor)this).setSelected(glueEntity);
                ((SuperGlueSectionHandlerAccessor)this).setSoundSource(new BlockPos(vec3));
                bestDistance = distanceToSqr;
            }

            for (SuperGlueEntity glueEntity : glueNearby) {
                boolean h = ((SuperGlueSectionHandlerAccessor)this).getClusterCooldown() == 0 && glueEntity == ((SuperGlueSectionHandlerAccessor)this).getSelected();
                AllSpecialTextures faceTex = h ? AllSpecialTextures.GLUE : null;
                CreateClient.OUTLINER.showAABB(glueEntity, glueEntity.getBoundingBox())
                        .colored(h ? HIGHLIGHT : PASSIVE)
                        .withFaceTextures(faceTex, faceTex)
                        .disableNormals()
                        .lineWidth(h ? 1 / 16f : 1 / 64f);
            }
        }

        HitResult hitResult = mc.hitResult;
        if (hitResult != null && hitResult.getType() == Type.BLOCK)
            hovered = ((BlockHitResult) hitResult).getBlockPos();

        if (hovered == null) {
            ((SuperGlueSectionHandlerAccessor)this).setHoveredPos(null);
            return;
        }

        if (((SuperGlueSectionHandlerAccessor)this).getFirstPos() != null && !((SuperGlueSectionHandlerAccessor)this).getFirstPos().closerThan(hovered, Config.MAX_GLUE_RANGE.get())) {
            Lang.translate("super_glue.too_far")
                    .color(FAIL)
                    .sendStatus(player);
            return;
        }

        boolean cancel = player.isSteppingCarefully();
        if (cancel && ((SuperGlueSectionHandlerAccessor)this).getFirstPos() == null)
            return;

        AABB currentSelectionBox = ((SuperGlueSectionHandlerAccessor)this).getSelectionBox();

        boolean unchanged = Objects.equal(hovered, ((SuperGlueSectionHandlerAccessor)this).getHoveredPos());

        if (unchanged) {
            if (((SuperGlueSectionHandlerAccessor)this).getCurrentCluster() != null) {
                boolean canReach = ((SuperGlueSectionHandlerAccessor)this).getCurrentCluster().contains(hovered);
                boolean canAfford = SuperGlueSelectionHelper.collectGlueFromInventory(player, ((SuperGlueSectionHandlerAccessor)this).getGlueRequired(), true);
                int color = HIGHLIGHT;
                String key = "super_glue.click_to_confirm";

                if (!canReach) {
                    color = FAIL;
                    key = "super_glue.cannot_reach";
                } else if (!canAfford) {
                    color = FAIL;
                    key = "super_glue.not_enough";
                } else if (cancel) {
                    color = FAIL;
                    key = "super_glue.click_to_discard";
                }

                Lang.translate(key)
                        .color(color)
                        .sendStatus(player);

                if (currentSelectionBox != null)
                    CreateClient.OUTLINER.showAABB(((SuperGlueSectionHandlerAccessor)this).getBbOutlineSlot(), currentSelectionBox)
                            .colored(canReach && canAfford && !cancel ? HIGHLIGHT : FAIL)
                            .withFaceTextures(AllSpecialTextures.GLUE, AllSpecialTextures.GLUE)
                            .disableNormals()
                            .lineWidth(1 / 16f);

                CreateClient.OUTLINER.showCluster(((SuperGlueSectionHandlerAccessor)this).getClusterOutlineSlot(), ((SuperGlueSectionHandlerAccessor)this).getCurrentCluster())
                        .colored(0x4D9162)
                        .disableNormals()
                        .lineWidth(1 / 64f);
            }

            return;
        }

        ((SuperGlueSectionHandlerAccessor)this).setHoveredPos(hovered);

        Set<BlockPos> cluster = SuperGlueSelectionHelper.searchGlueGroup(mc.level, ((SuperGlueSectionHandlerAccessor)this).getFirstPos(), ((SuperGlueSectionHandlerAccessor)this).getHoveredPos(), true);
        ((SuperGlueSectionHandlerAccessor)this).setCurrentCluster(cluster);
        ((SuperGlueSectionHandlerAccessor)this).setGlueRequired(1);
    }
}
