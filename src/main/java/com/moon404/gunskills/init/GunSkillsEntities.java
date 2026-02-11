package com.moon404.gunskills.init;

import com.moon404.gunskills.GunSkills;
import com.moon404.gunskills.entity.ExhibitEntity;
import com.moon404.gunskills.entity.FireEntity;
import com.moon404.gunskills.entity.HealthBottleEntity;
import com.moon404.gunskills.entity.LiftEntity;
import com.moon404.gunskills.entity.PearlEntity;
import com.moon404.gunskills.entity.SilenceEntity;
import com.moon404.gunskills.entity.SmokeEntity;
import com.moon404.gunskills.entity.SnareEntity;
import com.moon404.gunskills.entity.TotemEntity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GunSkillsEntities
{
    public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GunSkills.MODID);

    public static final RegistryObject<EntityType<LiftEntity>> LIFT =
        REGISTER.register("lift", () ->
            EntityType.Builder.of(LiftEntity::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(0).build("lift"));
    public static final RegistryObject<EntityType<HealthBottleEntity>> HEALTH_BOTTLE =
        REGISTER.register("health_bottle", () ->
            EntityType.Builder.of(HealthBottleEntity::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(0).build("health_bottle"));
    public static final RegistryObject<EntityType<PearlEntity>> PEARL =
        REGISTER.register("pearl", () ->
            EntityType.Builder.<PearlEntity>of(PearlEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("pearl"));
    public static final RegistryObject<EntityType<TotemEntity>> TOTEM =
        REGISTER.register("totem", () ->
            EntityType.Builder.of(TotemEntity::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(0).build("totem"));
    public static final RegistryObject<EntityType<ExhibitEntity>> EXHHIBIT =
        REGISTER.register("exhibit", () ->
            EntityType.Builder.of(ExhibitEntity::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(0).build("exhibit"));
    public static final RegistryObject<EntityType<SnareEntity>> SNARE =
        REGISTER.register("snare", () ->
            EntityType.Builder.<SnareEntity>of(SnareEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("snare"));
    public static final RegistryObject<EntityType<SilenceEntity>> SILENCE =
        REGISTER.register("silence", () ->
            EntityType.Builder.<SilenceEntity>of(SilenceEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("silence"));
    public static final RegistryObject<EntityType<SmokeEntity>> SMOKE =
        REGISTER.register("smoke", () ->
            EntityType.Builder.<SmokeEntity>of(SmokeEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("smoke"));
    public static final RegistryObject<EntityType<FireEntity>> FIRE =
        REGISTER.register("fire", () ->
            EntityType.Builder.<FireEntity>of(FireEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("fire"));
}
