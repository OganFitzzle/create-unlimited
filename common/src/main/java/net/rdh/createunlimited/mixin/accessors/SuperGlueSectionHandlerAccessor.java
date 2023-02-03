package net.rdh.createunlimited.mixin.accessors;

import com.simibubi.create.content.contraptions.components.structureMovement.glue.SuperGlueEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.glue.SuperGlueSelectionHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(SuperGlueSelectionHandler.class)
public interface SuperGlueSectionHandlerAccessor {
    @Invoker("isGlue") public boolean invokeIsGlue(ItemStack stack);
    @Accessor public void setFirstPos(BlockPos firstPos);
    @Accessor public BlockPos getFirstPos();
    @Invoker("discard") public void discard();
    @Accessor public int getClusterCooldown();
    @Accessor public void setClusterCooldown(int clusterCooldown);
    @Accessor public Object getClusterOutlineSlot();
    @Accessor public void setClusterOutlineSlot(Object clusterOutlineSlot);
    @Accessor public SuperGlueEntity getSelected();
    @Accessor public void setSelected(SuperGlueEntity selected);
    @Accessor("soundSourceForRemoval") public BlockPos getSoundSource();
    @Accessor("soundSourceForRemoval") public void setSoundSource(BlockPos soundSource);
    @Accessor public BlockPos getHoveredPos();
    @Accessor public void setHoveredPos(BlockPos hoveredPos);
    @Accessor public Set<BlockPos> getCurrentCluster();
    @Accessor public void setCurrentCluster(Set<BlockPos> currentCluster);
    @Accessor public Object getBbOutlineSlot();
    @Accessor public void setBbOutlineSlot(Object bbOutlineSlot);
    @Accessor public int getGlueRequired();
    @Accessor public void setGlueRequired(int glueRequired);
    @Invoker("getCurrentSelectionBox") public AABB getSelectionBox();
}
