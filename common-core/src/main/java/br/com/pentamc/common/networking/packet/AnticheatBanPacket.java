package br.com.pentamc.common.networking.packet;

import com.google.gson.JsonObject;

import br.com.pentamc.common.networking.Packet;
import br.com.pentamc.common.networking.PacketType;
import br.com.pentamc.common.utils.json.JsonBuilder;

public class AnticheatBanPacket extends Packet {

	public AnticheatBanPacket(JsonObject jsonObject) {
		super(jsonObject);
	}

	public AnticheatBanPacket(String hackType, long banTime) {
		super(new JsonBuilder().addProperty("packetType", PacketType.ANTICHEAT_BAN.name())
				.addProperty("hackType", hackType).addProperty("banTime", banTime).build());
	}

	public String getHackType() {
		return getJsonObject().get("hackType").getAsString();
	}

	public long getBanTime() {
		return getJsonObject().get("banTime").getAsLong();
	}

}
