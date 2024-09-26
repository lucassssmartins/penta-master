package br.com.pentamc.common.account.status.types.game.pvp.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Battle {

    private int
            kills,
            deaths,
            actualStreak,
            maxStreak;

    private List<String> kits = new ArrayList<>();

    public void addKit(String kitName) {
        if (!kits.contains("kit." + kitName.toLowerCase()))
            kits.add("kit." + kitName.toLowerCase());
    }

    public void removeKit(String kitName) {
        kits.remove("kit." + kitName.toLowerCase());
    }

    public boolean containsKit(String kitName) {
        return kits.contains("kit." + kitName.toLowerCase());
    }
}