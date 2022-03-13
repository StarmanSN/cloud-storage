package com.geekbrains.cloud.jan.model;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ListMessage implements CloudMessage {

    private final List<String> files;
    private final String serverDir;

    public ListMessage(Path path) throws IOException {
        files = Files.list(path)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
        serverDir = path.getFileName().toString();
    }

    @Override
    public CommandType getType() {
        return CommandType.LIST;
    }

    public List<String> getFiles() {
        return files;
    }

    public String getServerDir() {
        return serverDir;
    }
}
