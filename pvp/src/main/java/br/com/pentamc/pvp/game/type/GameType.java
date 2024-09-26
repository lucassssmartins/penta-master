package br.com.pentamc.pvp.game.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameType {

    BATTLE("arena"),
    FPS("fps"),
    LAVA("lava");

    private final String worldName;
}