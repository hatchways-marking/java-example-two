package com.hatchways.blogposts.schema;

public class PostPatchRequest extends PostRequest {

    private Integer[] authorIds;

    public Integer[] getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(final Integer[] authorIds) {
        this.authorIds = authorIds;
    }
}
