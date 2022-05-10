package com.mon1tor.radiocraft.block.custom;

import com.mon1tor.radiocraft.block.properties.RadioStationPart;
import com.mon1tor.radiocraft.container.RadioStationContainer;
import com.mon1tor.radiocraft.tileentity.ModTileEntities;
import com.mon1tor.radiocraft.tileentity.RadioStationTile;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class RadioStationBlock extends HorizontalBlock {
    public static final EnumProperty<RadioStationPart> PART = EnumProperty.create("part", RadioStationPart.class);
    public static final BooleanProperty ENABLED = BooleanProperty.create("enabled");
    private static final RadioStationPart DEFAULT_PART = RadioStationPart.LEFT;
    
    public RadioStationBlock() {
        super(AbstractBlock.Properties.of(Material.METAL).sound(SoundType.METAL).strength(0.2F).noOcclusion());
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return state.getValue(PART) == RadioStationPart.LEFT ? ModTileEntities.RADIO_STATION_TILE.get().create() : null;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return state.getValue(PART) == RadioStationPart.LEFT;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PART);
        builder.add(ENABLED);
        builder.add(FACING);
    }

    private static Direction getNeighbourDirection(RadioStationPart pPart, Direction facing) {
        return pPart == RadioStationPart.LEFT ? facing.getCounterClockWise() : facing.getClockWise();
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!worldIn.isClientSide) {
            TileEntity tileEntity;
            if(state.getValue(PART) == RadioStationPart.RIGHT) {
                pos = pos.relative(getNeighbourDirection(state.getValue(PART), state.getValue(FACING)));
            }
            tileEntity = worldIn.getBlockEntity(pos);

            if(tileEntity == null) {
                return ActionResultType.FAIL;
            }

            if(tileEntity instanceof RadioStationTile) {
                INamedContainerProvider containerProvider = createContainerProvider(worldIn, pos);
                NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getBlockPos());

                RadioStationTile radioStationTile = ((RadioStationTile) tileEntity);
                if(radioStationTile.isEnabled())
                    radioStationTile.sendHistoryUpdateToClient((ServerPlayerEntity) player);
            }
        }
        return ActionResultType.SUCCESS;
    }

    private INamedContainerProvider createContainerProvider(World worldIn, BlockPos pos) {
        return new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("screen.radiocraft.radio_station");
            }

            @Nullable
            @Override
            public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                return new RadioStationContainer(i, worldIn, pos, playerInventory, playerEntity);
            }
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext pContext) {
        Direction facing = pContext.getHorizontalDirection().getOpposite();
        World world = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();

        if (world.getBlockState(pos.relative(getNeighbourDirection(DEFAULT_PART, facing))).canBeReplaced(pContext)) {
            return this.defaultBlockState().setValue(PART, DEFAULT_PART).setValue(FACING, facing).setValue(ENABLED, false);
        } else if(world.getBlockState(pos.relative(getNeighbourDirection(DEFAULT_PART.opposite(), facing))).canBeReplaced(pContext)) {
            return this.defaultBlockState().setValue(PART, DEFAULT_PART.opposite()).setValue(FACING, facing).setValue(ENABLED, false);
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
    public boolean isPathfindable(BlockState pState, IBlockReader pLevel, BlockPos pPos, PathType pType) {
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        boolean isLeft = state.getValue(PART) == RadioStationPart.LEFT;
        switch (state.getValue(FACING)){
            case NORTH: return isLeft ? SHAPE_LEFT_N : SHAPE_RIGHT_N;
            case WEST: return isLeft ? SHAPE_LEFT_W : SHAPE_RIGHT_W;
            case SOUTH: return isLeft ? SHAPE_LEFT_S : SHAPE_RIGHT_S;
            case EAST: return isLeft ? SHAPE_LEFT_E : SHAPE_RIGHT_E;
        }
        return  super.getShape(state, worldIn, pos, context);
    }

    private static final VoxelShape SHAPE_LEFT_N = Block.box(0, 0, 3, 15, 11, 13);
    private static final VoxelShape SHAPE_LEFT_W = Block.box(3, 0, 1, 13, 11, 16);
    private static final VoxelShape SHAPE_LEFT_S = Block.box(1, 0, 3, 16, 11, 13);
    private static final VoxelShape SHAPE_LEFT_E = Block.box(3, 0, 0, 13, 11, 15);

    private static final VoxelShape SHAPE_RIGHT_N = Block.box(1, 0, 3, 16, 11, 13);
    private static final VoxelShape SHAPE_RIGHT_W = Block.box(3, 0, 0, 13, 11, 15);
    private static final VoxelShape SHAPE_RIGHT_S = Block.box(0, 0, 3, 15, 11, 13);
    private static final VoxelShape SHAPE_RIGHT_E = Block.box(3, 0, 1, 13, 11, 16);
}
