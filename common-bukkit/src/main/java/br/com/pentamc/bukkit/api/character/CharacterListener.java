package br.com.pentamc.bukkit.api.character;

import br.com.pentamc.bukkit.BukkitMain;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

public class CharacterListener implements Listener {

	private static final int MAX_DISTANCE = 128;

	public CharacterListener() {
		BukkitMain.getInstance().getProcotolManager()
				.addPacketListener(new PacketAdapter(BukkitMain.getInstance(), PacketType.Play.Client.USE_ENTITY) {

					@Override
					public void onPacketReceiving(PacketEvent event) {
						if (event.isCancelled())
							return;

						Player player = event.getPlayer();

						if (event.getPacket().getEntityUseActions().read(0) == EntityUseAction.INTERACT
								|| event.getPacket().getEntityUseActions().read(0) == EntityUseAction.ATTACK) {
							int entityId = event.getPacket().getIntegers().read(0);

							Character character = Character.getCharacter(entityId);

							if (character != null)
								character.getInteractHandler().onInteract(player,
										event.getPacket().getEntityUseActions().read(0) == EntityUseAction.INTERACT);
						}

					}

				});
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Character.getCharacters().forEach(character -> {
			if (character.getNpc().getLocation().getWorld().equals(event.getPlayer().getLocation().getWorld()) && character.getNpc().getLocation().distance(event.getPlayer().getLocation()) < MAX_DISTANCE)
				character.show(event.getPlayer());
		});
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Character.getCharacters().forEach(character -> {
			if (character.getNpc().isShowing(event.getPlayer().getUniqueId())) {
				if (!character.getNpc().getLocation().getWorld().equals(event.getTo().getWorld())) {
					character.hide(event.getPlayer());
				} else if (character.getNpc().getLocation().distance(event.getPlayer().getLocation()) > MAX_DISTANCE)
					character.hide(event.getPlayer());
			} else {
				if (character.getNpc().getLocation().getWorld().equals(event.getTo().getWorld()) && character.getNpc().getLocation().distance(event.getTo()) < MAX_DISTANCE) {
					character.show(event.getPlayer());
				}
			}
		});
	}

	@EventHandler
	public void onPlayerJoin(PlayerQuitEvent event) {
		Character.getCharacters().forEach(character -> character.hide(event.getPlayer()));
	}

}
