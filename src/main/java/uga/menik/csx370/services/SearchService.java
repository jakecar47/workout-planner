package uga.menik.csx370.services;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uga.menik.csx370.models.ExpandedPost;
import uga.menik.csx370.models.User;

@Service
public class SearchService {
    private final DataSource dataSource;
    private final UserService userService;

    @Autowired
    public SearchService(DataSource dataSource, UserService userService) {
        this.dataSource = dataSource;
        this.userService = userService;
    }

    public List<ExpandedPost> findExpandedPostsByHashtags(String[] tags) {
        List<ExpandedPost> results = new ArrayList<>();
        if (tags == null || tags.length == 0) return results;

        User loggedUser = userService.getLoggedInUser();
        int authUserId = Integer.parseInt(loggedUser.getUserId());

        StringBuilder in = new StringBuilder();
        for (int i = 0; i < tags.length; i++) {
            if (i > 0) in.append(",");
            in.append("?");
        }

        final String sqlPostIds =
            "SELECT h.postId " +
            "FROM hashtag h " +
            "WHERE h.content IN (" + in + ") " +
            "GROUP BY h.postId " +
            "HAVING COUNT(DISTINCT h.content) = ?";

        final String sqlPost =
            "SELECT p.postId, p.postText, p.postDate, u.userId, u.firstName, u.lastName " +
            "FROM posts p JOIN `user` u ON p.userId = u.userId " +
            "WHERE p.postId = ?";

        final String sqlLikeCount     = "SELECT COUNT(*) AS cnt FROM hearts    WHERE postID = ?";
        final String sqlCommentCount  = "SELECT COUNT(*) AS cnt FROM comments  WHERE postId = ?";
        final String sqlIsHearted     = "SELECT COUNT(*) AS cnt FROM hearts    WHERE postID = ? AND userID = ?";
        final String sqlIsBookmarked  = "SELECT COUNT(*) AS cnt FROM bookmarks WHERE postID = ? AND userID = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement psIds = conn.prepareStatement(sqlPostIds)) {

            int idx = 1;
            for (String t : tags) psIds.setString(idx++, t);
            psIds.setInt(idx, tags.length);

            List<Integer> postIds = new ArrayList<>();
            try (ResultSet rs = psIds.executeQuery()) {
                while (rs.next()) postIds.add(rs.getInt("postId"));
            }
            if (postIds.isEmpty()) return results;

            postIds.sort(Collections.reverseOrder());

            for (Integer pid : postIds) {
                try (PreparedStatement psPost = conn.prepareStatement(sqlPost)) {
                    psPost.setInt(1, pid);
                    try (ResultSet rs = psPost.executeQuery()) {
                        if (!rs.next()) continue;

                        Timestamp ts = rs.getTimestamp("postDate");
                        String formatted = new SimpleDateFormat("MMM dd, yyyy, hh:mm a").format(ts);

                        User author = new User(
                            rs.getString("userId"),
                            rs.getString("firstName"),
                            rs.getString("lastName")
                        );

                        int likeCount = 0, commentCount = 0;
                        boolean hearted = false, bookmarked = false;

                        try (PreparedStatement st = conn.prepareStatement(sqlLikeCount)) {
                            st.setInt(1, pid);
                            try (ResultSet r = st.executeQuery()) { if (r.next()) likeCount = r.getInt("cnt"); }
                        }
                        try (PreparedStatement st = conn.prepareStatement(sqlCommentCount)) {
                            st.setInt(1, pid);
                            try (ResultSet r = st.executeQuery()) { if (r.next()) commentCount = r.getInt("cnt"); }
                        }
                        try (PreparedStatement st = conn.prepareStatement(sqlIsHearted)) {
                            st.setInt(1, pid);
                            st.setInt(2, authUserId);
                            try (ResultSet r = st.executeQuery()) { hearted = r.next() && r.getInt("cnt") > 0; }
                        }
                        try (PreparedStatement st = conn.prepareStatement(sqlIsBookmarked)) {
                            st.setInt(1, pid);
                            st.setInt(2, authUserId);
                            try (ResultSet r = st.executeQuery()) { bookmarked = r.next() && r.getInt("cnt") > 0; }
                        }

                        ExpandedPost ep = new ExpandedPost(
                            rs.getString("postId"),
                            rs.getString("postText"),
                            formatted,
                            author,
                            likeCount,
                            commentCount,
                            hearted,
                            bookmarked,
                            new ArrayList<>() 
                        );

                        ep.setShowComments(true);
                        results.add(ep);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in findExpandedPostsByHashtags: " + e.getMessage());
        }
        return results;
    }
}
