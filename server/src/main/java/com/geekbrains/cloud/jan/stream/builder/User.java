package com.geekbrains.cloud.jan.stream.builder;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {

    private final long id;
    private final String name;
    private final int age;


}
