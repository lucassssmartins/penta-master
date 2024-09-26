package br.com.pentamc.common;

import java.util.UUID;

import br.com.pentamc.common.command.CommandSender;

public interface CommonPlatform {
	
	UUID getUuid(String playerName);
	
	<T> T getPlayerByName(String playerName, Class<T> clazz);
	
	<T> T getExactPlayerByName(String playerName, Class<T> clazz);
	
	<T> T getPlayerByUuid(UUID uniqueId, Class<T> clazz);
	
	CommandSender getConsoleSender();
	
	void runAsync(Runnable runnable);

}
