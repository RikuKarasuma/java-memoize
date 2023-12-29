package org.example;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        System.out.println("-----------------------------------------------------------");
        final var to_generate_from = new Integer[][]
        {
            { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 },
            { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 },
            { 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000 },
        };
        for (final var generation_sub : to_generate_from)
            runTwice(generation_sub);
    }

    private static void runTwice(final Integer[] to_generate_from) {
        final var memoized_gen_function = memoize(Main::generateSubsetsFromSet);

        Integer[][] generated = null;
        final var time_before_1 = System.currentTimeMillis();
        generated = memoized_gen_function.apply(to_generate_from, Main::compareSortedCombosLexico);
        final var time_after_1 = System.currentTimeMillis();
        printTime(time_before_1, time_after_1);

        final var time_before_2 = System.currentTimeMillis();


        generated = memoized_gen_function.apply(to_generate_from, Main::compareSortedCombosLexico);
        final var time_after_2 = System.currentTimeMillis();
        printTime(time_before_2, time_after_2);
        System.out.println("-----------------------------------------------------------");
    }

    private static void printTime(long time_then, long time_now) {
        System.out.println("Time it took: " + ((time_now - time_then) + "ms"));
    }

    private static void printAll(Integer[][] subsets) {
        Arrays.stream(subsets)
                .forEach(matching -> {
                    for (Integer integer : matching)
                        System.out.print(integer + ", ");

                    System.out.println();
                });
    }

    private static BiFunction<Integer[], Comparator<Integer[]>, Integer[][]> memoize(BiFunction<Integer[], Comparator<Integer[]>, Integer[][]> func) {
        final var cache = new HashMap<String, Integer[][]>();

        return (key, sorter) -> {
            final String joined_key = Arrays.stream(key).map(i ->  i + "-").collect(Collectors.joining());

            Integer[][] cached_value = cache.get(joined_key);
            if (Objects.isNull(cached_value)) {
                final var generated_value_to_cache = func.apply(key, sorter);
                cache.put(joined_key, generated_value_to_cache);
                cached_value = generated_value_to_cache;
            }

            return cached_value;
        };
    }


    /**
     * Binary counting subset generator.
     */
    private static Integer[][] generateSubsetsFromSet(final Integer[] subset_to_generate_combos_from,
                                                      final Comparator<Integer[]> set_sorter) {
        // Binary counting method of generating subsets.

        final int subset_length = subset_to_generate_combos_from.length;
        // Total combination length.
        final int generation_length = (int) Math.pow(2, subset_length);
        final var subsets = new int[generation_length];
        final var returnedTreeSet = new TreeSet<>(set_sorter);

        for ( int i = 0; i < generation_length; i ++) {

            int b = 1;
            // Init this index.
            subsets[i] = 0;
            // grab index for inner generation
            int number = i;
            while (number > 0) {
                // Create our unique binary for this index.
                subsets[i] += (number % 2) * b;
                number = number / 2;
                b = b * 10;
            }

            // Reverse the binary and store the combos in a usable
            // integer array.
            final var unique_combo_to_store_list = new TreeSet<Integer>();
            for ( int x = 0; x < subset_length; x ++) {

                // If we have a bit set. Confirm presence of number.
                if (subsets[i] % 10 == 1)
                    unique_combo_to_store_list.add(subset_to_generate_combos_from[x]);

                subsets[i] = subsets[i] / subset_to_generate_combos_from.length;
            }

            // use distinct size to create new resized array.
            returnedTreeSet.add(unique_combo_to_store_list.toArray(new Integer[0]));
        }

        return returnedTreeSet.toArray(new Integer[0][]);
    }

    // Different methods of sorting our generated subsets within a TreeSet.
    private static int compareSortedCombosSummed(Integer[] c1, Integer[] c2) {
        final var c1Sum = Arrays.stream(c1).mapToInt(i -> i).sum();
        final var c2Sum = Arrays.stream(c2).mapToInt(i -> i).sum();

        return Integer.compare(c1Sum, c2Sum);
    }

    private static int compareSortedCombosLexico(Integer[] c1, Integer[] c2) {
        final var c1Sum = Arrays.stream(c1).map(i -> "" + i).collect(Collectors.joining());
        final var c2Sum = Arrays.stream(c2).map(i -> "" + i).collect(Collectors.joining());

        return c1Sum.compareTo(c2Sum);
    }


    private static int compareSortedCombosStringLength(Integer[] c1, Integer[] c2) {
        final var c1Sum = Arrays.stream(c1).map(i -> "" + i).collect(Collectors.joining()).length();
        final var c2Sum = Arrays.stream(c2).map(i -> "" + i).collect(Collectors.joining()).length();

        return Integer.compare(c1Sum, c2Sum);
    }
}