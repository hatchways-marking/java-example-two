package com.hatchways.blogposts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hatchways.blogposts.controller.data.PostData;
import com.hatchways.blogposts.controller.data.PostPatchRequestData;
import com.hatchways.blogposts.controller.data.PostResponseData;
import com.hatchways.blogposts.exception.JwtAuthenticationEntryPoint;
import com.hatchways.blogposts.model.Post;
import com.hatchways.blogposts.model.User;
import com.hatchways.blogposts.schema.PostListResponse;
import com.hatchways.blogposts.schema.PostPatchRequest;
import com.hatchways.blogposts.schema.PostPatchResponse;
import com.hatchways.blogposts.schema.PostResponse;
import com.hatchways.blogposts.service.PostService;
import com.hatchways.blogposts.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class PostControllerTest {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.fromString("asc"), "id");

    private MockMvc mockMvc;
    @Mock private PostService postService;
    @Mock private UserService userService;
    @Mock private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Mock private ModelMapper modelMapper;
    @InjectMocks private PostController postController;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
    }

    /* http -> get */

    private void validateResultActions(final ResultActions resultActions, final List<PostResponse> postResponses) throws Exception {
        resultActions.andExpect(jsonPath("$.posts", hasSize(postResponses.size())));
        int i = 0;
        for (final PostResponse postResponse : postResponses) {
            resultActions
                .andExpect(jsonPath("$.posts[%d].text".formatted(i), is(postResponse.getText())))
                .andExpect(jsonPath("$.posts[%d].tags".formatted(i), hasItems(postResponse.getTags())))
                .andExpect(jsonPath("$.posts[%d].popularity".formatted(i), equalTo(postResponse.getPopularity()), Float.class))
                .andExpect(jsonPath("$.posts[%d].reads".formatted(i), equalTo(postResponse.getReads()), Long.class))
                .andExpect(jsonPath("$.posts[%d].likes".formatted(i), is(postResponse.getLikes()), Long.class))
                .andExpect(jsonPath("$.posts[%d].id".formatted(i), greaterThan(0)));
            i++;
        }
    }

    @Test
    public void submitRequestWithoutAuthorId() throws Exception {
        mockMvc.perform(get("/api/posts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(PostController.Errors.NO_AUTHOR_IDS)));
    }

    @Test
    public void submitRequestWithSortByButWithoutAuthorId() throws Exception {
        mockMvc.perform(get("/api/posts?sortBy=likes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(PostController.Errors.NO_AUTHOR_IDS)));
    }

    @Test
    public void authorIdIs0() throws Exception {
        mockMvc.perform(get("/api/posts?authorIds=0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(PostController.Errors.INVALID_SMALLER_THAN_0)));
    }

    @Test
    public void oneOfAuthorIdIs0() throws Exception {
        mockMvc.perform(get("/api/posts?authorIds=1,2,3,0,4")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(PostController.Errors.INVALID_SMALLER_THAN_0)));
    }

    @Test
    public void authorIdIsSmallerThan0() throws Exception {
        mockMvc.perform(get("/api/posts?authorIds=-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(PostController.Errors.INVALID_SMALLER_THAN_0)));
    }

    @Test
    public void oneOfAuthorIdIsSmallerThan0() throws Exception {
        mockMvc.perform(get("/api/posts?authorIds=1,2,3,-1,4")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(PostController.Errors.INVALID_SMALLER_THAN_0)));
    }

    @Test
    public void nonNumericAuthorId() throws Exception {
        mockMvc.perform(get("/api/posts?authorIds=x")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(PostController.Errors.INVALID_AUTHOR_IDS)));
    }

    @Test
    public void oneOfAuthorIdIsNonNumeric() throws Exception {
        mockMvc.perform(get("/api/posts?authorIds=1,2,3,x,4")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(PostController.Errors.INVALID_AUTHOR_IDS)));
    }

    @Test
    public void authorIdExceedsLongMax() throws Exception {
        final String bigValue = String.valueOf(new BigDecimal(Long.MAX_VALUE).add(BigDecimal.valueOf(1L)));
        mockMvc.perform(get("/api/posts?authorIds=" + bigValue)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(PostController.Errors.EXCEED_RANGE)));
    }

    @Test
    public void authorIdExceedsLongMin() throws Exception {
        final String bigValue = String.valueOf(new BigDecimal(Long.MIN_VALUE).subtract(BigDecimal.valueOf(1L)));
        mockMvc.perform(get("/api/posts?authorIds=" + bigValue)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(PostController.Errors.EXCEED_RANGE)));
    }

    @Test
    public void getSingleRecordDefaultSort() throws Exception {
        final PostListResponse response = new PostListResponse(List.of(PostResponseData.single()));

        Mockito.when(postService.findAllByUserIds(List.of(1L), DEFAULT_SORT)).thenReturn(response);

        final ResultActions resultActions = mockMvc.perform(get("/api/posts?authorIds=1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        validateResultActions(resultActions, response.getPosts());
    }

    @Test
    public void getMultipleRecordsDefaultSort() throws Exception {
        final PostListResponse response = new PostListResponse(PostResponseData.multiple(10));

        Mockito.when(postService.findAllByUserIds(List.of(1L), DEFAULT_SORT)).thenReturn(response);

        final ResultActions resultActions = mockMvc.perform(get("/api/posts?authorIds=1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        validateResultActions(resultActions, response.getPosts());
    }

    @Test
    public void getMultipleRecordsSortByLikesAsc() throws Exception {
        final PostListResponse response = new PostListResponse(PostResponseData.multiple(10));
        response.getPosts().sort(Comparator.comparing(PostResponse::getLikes));

        final Sort sort = Sort.by(Sort.Direction.fromString("asc"), "likes");
        Mockito.when(postService.findAllByUserIds(List.of(1L), sort)).thenReturn(response);

        final ResultActions resultActions = mockMvc.perform(get("/api/posts?authorIds=1&sortBy=likes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        validateResultActions(resultActions, response.getPosts());
    }

    @Test
    public void getMultipleRecordsSortByLikesDesc() throws Exception {
        final PostListResponse response = new PostListResponse(PostResponseData.multiple(10));
        response.getPosts().sort(Comparator.comparing(PostResponse::getLikes).reversed());

        final Sort sort = Sort.by(Sort.Direction.fromString("desc"), "likes");
        Mockito.when(postService.findAllByUserIds(List.of(1L), sort)).thenReturn(response);

        final ResultActions resultActions = mockMvc.perform(get("/api/posts?authorIds=1&sortBy=likes&direction=desc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        validateResultActions(resultActions, response.getPosts());
    }

    @Test
    public void getMultipleRecordsSortByPopularityAsc() throws Exception {
        final PostListResponse response = new PostListResponse(PostResponseData.multiple(10));
        response.getPosts().sort(Comparator.comparing(PostResponse::getPopularity));

        final Sort sort = Sort.by(Sort.Direction.fromString("asc"), "popularity");
        Mockito.when(postService.findAllByUserIds(List.of(1L), sort)).thenReturn(response);

        final ResultActions resultActions = mockMvc.perform(get("/api/posts?authorIds=1&sortBy=popularity")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        validateResultActions(resultActions, response.getPosts());
    }

    @Test
    public void getMultipleRecordsSortByPopularityDesc() throws Exception {
        final PostListResponse response = new PostListResponse(PostResponseData.multiple(10));
        response.getPosts().sort(Comparator.comparing(PostResponse::getPopularity).reversed());

        final Sort sort = Sort.by(Sort.Direction.fromString("desc"), "popularity");
        Mockito.when(postService.findAllByUserIds(List.of(1L), sort)).thenReturn(response);

        final ResultActions resultActions = mockMvc.perform(get("/api/posts?authorIds=1&sortBy=popularity&direction=desc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        validateResultActions(resultActions, response.getPosts());
    }

    @Test
    public void sortByUnknownField() throws Exception {
        final PostListResponse response = new PostListResponse(PostResponseData.multiple(10));
        Mockito.when(postService.findAllByUserIds(List.of(1L), DEFAULT_SORT)).thenReturn(response);

        mockMvc.perform(get("/api/posts?authorIds=1&sortBy=unknownField")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(PostController.Errors.INVALID_SORT_BY)));
    }

    @Test
    public void sortByInvalidDirection() throws Exception {
        final PostListResponse response = new PostListResponse(PostResponseData.multiple(10));
        Mockito.when(postService.findAllByUserIds(List.of(1L), DEFAULT_SORT)).thenReturn(response);

        mockMvc.perform(get("/api/posts?authorIds=1&direction=none")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    /* http -> patch */

    @Test
    public void patchHappyFlow() throws Exception {
        final Post post = PostData.get();
        final User usr = new User("user", null);
        final PostPatchRequest postPatchRequest = PostPatchRequestData.get();
        final Post modifiedPost = new Post(postPatchRequest.getText(),
                String.join(",", postPatchRequest.getTags()),
                post.getLikes(), post.getReads(), post.getPopularity());
        modifiedPost.setId(post.getId());
        modifiedPost.setAuthorIds(postPatchRequest.getAuthorIds());
        final PostPatchResponse response = new PostPatchResponse(modifiedPost);
        final Principal principal = mock(Principal.class);

        Mockito.when(principal.getName()).thenReturn(usr.getUsername());
        Mockito.when(postService.findById(1L)).thenReturn(Optional.of(post));
        Mockito.when(userService.findByUsername(usr.getUsername())).thenReturn(usr);
        Mockito.when(postService.patch(ArgumentMatchers.any(Post.class),
                ArgumentMatchers.any(PostPatchRequest.class), ArgumentMatchers.any(User.class))).thenReturn(response);

        mockMvc.perform(patch("/api/posts/1")
                        .principal(principal)
                .content(new ObjectMapper().writeValueAsString(postPatchRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(modifiedPost.getText())))
                .andExpect(jsonPath("$.tags", hasItems(modifiedPost.getTags())))
                .andExpect(jsonPath("$.authorIds", hasItems(modifiedPost.getAuthorIds())));
    }

    @Test
    public void patchingPostNotFound() throws Exception {
        Mockito.when(postService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/posts/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
