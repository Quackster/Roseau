package org.alexdev.roseau.server.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.server.IServerHandler;
import org.alexdev.roseau.server.netty.codec.NetworkDecoder;
import org.alexdev.roseau.server.netty.codec.NetworkEncoder;
import org.alexdev.roseau.server.netty.connections.ConnectionHandler;
import org.alexdev.roseau.server.netty.connections.SessionManager;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class NettyServer extends IServerHandler {

	private NioServerSocketChannelFactory factory;
	private ServerBootstrap bootstrap;
	private SessionManager sessionManager;

	public NettyServer() {
		super();
		this.sessionManager = new SessionManager();
	}

	@Override
	public boolean listenSocket() {

		this.factory = new NioServerSocketChannelFactory (
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()
		);
		

		this.bootstrap = new ServerBootstrap(this.factory);
		
		ChannelPipeline pipeline = this.bootstrap.getPipeline();

		pipeline.addLast("encoder", new NetworkEncoder());
		pipeline.addLast("decoder", new NetworkDecoder());
		pipeline.addLast("handler", new ConnectionHandler(this.sessionManager));
		
		try {
			this.bootstrap.bind(new InetSocketAddress(this.getIp(), this.getPort()));
		} catch (ChannelException ex) {
			Log.exception(ex);
			return false;
		}

		return true;
		
	}

}
