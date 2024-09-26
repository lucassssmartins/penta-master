package br.com.pentamc.common.account;

import java.util.UUID;

import br.com.pentamc.common.account.configuration.LoginConfiguration;
import net.md_5.bungee.api.chat.BaseComponent;

public class MemberVoid extends Member {

	public MemberVoid(MemberModel memberModel) {
		super(memberModel);
	}
	
	public MemberVoid(String playerName, UUID uniqueId, LoginConfiguration.AccountType accountType) {
		super(playerName, uniqueId, accountType);
	}

	@Override
	public void sendMessage(String message) {
		System.out.println("VOID -> " + message);
	}

	@Override
	public void sendMessage(BaseComponent message) {
		
	}

	@Override
	public void sendMessage(BaseComponent[] message) {
		
	}

}
