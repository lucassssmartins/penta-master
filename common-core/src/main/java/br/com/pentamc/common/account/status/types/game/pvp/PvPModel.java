package br.com.pentamc.common.account.status.types.game.pvp;

import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.game.pvp.model.Battle;
import br.com.pentamc.common.account.status.types.game.pvp.model.FPS;
import br.com.pentamc.common.account.status.types.game.pvp.model.Lava;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PvPModel {

    private UUID uniqueId;
    private StatusType statusType;

    private Battle battle;
    private FPS fps;
    private Lava lava;

    private int coins;

    public PvPModel(PvPStatus status) {
        uniqueId = status.getUniqueId();
        statusType = status.getStatusType();

        battle = status.getBattle();
        fps = status.getFps();
        lava = status.getLava();

        coins = status.getCoins();
    }
}
