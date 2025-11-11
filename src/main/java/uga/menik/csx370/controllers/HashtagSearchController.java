/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.csx370.controllers;

import java.util.List;
import java.sql.*;

import javax.sql.DataSource;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.models.Post;
import uga.menik.csx370.services.PostService;

/**
 * Handles /hashtagsearch URL and possibly others.
 * At this point no other URLs.
 */
@Controller
@RequestMapping("/hashtagsearch")
public class HashtagSearchController {

    private final DataSource dataSource;
    private final PostService postService;
    
    @Autowired
    public HashtagSearchController(DataSource dataSource, PostService postService) {
        this.dataSource = dataSource;
        this.postService = postService;
    }

    /**
     * Handles /hashtagsearch?hashtags=%23amazing+%23fireworks
     */
    @GetMapping
    public ModelAndView webpage(@RequestParam(name = "hashtags") String hashtags) {
        System.out.println("User is searching: " + hashtags);

        // Split hashtags into list (remove the '#' prefix)
        ArrayList<String> hashtagList = new ArrayList<>();
        String[] tags = hashtags.split(" ");
        for (String tag : tags) {
            hashtagList.add(tag.substring(1));
        }

        // Build SQL query dynamically
        String sql = "SELECT postID FROM hashtag WHERE content IN ('" + hashtagList.get(0) + "'";
        for (int i = 1; i < hashtagList.size(); i++) {
            sql += ", '" + hashtagList.get(i) + "'";
        }
        sql += ") GROUP BY postID HAVING COUNT(DISTINCT content) = " + hashtagList.size();

        // Execute SQL and filter posts
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            ArrayList<Integer> postIds = new ArrayList<>();
            while (rs.next()) {
                postIds.add(rs.getInt("postID"));
            }

            ModelAndView mv = new ModelAndView("posts_page");

            List<Post> posts = postService.getAllPosts();
            List<Post> filteredPosts = new ArrayList<>();

            for (Post post : posts) {
                if (postIds.contains(Integer.parseInt(post.getPostId()))) {
                    filteredPosts.add(post);
                }
            }

            mv.addObject("posts", filteredPosts);

            // Show "no content" message if empty
            if (filteredPosts.isEmpty()) {
                mv.addObject("isNoContent", true);
            }

            return mv;

        } catch (SQLException e) {
            // Handle SQL errors properly
            String errorMessage = "An error occured!";
            ModelAndView mv = new ModelAndView("posts_page");
            mv.addObject("errorMessage", errorMessage);
            return mv;
        }
    }
}
    
