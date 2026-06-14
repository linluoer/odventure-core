package com.odventure.core.block;

import com.odventure.core.block.entity.TrophyBaseBlockEntity;
import com.odventure.core.client.ClientHelper;
import com.odventure.core.config.ModConfig;
import com.odventure.core.data.TrophyData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class TrophyBaseBlock extends HorizontalDirectionalBlock implements EntityBlock {

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 4, 16);

    public TrophyBaseBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        if (level.getBlockEntity(pos) instanceof TrophyBaseBlockEntity be
                && be.getData().getContentType() != TrophyData.ContentType.NONE) {
            return Shapes.block();
        }
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TrophyBaseBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof TrophyBaseBlockEntity be)) {
            return InteractionResult.PASS;
        }
        TrophyData data = be.getData();
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
                Item item = held.getItem();
                if (item instanceof SpawnEggItem egg) {
                    EntityType<?> type = egg.getType(held.getTag());
                    data.setContent(TrophyData.ContentType.ENTITY, EntityType.getKey(type));
                } else if (item instanceof BlockItem blockItem) {
                    data.setContent(TrophyData.ContentType.BLOCK,
                            BuiltInRegistries.BLOCK.getKey(blockItem.getBlock()));
                } else {
                    data.setContent(TrophyData.ContentType.ITEM,
                            BuiltInRegistries.ITEM.getKey(item));
                }
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

        if (data.isContentLocked() && data.isNameLocked() && held.isEmpty() && player.isCreative()) {
            if (level.isClientSide) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                        () -> () -> ClientHelper.openAdjustScreen(pos,
                                data.getDisplayScale(), data.getDisplayYaw(), data.getDisplayY()));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity be = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof TrophyBaseBlockEntity trophy) {
            return Collections.singletonList(makeRetrieveStack(trophy.getData()));
        }
        return Collections.singletonList(new ItemStack(this));
    }

    private ItemStack makeRetrieveStack(TrophyData data) {
        ItemStack stack = new ItemStack(this);
        if (!data.isEmpty()) {
            CompoundTag tag = new CompoundTag();
            data.save(tag);
            stack.addTagElement("BlockEntityTag", tag);
        }
        return stack;
    }
}
