package br.com.pentamc.common.giftcode.types;

import br.com.pentamc.common.permission.RankType;
import lombok.Getter;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.giftcode.Giftcode;
import br.com.pentamc.common.tag.Tag;
import br.com.pentamc.common.utils.DateUtils;

@Getter
public class RankGiftcode implements Giftcode {

	private String code;

	private RankType rankType;
	private long rankTime;

	private boolean alreadyUsed;

	public RankGiftcode(String code, RankType rankType, long rankTime) {
		this.code = code;
		this.rankType = rankType;
		this.rankTime = rankTime;
	}

	@Override
	public void execute(Member member) {
		if (member.hasRank(getRankType())) {
			member.getRanks().put(getRankType(),
					member.getRanks().get(getRankType()) + (getRankTime() - System.currentTimeMillis()));
		} else
			member.getRanks().put(getRankType(), getRankTime());

		member.saveRanks();
		member.sendMessage("§aVocê ativou o código " + code + " de " + Tag.valueOf(getRankType().name()).getPrefix()
				+ "§a por " + DateUtils.getTime(getRankTime()) + "!");
		alreadyUsed = true;
	}

	@Override
	public boolean alreadyUsed() {
		return alreadyUsed;
	}

}
