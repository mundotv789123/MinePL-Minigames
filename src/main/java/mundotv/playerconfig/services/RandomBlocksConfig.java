package mundotv.playerconfig.services;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class RandomBlocksConfig implements CommandExecutor {
    private final JavaPlugin plugin;
    private Integer schedulerId;

    public RandomBlocksConfig(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (schedulerId != null) {
            Bukkit.getScheduler().cancelTask(schedulerId);
            schedulerId = null;
            sender.sendMessage("Blocos aleatórios desativado");
            return true;
        }

        schedulerId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            var random = new Random();
            for (var player : Bukkit.getOnlinePlayers()) {
                if (!player.getGameMode().equals(GameMode.SURVIVAL)) {
                    continue;
                }
                if (player.getInventory().firstEmpty() == -1) {
                    continue;
                }
                var materials = Material.values();
                var item = new ItemStack(materials[random.nextInt(materials.length)]);
                player.getInventory().addItem(item);
            }
        }, 0, 20 * plugin.getConfig().getInt("randomblocks.timer", 5));
        sender.sendMessage("Blocos aleatórios ativado");
        return true;
    }
}
