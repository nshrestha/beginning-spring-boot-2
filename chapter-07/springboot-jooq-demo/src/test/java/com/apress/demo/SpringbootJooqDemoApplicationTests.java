package com.apress.demo;


import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.apress.demo.entities.Comment;
import com.apress.demo.entities.Post;
import com.apress.demo.services.PostService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Siva
 *
 */
@SpringBootTest//(classes = SpringbootJooqDemoApplication.class)
public class SpringbootJooqDemoApplicationTests
{

	@Autowired
    private PostService postService;
     
    @Test
    public void findAllPosts()  {
        List<Post> posts = postService.getAllPosts();
        assertNotNull(posts);
        assertTrue(!posts.isEmpty());
        for (Post post : posts)
        {
            System.err.println(post);
        }
    }
     
    @Test
    public void findPostById()  {
        Post post = postService.getPost(1);
        assertNotNull(post);
        System.out.println(post);
        List<Comment> comments = post.getComments();
        System.out.println(comments);
         
    }
     
    @Test
    public void createPost() {
        Post post = new Post(0, "My new Post", 
                            "This is my new test post", 
                            new Timestamp(System.currentTimeMillis()));
        Post savedPost = postService.createPost(post);
        Post newPost = postService.getPost(savedPost.getId());
        assertEquals("My new Post", newPost.getTitle());
        assertEquals("This is my new test post", newPost.getContent());
    }
     
    @Test
    public void createComment() {
        Integer postId = 1;
        Comment comment = new Comment(0, postId, "User4", 
                                "user4@gmail.com", "This is my new comment on post1", 
                                new Timestamp(System.currentTimeMillis()));
        Comment savedComment = postService.createComment(comment);
        Post post = postService.getPost(postId);
        List<Comment> comments = post.getComments();
        assertNotNull(comments);
        for (Comment comm : comments)
        {
            if(savedComment.getId() == comm.getId()){
                assertEquals("User4", comm.getName());
                assertEquals("user4@gmail.com", comm.getEmail());
                assertEquals("This is my new comment on post1", comm.getContent());
            }
        }
         
    }

}
