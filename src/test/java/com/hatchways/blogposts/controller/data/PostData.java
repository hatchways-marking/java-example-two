package com.hatchways.blogposts.controller.data;

import com.hatchways.blogposts.model.Post;
import com.hatchways.blogposts.util.RandomUtil;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.atomic.AtomicLong;

public class PostData {

    private static final AtomicLong ID = new AtomicLong(0L);

    public static Post get() {
        final Post post = new Post();
        post.setId(ID.incrementAndGet());
        post.setText(RandomStringUtils.randomAlphabetic(100));
        post.setTags(RandomUtil.tags());
        post.setPopularity(popularity());
        post.setReads((long) RandomUtil.randomInt(1_000));
        post.setLikes((long) RandomUtil.randomInt(1_000));
        return post;
    }

    private static float popularity() {
        return (float) (Math.round(Math.random() * 100.0) / 100.0);
    }

}
