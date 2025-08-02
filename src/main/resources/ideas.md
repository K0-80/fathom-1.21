



DNA SAMPLES with sticks + wool dab them, clone them, stalk them, kill them! (i will never make this caz lazy)



todo:
DONE make codex texture
make wind ritual
DONE DNA samples
clone device
clone entity
wirlpool enchant particle effect

 
- mirage needs rework (made cooldowns  longer, hopefully it helps)
- involnble during dash  weapon
- steal heart containers weapon
- trap enemay and you in a 1v1 box weapon creaking themed
- eye of the creaking: when looking at user, nothing happens, when turning away to run, gain speed and extra damage

- repair anvils with iron ingot

- dying with shattered totem will fully break totem, allowing you to keep 50% of your items

- mirage sitll dosnt show skin
- update  mod to 1.21.5

add gambeling :)))
- false totem: 50% chance to deleate items 50% chance to keep inventory


creaking eye: + movement speed when not being looked at -movement speed when looked at
abilty: domain expantion to trap people (unbreakble at night) user can walk through
custom particles syetem 



https://modrinth.com/mod/aileron - elytra rework mod, nerfs elytra
https://modrinth.com/mod/postmortal-particles/gallery - better totem particles mod
https://modrinth.com/mod/celestisynth - cool looking weapons mod


    private static final Identifier SPEED_MODIFIER_ID = Identifier.of(Fathom.MOD_ID, "creaking_staff_speed_bonus");
    private static final Identifier SLOWNESS_MODIFIER_ID = Identifier.of(Fathom.MOD_ID, "creaking_staff_slowness_penalty");

    private static final EntityAttributeModifier SPEED_MODIFIER = new EntityAttributeModifier(
            SPEED_MODIFIER_ID,  0.20, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    private static final EntityAttributeModifier SLOWNESS_MODIFIER = new EntityAttributeModifier(
            SLOWNESS_MODIFIER_ID, -0.15, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);