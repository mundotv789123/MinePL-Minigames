package mundotv.playerconfig;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import mundotv.playerconfig.services.PlayerConfig;
import mundotv.playerconfig.services.RandomBlocksConfig;
import mundotv.playerconfig.services.SniperConfig;

public class PluginMain extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        RandomBlocksConfig randomBlocksConfig = new RandomBlocksConfig(this);
        SniperConfig sniperConfig = new SniperConfig(this);
        PlayerConfig playerConfig = new PlayerConfig(this, sniperConfig);

        Bukkit.getPluginManager().registerEvents(playerConfig, this);
        Bukkit.getPluginManager().registerEvents(sniperConfig, this);

        Bukkit.getPluginCommand("sniper").setExecutor(sniperConfig);
        Bukkit.getPluginCommand("randomblocks").setExecutor(randomBlocksConfig);
        Bukkit.getPluginCommand("config").setExecutor(playerConfig);

        Bukkit.getConsoleSender().sendMessage("Plugin PlayerConfig iniciando com sucesso!!");
    }
}