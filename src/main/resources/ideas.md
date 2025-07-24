 **Alacrity (I-III)**
    *   **What it does:** Reduces the time required to charge the teleport.
        *   **Alacrity I:** Reduces charge time to 1.2s.
        *   **Alacrity II:** Reduces charge time by 0.9s.
        *   **Alacrity III:** Reduces charge time by 0.5s.
 **Gaze (I-II)**
    *   **What it does:** The charge will not break if your crosshair strays slightly from the target. It increases the "stickiness" of the lock-on.
        *   **Zephyr's Gaze I:** You can aim further away without breaking the charge.
        *   **Zephyr's Gaze II:** You can aim even further away.
**Gale Force (I-III)**
    *   **What it does:** You teleport 0.5 blocks above the target, and unleash a blast of wind, knocking back other nearby enemies.
        *   **Gale Force I:** Knocks back enemies within a 2-block radius.
        *   **Gale Force II:** Knocks back enemies within a 3-block radius.
        *   **Gale Force III:** Knocks back enemies within a 4-block radius.





**Max Souls:** 10
Killing living entity grants 1 soul.
Blood Debt: *If you attempt to use an ability below minimum souls, the scythe will consume your health to pay the difference at a cost of 2 health per soul.

1.  **Rupture (Right-Click): Charge up for 1.5 seconds then slam the scythe into the ground, unleashing an expanding ring of blood particles. The ring starts at you and expands to a 5-block radius + 1 radius per extra soul. Any player Hit is afflicted with the ModEffects.ANCHORED status effect for 3s + 1s per extra soul.
Mininum cost: 8 souls each extra soul will incress the potenicy of the abilty. Cooldown:10s -1s per extra soul.

COMPLEATED Rend (I-V): 10% chance per level on hit to rip a soul from a player and apply Mining Fatigue I for 2 seconds.

COMPLEATED Sanguine Covenant (I) Instead of 1 Soul for 2 Health, it's now 1 Soul for 1 Health.

Flowstate (I-II) Replaces Rupture abilty with Bargain: Instantly gain Haste 1-2 (scales off level) for 5 seconds and reduce the cooldowns of all other items in your inventory by 50% + 25% per level. Cost: 4 Souls. Cooldown: 2-4 seconds (scales off level).

use the following to get enchant levels:
int level = user.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT)
.getEntry(ModEnchantments.BARGAIN)
.map(entry -> EnchantmentHelper.getLevel(entry, stack))
.orElse(0);


make effects on scythe better ok bye good night

make windblde uhh do oonly teleport u above someone if they are on solid  ground, so air combos are more fluid (sitll not fixed btw)

DNA SAMPLES with sticks + wool dab them, clone them, stalk them, kill them! (i will never make this caz lazy)

TO DO::::::::::: CODE TETHER
Tether (I-V): Replaces Phase-Swap with a projectile. On hit, create a tether that links the target and your clone. After 5/4/3/2/1 seconds, if the target is still within 25 blocks and in line of sight of the clone, they are teleported to it. The tether breaks if the clone is destroyed, line of sight is broken, or the target moves out of range.
why the hel;l did i make it so complecated its 2am am suriving on bubble tea