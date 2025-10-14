package mundotv.playerconfig;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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
}