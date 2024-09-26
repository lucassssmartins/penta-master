package br.com.pentamc.common.networking;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import br.com.pentamc.common.server.loadbalancer.server.ProxiedServer;

/**
 * 
 * Simple Packet Handler
 * 
 * Implements this class and register in your PacketController
 * 
 * @author yandv
 *
 */

public interface PacketHandler {
	
	void handlePacket(Packet packet, ProxiedServer server, ProxiedPlayer player);
	
}
