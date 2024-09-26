package br.com.pentamc.common.server;

import br.com.pentamc.common.CommonConst;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServerType {

    LOGIN,
    LOBBY,
    LOBBY_HG,
    LOBBY_DUELS,
    LOBBY_PVP,

    PVP,

    SIMULATOR,
    GLADIATOR,
    ONEXONE,

    EVENTO,
    HUNGERGAMES,

    NETWORK,
    NONE;

    public boolean isLobby() {
        return this.name().contains("LOBBY");
    }

    public ServerType getServerLobby() {
        switch (this) {
        case HUNGERGAMES:
        case EVENTO:
            return CommonConst.LOBBY_HG ? LOBBY_HG : LOBBY;
        default:
            return LOBBY;
        }
    }

    public boolean canSendData() {
        return true;
    }
}
