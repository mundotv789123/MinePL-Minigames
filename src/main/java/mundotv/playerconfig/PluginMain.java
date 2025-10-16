package mundotv.playerconfig;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import mundotv.playerconfig.services.PlayerConfig;
import mundotv.playerconfig.services.RandomBlocksConfig;
import mundotv.playerconfig.services.SniperConfig;

public class PluginMain extends JavaPlugin {
    @Override
    public void onEnable() {
        var playerConfig = new PlayerConfig(this);
        var randomBlocksConfig = new RandomBlocksConfig(this);
        var sniperConfig = new SniperConfig(this);

        Bukkit.getPluginManager().registerEvents(playerConfig, this);
        Bukkit.getPluginManager().registerEvents(sniperConfig, this);

        Bukkit.getPluginCommand("sniper").setExecutor(sniperConfig);
        Bukkit.getPluginCommand("randomblocks").setExecutor(randomBlocksConfig);
        this.saveDefaultConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.reloadConfig();
        sender.sendMessage("Configurações recarregadas");
        for (var player : Bukkit.getOnlinePlayers()) {
            if (this.getConfig().getBoolean("saturacao", true)) {
                if (player.getPotionEffect(PotionEffectType.SATURATION) == null) {
                    var effect = new PotionEffect(PotionEffectType.SATURATION, -1, 10, false, false, false);
                    player.addPotionEffect(effect);
                }
            } else {
                if (player.getPotionEffect(PotionEffectType.SATURATION) != null) {
                    player.removePotionEffect(PotionEffectType.SATURATION);
                }
            }
        }
        return true;
    }
}