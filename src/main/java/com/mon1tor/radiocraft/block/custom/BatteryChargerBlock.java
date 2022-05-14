package com.mon1tor.radiocraft.block.custom;

import com.mon1tor.radiocraft.block.properties.BatteryChargerSlots;
import com.mon1tor.radiocraft.container.custom.BatteryChargerContainer;
import com.mon1tor.radiocraft.item.ModItems;
import com.mon1tor.radiocraft.tileentity.ModTileEntities;
import com.mon1tor.radiocraft.tileentity.custom.BatteryChargerTile;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
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

public class BatteryChargerBlock extends HorizontalBlock {
    public static final EnumProperty<BatteryChargerSlots> SLOTS_CHARGING = EnumProperty.create("slots_charging", BatteryChargerSlots.class);

    public BatteryChargerBlock() {
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
    public void onRemove(BlockState pState, World pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.hasTileEntity() && (!pState.is(pNewState.getBlock()) || !pNewState.hasTileEntity())) {
            pLevel.getBlockEntity(pPos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                for (int i = 0; i < h.getSlots(); ++i) {
                    InventoryHelper.dropItemStack(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), h.getStackInSlot(i));
                }
            });

            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
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
            if(tileEntity instanceof BatteryChargerTile) {
                ItemStack heldStack = player.getMainHandItem();

                LazyOptional<IItemHandler> optional = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
                if(!optional.isPresent()) return ActionResultType.PASS;

                IItemHandler handler = optional.resolve().get();
                boolean slot0HasBattery = !handler.getStackInSlot(0).isEmpty();
                boolean slot1HasBattery = !handler.getStackInSlot(1).isEmpty();
                boolean isHoldingBattery = heldStack.getItem() == ModItems.BATTERY.get();

                if(isHoldingBattery && (!slot0HasBattery || !slot1HasBattery)) {
                    player.setItemInHand(Hand.MAIN_HAND, handler.insertItem(slot0HasBattery ? 1 : 0, heldStack, false));
                } else if(player.isCrouching() && (slot0HasBattery || slot1HasBattery)) {
                    ItemStack extracted = handler.extractItem(slot0HasBattery ? 0 : 1, 1, false);
                    if(heldStack.isEmpty()) player.setItemInHand(Hand.MAIN_HAND, extracted);
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
                return new TranslationTextComponent("screen.radiocraft.battery_charger");
            }

            @Nullable
            @Override
            public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                return new BatteryChargerContainer(i, worldIn, pos, playerInventory, playerEntity);
            }
        };
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.BATTERY_CHARGER_TILE.get().create();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, SLOTS_CHARGING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(SLOTS_CHARGING, BatteryChargerSlots.NONE);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState pState) {
        return PushReaction.DESTROY;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        BatteryChargerSlots slots = state.getValue(SLOTS_CHARGING);
        switch (state.getValue(FACING)) {
            case NORTH: { switch (slots) {
                case NONE: return SHAPE_NS_NONE;
                case LEFT: return SHAPE_N_LEFT;
                case RIGHT: return SHAPE_N_RIGHT;
                case BOTH: return SHAPE_NS_BOTH;
            } break; }
            case WEST: { switch (slots) {
                case NONE: return SHAPE_WE_NONE;
                case LEFT: return SHAPE_W_LEFT;
                case RIGHT: return SHAPE_W_RIGHT;
                case BOTH: return SHAPE_WE_BOTH;
            } break; }
            case SOUTH: { switch (slots) {
                case NONE: return SHAPE_NS_NONE;
                case LEFT: return SHAPE_N_RIGHT;
                case RIGHT: return SHAPE_N_LEFT;
                case BOTH: return SHAPE_NS_BOTH;
            } break; }
            case EAST: { switch (slots) {
                case NONE: return SHAPE_WE_NONE;
                case LEFT: return SHAPE_W_RIGHT;
                case RIGHT: return SHAPE_W_LEFT;
                case BOTH: return SHAPE_WE_BOTH;
            } break; }
        }
        return super.getShape(state, worldIn, pos, context);
    }

    private static final VoxelShape SHAPE_NS_NONE = Stream.of(
            Block.box(1.5, 0, 4.5, 14.5, 1, 11.5),
            Block.box(2.5, 1, 4.5, 13.5, 2, 5.5),
            Block.box(2.5, 1, 10.5, 13.5, 2, 11.5),
            Block.box(7.5, 1, 5.5, 8.5, 2, 10.5),
            Block.box(1.5, 1, 4.5, 2.5, 2, 11.5),
            Block.box(13.5, 1, 4.5, 14.5, 2, 11.5)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_WE_NONE = Stream.of(
            Block.box(4.5, 0, 1.5, 11.5, 1, 14.5),
            Block.box(4.5, 1, 2.5, 5.5, 2, 13.5),
            Block.box(10.5, 1, 2.5, 11.5, 2, 13.5),
            Block.box(5.5, 1, 7.5, 10.5, 2, 8.5),
            Block.box(4.5, 1, 13.5, 11.5, 2, 14.5),
            Block.box(4.5, 1, 1.5, 11.5, 2, 2.5)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_N_LEFT = Stream.of(
            Block.box(9, 0.75, 7, 13, 6.75, 9),
            Block.box(1.5, 0, 4.5, 14.5, 1, 11.5),
            Block.box(2.5, 1, 4.5, 13.5, 2, 5.5),
            Block.box(2.5, 1, 10.5, 13.5, 2, 11.5),
            Block.box(7.5, 1, 5.5, 8.5, 2, 10.5),
            Block.box(1.5, 1, 4.5, 2.5, 2, 11.5),
            Block.box(13.5, 1, 4.5, 14.5, 2, 11.5)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_W_LEFT = Stream.of(
            Block.box(7, 0.75, 3, 9, 6.75, 7),
            Block.box(4.5, 0, 1.5, 11.5, 1, 14.5),
            Block.box(4.5, 1, 2.5, 5.5, 2, 13.5),
            Block.box(10.5, 1, 2.5, 11.5, 2, 13.5),
            Block.box(5.5, 1, 7.5, 10.5, 2, 8.5),
            Block.box(4.5, 1, 13.5, 11.5, 2, 14.5),
            Block.box(4.5, 1, 1.5, 11.5, 2, 2.5)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_N_RIGHT = Stream.of(
            Block.box(3, 0.75, 7, 7, 6.75, 9),
            Block.box(1.5, 0, 4.5, 14.5, 1, 11.5),
            Block.box(2.5, 1, 4.5, 13.5, 2, 5.5),
            Block.box(2.5, 1, 10.5, 13.5, 2, 11.5),
            Block.box(7.5, 1, 5.5, 8.5, 2, 10.5),
            Block.box(1.5, 1, 4.5, 2.5, 2, 11.5),
            Block.box(13.5, 1, 4.5, 14.5, 2, 11.5)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_W_RIGHT = Stream.of(
            Block.box(7, 0.75, 9, 9, 6.75, 13),
            Block.box(4.5, 0, 1.5, 11.5, 1, 14.5),
            Block.box(4.5, 1, 2.5, 5.5, 2, 13.5),
            Block.box(10.5, 1, 2.5, 11.5, 2, 13.5),
            Block.box(5.5, 1, 7.5, 10.5, 2, 8.5),
            Block.box(4.5, 1, 13.5, 11.5, 2, 14.5),
            Block.box(4.5, 1, 1.5, 11.5, 2, 2.5)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_NS_BOTH = Stream.of(
            Block.box(3, 0.75, 7, 7, 6.75, 9),
            Block.box(9, 0.75, 7, 13, 6.75, 9),
            Block.box(1.5, 0, 4.5, 14.5, 1, 11.5),
            Block.box(2.5, 1, 4.5, 13.5, 2, 5.5),
            Block.box(2.5, 1, 10.5, 13.5, 2, 11.5),
            Block.box(7.5, 1, 5.5, 8.5, 2, 10.5),
            Block.box(1.5, 1, 4.5, 2.5, 2, 11.5),
            Block.box(13.5, 1, 4.5, 14.5, 2, 11.5)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_WE_BOTH = Stream.of(
            Block.box(7, 0.75, 9, 9, 6.75, 13),
            Block.box(7, 0.75, 3, 9, 6.75, 7),
            Block.box(4.5, 0, 1.5, 11.5, 1, 14.5),
            Block.box(4.5, 1, 2.5, 5.5, 2, 13.5),
            Block.box(10.5, 1, 2.5, 11.5, 2, 13.5),
            Block.box(5.5, 1, 7.5, 10.5, 2, 8.5),
            Block.box(4.5, 1, 13.5, 11.5, 2, 14.5),
            Block.box(4.5, 1, 1.5, 11.5, 2, 2.5)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
}
