package br.com.pentamc.competitive.controller;

import br.com.pentamc.competitive.constructor.SimpleKit;
import br.com.pentamc.competitive.GameMain;
import com.google.gson.JsonParser;
import br.com.pentamc.common.controller.StoreController;

public class SimplekitController extends StoreController<String, SimpleKit> {

    public SimplekitController() {
        super();

        if (GameMain.getInstance().getConfig().contains("skit")) {
            GameMain.getInstance().getConfig().getConfigurationSection("skit").getKeys(false).forEach(this::loadKit);
        }
    }

    @Override
    public boolean containsKey(String key) {
        return super.containsKey(key.toLowerCase());
    }

    @Override
    public void load(String key, SimpleKit value) {
        super.load(key.toLowerCase(), value);
    }

    @Override
    public boolean unload(String key) {
        return super.unload(key.toLowerCase());
    }

    @Override
    public SimpleKit getValue(String key) {
        return super.getValue(key.toLowerCase());
    }

    public void saveKit(SimpleKit simpleKit) {
        GameMain.getInstance().getConfig().set("skit." + simpleKit.getKitName().toLowerCase(), simpleKit.toString());
        GameMain.getInstance().saveConfig();
    }

    public void loadKit(String kitName) {
        if (GameMain.getInstance().getConfig().contains("skit." + kitName.toLowerCase())) {
            load(kitName.toLowerCase(), SimpleKit.fromString(new JsonParser().parse(
                                                                               GameMain.getInstance().getConfig().getString("skit." + kitName.toLowerCase())).getAsJsonObject()
                                                                       .getAsJsonObject()));
        }
    }
}
