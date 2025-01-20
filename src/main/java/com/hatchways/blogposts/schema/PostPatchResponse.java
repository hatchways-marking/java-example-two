package com.hatchways.blogposts.schema;

import com.hatchways.blogposts.model.Post;

public class PostPatchResponse extends PostResponse {

    private Integer[] authorIds;

    public PostPatchResponse(final Post post) {
        super(post);
        authorIds = post.getAuthorIds();
    }

    public Integer[] getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(Integer[] authorIds) {
        this.authorIds = authorIds;
    }

}
