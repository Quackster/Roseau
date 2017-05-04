package org.alexdev.roseau;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

import org.alexdev.roseau.dao.Dao;
import org.alexdev.roseau.dao.mysql.MySQLDao;
import org.alexdev.roseau.game.Game;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.server.IServerHandler;
import org.alexdev.roseau.util.Configuration;
import org.alexdev.roseau.util.Util;

public class Roseau {

	private static IServerHandler server;
	private static Game game;
	private static Dao dao;

	private static String serverIP;
	private static String rawConfigIP;
	private static int serverPort;
	private static Configuration socketConfiguration;
	private static IServerHandler privateRoomServer;

	public static void main(String[] args) {

		try {

			createConfig();
			Log.startup();

			serverIP = Util.getConfiguration().get("Server", "server.ip", String.class);
			rawConfigIP = serverIP;
			
			if (!validIP(rawConfigIP)) {
				serverIP = "0.0.0.0"; //InetAddress.getByName(rawConfigIP).getHostAddress();
			}
			
			serverPort = Util.getConfiguration().get("Server", "server.port", int.class);

			if (Util.getConfiguration().get("Database", "type", String.class).equalsIgnoreCase("mysql")) {
				dao = new MySQLDao();
			}

			if (dao.isConnected()) {
				game = new Game(dao);
				game.load();
				Log.println();

				server = Class.forName(Roseau.getServerClassPath()).asSubclass(IServerHandler.class).getDeclaredConstructor(String.class).newInstance("");

				Log.println("Settting up server");

				server.setIp(serverIP);
				server.setPort(serverPort);

				privateRoomServer = Class.forName(Roseau.getServerClassPath()).asSubclass(IServerHandler.class).getDeclaredConstructor(String.class).newInstance("");
				privateRoomServer.setIp(serverIP);
				privateRoomServer.setPort(serverPort - 1);
				privateRoomServer.listenSocket();

				if (!validIP(rawConfigIP)) {
					serverIP = InetAddress.getByName(rawConfigIP).getHostAddress();
				}
				
				if (server.listenSocket()) {
					Log.println("Server is listening on " + serverIP + ":" + serverPort);
				} else {
					Log.println("Server could not listen on " + serverPort + ":" + serverPort + ", please double check everything is correct in icarus.properties");
				}
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean validIP (String ip) {
	    try {
			String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
			return ip.matches(PATTERN);
		} catch (Exception e) {
			return false;
		}
	}

	private static void createConfig() throws IOException {
		File file = new File("roseau.properties");

		if (!file.isFile()) { 
			file.createNewFile();
			PrintWriter writer = new PrintWriter(file.getAbsoluteFile());
			writeMainConfiguration(writer);
			writer.flush();
			writer.close();
		}

		file = new File("habbohotel.properties");

		if (!file.isFile()) { 
			file.createNewFile();
			PrintWriter writer = new PrintWriter(file.getAbsoluteFile());
			writeHabboHotelConfiguration(writer);
			writer.flush();
			writer.close();
		}

		Util.load();
	}


	private static void writeMainConfiguration(PrintWriter writer) {
		writer.println("[Server]");
		writer.println("server.ip=127.0.0.1");
		writer.println("server.port=30000");
		writer.println();
		writer.println("[Database]");
		writer.println("type=mysql");
		writer.println("mysql.hostname=127.0.0.1");
		writer.println("mysql.username=user");
		writer.println("mysql.password=");
		writer.println("mysql.database=roseau");
		writer.println();
		writer.println("[Logging]");
		writer.println("log.errors=true");
		writer.println("log.output=true");
		writer.println("log.connections=true");
		writer.println("log.packets=true");
		writer.println();
	}

	private static void writeHabboHotelConfiguration(PrintWriter writer) {
		writer.println("[Register]");
		writer.println("user.name.chars=1234567890qwertyuiopasdfghjklzxcvbnm-=?!@:.,");
		writer.println("user.default.credits=100");
		writer.println();
		writer.println("[Scheduler]");
		writer.println("credits.every.x.secs=600");
		writer.println("credits.every.x.amount=10");
		writer.println();
		writer.println("[Bot]");
		writer.println("bot.response.delay=1500");
		writer.println();
		writer.println("[Player]");
		writer.println("carry.drink.time=180");
		writer.println("carry.drink.interval=12");
		writer.println();
		writer.println("talking.lookat.distance=30");
		writer.println("talking.lookat.reset=6");
		writer.println();
		writer.println("afk.room.kick=1800");
		writer.println();
		writer.println("[Debug]");
		writer.println("debug.enable=true");
		writer.println();
	}

	private static String getServerClassPath() {
		return "org.alexdev.roseau.server.netty.NettyServer";
	}


	public static IServerHandler getServer() {
		return server;
	}

	public static Game getGame() {
		return game;
	}

	public static Dao getDao() {
		return dao;
	}

	public static String getServerIP() {
		return serverIP;
	}

	public static void setServerIP(String serverIP) {
		Roseau.serverIP = serverIP;
	}

	public static int getServerPort() {
		return serverPort;
	}

	public static int getPrivateServerPort() {
		return serverPort -1;
	}

	public static Configuration getSocketConfiguration() {
		return socketConfiguration;
	}

	public static void setSocketConfiguration(Configuration socketConfiguration) {
		Roseau.socketConfiguration = socketConfiguration;
	}

	public static String getRawConfigIP() {
		return rawConfigIP;
	}

	public static void setRawConfigIP(String rawConfigIP) {
		Roseau.rawConfigIP = rawConfigIP;
	}
}
