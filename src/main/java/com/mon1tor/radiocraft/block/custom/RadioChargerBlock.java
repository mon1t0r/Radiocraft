package com.mon1tor.radiocraft.block.custom;

import com.mon1tor.radiocraft.container.RadioChargerContainer;
import com.mon1tor.radiocraft.item.ModItems;
import com.mon1tor.radiocraft.tileentity.ModTileEntities;
import com.mon1tor.radiocraft.tileentity.RadioChargerTile;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class RadioChargerBlock extends HorizontalBlock {
    public static final BooleanProperty CHARGING = BooleanProperty.create("charging");

    public RadioChargerBlock() {
        super(AbstractBlock.Properties.of(Material.METAL).noOcclusion().sound(SoundType.METAL));
    }

    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return !state.getCollisionShape(worldIn, pos).getFaceShape(Direction.UP).isEmpty() || state.isFaceSturdy(worldIn, pos, Direction.UP);
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.below();
        return this.isValidGround(worldIn.getBlockState(blockpos), worldIn, blockpos);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (!stateIn.canSurvive(worldIn, currentPos)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!worldIn.isClientSide) {
            TileEntity tileEntity = worldIn.getBlockEntity(pos);
            if(tileEntity instanceof RadioChargerTile) {
                ItemStack heldStack = player.getMainHandItem();

                LazyOptional<IItemHandler> optional = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
                if(!optional.isPresent()) return ActionResultType.PASS;

                IItemHandler handler = optional.resolve().get();
                boolean isRadioInside = !handler.getStackInSlot(0).isEmpty();
                boolean isHoldingRadio = heldStack.getItem() == ModItems.RADIO.get();

                if(isHoldingRadio && !isRadioInside) {
                    player.setItemInHand(Hand.MAIN_HAND, handler.insertItem(0, heldStack, false));
                } else if(player.isCrouching() && isRadioInside) {
                    ItemStack extracted = handler.extractItem(0, 1, false);
                    if(isHoldingRadio && heldStack.getCount() < heldStack.getMaxStackSize()) heldStack.setCount(heldStack.getCount() + 1);
                    else if(heldStack.isEmpty()) player.setItemInHand(Hand.MAIN_HAND, extracted);
                    else player.addItem(extracted);
                } else {
                    INamedContainerProvider containerProvider = createContainerProvider(worldIn, pos);
                    NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getBlockPos());
                }
            } else throw new IllegalStateException("Container provider is missing");
        }
        return ActionResultType.SUCCESS;
    }

    private INamedContainerProvider createContainerProvider(World worldIn, BlockPos pos) {
        return new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("screen.radiocraft.radio_charger");
            }

            @Nullable
            @Override
            public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                return new RadioChargerContainer(i, worldIn, pos, playerInventory, playerEntity);
            }
        };
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.RADIO_CHARGER_TILE.get().create();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(CHARGING);
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(CHARGING, false);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState pState) {
        return PushReaction.DESTROY;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        boolean isCharging = state.getValue(CHARGING);
        switch (state.getValue(FACING)){
            case NORTH: return isCharging ? SHAPE_CHARGING_N : SHAPE_N;
            case WEST: return  isCharging ? SHAPE_CHARGING_W : SHAPE_W;
            case SOUTH: return  isCharging ? SHAPE_CHARGING_S : SHAPE_S;
            case EAST: return  isCharging ? SHAPE_CHARGING_E : SHAPE_E;
        }
        return  super.getShape(state, worldIn, pos, context);
    }

    private static final VoxelShape SHAPE_N = Stream.of(
            Block.box(2.5, 0, 2.5, 13.5, 2, 13.5),
            Block.box(5.5, 2, 9.5, 10.5, 3, 11.5),
            Block.box(3.5, 2, 6.5, 5.5, 3, 11.5),
            Block.box(10.5, 2, 6.5, 12.5, 3, 11.5),
            Block.box(9.5, 2, 4.5, 12.5, 3, 6.5),
            Block.box(3.5, 2, 4.5, 6.5, 3, 6.5)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_W = Stream.of(
            Block.box(2.5, 0, 2.5, 13.5, 2, 13.5),
            Block.box(9.5, 2, 5.5, 11.5, 3, 10.5),
            Block.box(6.5, 2, 10.5, 11.5, 3, 12.5),
            Block.box(6.5, 2, 3.5, 11.5, 3, 5.5),
            Block.box(4.5, 2, 3.5, 6.5, 3, 6.5),
            Block.box(4.5, 2, 9.5, 6.5, 3, 12.5)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_S = Stream.of(
            Block.box(2.5, 0, 2.5, 13.5, 2, 13.5),
            Block.box(5.5, 2, 4.5, 10.5, 3, 6.5),
            Block.box(10.5, 2, 4.5, 12.5, 3, 9.5),
            Block.box(3.5, 2, 4.5, 5.5, 3, 9.5),
            Block.box(3.5, 2, 9.5, 6.5, 3, 11.5),
            Block.box(9.5, 2, 9.5, 12.5, 3, 11.5)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_E = Stream.of(
            Block.box(2.5, 0, 2.5, 13.5, 2, 13.5),
            Block.box(4.5, 2, 5.5, 6.5, 3, 10.5),
            Block.box(4.5, 2, 3.5, 9.5, 3, 5.5),
            Block.box(4.5, 2, 10.5, 9.5, 3, 12.5),
            Block.box(9.5, 2, 9.5, 11.5, 3, 12.5),
            Block.box(9.5, 2, 3.5, 11.5, 3, 6.5)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_CHARGING_N = Stream.of(
            Block.box(2.5, 0, 2.5, 13.5, 2, 13.5),
            Block.box(5.5, 2, 9.5, 10.5, 3, 11.5),
            Block.box(3.5, 2, 6.5, 5.5, 3, 11.5),
            Block.box(10.5, 2, 6.5, 12.5, 3, 11.5),
            Block.box(9.5, 2, 4.5, 12.5, 3, 6.5),
            Block.box(3.5, 2, 4.5, 6.5, 3, 6.5),
            Block.box(5.5, 2, 6.5, 10.5, 12, 9.5),
            Block.box(6, 12, 7.5, 7, 18, 8.5)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_CHARGING_W = Stream.of(
            Block.box(2.5, 0, 2.5, 13.5, 2, 13.5),
            Block.box(9.5, 2, 5.5, 11.5, 3, 10.5),
            Block.box(6.5, 2, 10.5, 11.5, 3, 12.5),
            Block.box(6.5, 2, 3.5, 11.5, 3, 5.5),
            Block.box(4.5, 2, 3.5, 6.5, 3, 6.5),
            Block.box(4.5, 2, 9.5, 6.5, 3, 12.5),
            Block.box(6.5, 2, 5.5, 9.5, 12, 10.5),
            Block.box(7.5, 12, 9, 8.5, 18, 10)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_CHARGING_S = Stream.of(
            Block.box(2.5, 0, 2.5, 13.5, 2, 13.5),
            Block.box(5.5, 2, 4.5, 10.5, 3, 6.5),
            Block.box(10.5, 2, 4.5, 12.5, 3, 9.5),
            Block.box(3.5, 2, 4.5, 5.5, 3, 9.5),
            Block.box(3.5, 2, 9.5, 6.5, 3, 11.5),
            Block.box(9.5, 2, 9.5, 12.5, 3, 11.5),
            Block.box(5.5, 2, 6.5, 10.5, 12, 9.5),
            Block.box(9, 12, 7.5, 10, 18, 8.5)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_CHARGING_E = Stream.of(
            Block.box(2.5, 0, 2.5, 13.5, 2, 13.5),
            Block.box(4.5, 2, 5.5, 6.5, 3, 10.5),
            Block.box(4.5, 2, 3.5, 9.5, 3, 5.5),
            Block.box(4.5, 2, 10.5, 9.5, 3, 12.5),
            Block.box(9.5, 2, 9.5, 11.5, 3, 12.5),
            Block.box(9.5, 2, 3.5, 11.5, 3, 6.5),
            Block.box(6.5, 2, 5.5, 9.5, 12, 10.5),
            Block.box(7.5, 12, 6, 8.5, 18, 7)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
}
