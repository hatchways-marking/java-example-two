package com.hatchways.blogposts.controller.data;

import com.hatchways.blogposts.schema.PostPatchRequest;
import com.hatchways.blogposts.util.RandomUtil;
import org.apache.commons.lang3.RandomStringUtils;

public class PostPatchRequestData {

    public static PostPatchRequest get() {
        final PostPatchRequest post = new PostPatchRequest();
        post.setAuthorIds(new Integer[] { 1 });
        post.setText(RandomStringUtils.randomAlphabetic(100));
        post.setTags(RandomUtil.tags());
        return post;
    }

}
