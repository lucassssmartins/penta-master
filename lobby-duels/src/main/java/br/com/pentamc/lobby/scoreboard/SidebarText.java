package br.com.pentamc.lobby.scoreboard;

import org.bukkit.ChatColor;

public final class SidebarText {
	
	private String before16 = "", after16 = "";

	// &a -> 15 - & -> 16
	public void build(String input) {
		if (input.length() > 16) {
			boolean endsWithChar = input.substring(0, 16).endsWith("§");
			if (endsWithChar)
				this.before16 = input.substring(0, 15);
			else
				this.before16 = input.substring(0, 16);
			while (this.before16.endsWith("§"))
				this.before16 = this.before16.substring(0, this.before16.length() - 1);
			if (endsWithChar)
				this.after16 = input.substring(15);
			else
				this.after16 = input.substring(16);
			this.after16 = ChatColor.getLastColors(this.before16) + (endsWithChar ? input.substring(15)
					: input.substring(16));
			if (this.after16.length() > 16) {
				this.after16 = this.after16.substring(0, 16);
			}
		} else {
			this.before16 = input;
			this.after16 = "";
		}
	}

	public String getBefore16() {
		return this.before16;
	}

	public String getAfter16() {
		return this.after16;
	}
}
