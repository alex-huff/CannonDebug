package org.originmc.cannondebug.cmd;

import dev.phonis.networking.CDAdapter;
import dev.phonis.networking.CDManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.originmc.cannondebug.CannonDebugPlugin;

import java.io.IOException;

public class CmdExcel extends CommandExecutor {

    public CmdExcel(CannonDebugPlugin plugin, CommandSender sender, String[] args, String permission) {
        super(plugin, sender, args, permission);
    }

    @Override
    public boolean perform() {
        if (this.sender instanceof Player) {
            Player player = (Player) this.sender;

            try {
                CDAdapter.payloadFromBlockSelections(this.user.getSelections()).forEach(packet -> CDManager.sendToPlayer(player, packet));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return true;
    }

}
