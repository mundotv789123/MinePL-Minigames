package mundotv.playerconfig.config;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class SniperConfig implements CommandExecutor {
    private final JavaPlugin plugin;

    private boolean enabled = false;
    private Player sniper = null;

    private final Material portalBlock;
    private final List<ItemStack> sniperItems;
    private Location spawn;
    private Location sniper_spawn;

    @SuppressWarnings("unchecked")
    public SniperConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        portalBlock = Material
                .valueOf(plugin.getConfig().getString("sniper.portal.block", Material.WHITE_TERRACOTTA.name()));
        sniperItems = (List<ItemStack>) plugin.getConfig().getList("sniper.items", List.of());
        spawn = plugin.getConfig().getLocation("sniper.spawn");
        sniper_spawn = plugin.getConfig().getLocation("sniper.sniper_spawn");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(command.getUsage());
            return true;
        }

        if (args[0].equalsIgnoreCase("toggle")) {
            if (enabled) {
                enabled = false;
                sniper = null;
                sender.sendMessage("Modo sniper desativado");
            } else {
                enabled = true;
                sender.sendMessage("Modo sniper ativado");
            }
            return true;
        }

        if (sender instanceof Player player) {
            Location location = player.getLocation().clone();

            if (args[0].equalsIgnoreCase("set_spawn")) {
                plugin.getConfig().set("sniper.spawn", location);
                plugin.saveConfig();
                spawn = location;
                sender.sendMessage("Spawn setado");
                return true;
            }

            if (args[0].equalsIgnoreCase("set_sniper_spawn")) {
                plugin.getConfig().set("sniper.sniper_spawn", location);
                plugin.saveConfig();
                sniper_spawn = location;
                sender.sendMessage("Spawn sniper setado");
                return true;
            }

            var itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand != null) {
                if (args[0].equalsIgnoreCase("add_sniper_item")) {
                    sniperItems.add(itemInHand.clone());
                    plugin.getConfig().set("sniper.items", sniperItems);
                    plugin.saveConfig();
                    sender.sendMessage("Item adicionado");
                    return true;
                }

                if (args[0].equalsIgnoreCase("remove_sniper_item")) {
                    sniperItems.removeIf(item -> item.isSimilar(itemInHand));
                    plugin.getConfig().set("sniper.items", sniperItems);
                    plugin.saveConfig();
                    sender.sendMessage("Item removido");
                    return true;
                }
            } else {
                sender.sendMessage("Segure um item na mão para adicionar ou remover");
            }
        }

        return true;

    }

    public boolean isEnabled() {
        return enabled;
    }

    public Player getSniper() {
        return sniper;
    }

    public void setSniper(Player sniper) {
        this.sniper = sniper;
    }

    public Material getPortalBlock() {
        return portalBlock;
    }

    public List<ItemStack> getSniperItems() {
        return sniperItems;
    }

    public Location getSpawn() {
        return spawn;
    }

    public Location getSniperSpawn() {
        return sniper_spawn;
    }
}
