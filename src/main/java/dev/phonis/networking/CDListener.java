package dev.phonis.networking;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class CDListener implements PluginMessageListener {

    public static final CDListener INSTANCE = new CDListener();

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        // nothing here yet
    }

}
