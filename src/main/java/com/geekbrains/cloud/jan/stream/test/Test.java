package com.geekbrains.cloud.jan.stream.test;

import com.geekbrains.cloud.jan.model.CloudMessage;
import com.geekbrains.cloud.jan.model.ListMessage;
import com.geekbrains.cloud.jan.stream.builder.User;
import com.geekbrains.cloud.jan.stream.func.Func;
import com.geekbrains.cloud.jan.stream.func.MessageCallback;

import java.io.IOException;
import java.nio.file.Paths;

public class Test {

    static int calculate(int x, int y, Func func) {
        return func.apply(x, y);
    }

    static int mul(int a, int b) {
        return a * b;
    }

    static void processMessage(CloudMessage message, MessageCallback callback) {
        callback.onMessageReceived(message);
    }

    static void read(CloudMessage message) {
        System.out.println(message);
    }

    public static void main(String[] args) throws IOException {

        // method reference
        System.out.println(calculate(1, 2, Integer::sum));
        System.out.println(calculate(4, 4, Test::mul));

        processMessage(new ListMessage(Paths.get("data")), Test::read);
        processMessage(new ListMessage(Paths.get("data")), System.out::println);

        /*User user = User.builder()
                .setAge(12)
                .setName("Iva")
                *//*.build();*/

    }
}
