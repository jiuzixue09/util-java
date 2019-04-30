package com.dave.util;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ListUtil {

    public static Collection<List<Integer>> partitionIntegerListBasedOnSize(List<Integer> inputList, int size) {
        return inputList.stream()
                .collect(Collectors.groupingBy(s -> (s-1)/size))
                .values();
    }
    public static <T> Collection<List<T>> partitionBasedOnSize(List<T> inputList, int size) {
        final AtomicInteger counter = new AtomicInteger(0);
        return inputList.stream()
                .collect(Collectors.groupingBy(s -> counter.getAndIncrement()/size))
                .values();
    }
    public static <T> Collection<List<T>> partitionBasedOnCondition(List<T> inputList, Predicate<T> condition) {
        return inputList.stream().collect(Collectors.partitioningBy(s-> (condition.test(s)))).values();
    }
}
