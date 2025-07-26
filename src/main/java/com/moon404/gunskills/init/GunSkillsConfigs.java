package com.moon404.gunskills.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class GunSkillsConfigs
{
    public static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CLIENT_CONFIG;
    public static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SERVER_CONFIG;

    public static final ForgeConfigSpec.ConfigValue<Integer> RECOVER_INDEX;
    public static final ForgeConfigSpec.ConfigValue<Integer> DROPA_INDEX;
    public static final ForgeConfigSpec.ConfigValue<Integer> DROPB_INDEX;
    public static final ForgeConfigSpec.ConfigValue<Integer> DROPC_INDEX;
    public static final ForgeConfigSpec.ConfigValue<Float> STEP_SOUND_MUL;
    public static final ForgeConfigSpec.ConfigValue<Float> STEP_RANGE;

    public static final ForgeConfigSpec.ConfigValue<String> Banished_Gun_Nbt_key;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> Gun_ID;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> Banished_Gun_Nbt_values;

    static
    {
        RECOVER_INDEX = CLIENT_BUILDER.comment(" 回复品轮盘对应的快捷栏位（取值0-9，默认4，0代表不启用此功能）").defineInRange("recover item hotbar slot", 4, 0, 9);
        DROPA_INDEX = CLIENT_BUILDER.comment(" 技能A对应的快捷栏位（取值1-9，默认5）").defineInRange("skill A hotbar slot", 5, 1, 9);
        DROPB_INDEX = CLIENT_BUILDER.comment(" 技能B对应的快捷栏位（取值1-9，默认6）").defineInRange("skill B hotbar slot", 6, 1, 9);
        DROPC_INDEX = CLIENT_BUILDER.comment(" 技能C对应的快捷栏位（取值1-9，默认7）").defineInRange("skill C hotbar slot", 7, 1, 9);
        STEP_SOUND_MUL = CLIENT_BUILDER.comment(" 脚步声放大倍率（默认6.0）").define("step sound multipier", 6.0f);
        CLIENT_CONFIG = CLIENT_BUILDER.build();

        Gun_ID = SERVER_BUILDER.comment("枪包中枪械的id")
                .defineListAllowEmpty("Gun_IDs",
                        () -> new ArrayList<>(List.of("tacz:modern_kinetic_gun")),
                        o -> o instanceof ResourceLocation);

        Banished_Gun_Nbt_key = SERVER_BUILDER.comment("排除的Nbt键")
                .define("Banished_Gun_Nbt_key", "GunId");

        Banished_Gun_Nbt_values = SERVER_BUILDER.comment("排除的Nbt值(有的枪包比如tacz:所有的枪共用一个ID,通过nbt来区分不同的枪)")
                .defineListAllowEmpty("Banished_Gun_Nbt_values",
                        () -> new ArrayList<>(List.of()),
                        o -> o instanceof String);


        STEP_RANGE = SERVER_BUILDER.comment(" 脚步声传播距离（默认32.0）").define("step range multipier", 32.0f);
        SERVER_CONFIG = SERVER_BUILDER.build();
    }
}
