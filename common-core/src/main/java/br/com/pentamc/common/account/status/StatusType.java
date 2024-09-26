package br.com.pentamc.common.account.status;

import br.com.pentamc.common.account.status.types.challenge.ChallengeStatus;
import br.com.pentamc.common.account.status.types.game.pvp.PvPStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import br.com.pentamc.common.account.status.types.combat.CombatStatus;
import br.com.pentamc.common.account.status.types.game.GameStatus;
import br.com.pentamc.common.account.status.types.normal.NormalStatus;

@AllArgsConstructor
@Getter
public enum StatusType {

    LOBBY("lobby-combat", NormalStatus.class),
    GLADIATOR("gladiator", CombatStatus.class),
    SHADOW("shadow", GameStatus.class),
    PVP("pvp", PvPStatus.class),
    HG("hungergames", GameStatus.class),
    EVENTO("evento", GameStatus.class);

    private String mongoCollection;
    private Class<? extends Status> statusClass;
}
