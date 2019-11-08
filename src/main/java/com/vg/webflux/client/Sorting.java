package com.vg.webflux.client;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/7/2019
 */


public class Sorting {
    public static void main(String[] args) {
        Animal a1 = new Animal("lion", 100);
        Animal a2 = new Animal("tiger", 80);
        Animal a3 = new Animal("elefant", 180);

        List<Animal> sortedList = Arrays.asList(a1, a2, a3).stream()
                .sorted()
                .collect(Collectors.toList());

        System.out.println(sortedList);

        List<Animal> sortedNameList = Arrays.asList(a1, a2).stream()
                .sorted(new AnimalNameCompare())
                .collect(Collectors.toList());

        System.out.println(sortedList);


    }
}

class Animal implements Comparable<Animal>{

    String name;
    int weight;

    public Animal(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }
    @Override
    public int compareTo( final Animal o) {
        return Integer.compare(this.weight, o.weight);
    }

    @Override
    public String toString() {
        return name;
    }
}

class AnimalNameCompare implements Comparator<Animal> {
    @Override
    public int compare(Animal o1, Animal o2) {
        return o1.name.compareTo(o2.name);
    }
}


