package com.odventure.core.config;

import com.odventure.core.OdventureCore;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = OdventureCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfig {

    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec.BooleanValue RENDER_FINISHED_ITEMS;
    public static final ForgeConfigSpec.BooleanValue SHOW_FLOATING_NAME;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> RENDER_BLACKLIST;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();

        b.push("render");
        RENDER_FINISHED_ITEMS = b
                .comment("\u6210\u54c1\u7269\u54c1\u662f\u5426\u52a8\u6001\u6e32\u67d3\u5c55\u793a\u5185\u5bb9(\u5173\u95ed\u5219\u4f7f\u7528\u666e\u901a\u56fe\u6807)\u3002",
                         "Whether finished items dynamically render their display content (off = plain icon).")
                .define("renderFinishedItems", true);
        SHOW_FLOATING_NAME = b
                .comment("\u51c6\u5fc3\u6307\u5411\u5956\u676f/\u5956\u7ae0\u65f6\u662f\u5426\u60ac\u6d6e\u663e\u793a\u540d\u5b57\u3002",
                         "Show a floating name when looking at a trophy/medal.")
                .define("showFloatingName", true);
        b.pop();

        b.push("entities");
        RENDER_BLACKLIST = b
                .comment("\u5b9e\u4f53\u6e32\u67d3\u9ed1\u540d\u5355(\u6ce8\u518c\u540d)\u3002\u540d\u5355\u5185\u5b9e\u4f53\u5728\u4e16\u754c\u4e0e\u7269\u54c1\u4e2d\u90fd\u4e0d\u6e32\u67d3\u3002",
                         "Entity render blacklist (registry names). Not rendered anywhere, prevents crashes.")
                .defineListAllowEmpty("renderBlacklist",
                        List.of(
                                "minecraft:area_effect_cloud",
                                "minecraft:lightning_bolt",
                                "minecraft:falling_block",
                                "minecraft:item",
                                "minecraft:player"
                        ),
                        o -> o instanceof String);
        b.pop();

        CLIENT_SPEC = b.build();
    }

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final ForgeConfigSpec.BooleanValue ENABLE_ANVIL_BLOCK;
    public static final ForgeConfigSpec.BooleanValue ENABLE_SNEAK_RETRIEVE;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();

        b.push("gameplay");
        ENABLE_ANVIL_BLOCK = b
                .comment("\u662f\u5426\u5c4f\u853d\u94c1\u7827\u5bf9\u6210\u54c1\u6539\u540d\u3002",
                         "Whether to block anvil renaming of finished items.")
                .define("enableAnvilBlock", true);
        ENABLE_SNEAK_RETRIEVE = b
                .comment("\u662f\u5426\u652f\u6301\u6f5c\u884c\u53f3\u952e\u53d6\u56de(\u5173\u95ed\u5219\u53ea\u80fd\u7834\u574f\u53d6\u56de)\u3002",
                         "Whether sneak right-click retrieves the item (off = break only).")
                .define("enableSneakRetrieve", true);
        b.pop();

        COMMON_SPEC = b.build();
    }
}
