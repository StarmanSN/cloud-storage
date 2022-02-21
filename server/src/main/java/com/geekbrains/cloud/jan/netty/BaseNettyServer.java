package com.geekbrains.cloud.jan.netty;

import com.geekbrains.cloud.jan.AuthService;
import com.geekbrains.cloud.jan.BaseAuthService;
import com.geekbrains.cloud.jan.Handler;
import com.geekbrains.cloud.jan.model.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BaseNettyServer {

    private AuthService authService;
    private List<Handler> clients;

    public AuthService getAuthService() {
        return authService;
    }

    public BaseNettyServer(ChannelHandler... handlers) {
        EventLoopGroup auth = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            authService = new BaseAuthService();
            authService.start();
            clients = new ArrayList<>();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(auth, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(handlers);
                        }
                    });
            ChannelFuture future = bootstrap.bind(Constants.SERVER_PORT).sync();
            // server started!
            future.channel().closeFuture().sync(); // blocking

        } catch (InterruptedException | SQLException e) {
            e.printStackTrace();
        } finally {
            if (authService != null) {
                authService.stop();
            }
            auth.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public synchronized void broadcastMessage(String message) {
        clients.forEach(client -> {
            try {
                client.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized String getActiveClients() {
        StringBuilder sb = new StringBuilder(Constants.CHECK_COMMAND).append(" ");
        sb.append(clients.stream()
                .map(c -> c.getName())
                .collect(Collectors.joining())
        );
        return sb.toString();
    }

    public synchronized void subscribe(Handler client) {
        clients.add(client);
    }

    public synchronized void unsubscribe(Handler client) {
        clients.remove(client);
    }
}
