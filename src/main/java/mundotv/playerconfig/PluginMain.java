package mundotv.playerconfig;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import mundotv.playerconfig.services.PlayerConfig;
import mundotv.playerconfig.services.RandomBlocksConfig;
import mundotv.playerconfig.services.SniperConfig;
import mundotv.playerconfig.services.TntConfig;

public class PluginMain extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        RandomBlocksConfig randomBlocksConfig = new RandomBlocksConfig(this);
        SniperConfig sniperConfig = new SniperConfig(this);
        PlayerConfig playerConfig = new PlayerConfig(this, sniperConfig);
        TntConfig tntConfig = new TntConfig(this);

        Bukkit.getPluginManager().registerEvents(playerConfig, this);
        Bukkit.getPluginManager().registerEvents(sniperConfig, this);
        Bukkit.getPluginManager().registerEvents(tntConfig, this);

        Bukkit.getPluginCommand("sniper").setExecutor(sniperConfig);
        Bukkit.getPluginCommand("randomblocks").setExecutor(randomBlocksConfig);
        Bukkit.getPluginCommand("config").setExecutor(playerConfig);

        Bukkit.getConsoleSender().sendMessage("Plugin PlayerConfig iniciando com sucesso!!");
    }
}