package org.alexdev.roseau.game.commands;

import java.util.Map;

import org.alexdev.roseau.game.commands.types.SitCommand;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.log.Log;

import com.google.common.collect.Maps;

public class CommandManager {

	private Map<String, Command> commands;

	public CommandManager() {
		this.commands = Maps.newHashMap();
	}
	
	public void load() {
		this.commands.put("sit", new SitCommand());
	}

	public boolean hasCommand(String message) {

		if (message.startsWith(":") && message.length() > 1) {

			String commandName = message.split(":")[1];
			
			Log.println("COMMAND: " + commandName);

			if (commands.containsKey(commandName)) {
				return true;
			}
		}

		return false;
	}

	public void invokeCommand(Player player, String message) {

		String commandName = message.split(":")[1];

		if (commands.containsKey(commandName)) {
			commands.get(commandName).handleCommand(player, message);
		}
	}
}
/*package org.alexdev.roseau.game.commands;

import java.util.Map;
import java.util.Map.Entry;

import org.alexdev.roseau.game.commands.types.SitCommand;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.log.Log;

import com.google.common.collect.Maps;

public class CommandManager {

	private Map<String, Command> commands;

	public CommandManager() {
		this.commands = Maps.newHashMap();
	}

	public void load() {
		this.commands.put("sit", new SitCommand());
	}

	public boolean hasCommand(String message) {

		if (message.startsWith(":") && message.length() > 1) {

			String commandName = message.split(":")[1];

			for (String command : this.commands.keySet()) {
				if (command.equals(commandName)) {
					return true;
				}
			}
		}

		return false;
	}

	public void invokeCommand(Player player, String message) {

		String commandName = message.split(":")[1];

		for (Entry<String, Command> set : this.commands.entrySet()) {
			if (set.getKey().equals(commandName)) {
				set.getValue().handleCommand(player, message);
			}
		}
	}
}*/