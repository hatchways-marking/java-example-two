package com.hatchways.blogposts.controller.data;

import com.hatchways.blogposts.model.Post;
import com.hatchways.blogposts.schema.PostResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class PostResponseData {

    public static PostResponse single() {
        final Post post = PostData.get();
        return new PostResponse(post);
    }

    public static List<PostResponse> multiple(final int count) {
        final List<PostResponse> posts = new ArrayList<>(count);
        IntStream.range(0, count).forEach(i -> posts.add(single()));
        return posts;
    }

}
