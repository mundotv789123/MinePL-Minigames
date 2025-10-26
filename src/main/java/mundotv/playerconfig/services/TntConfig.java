package mundotv.playerconfig.services;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.Material;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TntConfig implements Listener {

    private final JavaPlugin plugin;

    private TNTPrimed tntEntity = null;
    private Player playerTnt = null;

    private boolean enabled = true;

    public TntConfig(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private void resetTnt() {
        this.tntEntity = null;
        this.playerTnt = null;
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        if (!enabled || event.getItem() == null || event.getItem().getType() != Material.TNT) {
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            var block = event.getBlock();
            var world = block.getLocation().getWorld();
            var entities = world.getNearbyEntities(block.getLocation(), 1.5, 1.5, 1.5,
                    (entity) -> entity instanceof TNTPrimed)
                    .stream()
                    .filter(entity -> entity != tntEntity)
                    .toList();

            if (entities.isEmpty()) {
                return;
            }
            Random random = new Random();

            if (this.tntEntity != null) {
                this.tntEntity.remove();
                resetTnt();
            }

            this.tntEntity = (TNTPrimed) entities.iterator().next();
            this.tntEntity.setFuseTicks(random.nextInt(200) + 200);

            Vector vector = this.tntEntity.getVelocity();

            float variant = 0.06f;

            var rx = random.nextInt(3);
            var zx = random.nextInt(3);

            var x = rx == 0 ? 0 : rx == 1 ? variant : -variant;
            var z = zx == 0 ? 0 : rx == 1 ? variant : -variant;

            vector.setY(1.5);
            vector.setX(x);
            vector.setZ(z);

            this.tntEntity.setVelocity(vector);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (tntEntity == null || tntEntity.isDead() || playerTnt != null) {
                        resetTnt();
                        this.cancel();
                        return;
                    }

                    if (tntEntity.getVelocity().getY() == 0) {
                        var world = tntEntity.getLocation().getWorld();
                        var entities = world.getNearbyEntities(tntEntity.getLocation(), 1, 1, 1,
                                (entity) -> entity instanceof Player);

                        if (entities.isEmpty()) {
                            tntEntity.remove();
                            resetTnt();
                        } else {
                            playerTnt = (Player) entities.iterator().next();
                        }

                        this.cancel();
                    }
                }
            }.runTaskTimer(plugin, 2L, 2L);
        }, 1);
    }

    @EventHandler
    public void onBlockExplode(EntityExplodeEvent event) {
        if (!enabled || event.getEntity().getType() != EntityType.TNT || event.getEntity() != tntEntity) {
            return;
        }
        if (playerTnt == null) {
            event.setCancelled(true);
            return;
        }
        event.blockList().removeIf(affected -> {
            return affected.getType() == Material.DISPENSER ||
                    affected.getType() == Material.STONE_BUTTON ||
                    affected.getType() == Material.OAK_BUTTON;
        });
    }

    @EventHandler
    void onPlayerMoveEvent(PlayerMoveEvent e) {
        if (!enabled || tntEntity == null) {
            return;
        }
        var player = e.getPlayer();
        if (playerTnt == player) {
            tntEntity.teleport(player);
        }
    }

    @EventHandler
    void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        if (!enabled) {
            return;
        }
        if (e.getEntity() instanceof Player player && e.getDamager() instanceof Player damager) {
            if (playerTnt == damager) {
                playerTnt = player;
            }
        }
    }
}
