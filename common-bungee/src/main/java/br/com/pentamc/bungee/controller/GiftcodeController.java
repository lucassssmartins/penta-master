package br.com.pentamc.bungee.controller;

import java.util.HashMap;
import java.util.Map;

import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.controller.StoreController;
import br.com.pentamc.common.giftcode.Giftcode;

public class GiftcodeController extends StoreController<String, Giftcode>
		implements br.com.pentamc.common.controller.GiftcodeController {

	@Override
	public Map<String, Giftcode> load() {
		return new HashMap<>();
	}

	@Override
	public boolean registerGiftcode(String code, Giftcode giftcode) {
		if (containsKey(code))
			return false;

		load(code, giftcode);
		return true;
	}

	@Override
	public boolean deleteGiftcode(String code) {
		if (containsKey(code)) {
			unload(code);
			return true;
		}

		return false;
	}

	@Override
	public ExecutionResponse execute(Member member, String code) {
		if (containsKey(code)) {
			Giftcode giftcode = getValue(code);

			if (giftcode.alreadyUsed())
				return ExecutionResponse.ALREADY_USED;

			giftcode.execute(member);
			return ExecutionResponse.SUCCESS;
		}

		return ExecutionResponse.NOT_FOUND;
	}

}
