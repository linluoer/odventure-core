package com.odventure.core.block;

import com.odventure.core.block.entity.MedalBlockEntity;
import com.odventure.core.client.ClientHelper;
import com.odventure.core.config.ModConfig;
import com.odventure.core.data.MedalData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class MedalBlock extends HorizontalDirectionalBlock implements EntityBlock {

    public MedalBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction clicked = ctx.getClickedFace();
        if (!clicked.getAxis().isHorizontal()) {
            clicked = ctx.getHorizontalDirection().getOpposite();
        }
        BlockState state = defaultBlockState().setValue(FACING, clicked);
        return state.canSurvive(ctx.getLevel(), ctx.getClickedPos()) ? state : null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos supportPos = pos.relative(facing.getOpposite());
        BlockState support = level.getBlockState(supportPos);
        if (support.is(Blocks.GLOWSTONE)) return true;
        if (support.getBlock() instanceof HalfTransparentBlock) return true;
        return support.isFaceSturdy(level, supportPos, facing);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighbor,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.getValue(FACING);
        if (dir == facing.getOpposite() && !canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, dir, neighbor, level, pos, neighborPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return switch (state.getValue(FACING)) {
            case NORTH -> Block.box(1, 1, 14.5, 15, 15, 16);
            case SOUTH -> Block.box(1, 1, 0, 15, 15, 1.5);
            case WEST  -> Block.box(14.5, 1, 1, 16, 15, 15);
            case EAST  -> Block.box(0, 1, 1, 1.5, 15, 15);
            default    -> Block.box(1, 1, 0, 15, 15, 1.5);
        };
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MedalBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof MedalBlockEntity be)) {
            return InteractionResult.PASS;
        }
        MedalData data = be.getData();
        ItemStack held = player.getItemInHand(hand);

        if (player.isShiftKeyDown() && ModConfig.ENABLE_SNEAK_RETRIEVE.get()) {
            if (!level.isClientSide) {
                ItemStack drop = makeRetrieveStack(data);
                level.removeBlock(pos, false);
                if (!player.addItem(drop)) {
                    player.drop(drop, false);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!data.isContentLocked() && !held.isEmpty()) {
            if (!level.isClientSide) {
                ItemStack one = held.copy();
                one.setCount(1);
                data.setEmbeddedItem(one);
                data.setContentLocked(true);
                be.setData(data);
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (data.isContentLocked() && !data.isNameLocked() && held.isEmpty()) {
            if (level.isClientSide) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                        () -> () -> ClientHelper.openNameScreen(pos, data.getCustomName()));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity be = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof MedalBlockEntity medal) {
            return Collections.singletonList(makeRetrieveStack(medal.getData()));
        }
        return Collections.singletonList(new ItemStack(this));
    }

    private ItemStack makeRetrieveStack(MedalData data) {
        ItemStack stack = new ItemStack(this);
        if (!data.isEmpty()) {
            CompoundTag tag = new CompoundTag();
            data.save(tag);
            stack.addTagElement("BlockEntityTag", tag);
        }
        return stack;
    }
}
