package br.com.pentamc.common.account.status.types.game.pvp;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.status.Status;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.game.pvp.model.Battle;
import br.com.pentamc.common.account.status.types.game.pvp.model.FPS;
import br.com.pentamc.common.account.status.types.game.pvp.model.Lava;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PvPStatus implements Status {

    private UUID uniqueId;
    private final StatusType statusType;

    private Battle battle;
    private FPS fps;
    private Lava lava;

    private int coins;

    public PvPStatus(PvPModel model) {
        uniqueId = model.getUniqueId();
        statusType = model.getStatusType();

        battle = model.getBattle();
        fps = model.getFps();
        lava = model.getLava();

        coins = model.getCoins();
    }

    public PvPStatus(UUID uniqueId, StatusType statusType) {
        this.uniqueId = uniqueId;
        this.statusType = statusType;

        battle = new Battle();
        fps = new FPS();
        lava = new Lava();
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;

        save("uniqueId");
    }

    @Override
    public StatusType getStatusType() {
        return statusType;
    }

    public void addCoins(int quantity) {
        coins += quantity;

        save("coins");
    }

    public void removeCoins(int quantity) {
        coins -= quantity;

        save("coins");
    }

    public void save(String field) {
        CommonGeneral.getInstance().getStatusData().updateStatus(this, field);
    }
}
