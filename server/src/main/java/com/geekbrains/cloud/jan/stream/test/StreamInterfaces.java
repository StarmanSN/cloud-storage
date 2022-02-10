package com.geekbrains.cloud.jan.stream.test;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class StreamInterfaces {
    public static void main(String[] args) {

        // forEach, - terminal (void)
        // peek    - stream
        Consumer<Object> printer = System.out::println;
        printer.accept("Hello world");

        //filter, - terminal
        // anyMatch, allMatch, noneMatch,  - stream (boolean)
        Predicate<Integer> isOdd = x -> x % 2 == 1;
        printer.accept(isOdd.test(12));

        //mao, flatMap  -  stream<R> -> stream<U>
        Function<Integer, String> toStr = String::valueOf;
        printer.accept(toStr.apply(123));

        // collect  - terminal (Collection)
        Supplier<Integer> one = () -> 1;
        one.get();

        Stream.of(1, 2, 3, 4, 5)
                .filter(x -> x % 2 == 0)
                .map(String::valueOf)
                .forEach(System.out::println);
    }
}
