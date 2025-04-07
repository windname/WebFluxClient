package com.vg.webflux.example.lessons;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamsMapsFlatMap {
    public static void main(String[] args) {

        List.of("apple", "banana", "cherry")
                .stream().map(s -> s.toUpperCase())
                .forEach(System.out::println);

        List<String> rez = List.of("apple", "banana", "cherry")
                .stream().map(s -> s.toUpperCase())
                .collect(Collectors.toList());
        System.out.println(rez);

        List<Integer> rez2 = List.of("apple", "banana", "cherry")
                .stream().map(s -> s.length())
                .toList();
        System.out.println(rez2);

        var rez3 = List.of(1, 2, 3, 4).stream().map(n -> n*n).toList();
        System.out.println(rez3);

        var ex5 = List.of("apple", "banana", "cherry")
                .stream().flatMap(s -> s.chars().mapToObj(c -> (char)c)).toList();
        System.out.println(ex5);

        var ex6 = List.of(List.of(1, 2), List.of(3, 4)).stream().flatMap(l -> l.stream()).toList();
        System.out.println(ex6);

        Map<String, List<Integer>> map2 = Map.of("A", List.of(1,2), "B", List.of(2,3));
        var ex7 = map2.values().stream().flatMap(List::stream).toList();
        System.out.println(ex7);

        int sum = List.of(1, 2, 3, 4).stream().reduce((s,i) -> s+i).orElseGet(() -> 0);
        int sum2 = List.of(1, 2, 3, 4).stream().mapToInt(i -> i).sum();
        int sum3 = IntStream.of(1,2,3).reduce(0, Integer::sum);
        int max = List.of(1, 2, 3, 4).stream().mapToInt(i -> i).max().orElse(0);
        max = List.of(1, 2, 3, 4).stream().max(Integer::compare).orElse(0);
        System.out.println(sum + " " + sum2 + " " + max + " " +sum3);

        var ex8 = List.of("apple", "banana", "cherry").stream().filter(s -> s.length() == 6).count();
        System.out.println("count" + ex8);

        List<Integer> distinct = List.of(1, 2, 2, 4).stream().distinct().toList();

        var ex10 = List.of("apple", "banana", "cherry").stream()
                .reduce((s1, s2) -> s1.concat(s2)).orElseGet(() -> "");
        String joined =  List.of("apple", "banana", "cherry")
                .stream().collect(Collectors.joining(", "));
        System.out.println("concat: " + ex10);

        Map<Integer, List<String>> byLength = List.of("apple", "banana", "cherry")
                .stream()
                .collect(Collectors.groupingBy(String::length));
        System.out.println("groupping: " + byLength );
        var ex11 = List.of("apple", "banana", "cherry")
                .stream().collect(Collectors.toMap(s->s, s->s.length()));
        System.out.println(ex11);
//                .reduce(new HashMap<>(),
//                        (map, word) -> {
//                            map.put(word, word.length());
//                            return map;}
//                        ,
//                        (m1, m2) -> {
//                            m1.putAll(m2);
//                            return m1;
//                        }
//                );

        double avg = List.of(1, 2, 2, 4).stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
        avg = IntStream.of(1,2,3)
                .average()
                .orElse(0.0);

        var ex12 = List.of("apple", "banana", "cherry")
                .stream().sorted(Comparator.reverseOrder()).toList();
        System.out.println("sorted:" + ex12);

        var ex13 = Map.of("even",new ArrayList<Integer>(), "odds", new ArrayList<Integer>());
        IntStream.of(1,2,3).forEach(n -> {
            switch (n %2 ) {
                case 0: ex13.get("odds").add(n); return;
                case 1: ex13.get("even").add(n); return;
                default: throw new RuntimeException("wrong number");
            }
        });
        System.out.println(ex13);
        Stream.of(1,2,3).collect(Collectors.partitioningBy(n -> n % 2 == 0 ));

        Stream.of("one", "two", "one").collect(Collectors.toSet());
        var ex14 = Stream.of("apple", "strawebrry", "banana")
                .sorted((s1, s2) -> s1.length()-s2.length()).toList();
        System.out.println("ordered words:" + ex14);


    }
}
