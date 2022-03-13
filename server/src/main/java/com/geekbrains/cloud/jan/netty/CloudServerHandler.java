package com.geekbrains.cloud.jan.netty;

import com.geekbrains.cloud.jan.model.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.isDirectory;

public class CloudServerHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private Path currentDir;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        currentDir = Paths.get("data");
        sendList(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        switch (cloudMessage.getType()) {
            case FILE_REQUEST:
                processFileRequest((FileRequest) cloudMessage, ctx);
                break;
            case FILE:
                processFileMessage((FileMessage) cloudMessage);
                sendList(ctx);
                break;
            case LIST_REQUEST:
                processListRequest((ListRequest) cloudMessage, ctx);
                break;
        }
    }

    private void sendList(ChannelHandlerContext ctx) throws IOException {
        ctx.writeAndFlush(new ListMessage(currentDir));
    }

    private void processFileMessage(FileMessage cloudMessage) throws IOException {
        Files.write(currentDir.resolve(cloudMessage.getFileName()), cloudMessage.getBytes());
    }

    private void processFileRequest(FileRequest cloudMessage, ChannelHandlerContext ctx) throws IOException {
        Path path = currentDir.resolve(cloudMessage.getFileName());
        ctx.writeAndFlush(new FileMessage(path));
    }

    private void processListRequest(ListRequest cloudMessage, ChannelHandlerContext ctx) throws IOException {
        if (cloudMessage.isUpper()) {
            currentDir = currentDir.getParent();
            sendList(ctx);
        } else if (isDirectory(currentDir.resolve(cloudMessage.getDirName()))) {
            currentDir = currentDir.resolve(cloudMessage.getDirName());
            sendList(ctx);
        }
    }
}
