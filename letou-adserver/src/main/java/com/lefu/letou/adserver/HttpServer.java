package com.lefu.letou.adserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.lefu.letou.adserver.service.AdsConfig;
import com.lefu.letou.adserver.service.AdsHolder;
import com.lefu.letou.common.LoggerUtil;

public class HttpServer {
	public static void main(String[] args) throws InterruptedException,
			IOException {
		initTimer();
		initServer(AdsConfig.port);
	}

	private static void initServer(int p) throws InterruptedException {
		EventLoopGroup bossGroup = new NioEventLoopGroup(); // 接收客户端连接用默认线程数为cpu的2倍,
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap server = new ServerBootstrap();
			server.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch)
								throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast(new HttpServerCodec());
							ch.pipeline().addLast(new ServerHandler());
						}
					});
			ChannelFuture f = server.bind(p).sync(); // 绑定端口 ,等待绑定成功
			f.channel().closeFuture().sync(); // 等待服务器退出
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
			LoggerUtil.info("adserver shutdown....");
		}
	}

	private static void initTimer() {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		AdsHolder holder = new AdsHolder();
		executor.scheduleAtFixedRate(holder, 1, 30, TimeUnit.SECONDS);
	}
}
