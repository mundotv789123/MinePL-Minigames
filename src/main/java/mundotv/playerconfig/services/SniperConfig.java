package mundotv.playerconfig.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class SniperConfig implements Listener, CommandExecutor {
    private final JavaPlugin plugin;

    private boolean enabled = false;
    private Player sniper = null;

    private final List<Map<String, Integer>> portal;
    private final List<ItemStack> sniperItems;
    private final Location spawn;
    private final Location sniper_spawn;

    public SniperConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        portal = (List<Map<String, Integer>>) plugin.getConfig().getList("sniper.portal", List.of());
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
            Location location = player.getLocation();

            if (args[0].equalsIgnoreCase("set_spawn")) {
                plugin.getConfig().set("sniper.spawn", location);
                plugin.saveConfig();
                sender.sendMessage("Spawn setado");
                return true;
            }

            if (args[0].equalsIgnoreCase("set_sniper_spawn")) {
                plugin.getConfig().set("sniper.sniper_spawn", location);
                plugin.saveConfig();
                sender.sendMessage("Spawn sniper setado");
                return true;
            }

            var itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.getType() != null && !itemInHand.getType().isAir()) {
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

    @EventHandler(ignoreCancelled = true)
    void onPlayerPlayerMoveEvent(PlayerMoveEvent e) {
        var player = e.getPlayer();
        if (!enabled || !player.getGameMode().equals(GameMode.SURVIVAL)) {
            return;
        }

        var playerLocation = player.getLocation();
        Optional<Map<String, Integer>> portalFilter = portal.stream()
                .filter(location -> location.get("x") == playerLocation.getBlockX() &&
                        location.get("y") == playerLocation.getBlockY() &&
                        location.get("z") == playerLocation.getBlockZ())
                .findFirst();

        if (portalFilter.isEmpty()) {
            return;
        }

        if (sniper != null) {
            if (sniper == player) {
                return;
            }

            sniper.getInventory().clear();
            sniper.getInventory().setArmorContents(null);
            sniper.teleport(spawn);
            sniper.sendTitle("Você não agora é mais o sniper", "", 10, 20, 10);
        }

        sniper = player;

        sniper.getInventory().clear();
        sniper.getInventory().setArmorContents(null);
        sniper.getInventory().addItem(sniperItems.toArray(new ItemStack[0]));
        sniper.teleport(sniper_spawn);
        player.sendTitle("Você agora é o sniper", "", 10, 20, 10);
    }

    @EventHandler(ignoreCancelled = true)
    void onPlayerRespawnEvent(PlayerRespawnEvent e) {
        if (!enabled) {
            return;
        }
        var player = e.getPlayer();
        player.teleport(spawn);
    }
}
