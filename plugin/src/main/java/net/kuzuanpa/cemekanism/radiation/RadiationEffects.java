package net.kuzuanpa.cemekanism.radiation;

import mekcore.chemical.DoseTracker;
import mekcore.chemical.Radiation;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/** 四档效果：视野/掉血/饥饿/致命；颜色滤镜是客户端渲染，这里用状态效果近似。 */
final class RadiationEffects {
    private RadiationEffects() {
    }

    static void apply(Player player, DoseTracker dose) {
        Radiation.Effect effect = dose.effect();
        if (effect == Radiation.Effect.NONE) {
            return;
        }
        if (effect.ordinal() >= Radiation.Effect.VISION_DIM.ordinal()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 60, 0,
                    false, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 60, 0,
                    false, false, false));
        }
        if (effect.ordinal() >= Radiation.Effect.DAMAGE.ordinal()) {
            damage(player, RadiationConfig.damage());
        }
        if (effect.ordinal() >= Radiation.Effect.HUNGER.ordinal()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 100, 2,
                    false, false, false));
            player.setExhaustion(player.getExhaustion() + RadiationConfig.hunger());
        }
        if (effect.ordinal() >= Radiation.Effect.LETHAL.ordinal()) {
            damage(player, RadiationConfig.lethalDamage());
        }
    }

    static void clear(Player player) {
        player.removePotionEffect(PotionEffectType.DARKNESS);
        player.removePotionEffect(PotionEffectType.NAUSEA);
        player.removePotionEffect(PotionEffectType.HUNGER);
    }

    private static void damage(Player player, double amount) {
        if (amount > 0.0) {
            player.damage(amount);
        }
    }
}
