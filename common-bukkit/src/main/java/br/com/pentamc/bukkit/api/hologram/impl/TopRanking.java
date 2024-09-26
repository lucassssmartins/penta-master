package br.com.pentamc.bukkit.api.hologram.impl;

import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.api.hologram.Hologram;
import br.com.pentamc.bukkit.api.hologram.TouchHandler;
import br.com.pentamc.bukkit.api.hologram.ViewHandler;
import br.com.pentamc.common.account.status.Status;
import br.com.pentamc.common.permission.Group;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

public class TopRanking<T> {

    public static final Set<TopRanking<?>> RANKING_SET = new HashSet<>();

    private Hologram mainHologram;

    private Map<UUID, Integer> playerPageMap;

    private int firstLineIndex;
    private UpdateHandler<T> updateHandler;
    private FormatString<T> format;

    private List<T> list;

    public TopRanking(Hologram mainHologram, UpdateHandler<T> updateHandler, FormatString<T> f) {
        this.mainHologram = mainHologram;
        this.playerPageMap = new HashMap<>();

        this.firstLineIndex = mainHologram.getLinesBelow().size() - 1;
        this.updateHandler = updateHandler;
        this.format = f;

        this.list = this.updateHandler.update();
        this.mainHologram.setTouchHandler((hologram, player, touchType) -> {
            int currentPage = this.playerPageMap.getOrDefault(player.getUniqueId(), 1);

            if (touchType == TouchHandler.TouchType.RIGHT) {
                currentPage--;

                if (currentPage < 1) {
                    currentPage = 10;
                }
            } else {
                currentPage++;

                if (currentPage > 10) {
                    currentPage = 1;
                }
            }

            this.playerPageMap.put(player.getUniqueId(), currentPage);

            for (Hologram page : mainHologram.getLinesBelow())
                page.updateTitle(player);
        });

        ViewHandler viewHandler = (hologram, player, text) -> {
            int page = playerPageMap.getOrDefault(player.getUniqueId(), 1);
            int index = Integer.parseInt(text);
            int realIndex = (page - 1) * 10 + index;

            if (realIndex >= list.size()) {
                return format.format(null, realIndex + 1);
            }

            return format.format(list.get(realIndex), realIndex + 1);
        };

        for (int i = 0; i < 10; i++) {
            mainHologram.addLineBelow(Integer.toString(i)).setViewHandler(viewHandler);
        }

        BukkitMain.getInstance().getHologramController().loadHologram(mainHologram);
        RANKING_SET.add(this);
    }

    @AllArgsConstructor
    @Getter
    public static class RankingModel<T extends Status> {

        private T status;

        private String playerName;
        private Group group;


    }

    public void update() {
        this.list = this.updateHandler.update();
    }

    public interface UpdateHandler<T> {

        List<T> update();
    }

    public interface FormatString<T> {

        String format(T toFormat, int position);
    }
}
