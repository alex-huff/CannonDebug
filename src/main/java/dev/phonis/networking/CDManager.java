package dev.phonis.networking;

import org.bukkit.entity.Player;
import org.originmc.cannondebug.CannonDebugPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CDManager {

    public static void sendToPlayer(Player player, CDPacket packet) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeByte(packet.packetID());
            packet.toBytes(dos);

            byte[] bytes = baos.toByteArray();

            player.sendPluginMessage(CannonDebugPlugin.instance, CannonDebugPlugin.cdChannel, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
