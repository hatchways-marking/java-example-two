package com.hatchways.blogposts.schema;

import java.util.List;

public class PostListResponse {

    private List<PostResponse> posts;

    public PostListResponse(final List<PostResponse> posts) {
        this.posts = posts;
    }

    public List<PostResponse> getPosts() {
        return posts;
    }

    public void setPosts(final List<PostResponse> posts) {
        this.posts = posts;
    }

}
