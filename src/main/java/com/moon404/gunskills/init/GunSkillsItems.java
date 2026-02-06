package com.moon404.gunskills.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.moon404.gunskills.GunSkills;
import com.moon404.gunskills.item.recover.MedKit;
import com.moon404.gunskills.item.recover.PhoenixKit;
import com.moon404.gunskills.item.recover.RecoverItem;
import com.moon404.gunskills.item.recover.Reviver;
import com.moon404.gunskills.item.recover.ShieldBattery;
import com.moon404.gunskills.item.recover.ShieldBoost;
import com.moon404.gunskills.item.recover.ShieldCell;
import com.moon404.gunskills.item.recover.Syringe;
import com.moon404.gunskills.item.skill.Boot;
import com.moon404.gunskills.item.skill.Charge;
import com.moon404.gunskills.item.skill.EnterVoid;
import com.moon404.gunskills.item.skill.Exhibit;
import com.moon404.gunskills.item.skill.Fast;
import com.moon404.gunskills.item.skill.Glow;
import com.moon404.gunskills.item.skill.HealthBottle;
import com.moon404.gunskills.item.skill.Ire;
import com.moon404.gunskills.item.skill.Lift;
import com.moon404.gunskills.item.skill.Pearl;
import com.moon404.gunskills.item.skill.Purify;
import com.moon404.gunskills.item.skill.Scan;
import com.moon404.gunskills.item.skill.ShieldBottle;
import com.moon404.gunskills.item.skill.Silence;
import com.moon404.gunskills.item.skill.SkillBag;
import com.moon404.gunskills.item.skill.Smoke;
import com.moon404.gunskills.item.skill.Snare;
import com.moon404.gunskills.item.skill.Stim;
import com.moon404.gunskills.item.skill.Totem;

public class GunSkillsItems
{
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, GunSkills.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GunSkills.MODID);

    public static final RegistryObject<Item> VERSION = REGISTER.register("0.5.8", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SHIELD_CELL = REGISTER.register("shield_cell", () -> new ShieldCell(new Item.Properties().food(RecoverItem.RECOVER_ITEM)));
    public static final RegistryObject<Item> SHIELD_BATTERY = REGISTER.register("shield_battery", () -> new ShieldBattery(new Item.Properties().food(RecoverItem.RECOVER_ITEM)));
    public static final RegistryObject<Item> SYRINGE = REGISTER.register("syringe", () -> new Syringe(new Item.Properties().food(RecoverItem.RECOVER_ITEM)));
    public static final RegistryObject<Item> MED_KIT = REGISTER.register("med_kit", () -> new MedKit(new Item.Properties().food(RecoverItem.RECOVER_ITEM)));
    public static final RegistryObject<Item> PHOENIX_KIT = REGISTER.register("phoenix_kit", () -> new PhoenixKit(new Item.Properties().food(RecoverItem.RECOVER_ITEM)));
    public static final RegistryObject<Item> SHIELD_BOOST = REGISTER.register("shield_boost", () -> new ShieldBoost(new Item.Properties().food(RecoverItem.RECOVER_ITEM)));
    public static final RegistryObject<Item> REVIVER = REGISTER.register("reviver", () -> new Reviver(new Item.Properties().food(RecoverItem.RECOVER_ITEM)));

    public static final RegistryObject<Item> STIM = REGISTER.register("stim", () -> new Stim(new Item.Properties()));
    public static final RegistryObject<Item> LIFT = REGISTER.register("lift", () -> new Lift(new Item.Properties()));
    public static final RegistryObject<Item> GLOW = REGISTER.register("glow", () -> new Glow(new Item.Properties()));
    public static final RegistryObject<Item> VOID = REGISTER.register("void", () -> new EnterVoid(new Item.Properties()));
    public static final RegistryObject<Item> HEALTH_BOTTLE = REGISTER.register("health_bottle", () -> new HealthBottle(new Item.Properties()));
    public static final RegistryObject<Item> SHIELD_BOTTLE = REGISTER.register("shield_bottle", () -> new ShieldBottle(new Item.Properties()));
    public static final RegistryObject<Item> PEARL = REGISTER.register("pearl", () -> new Pearl(new Item.Properties()));
    public static final RegistryObject<Item> TOTEM = REGISTER.register("totem", () -> new Totem(new Item.Properties()));
    public static final RegistryObject<Item> FAST = REGISTER.register("fast", () -> new Fast(new Item.Properties()));
    public static final RegistryObject<Item> IRE = REGISTER.register("ire", () -> new Ire(new Item.Properties()));
    public static final RegistryObject<Item> SCAN = REGISTER.register("scan", () -> new Scan(new Item.Properties()));
    public static final RegistryObject<Item> PURIFY = REGISTER.register("purify", () -> new Purify(new Item.Properties()));
    public static final RegistryObject<Item> CHARGE = REGISTER.register("charge", () -> new Charge(new Item.Properties()));
    public static final RegistryObject<Item> EXHIBIT = REGISTER.register("exhibit", () -> new Exhibit(new Item.Properties()));
    public static final RegistryObject<Item> SNARE = REGISTER.register("snare", () -> new Snare(new Item.Properties()));
    public static final RegistryObject<Item> SILENCE = REGISTER.register("silence", () -> new Silence(new Item.Properties()));
    public static final RegistryObject<Item> BOOT = REGISTER.register("boot", () -> new Boot(new Item.Properties()));
    public static final RegistryObject<Item> SMOKE = REGISTER.register("smoke", () -> new Smoke(new Item.Properties()));
    // skill_bag 必须最后一个注册
    public static final RegistryObject<Item> SKILL_BAG = REGISTER.register("skill_bag", () -> new SkillBag(new Item.Properties()));

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
        .icon(() -> SHIELD_BATTERY.get().getDefaultInstance())
        .displayItems((parameters, output) ->
        {
            output.accept(SHIELD_CELL.get());
            output.accept(SHIELD_BATTERY.get());
            output.accept(SYRINGE.get());
            output.accept(MED_KIT.get());
            output.accept(PHOENIX_KIT.get());
            output.accept(SHIELD_BOOST.get());
            output.accept(REVIVER.get());
            output.accept(STIM.get());
            output.accept(LIFT.get());
            output.accept(GLOW.get());
            output.accept(VOID.get());
            output.accept(HEALTH_BOTTLE.get());
            output.accept(SHIELD_BOTTLE.get());
            output.accept(PEARL.get());
            output.accept(TOTEM.get());
            output.accept(FAST.get());
            output.accept(IRE.get());
            output.accept(SCAN.get());
            output.accept(PURIFY.get());
            output.accept(CHARGE.get());
            output.accept(EXHIBIT.get());
            output.accept(SNARE.get());
            output.accept(SILENCE.get());
            output.accept(BOOT.get());
            output.accept(SMOKE.get());
            output.accept(SKILL_BAG.get());
        }).build());
}
