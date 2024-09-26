package br.com.pentamc.common.server.route.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArcadeType {

    SOLO(2),
    PAIR(4);

    private final int necessaryPlayers;
}
