package org.alexdev.roseau.server.netty;

import org.oldskooler.simplelogger4j.SimpleLog;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.alexdev.roseau.server.IServerHandler;
import org.alexdev.roseau.server.netty.codec.NetworkDecoder;
import org.alexdev.roseau.server.netty.codec.NetworkEncoder;
import org.alexdev.roseau.server.netty.connections.ConnectionHandler;

import java.net.InetSocketAddress;
import java.util.List;

public class NettyServer extends IServerHandler {
    private static final SimpleLog logger = SimpleLog.of(NettyServer.class);
    
	public NettyServer(List<Integer> ports) {
		super(ports);
	}

	@Override
	public boolean listenSocket() {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true)
				.childHandler(new NettyChannelInitializer(this));

			for (int port : this.getPorts()) {
				ChannelFuture future = bootstrap.bind(new InetSocketAddress(this.getIp(), port)).sync();
				logger.info("Server bound to port: " + port);
			}

			return true;

		} catch (Exception ex) {
			logger.error("Failed to bind server socket", ex);
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
			return false;
		}
	}

	private static class NettyChannelInitializer extends io.netty.channel.ChannelInitializer<io.netty.channel.socket.SocketChannel> {
		private final IServerHandler serverHandler;

		public NettyChannelInitializer(IServerHandler serverHandler) {
			this.serverHandler = serverHandler;
		}

		@Override
		protected void initChannel(io.netty.channel.socket.SocketChannel ch) {
			ch.pipeline()
				.addLast("decoder", new NetworkDecoder(serverHandler))
				.addLast("encoder", new NetworkEncoder())
				.addLast("handler", new ConnectionHandler(serverHandler));
		}
	}
}
