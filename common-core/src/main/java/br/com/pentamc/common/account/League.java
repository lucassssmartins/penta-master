package br.com.pentamc.common.account;

import lombok.Getter;

@Getter
public enum League {

    VOID("§8", "⋆", "Void", 0),
    COSMIC("§a", "❃", "Cosmic", 1000),
    REALM("§e", "✣", "Realm", 14000),
    LUNAR("§5", "☽", "Lunar", 31000),
    LEGION("§6", "☯", "Legion", 55000),
    PHOENIX("§c", "❉", "Phoenix", 73500),
    SPECTRAL("§3", "❂", "Spectral", 100000);

    private String color;
    private String symbol;
    private String name;
    private int maxXp;

    League(String color, String symbol, String name, int maxXp) {
        this.symbol = symbol;
        this.color = color;
        this.name = name;
        this.maxXp = maxXp;
    }

    public String getSimplifiedName() {
        return getColor() + (getName().contains(" ") ? getName().split(" ")[1] : "") + getSymbol();
    }

    public League getNextLeague() {
        return ordinal() + 1 <= values()[values().length - 1].ordinal() ? League.values()[ordinal() + 1]
                                                                        : values()[values().length - 1];
    }

    public League getPreviousLeague() {
        return ordinal() - 1 >= 0 ? values()[ordinal() - 1] : values()[0];
    }
}
