package mundotv.playerconfig.services;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerConfig implements Listener, CommandExecutor {
    private final JavaPlugin plugin;
    private final SniperConfig sniperConfig;

    public PlayerConfig(JavaPlugin plugin, SniperConfig sniperConfig) {
        this.plugin = plugin;
        this.sniperConfig = sniperConfig;
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent e) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            var player = e.getPlayer();
            if (plugin.getConfig().getBoolean("espectador_quando_morrer", true) && !sniperConfig.isEnabled()) {
                player.setGameMode(GameMode.SPECTATOR);
            } else {
                loadPlayerConfig(e.getPlayer());
            }
        }, 1);
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(command.getUsage());
            return true;
        }

        if (args[0].equalsIgnoreCase("set_max_health")) {
            try {
                var max_health = Integer.parseInt(args[1]);
                plugin.getConfig().set("maximo_vida", max_health);
                plugin.saveConfig();
                sender.sendMessage("Configuração 'maximo_vida' definida para " + max_health);
                Bukkit.getOnlinePlayers().forEach(player -> {
                    loadPlayerConfig(player);
                });
            } catch (NumberFormatException e) {
                sender.sendMessage("Valor inválido para 'maximo_vida'");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("set_saturation")) {
            var saturation = Boolean.parseBoolean(args[1]);
            plugin.getConfig().set("saturacao", saturation);
            plugin.saveConfig();
            sender.sendMessage("Configuração 'saturacao' definida para " + saturation);
            Bukkit.getOnlinePlayers().forEach(player -> {
                loadPlayerConfig(player);
            });
            return true;
        }

        if (args[0].equalsIgnoreCase("set_spectator_on_death")) {
            var spectatorOnDeath = Boolean.parseBoolean(args[1]);
            plugin.getConfig().set("espectador_quando_morrer", spectatorOnDeath);
            plugin.saveConfig();
            sender.sendMessage("Configuração 'espectador_quando_morrer' definida para " + spectatorOnDeath);
            return true;
        }

        return true;
    }

    public void loadPlayerConfig(Player player) {
        var max_health = plugin.getConfig().getInt("maximo_vida", 6);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(max_health);
        if (player.getHealth() > max_health) {
            player.setHealth(max_health);
        }

        if (plugin.getConfig().getBoolean("saturacao", true)) {
            var potionEffect = player.getPotionEffect(PotionEffectType.SATURATION);
            if (potionEffect == null) {
                var effect = new PotionEffect(PotionEffectType.SATURATION, -1, 10, false, false, false);
                player.addPotionEffect(effect);
            }
        } else if (player.getPotionEffect(PotionEffectType.SATURATION) == null) {
            player.removePotionEffect(PotionEffectType.SATURATION);
        }
    }
}
