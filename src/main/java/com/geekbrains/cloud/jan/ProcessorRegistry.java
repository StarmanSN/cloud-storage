package com.geekbrains.cloud.jan;

import com.geekbrains.cloud.jan.model.CloudMessage;
import com.geekbrains.cloud.jan.model.CommandType;

import java.util.HashMap;
import java.util.Map;

public class ProcessorRegistry {

    private Map<CommandType, MessageProcessor> map;

    public ProcessorRegistry() {
        map = new HashMap<>();
        map.put(CommandType.FILE, message -> {

        });
    }

    public void process(CloudMessage msg) {
        map.get(msg.getType().processMessage);
    }
}
