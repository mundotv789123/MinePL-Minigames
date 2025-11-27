package mundotv.playerconfig;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import mundotv.playerconfig.config.PlayerConfig;
import mundotv.playerconfig.config.RandomBlocksConfig;
import mundotv.playerconfig.config.SniperConfig;
import mundotv.playerconfig.config.TntConfig;
import mundotv.playerconfig.listeners.PlayerListener;
import mundotv.playerconfig.listeners.SniperListener;
import mundotv.playerconfig.listeners.TntListener;

public class PluginMain extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        RandomBlocksConfig randomBlocksConfig = new RandomBlocksConfig(this);
        SniperConfig sniperConfig = new SniperConfig(this);
        PlayerConfig playerConfig = new PlayerConfig(this);
        TntConfig tntConfig = new TntConfig();

        PlayerListener playerListener = new PlayerListener(this, playerConfig, sniperConfig);
        SniperListener sniperListener = new SniperListener(sniperConfig);
        TntListener tntListener = new TntListener(this, tntConfig);

        Bukkit.getPluginManager().registerEvents(playerListener, this);
        Bukkit.getPluginManager().registerEvents(sniperListener, this);
        Bukkit.getPluginManager().registerEvents(tntListener, this);

        Bukkit.getPluginCommand("sniper").setExecutor(sniperConfig);
        Bukkit.getPluginCommand("randomblocks").setExecutor(randomBlocksConfig);
        Bukkit.getPluginCommand("config").setExecutor(playerConfig);
        Bukkit.getPluginCommand("tnt").setExecutor(tntConfig);

        Bukkit.getConsoleSender().sendMessage("§aPlugin PlayerConfig iniciando com sucesso!");
    }
}