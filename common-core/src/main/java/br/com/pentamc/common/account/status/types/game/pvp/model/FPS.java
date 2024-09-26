package br.com.pentamc.common.account.status.types.game.pvp.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FPS {

    private int
            kills,
            deaths,
            actualStreak,
            maxStreak;
}