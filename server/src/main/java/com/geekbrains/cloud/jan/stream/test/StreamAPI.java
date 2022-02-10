package com.geekbrains.cloud.jan.stream.test;

import com.geekbrains.cloud.jan.stream.builder.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamAPI {

    public static void main(String[] args) throws IOException {

        // forEach
        Stream.iterate(1, x -> x + 1)
                .limit(20)
                .forEach(x -> System.out.print(x + " "));
        System.out.println();

        // filter
        Stream.iterate(1, x -> x + 1)
                .limit(20)
                .filter(x -> x % 2 == 1)
                .forEach(x -> System.out.print(x + " "));
        System.out.println();

        // map
        Stream.iterate(1, x -> x + 1)
                .limit(200)
                .filter(x -> x % 2 == 1)
                .map(StreamAPI::compress)
                .filter(x -> x > 10)
                .forEach(x -> System.out.print(x + " "));

        // flatMap
        // map(Stream<R> -> R.stream()) -> Stream<Stream<R>>
        // flatMap(Stream<R> -> R.stream()) -> Stream<R>

        System.out.println();
        Files.lines(Paths.get("data", "file.txt"))
                .filter(s -> !s.isEmpty())
                // Stream<String>
                .flatMap(s -> Arrays.stream(s.split(" +")))
                .forEach(StreamAPI::printer);
        System.out.println();

        // reduce
        // 1 2 3 4 5
        // 1 + 2 + 3 + 4 + 5
        Stream.iterate(1, x -> x + 1)
                .limit(10)
                .reduce(Integer::sum)
                .orElse(0);

        Stream.iterate(1, x -> x + 1)
                .limit(10)
                .reduce(Integer::sum)
                .ifPresent(StreamAPI::printer);

        System.out.println();
        int sumI = Stream.iterate(1, x -> x + 1)
                .limit(10)
                .reduce(0, Integer::sum);
        System.out.println();

        System.out.println();
        int mul = Stream.iterate(1, x -> x + 1)
                .limit(10)
                .reduce(1, (x, y) -> x * y);
        System.out.println(mul);

        // collect -> list
        // Identity -> emptyList
        ArrayList<Integer> result = Stream.iterate(1, x -> x + 1)
                .limit(10)
                .reduce(
                        new ArrayList<>(),
                        (l, value) -> {
                            l.add(value);
                            return l;
                        },
                        (l, r) -> {
                            System.out.println(r);
                            return l;
                        }
                );
        System.out.println(result);

        // collect easy
        List<Integer> integerList = Stream.iterate(1, x -> x + 1)
                .limit(10)
                .collect(Collectors.toList()); // toList, toSet
        System.out.println(integerList);

        // collect to String
        String resultStr = Stream.iterate(1, x -> x + 1)
                .limit(10)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        System.out.println(resultStr);

        // collect toMap
        Map<Integer, Integer> map = Stream.of(1, 1, 1, 1, 2, 2, 2, 3, 4, 5, 5)
                .collect(Collectors.toMap(
                        Function.identity(),
                        value -> 1,
                        Integer::sum
                ));
        System.out.println(map);

        User u1 = User.builder()
                .id(1)
                .age(12)
                .name("Ivan")
                .build();
        User u2 = User.builder()
                .id(2)
                .age(16)
                .name("Alex")
                .build();
        User u3 = User.builder()
                .id(3)
                .age(18)
                .name("Oleg")
                .build();

        List<User> users = Arrays.asList(u1, u2, u3);

        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(
                        User::getId,
                        Function.identity()
                ));
    }

    // O(1)
    static User findById(Map<Long, User> users, long id) {
        return users.get(id);
    }

    // O(N)
    static User findById(List<User> users, long id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    static void printer(Object x) {
        System.out.print(x + " ");
    }

    static int compress(int x) {
        int sum = 0;
        while (x > 0) {
            sum += x % 10;
            x /= 10;
        }
        return sum;
    }
}
