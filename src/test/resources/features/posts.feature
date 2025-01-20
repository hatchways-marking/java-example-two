# TODO: integration tests
@posts @delete_post_teardown
Feature: Fetching Blog Posts

    Background:
        Given I am logged in as an authenticated user

    Scenario: Posts can be fetched using author's ID
        When I create a new post
        Then the post should be retrieved using "authorId"

    Scenario: Fetching posts using invalid authorIds
        When I fetch a post using an invalid "authorId"
        Then the response returns statusCode "BAD_REQUEST" with error "INVALID_AUTHORID_ERROR"

    Scenario: Distinct posts
        Given there are posts available with more than 1 userId
        When I fetch posts containing more than 1 userId
        Then the response must not contain duplicates
