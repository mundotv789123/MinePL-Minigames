package mundotv.playerconfig.services;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class TntConfig implements Listener {

    private final JavaPlugin plugin;

    private TNTPrimed tntEntity = null;

    public TntConfig(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    void onTNTPrimeEvent(TNTPrimeEvent event) {
        Bukkit.getConsoleSender().sendMessage("Evento TNTPrimeEvent acionado.");
        // TODO verificar se a tnt saiu de um dispenser, se sim, aplicar a lógica abaixo
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            var entities = event.getBlock().getLocation().getWorld().getNearbyEntities(event.getBlock().getLocation(),
                    1, 1, 1, (entity) -> entity instanceof TNTPrimed);
            if (entities.isEmpty()) {
                Bukkit.getConsoleSender().sendMessage("Nenhuma entidade TNT encontrada perto do bloco.");
                return;
            }
            this.tntEntity = (TNTPrimed) entities.iterator().next();
            this.tntEntity.setFuseTicks(200);

            Vector vector = this.tntEntity.getVelocity();
            Random random = new Random();

            // TODO corrigrir lógica de movimento aleatório
            float variant = 0.06f;
            var x = (random.nextInt(2) - 1) > 0 ? variant : -variant;
            var z = (random.nextInt(2) - 1) > 0 ? variant : -variant;

            Bukkit.getConsoleSender().sendMessage("x: " + x + " z: " + z);

            vector.setY(1.5);
            vector.setX(x);
            vector.setZ(z);

            this.tntEntity.setVelocity(vector);
        }, 1);
    }

    @EventHandler
    void onPlayerMoveEvent(PlayerMoveEvent e) {
        // if (tntEntity == null) {
        // return;
        // }
        // Player player = e.getPlayer();
        // tntEntity.teleport(player);
        // TODO implementar lógica quando entidade ficar perto do jogador grudar nele,
        // se o mesmo jogador ritar outro, a tnt vai para o outro jogador
        // TODO Se a tnt cair muito longe, remove do mundo
    }
}
