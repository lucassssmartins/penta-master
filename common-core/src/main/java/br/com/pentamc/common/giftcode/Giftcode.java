package br.com.pentamc.common.giftcode;

import br.com.pentamc.common.account.Member;

public interface Giftcode {

	void execute(Member member);
	
	String getCode();
	
	boolean alreadyUsed();
	
}
