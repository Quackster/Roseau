package org.alexdev.roseau.game.commands;

import org.alexdev.roseau.game.commands.types.AboutCommand;
import org.alexdev.roseau.game.commands.types.HelpCommand;
import org.alexdev.roseau.game.commands.types.SitCommand;
import org.alexdev.roseau.game.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandManager {

	private final Map<String, Command> commands;

	public CommandManager() {
		this.commands = new HashMap<>();
	}
	
	public void load() {
		this.commands.put("about", new AboutCommand());
		this.commands.put("sit", new SitCommand());
		this.commands.put("help", new HelpCommand());
	}

	public boolean hasCommand(String message) {
		return Optional.ofNullable(message)
			.filter(msg -> msg.startsWith(":") && msg.length() > 1)
			.map(msg -> msg.split(":", 2))
			.filter(parts -> parts.length > 1)
			.map(parts -> commands.containsKey(parts[1]))
			.orElse(false);
	}

	public void invokeCommand(Player player, String message) {
		Optional.ofNullable(message)
			.map(msg -> msg.split(":", 2))
			.filter(parts -> parts.length > 1)
			.map(parts -> parts[1])
			.map(commands::get)
			.ifPresent(command -> command.handleCommand(player, message));
	}
}
