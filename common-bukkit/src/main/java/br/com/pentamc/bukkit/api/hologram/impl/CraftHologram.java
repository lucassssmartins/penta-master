package br.com.pentamc.bukkit.api.hologram.impl;

import br.com.pentamc.bukkit.api.hologram.Hologram;
import br.com.pentamc.bukkit.api.hologram.TouchHandler;
import br.com.pentamc.bukkit.api.hologram.ViewHandler;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class CraftHologram extends CraftSingleHologram {

    private final List<Hologram> linesBelow;
    private final List<Hologram> linesAbove;

    public CraftHologram(String displayName, Location location, TouchHandler<Hologram> touchHandler, ViewHandler viewHandler) {
        super(displayName, location, touchHandler, viewHandler);

        this.linesBelow = new ArrayList<>();
        this.linesAbove = new ArrayList<>();
    }

    public CraftHologram(String displayName, Location location) {
        this(displayName, location, Hologram.EMPTY_TOUCH_HANDLER, ViewHandler.EMPTY);
    }

    public CraftHologram(String displayName, Location location, ViewHandler viewHandler) {
        this(displayName, location, Hologram.EMPTY_TOUCH_HANDLER, viewHandler);
    }

    @Override
    public Hologram teleport(Location location) {
        super.teleport(location);

        for (int i = 0; i < this.linesBelow.size(); i++) {
            this.linesBelow.get(i).teleport(location.clone().subtract(0.0D, (i + 1) * Hologram.DISTANCE, 0.0D));
        }

        for (int i = 0; i < this.linesAbove.size(); i++) {
            this.linesAbove.get(i).teleport(location.clone().add(0.0D, (i + 1) * Hologram.DISTANCE, 0.0D));
        }
        return this;
    }

    @Override
    public Hologram addLineAbove(String line) {
        Hologram hologram = new CraftSingleHologram(line, getLocation().clone().add(0.0D, (getLinesAbove().size() + 1) *
                                                                                          Hologram.DISTANCE, 0.0D), getTouchHandler(),
                                                    getViewHandler());

        this.linesAbove.add(hologram);
        return hologram;
    }

    @Override
    public Hologram addLineBelow(String line) {
        Hologram hologram = new CraftSingleHologram(line, getLocation().clone().subtract(0.0D,
                                                                                         (getLinesBelow().size() + 1) *
                                                                                         Hologram.DISTANCE, 0.0D), getTouchHandler(),
                                                    getViewHandler());

        this.linesBelow.add(hologram);
        return hologram;
    }

    @Override
    public Hologram hide(Player player) {
        super.hide(player);
        linesBelow.forEach(hologram -> hologram.hide(player));
        linesAbove.forEach(hologram -> hologram.hide(player));
        return this;
    }

    @Override
    public Hologram show(Player player) {
        super.show(player);
        linesBelow.forEach(hologram -> hologram.show(player));
        linesAbove.forEach(hologram -> hologram.show(player));
        return this;
    }
}
