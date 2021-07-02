package moe.neat.multiplayerhc.event;

import moe.neat.multiplayerhc.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageEventListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && "true".equals(ConfigManager.read("resetWorld"))) {
            event.setCancelled(true);
        }
    }
}
