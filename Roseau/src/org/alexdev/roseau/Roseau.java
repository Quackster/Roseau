package org.alexdev.roseau;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.alexdev.roseau.dao.Dao;
import org.alexdev.roseau.dao.mysql.MySQLDao;
import org.alexdev.roseau.game.Game;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.server.IServerHandler;
import org.alexdev.roseau.server.netty.NettyServer;
import org.alexdev.roseau.util.Configuration;
import org.alexdev.roseau.util.JarUtils;
import org.alexdev.roseau.util.Util;

public class Roseau {

	private static IServerHandler server;
	private static Util utilities;
	private static Game game;
	private static Dao dao;
	private static boolean isDebug;

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

			if (utilities.getConfiguration().get("database-type").equalsIgnoreCase("mysql")) {
				dao = new MySQLDao();
			}

			if (dao.isConnected()) {
				game = new Game(dao);
				game.load();

				startServer();
			}


		} catch (Exception e) {
			return;
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
		
		Configuration socketConf = new Configuration(new File("extensions" + File.separator + "roseau_socket_extension.conf"));
		libs.add(new File(socketConf.get("extension.socket.jar")));


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

			server = Class.forName(socketConf.get("extension.socket.entry")).asSubclass(IServerHandler.class).newInstance();
			
			if (server == null) {
				Log.println("Server null");
			}

		} catch (final Exception e) { 
			Log.exception(e);
		}
	}

	private static void createConfig() throws IOException {
		File file = new File("icarus.properties");

		if (!file.isFile()) { 
			file.createNewFile();
			PrintWriter writer = new PrintWriter(file.getAbsoluteFile());
			writeConfiguration(writer);
			writer.flush();
			writer.close();
		}

		utilities = new Util();
	}

	private static void writeConfiguration(PrintWriter writer) {

		writer.println("#######################");
		writer.println("###  Server Config  ###");
		writer.println("#######################");
		writer.println();
		writer.println("server-ip=127.0.0.1");
		writer.println("server-port=30000");
		writer.println();
		writer.println("#########################");
		writer.println("###  Database Config  ###");
		writer.println("#########################");
		writer.println();
		writer.println("database-type=mysql");
		writer.println();
		writer.println("mysql-hostname=127.0.0.1");
		writer.println("mysql-username=root");
		writer.println("mysql-password=changeme");
		writer.println("mysql-database=icarusdb");
		writer.println();
		writer.println("########################");
		writer.println("###  Logging Config  ###");
		writer.println("########################");
		writer.println();
		writer.println("log-errors=true");
		writer.println("log-output=true");
		writer.println("log-connections=true");
		writer.println("log-packets=true");
		writer.println();
		writer.println("#######################");
		writer.println("###  Plugin Config  ###");
		writer.println("#######################");
		writer.println();
		writer.println("plugin.runtime.timeout=5");
		writer.println("plugin.runtime.timeout.unit=SECONDS");

	}

	private static void startServer() {

		String IPAddress = utilities.getConfiguration().get("server-ip");
		int serverPort = Integer.valueOf(utilities.getConfiguration().get("server-port"));

		Log.println("Settting up server");

		server.setIp(IPAddress);
		server.setPort(serverPort);

		if (server.listenSocket()) {
			Log.println("Server is listening on " + IPAddress + ":" + serverPort);
		} else {
			Log.println("Server could not listen on " + IPAddress + ":" + serverPort + ", please double check everything is correct in icarus.properties");
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
}
