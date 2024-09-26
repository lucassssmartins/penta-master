package br.com.pentamc.bukkit.listener.register;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.listener.Listener;
import br.com.pentamc.common.account.Member;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import br.com.pentamc.bukkit.api.player.PlayerAPI;
import br.com.pentamc.bukkit.api.player.TextureFetcher;

public class FakeListener extends Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		if (member.hasSkin())
			new BukkitRunnable() {

				@Override
				public void run() {
					if (player.isOnline()) {
						if (member.isUsingFake())
							new BukkitRunnable() {

								@Override
								public void run() {
									PlayerAPI.changePlayerName(player, member.getFakeName());
								}
							}.runTask(BukkitMain.getInstance());

						if (member.hasSkin()) {
							WrappedSignedProperty property = TextureFetcher.loadTexture(new WrappedGameProfile(
									member.getSkinProfile().getUniqueId(), member.getSkinProfile().getPlayerName()));
							if (property != null)
								new BukkitRunnable() {

									@Override
									public void run() {
										PlayerAPI.changePlayerSkin(player, property, true);
									}
								}.runTask(BukkitMain.getInstance());
						}
					}
				}
			}.runTaskAsynchronously(BukkitMain.getInstance());
	}

}
