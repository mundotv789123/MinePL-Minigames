package mundotv.playerconfig;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PluginMain extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent e) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            var player = e.getPlayer();
            if ((boolean) this.getConfig().get("espectador_quando_morrer", true)) {
                player.setGameMode(GameMode.SPECTATOR);
            } else {
                loadPlayerConfig(e.getPlayer());
            }
        }, 5);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            loadPlayerConfig(e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent e) {
        if (e.getNewGameMode() == GameMode.SURVIVAL) {
            loadPlayerConfig(e.getPlayer());
        }
    }

    public void loadPlayerConfig(Player player) {
        var max_health = (int) this.getConfig().get("maximo_vida", 6);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(max_health);
        if (player.getHealth() > max_health) {
            player.setHealth(max_health);
        }

        if ((boolean) this.getConfig().get("saturacao", true)) {
            var potionEffect = player.getPotionEffect(PotionEffectType.SATURATION);
            if (potionEffect == null) {
                var effect = new PotionEffect(PotionEffectType.SATURATION, -1, 10, false, false, false);
                player.addPotionEffect(effect);
            }
        }
    }
}