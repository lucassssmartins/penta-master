package br.com.pentamc.bukkit.utils.schematic.object;

import br.com.pentamc.bukkit.utils.schematic.utils.Vector;

public class SchematicBlock {

    private final Vector location;

    private final int id;

    private final byte data;

    public SchematicBlock(Vector location, int id, byte data) {
        this.location = location;
        this.id = id;
        this.data = data;
    }

    public Vector getLocation() {
        return location;
    }

    public int getID() {
        return id;
    }

    public byte getData() {
        return data;
    }
}