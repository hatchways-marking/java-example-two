package com.hatchways.blogposts.controller;

import static com.hatchways.blogposts.config.SecurityConfig.AUTHENTICATION_HEADER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hatchways.blogposts.BlogPostsApplication;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = BlogPostsApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.properties")
@Import(ControllerTestConfiguration.class)
@Sql(
    scripts = {
        "/data.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(
    scripts = {
        "/cleanup.sql"
    },
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class HiddenPostControllerTest {
    @Autowired MockMvc mockMvc;

    @Autowired TestUtil testUtil;

    @Value("classpath:hiddenTests/get/post_single_author.json")
    Resource singlePostsJsonFile;

    @Value("classpath:hiddenTests/get/post_multiple_authors.json")
    Resource multiplePostsJsonFile;

    @Value("classpath:hiddenTests/get/post_sort_by_read.json")
    Resource sortByReadJsonFile;

    @Value("classpath:hiddenTests/get/post_sort_by_read_direction1.json")
    Resource sortByReadDirectionJsonFile1;

    @Value("classpath:hiddenTests/get/post_sort_by_read_direction2.json")
    Resource sortByReadDirectionJsonFile2;

    @Value("classpath:hiddenTests/get/post_sort_by_read_direction3.json")
    Resource sortByReadDirectionJsonFile3;

    @Value("classpath:hiddenTests/patch/post_update_text_req.json")
    Resource updateTextRequest;
    @Value("classpath:hiddenTests/patch/post_update_text_resp.json")
    Resource updateTextResponse;

    @Value("classpath:hiddenTests/patch/post_update_tags_req.json")
    Resource updateTagsRequest;
    @Value("classpath:hiddenTests/patch/post_update_tags_resp.json")
    Resource updateTagsResponse;

    @Value("classpath:hiddenTests/patch/post_add_an_author_req.json")
    Resource addAnAuthorRequest;
    @Value("classpath:hiddenTests/patch/post_add_an_author_resp.json")
    Resource addAnAuthorResponse;

    @Value("classpath:hiddenTests/patch/post_remove_an_author_req.json")
    Resource removeAnAuthorRequest;
    @Value("classpath:hiddenTests/patch/post_remove_an_author_resp.json")
    Resource removeAnAuthorResponse;

    @Value("classpath:hiddenTests/patch/post_multiple_fields_req.json")
    Resource updateMultipleFieldsRequest;
    @Value("classpath:hiddenTests/patch/post_multiple_fields_resp.json")
    Resource updateMultipleFieldsResponse;

    @Value("classpath:hiddenTests/patch/post_authors_id_not_array_req.json")
    Resource authorsIdNotArrayRequest;
    
    @Value("classpath:hiddenTests/patch/post_tags_not_array_req.json")
    Resource tagsNotArrayRequest;

    @Value("classpath:hiddenTests/patch/post_not_exist_req.json")
    Resource doesNotExistRequest;

    @Value("classpath:hiddenTests/patch/post_author_does_not_own_the_post_req.json")
    Resource doesNotOwnThePostRequest;

    private void assertPatchResponse(int postId, String body, String expected, String authorName) throws Exception{
        String token = testUtil.getUserToken(authorName);
        mockMvc
            .perform(patch("/api/posts/"+postId).header(AUTHENTICATION_HEADER, token).contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(expected))
            .andReturn();
    }

    private void assertPatchError(int postId, String body, String authorName, int useCase) throws Exception {
        String token = testUtil.getUserToken(authorName);
        MvcResult result = mockMvc
            .perform(patch("/api/posts/"+postId).header(AUTHENTICATION_HEADER, token).contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().is4xxClientError())
            .andReturn();

        String content = result.getResponse().getContentAsString();
        int statusCode = result.getResponse().getStatus();

        switch (useCase) {
            case 1:
                //NOT FOUND
                assertNotEquals(content,"The route is not defined"); 
                assertEquals(404,statusCode); 
                break;
            case 2: 
                //UNAUTHORIZED
                assertEquals(403,statusCode); 
            default:
                //BAD REQUEST
                assertNotEquals(405,statusCode); 
                assertFalse(statusCode == 404 && content.equals("The route is not defined"));
                break;
        }
    }

    private void assertGetResponse(String postsData, String query) throws Exception {
        String token = testUtil.getUserToken("santiago");
        mockMvc
            .perform(get(query).header(AUTHENTICATION_HEADER, token))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(postsData))
            .andReturn();
    }

    private void assertGetBadRequest(String query) throws Exception {
        String token = testUtil.getUserToken("santiago");
        MvcResult result = mockMvc
            .perform(get(query).header(AUTHENTICATION_HEADER, token))
            .andExpect(status().is4xxClientError())
            .andReturn();
        
        String content = result.getResponse().getContentAsString();
        int statusCode = result.getResponse().getStatus();

        assertNotEquals(405,statusCode); 
        assertFalse(statusCode == 404 && content.equals("The route is not defined"));
    }

    @Test
    @Tag("Security")
    public void testEmptyAccessToken() throws Exception {
        mockMvc
            .perform(get("/api/posts?authorIds=3&direction=desc"))
            .andExpect(status().is(401));
    }

    @Test
    @Tag("Security")
    public void testInvalidAccessToken() throws Exception {
        mockMvc
            .perform(get("/api/posts?authorIds=3&direction=desc").header(AUTHENTICATION_HEADER, "123"))
            .andExpect(status().is(401));
    }

    @Test
    @Tag("GetPosts")
    public void testGetPostsForSingleAuthor() throws Exception {
        String postsData = new String(singlePostsJsonFile.getInputStream().readAllBytes());
        assertGetResponse(postsData, "/api/posts?authorIds=5");
    }

    @Test
    @Tag("GetPosts")
    public void testGetPostsForMultipleAuthors() throws Exception {
        String postsData = new String(multiplePostsJsonFile.getInputStream().readAllBytes());
        assertGetResponse(postsData, "/api/posts?authorIds=4,5");
    }

    @Test
    @Tag("GetPosts")
    public void testSortByReads() throws Exception {
        String postsData = new String(sortByReadJsonFile.getInputStream().readAllBytes());
        assertGetResponse(postsData, "/api/posts?authorIds=4&sortBy=reads");
    }

    @Test
    @Tag("GetPosts")
    public void testSortByReadsDirectionOneAuthor() throws Exception {
        String postsData = new String(sortByReadDirectionJsonFile1.getInputStream().readAllBytes());
        assertGetResponse(postsData, "/api/posts?authorIds=3&sortBy=reads&direction=desc");
    }

    @Test
    @Tag("GetPosts")
    public void testSortByReadsDirectionTwoAuthors() throws Exception {
        String postsData = new String(sortByReadDirectionJsonFile2.getInputStream().readAllBytes());
        assertGetResponse(postsData, "/api/posts?authorIds=3,4&sortBy=reads&direction=desc");
    }

    @Test
    @Tag("GetPosts")
    public void testSortByDefaultDirectionDesc() throws Exception {
        String postsData = new String(sortByReadDirectionJsonFile3.getInputStream().readAllBytes());
        assertGetResponse(postsData, "/api/posts?authorIds=3&direction=desc");
    }

    @Test
    @Tag("GetPosts")
    public void testInvalidNoAuthorId() throws Exception {
        assertGetBadRequest("/api/posts?sortBy=id");
    }

    @Test
    @Tag("GetPosts")
    public void testInvalidAuthorIdsParam() throws Exception {
        assertGetBadRequest("/api/posts?authorIds=[1,2]");
    }

    @Test
    @Tag("GetPosts")
    public void testInvalidSortByParams() throws Exception {
        assertGetBadRequest("/api/posts?authorIds=3&sortBy=nothing&direction=desc");
    }

    @Test
    @Tag("GetPosts")
    public void testInvalidSortByDirection() throws Exception {
        assertGetBadRequest("/api/posts?authorIds=3&sortBy=reads&direction=up");
    }

    @Test
    @Tag("UpdatePost")
    public void testUpdateText() throws Exception {
        String request = new String(updateTextRequest.getInputStream().readAllBytes());
        String response = new String(updateTextResponse.getInputStream().readAllBytes());
        assertPatchResponse(9,request,response,"cheng");
    }

    @Test
    @Tag("UpdatePost")
    public void testUpdateTags() throws Exception {
        String request = new String(updateTagsRequest.getInputStream().readAllBytes());
        String response = new String(updateTagsResponse.getInputStream().readAllBytes());
        assertPatchResponse(9,request,response,"cheng");
    }

    @Test
    @Tag("UpdatePost")
    public void testAddAnAuthor() throws Exception {
        String request = new String(addAnAuthorRequest.getInputStream().readAllBytes());
        String response = new String(addAnAuthorResponse.getInputStream().readAllBytes());
        assertPatchResponse(9,request,response,"cheng");
    }

    @Test
    @Tag("UpdatePost")
    public void testRemoveAnAuthor() throws Exception {
        String request = new String(removeAnAuthorRequest.getInputStream().readAllBytes());
        String response = new String(removeAnAuthorResponse.getInputStream().readAllBytes());
        assertPatchResponse(5,request,response,"cheng");
    }

    @Test
    @Tag("UpdatePost")
    public void testUpdateMultipleFields() throws Exception {
        String request = new String(updateMultipleFieldsRequest.getInputStream().readAllBytes());
        String response = new String(updateMultipleFieldsRequest.getInputStream().readAllBytes());
        assertPatchResponse(1,request,response,"thomas");
    }

    @Test
    @Tag("UpdatePost")
    public void testAuthorsIdNotArray() throws Exception {
        String request = new String(authorsIdNotArrayRequest.getInputStream().readAllBytes());
        assertPatchError(2, request, "santiago",3);
    }

    @Test
    @Tag("UpdatePost")
    public void testTagsNotArray() throws Exception {
        String request = new String(tagsNotArrayRequest.getInputStream().readAllBytes());
        assertPatchError(2, request, "santiago",3);
    }

    @Test
    @Tag("UpdatePost")
    public void testDoesNotExist() throws Exception {
        String request = new String(doesNotExistRequest.getInputStream().readAllBytes());
        assertPatchError(21, request, "santiago",1);
    }

    @Test
    @Tag("UpdatePost")
    public void testAuthorDoesNotOwnThePost() throws Exception {
        String request = new String(doesNotOwnThePostRequest.getInputStream().readAllBytes());
        assertPatchError(1, request, "ashanti",2);
    }
}