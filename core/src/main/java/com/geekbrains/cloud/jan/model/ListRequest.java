package com.geekbrains.cloud.jan.model;

import lombok.Data;

@Data
public class ListRequest implements CloudMessage {

    private final String dirName;
    private final boolean upper;

    public ListRequest(boolean upper, String dirName) {
        this.dirName = dirName;
        this.upper = upper;
    }

    public ListRequest(boolean upper) {
        this.dirName = "";
        this.upper = upper;
    }

    @Override
    public CommandType getType() {
        return CommandType.LIST_REQUEST;
    }

}