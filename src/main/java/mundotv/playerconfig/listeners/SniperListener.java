package mundotv.playerconfig.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import mundotv.playerconfig.config.SniperConfig;

public class SniperListener implements Listener {
    private final SniperConfig config;

    public SniperListener(SniperConfig config) {
        this.config = config;
    }

    @EventHandler(ignoreCancelled = true)
    void onPlayerPlayerMoveEvent(PlayerMoveEvent e) {
        var player = e.getPlayer();
        if (!config.isEnabled() || !player.getGameMode().equals(GameMode.SURVIVAL)) {
            return;
        }

        var playerLocation = player.getLocation().add(0, -1, 0);
        if (playerLocation.getBlock().getType() != config.getPortalBlock()) {
            return;
        }

        if (config.getSniper() != null) {
            if (config.getSniper() == player) {
                config.getSniper().teleport(config.getSniperSpawn());
                return;
            }

            config.getSniper().getInventory().clear();
            config.getSniper().getInventory().setArmorContents(null);
            config.getSniper().teleport(config.getSpawn());
            config.getSniper().sendTitle("", "§cVocê não agora é mais o sniper", 10, 20, 10);
        }

        config.setSniper(player);

        config.getSniper().getInventory().clear();
        config.getSniper().getInventory().setArmorContents(null);
        config.getSniper().getInventory().addItem(config.getSniperItems().toArray(new ItemStack[0]));
        config.getSniper().teleport(config.getSniperSpawn());
        player.sendTitle("", "§aVocê agora é o sniper", 10, 20, 10);

        for (var p : Bukkit.getOnlinePlayers()) {
            if (!p.getGameMode().equals(GameMode.SURVIVAL) || p == config.getSniper()) {
                continue;
            }
            p.teleport(config.getSpawn());
            p.sendTitle("", "§eO jogador " + config.getSniper().getName() + " é o novo sniper", 10, 20, 10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    void onPlayerRespawnEvent(PlayerRespawnEvent e) {
        if (!config.isEnabled()) {
            return;
        }
        if (config.getSniper() == e.getPlayer()) {
            e.setRespawnLocation(config.getSniperSpawn());
            return;
        }
        e.setRespawnLocation(config.getSpawn());
    }

    @EventHandler(ignoreCancelled = true)
    void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        if (!config.isEnabled()) {
            return;
        }
        if (!(e.getEntity() instanceof Player player)) {
            return;
        }
        if (player == config.getSniper()) {
            e.setCancelled(true);
            return;
        }
    }
}
