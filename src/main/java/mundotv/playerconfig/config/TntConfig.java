package mundotv.playerconfig.config;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TntConfig implements CommandExecutor {

    private boolean enabled = true;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        enabled = !enabled;
        if (enabled) {
            sender.sendMessage("TNT ativado");
        } else {
            sender.sendMessage("TNT desativado");
        }
        return true;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
