package mundotv.playerconfig.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import mundotv.playerconfig.config.PlayerConfig;
import mundotv.playerconfig.config.SniperConfig;

public class PlayerListener implements Listener {
    private final JavaPlugin plugin;
    private final PlayerConfig playerConfig;
    private final SniperConfig sniperConfig;

    public PlayerListener(JavaPlugin plugin, PlayerConfig playerConfig, SniperConfig sniperConfig) {
        this.plugin = plugin;
        this.playerConfig = playerConfig;
        this.sniperConfig = sniperConfig;
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent e) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            var player = e.getPlayer();
            if (plugin.getConfig().getBoolean("espectador_quando_morrer", true) && !sniperConfig.isEnabled()) {
                player.setGameMode(GameMode.SPECTATOR);
            } else {
                playerConfig.loadPlayerConfig(e.getPlayer());
            }
        }, 1);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            playerConfig.loadPlayerConfig(e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent e) {
        if (e.getNewGameMode() == GameMode.SURVIVAL) {
            playerConfig.loadPlayerConfig(e.getPlayer());
        }
    }
}
