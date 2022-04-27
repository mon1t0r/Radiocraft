package com.mon1tor.radiocraft.block.custom;

import com.mon1tor.radiocraft.block.properties.RadioStationPart;
import com.mon1tor.radiocraft.tileentity.ModTileEntities;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RadioStationBlock extends HorizontalBlock {
    public static final EnumProperty<RadioStationPart> PART = EnumProperty.create("part", RadioStationPart.class);
    private static final RadioStationPart DEFAULT_PART = RadioStationPart.LEFT;
    
    public RadioStationBlock() {
        super(AbstractBlock.Properties.of(Material.METAL).sound(SoundType.METAL).strength(0.2F).noOcclusion());
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.RADIO_STATION_TILE.get().create();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PART);
        builder.add(FACING);
    }

    private static Direction getNeighbourDirection(RadioStationPart pPart, Direction facing) {
        return pPart == RadioStationPart.LEFT ? facing.getCounterClockWise() : facing.getClockWise();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext pContext) {
        Direction facing = pContext.getHorizontalDirection().getOpposite();
        World world = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();

        if (world.getBlockState(pos.relative(getNeighbourDirection(DEFAULT_PART, facing))).canBeReplaced(pContext)) {
            return this.defaultBlockState().setValue(PART, DEFAULT_PART).setValue(FACING, facing);
        } else if(world.getBlockState(pos.relative(getNeighbourDirection(DEFAULT_PART.opposite(), facing))).canBeReplaced(pContext)) {
            return this.defaultBlockState().setValue(PART, DEFAULT_PART.opposite()).setValue(FACING, facing);
        }
        return null;
    }

    @Override
    public void setPlacedBy(World pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        if (!pLevel.isClientSide) {
            BlockPos blockpos = pPos.relative(getNeighbourDirection(pState.getValue(PART), pState.getValue(FACING)));
            pLevel.setBlock(blockpos, pState.setValue(PART, pState.getValue(PART).opposite()), 3);
            pLevel.blockUpdated(pPos, Blocks.AIR);
            pState.updateNeighbourShapes(pLevel, pPos, 3);
        }

    }

    @Override
    public void playerWillDestroy(World pLevel, BlockPos pPos, BlockState pState, PlayerEntity pPlayer) {
        if (!pLevel.isClientSide && pPlayer.isCreative()) {
            RadioStationPart part = pState.getValue(PART);
            if(part == RadioStationPart.RIGHT){
                BlockPos blockpos = pPos.relative(getNeighbourDirection(part, pState.getValue(FACING)));
                BlockState blockstate = pLevel.getBlockState(blockpos);
                if (blockstate.getBlock() == this && blockstate.getValue(PART) == RadioStationPart.LEFT) {
                    pLevel.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                    pLevel.levelEvent(pPlayer, 2001, blockpos, Block.getId(blockstate));
                }
            }
        }

        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, IWorld pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if(pFacing == getNeighbourDirection(pState.getValue(PART), pState.getValue(FACING))) {
            return pFacingState.is(this) && pFacingState.getValue(PART) != pState.getValue(PART) ? pState : Blocks.AIR.defaultBlockState();
        }
        return pState;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState pState) {
        return PushReaction.DESTROY;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.getValue(FACING)){
            case NORTH: return SHAPE_N;
            case WEST: return SHAPE_W;
            case SOUTH: return SHAPE_S;
            case EAST: return SHAPE_E;
        }
        return  super.getShape(state, worldIn, pos, context);
    }

    private static final VoxelShape SHAPE_N = Block.box(0, 0, 1, 16, 12, 15);
    private static final VoxelShape SHAPE_W = Block.box(1, 0, 0, 15, 12, 16);
    private static final VoxelShape SHAPE_S = Block.box(0, 0, 1, 16, 12, 15);
    private static final VoxelShape SHAPE_E = Block.box(1, 0, 0, 15, 12, 16);
}
