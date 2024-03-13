package com.hatchways.blogposts.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;
import java.util.stream.IntStream;

public class RandomUtil {

    private RandomUtil() { }

    public static String[] tags() {
        final String[] tags = new String[randomInt(5)];
        IntStream.range(0, tags.length).forEach(i -> tags[i] = RandomStringUtils.randomAlphabetic(5));
        return tags;
    }

    public static int randomInt(final int range) {
        final Random rand = new Random();
        return rand.nextInt(range);
    }

}
