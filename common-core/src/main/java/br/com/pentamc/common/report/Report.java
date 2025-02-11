package br.com.pentamc.common.report;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.MemberModel;
import br.com.pentamc.common.account.MemberVoid;
import lombok.Getter;

@Getter
public class Report {

	private String playerName;
	private UUID uniqueId;

	private Map<UUID, ReportInformation> playersReason;
	private int reportLevel;
	private long reportExpire = Long.MIN_VALUE;

	private UUID lastReport = null;

	@Getter
	private boolean online = false;

	public Report(UUID uniqueId, String playerName) {
		playersReason = new HashMap<>();
		reportLevel = 0;

		this.playerName = playerName;
		this.uniqueId = uniqueId;
		this.online = true;
	}

	public UUID getPlayerUniqueId() {
		return uniqueId;
	}

	public int getReportLevel() {
		return reportLevel / playersReason.size();
	}

	public long getLastReportTime() {
		if (lastReport != null) {
			return getPlayersReason().get(lastReport).getReportTime();
		}

		return Long.MIN_VALUE;
	}

	public ReportInformation getLastReport() {
		if (lastReport != null) {
			return getPlayersReason().get(lastReport);
		}

		return null;
	}

	public boolean isExpired() {
		return reportExpire < System.currentTimeMillis();
	}

	public void setReportLevel(int reportLevel) {
		this.reportLevel += reportLevel;
		CommonGeneral.getInstance().getReportData().updateReport(this, "reportLevel");
	}

	public void setOnline(boolean online) {
		this.online = online;
		CommonGeneral.getInstance().getReportData().updateReport(this, "online");
	}

	public void setPlayerName(String playerName) {
		if (this.playerName != playerName) {
			this.playerName = playerName;
			CommonGeneral.getInstance().getReportData().updateReport(this, "playerName");
		}
	}

	public boolean addReport(UUID playerReporting, String playerName, int reportLevel, String reason) {
		if (playersReason.containsKey(playerReporting))
			return false;

		playersReason.put(playerReporting, new ReportInformation(playerName, reason, reportLevel));
		reportExpire = System.currentTimeMillis() + (1000 * 60 * 60 * 12);
		lastReport = playerReporting;
		CommonGeneral.getInstance().getReportData().updateReport(this, "playersReason");
		CommonGeneral.getInstance().getReportData().updateReport(this, "reportExpire");
		CommonGeneral.getInstance().getReportData().updateReport(this, "lastReport");
		setReportLevel(getReportLevel() + reportLevel);
		return true;
	}

	public void expire() {
		CommonGeneral.getInstance().getReportData().deleteReport(getPlayerUniqueId());
		CommonGeneral.getInstance().getReportManager().unloadReport(getPlayerUniqueId());
	}

	public void banPlayer() {
		CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

			@Override
			public void run() {

				for (Entry<UUID, ReportInformation> entry : playersReason.entrySet()) {
					Member member = CommonGeneral.getInstance().getMemberManager().getMember(entry.getKey());

					if (member == null) {
						MemberModel memberModel = CommonGeneral.getInstance().getPlayerData()
								.loadMember(entry.getKey());

						if (memberModel == null)
							continue;

						member = new MemberVoid(memberModel);
						memberModel = null;
					}

					member.sendMessage("§aUm jogador utilizando trapaças em sua sala foi banido.");
					member.sendMessage("§aObrigado por ajudar a nossa comunidade!");
					member.setReputation(member.getReputation() + 1);
					member = null;
				}

			}
		});

		CommonGeneral.getInstance().getReportData().deleteReport(getPlayerUniqueId());
		CommonGeneral.getInstance().getReportManager().unloadReport(getPlayerUniqueId());
	}

	public void mutePlayer() {
		CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

			@Override
			public void run() {

				for (Entry<UUID, ReportInformation> entry : playersReason.entrySet()) {
					Member member = CommonGeneral.getInstance().getMemberManager().getMember(entry.getKey());

					if (member == null) {
						MemberModel memberModel = CommonGeneral.getInstance().getPlayerData()
								.loadMember(entry.getKey());

						if (memberModel == null)
							continue;

						member = new MemberVoid(memberModel);
						memberModel = null;
					}

					member.sendMessage("§aUm jogador utilizando trapaças em sua sala foi banido.");
					member.sendMessage(
							"§aVocê ganhou 50 coins e 1 ponto de reputação por ter reportado ele.");
					member.sendMessage("§aObrigado por ajudar a comunidade do servidor.");
					member.setReputation(member.getReputation() + 1);
					member = null;
				}

			}
		});

		CommonGeneral.getInstance().getReportData().deleteReport(getPlayerUniqueId());
		CommonGeneral.getInstance().getReportManager().unloadReport(getPlayerUniqueId());
	}

	public void denyPlayer() {
		CommonGeneral.getInstance().getReportData().deleteReport(getPlayerUniqueId());
		CommonGeneral.getInstance().getReportManager().unloadReport(getPlayerUniqueId());
	}

	public static class ReportInformation {
		private String playerName;
		private String reason;
		private long reportTime;
		private int reportLevel;
		private boolean rejected = false;

		public ReportInformation(String playerName, String reason, int reportLevel) {
			this.playerName = playerName;
			this.reason = reason;
			this.reportTime = System.currentTimeMillis();
			this.reportLevel = reportLevel;
		}

		public String getPlayerName() {
			return playerName;
		}

		public String getReason() {
			return reason;
		}

		public int getReportLevel() {
			return reportLevel;
		}

		public boolean isRejected() {
			return rejected;
		}

		public long getReportTime() {
			return reportTime;
		}

		public void reject() {
			rejected = true;
		}
	}
}
