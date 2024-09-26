package br.com.pentamc.common.networking;

import br.com.pentamc.common.networking.packet.AnticheatAlertPacket;
import br.com.pentamc.common.networking.packet.AnticheatBanPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PacketType {
	
	ANTICHEAT_BAN(0, AnticheatBanPacket.class),
	ANTICHEAT_ALERT(1, AnticheatAlertPacket.class);
	
	private int packetId;
	private Class<? extends Packet> packetClass;
	
}
