package br.com.pentamc.competitive.abilities.register;

import java.util.ArrayList;

import br.com.pentamc.competitive.abilities.Ability;
import org.bukkit.event.EventHandler;

import br.com.pentamc.competitive.event.ability.ChallengeGladiatorEvent;
import br.com.pentamc.competitive.event.ability.ChallengeUltimatoEvent;
import br.com.pentamc.competitive.event.ability.PlayerAjninTeleportEvent;
import br.com.pentamc.competitive.event.ability.PlayerEndermageEvent;
import br.com.pentamc.competitive.event.ability.PlayerNinjaTeleportEvent;

public class NeoAbility extends Ability {

	public NeoAbility() {
		super("neo", new ArrayList<>());
	}

	@EventHandler
	public void onChallengeGladiator(ChallengeGladiatorEvent event) {
		if (hasAbility(event.getTarget()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onChallengeUltimato(ChallengeUltimatoEvent event) {
		if (hasAbility(event.getTarget()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onChallengeUltimato(PlayerEndermageEvent event) {
		if (hasAbility(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerNinjaTeleport(PlayerNinjaTeleportEvent event) {
		if (hasAbility(event.getTarget()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerAjninTeleport(PlayerAjninTeleportEvent event) {
		if (hasAbility(event.getTarget()))
			event.setCancelled(true);
	}

}
