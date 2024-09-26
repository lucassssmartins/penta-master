package br.com.pentamc.pvp.listener;

import br.com.pentamc.common.utils.ClassGetter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

@Getter
@RequiredArgsConstructor
public class ListenerLoader {

    protected final Plugin plugin;
    protected final String packageName;

    public void registry() {
        plugin.getLogger().warning("> Preparations for loading the listeners...");

        int count = 0;

        for (Class<?> clazz : ClassGetter.getClassesForPackage(plugin.getClass(), packageName))
            if (Listener.class.isAssignableFrom(clazz))
                try {
                    Listener listener = (Listener) clazz.newInstance();

                    Bukkit.getPluginManager().registerEvents(listener, plugin);
                    count++;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

        plugin.getLogger().warning("> " + count + " listeners were loaded!");
    }
}
