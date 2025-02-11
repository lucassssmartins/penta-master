package br.com.pentamc.common.clan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.MemberModel;
import br.com.pentamc.common.account.MemberVoid;
import br.com.pentamc.common.clan.event.ClanEvent;
import br.com.pentamc.common.clan.event.member.MemberChangeNameEvent;
import br.com.pentamc.common.permission.Group;
import lombok.Getter;
import br.com.pentamc.common.clan.enums.ClanHierarchy;
import br.com.pentamc.common.clan.enums.ClanRank;
import br.com.pentamc.common.clan.event.member.MemberChatEvent;
import br.com.pentamc.common.clan.event.member.MemberJoinEvent;
import br.com.pentamc.common.clan.event.member.MemberLeaveEvent;
import br.com.pentamc.common.clan.event.member.MemberOnlineEvent;

@Getter
public abstract class Clan {

	public static final int MAX_MEMBERS = 32;

	public static final String MESSAGE_PREFIX = "§9Clan> ";
	public static final String CLANCHAT_PREFIX = "§9§l[CLAN-CHAT]";

	private UUID uniqueId;

	private String clanName;
	private String clanAbbreviation;

	private Map<UUID, ClanInfo> memberMap;

	private ClanRank clanRank;
	private int xp;

	private long disbanTime;
	private int maxMembers = 15;

	public Clan(UUID uniqueId, String clanName, String clanAbbreviation, Member owner) {
		this.uniqueId = uniqueId;
		this.clanName = clanName;
		this.clanAbbreviation = clanAbbreviation;
		this.memberMap = new HashMap<>();
		this.memberMap.put(owner.getUniqueId(), new ClanInfo(owner, ClanHierarchy.OWNER));
		this.clanRank = ClanRank.INITIAL;
	}

	public Clan(ClanModel clanModel) {
		this.uniqueId = clanModel.getUniqueId();

		this.clanName = clanModel.getClanName();
		this.clanAbbreviation = clanModel.getClanAbbreviation();

		this.memberMap = clanModel.getMemberMap();

		this.clanRank = clanModel.getClanRank();
		this.xp = clanModel.getXp();
	}
	
	public int addXp(int xp) {
		if (xp < 0)
			xp = 0;
		
		setXp(getXp() + xp);
		return xp;
	}

	public int removeXp(int xp) {
		if (xp < 0)
			xp = 0;
		
		if (getXp() - xp < 0)
			setXp(0);
		else
			setXp(getXp() - xp);
		return xp;
	}
	
	public void setXp(int xp) {
		this.xp = xp;
		save("xp");
	}

	public boolean addMember(Member member) {
		if (this.memberMap.containsKey(member.getUniqueId()))
			return false;

		sendMessage("§aO " + member.getPlayerName() + " entrou no clan!");
		onClanEvent(new MemberJoinEvent(this, member));
		this.memberMap.put(member.getUniqueId(), new ClanInfo(member));
		save("memberMap");
		return true;
	}

	public boolean removeMember(Member member) {
		if (!this.memberMap.containsKey(member.getUniqueId()))
			return false;

		sendMessage("§cO " + member.getPlayerName() + " saiu do clan!");
		onClanEvent(new MemberLeaveEvent(this, member));
		this.memberMap.remove(member.getUniqueId());
		save("memberMap");
		return true;
	}

	public boolean kickMember(Member member, Member player) {
		if (!this.memberMap.containsKey(member.getUniqueId()))
			return false;

		member.sendMessage("§cVocê foi expulso do clan!");
		sendMessage("§cO " + member.getPlayerName() + " foi expulso do clan pelo " + player.getPlayerName() + "!");
		onClanEvent(new MemberLeaveEvent(this, member));
		this.memberMap.remove(member.getUniqueId());
		save("memberMap");
		return true;
	}

	public boolean isGroup(UUID uniqueId, ClanHierarchy clanHierarchy) {
		if (this.memberMap.containsKey(uniqueId))
			if (this.memberMap.get(uniqueId).getClanHierarchy() == clanHierarchy)
				return true;
		return false;
	}

	public boolean hasGroup(UUID uniqueId, ClanHierarchy clanHierarchy) {
		if (this.memberMap.containsKey(uniqueId))
			if (this.memberMap.get(uniqueId).getClanHierarchy().ordinal() >= clanHierarchy.ordinal())
				return true;
		return false;
	}

	public boolean setGroup(UUID uuid, ClanHierarchy clanHierarchy) {
		if (this.memberMap.containsKey(uuid)) {
			this.memberMap.get(uuid).setClanHierarchy(clanHierarchy);
			save("memberMap");
			return true;
		}

		return false;
	}

	public void updateMember(Member member) {
		if (isMember(member.getUniqueId()))
			if (this.memberMap.get(member.getUniqueId()).updateMember(member)) {
				callEvent(new MemberChangeNameEvent(this, member));
				save("memberMap");
			}
	}

	public boolean isMember(UUID uniqueId) {
		return this.memberMap.containsKey(uniqueId);
	}

	public boolean isMember(String playerName) {
		return this.memberMap.values().stream().filter(clanInfo -> clanInfo.getPlayerName().equals(playerName))
				.findFirst().isPresent();
	}

	public void calculateMembers() {
		if (this.memberMap.size() > maxMembers)
			maxMembers = this.memberMap.size();
		else {
			long paymentMembers = this.memberMap.values().stream()
					.filter(member -> member.getGroup().ordinal() >= Group.VIP.ordinal()).count();

			maxMembers = 16 + (int) paymentMembers;
		}
	}

	public boolean disband() {
		if (disbanTime > System.currentTimeMillis()) {
			CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

				@Override
				public void run() {
					CommonGeneral.getInstance().getClanData().deleteClan(Clan.this);

					for (UUID uniqueId : memberMap.keySet()) {
						Member member = CommonGeneral.getInstance().getMemberManager().getMember(uniqueId);

						if (member == null) {
							MemberModel memberModel = CommonGeneral.getInstance().getPlayerData().loadMember(uniqueId);

							if (memberModel == null)
								continue;

							member = new MemberVoid(memberModel);
						}

						member.setClanUniqueId(null);
						sendMessage(member,
								"§cO clan " + getClanName() + " (" + getClanAbbreviation() + ") foi desfeito!");
					}

				}
			});

			CommonGeneral.getInstance().getClanManager().unloadClan(getUniqueId());
			return true;
		}

		disbanTime = System.currentTimeMillis() + 10000l;
		return false;
	}

	public void chat(Member member, String message) {

		if (!member.getAccountConfiguration().isClanChatEnabled()) {
			member.sendMessage("§aO chat do clan foi ativado!");
			member.sendMessage("§eUse /clan chat para desativar ou ativar o chat do clan!");
			member.getAccountConfiguration().setClanChatEnabled(true);
		}

		MemberChatEvent event = callEvent(new MemberChatEvent(this, member, getOnlineMembers(), message));

		event.getRecipients().stream().filter(m -> m.getAccountConfiguration().isClanChatEnabled())
				.forEach(m -> m.sendMessage(
						CLANCHAT_PREFIX + " " + memberMap.get(member.getUniqueId()).getClanHierarchy().getTag() + " "
								+ member.getPlayerName() + "§7: §f" + message));
	}

	public void sendMessage(Member member, String message) {
		if (memberMap.containsKey(member.getUniqueId()))
			member.sendMessage(MESSAGE_PREFIX + message);
	}

	public void sendMessage(String message) {
		CommonGeneral.getInstance().getMemberManager().getMemberMap().values().stream()
				.filter(member -> memberMap.containsKey(member.getUniqueId()))
				.forEach(member -> member.sendMessage(MESSAGE_PREFIX + message));
	}

	public List<Member> getOnlineMembers() {
		return CommonGeneral.getInstance().getMemberManager().getMemberMap().values().stream()
				.filter(member -> memberMap.containsKey(member.getUniqueId())).collect(Collectors.toList());
	}

	public <T extends ClanEvent> T callEvent(T clan) {
		onClanEvent(clan);
		return clan;
	}

	public void onClanEvent(ClanEvent event) {
		if (event instanceof MemberOnlineEvent)
			onMemberOnline((MemberOnlineEvent) event);
		else if (event instanceof MemberChatEvent)
			onMemberChat((MemberChatEvent) event);
		else if (event instanceof MemberChangeNameEvent)
			onMemberChangeName((MemberChangeNameEvent) event);
	}

	public void onMemberChat(MemberChatEvent event) {
		
	}

	public void onMemberOnline(MemberOnlineEvent event) {

	}

	public void onMemberChangeName(MemberChangeNameEvent event) {

	}

	public void save(String fieldName) {
		CommonGeneral.getInstance().getClanData().updateClan(this, fieldName);
	}

}
