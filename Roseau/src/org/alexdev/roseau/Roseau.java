package org.alexdev.roseau;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.alexdev.roseau.dao.Dao;
import org.alexdev.roseau.dao.mysql.MySQLDao;
import org.alexdev.roseau.game.Game;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.server.IServerHandler;
import org.alexdev.roseau.util.Configuration;
import org.alexdev.roseau.util.JarUtils;
import org.alexdev.roseau.util.Util;

public class Roseau {

	private static IServerHandler server;
	private static Util utilities;
	private static Game game;
	private static Dao dao;
	private static boolean isDebug;

	private static String serverIP;
	private static int serverPort;
	private static Configuration socketConfiguration;
	private static IServerHandler privateRoomServer;

	public static void main(String[] args) {

		try {

			for (String arg : args) {
				if (arg.equalsIgnoreCase("-debug")) {
					isDebug = true;
				}
			}

			createConfig();
			Log.startup();
			loadDependencies();

			serverIP = utilities.getConfiguration().get("Server", "server.ip", String.class);
			serverPort = utilities.getConfiguration().get("Server", "server.port", int.class);	

			if (utilities.getConfiguration().get("Database", "type", String.class).equalsIgnoreCase("mysql")) {
				dao = new MySQLDao();
			}

			if (dao.isConnected()) {
				game = new Game(dao);
				game.load();
				Log.println();
				startServer();
			}


		} catch (Exception e) {

		}
	}

	private static void loadDependencies() throws InstantiationException, IllegalAccessException, ClassNotFoundException {

		List<File> libs = new ArrayList<File>();

		Log.println("Loading dependencies");

		if (!isDebug) { // if not debug mode, then we include libraries
			Configuration libraryConfig = new Configuration(new File("lib" + File.separator + "libraries.properties"));
			String[] libraries = libraryConfig.get("libraries").split(",");

			for (String library : libraries) {

				File libFile = new File("lib" + File.separator + libraryConfig.get("library." + library));
				libs.add(libFile);
			}
		}

		socketConfiguration = new Configuration(new File("extensions" + File.separator + "roseau_socket_extension.conf"));
		libs.add(new File(socketConfiguration.get("extension.socket.jar")));


		try {
			for (final File lib : libs) { 
				if (lib.exists()) { 

					Log.println("Loading: " + lib.getName());

					JarUtils.extractFromJar(lib.getName(), lib.getAbsolutePath()); 
				} 
			} 

			for (final File lib : libs) { 
				if (!lib.exists()) { 
					Log.println("[ERROR] There was a critical error loading My plugin! Could not find lib: " + lib.getName());
					continue;
				}

				JarUtils.addClassPath(JarUtils.getJarUrl(lib)); 
			}

			Log.println();

			server = Class.forName(socketConfiguration.get("extension.socket.entry")).asSubclass(IServerHandler.class).getDeclaredConstructor(String.class).newInstance("");

			if (server == null) {
				Log.println("Server null");
			}

		} catch (final Exception e) { 
			Log.exception(e);
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

		utilities = new Util();
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
		writer.println();
		writer.println("[Scheduler]");
		writer.println("credits.every.x.mins=10");
		writer.println("credits.every.x.amount=25");
		writer.println();

	}


	private static void startServer() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {

		String serverIP = utilities.getConfiguration().get("Server", "server.ip", String.class);
		int serverPort = utilities.getConfiguration().get("Server", "server.port", int.class);


		Log.println("Settting up server");

		server.setIp(serverIP);
		server.setPort(serverPort);

		privateRoomServer = Class.forName(socketConfiguration.get("extension.socket.entry")).asSubclass(IServerHandler.class).getDeclaredConstructor(String.class).newInstance("");
		privateRoomServer.setIp(serverIP);
		privateRoomServer.setPort(serverPort - 1);
		privateRoomServer.listenSocket();

		if (server.listenSocket()) {
			Log.println("Server is listening on " + serverIP + ":" + serverPort);
		} else {
			Log.println("Server could not listen on " + serverPort + ":" + serverPort + ", please double check everything is correct in icarus.properties");
		}
	}

	public static IServerHandler getServer() {
		return server;
	}

	public static Util getUtilities() {
		return utilities;
	}

	public static Game getGame() {
		return game;
	}

	public static Dao getDataAccess() {
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
}
