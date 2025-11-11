package uga.menik.csx370.services;

import java.sql.*;
import java.text.SimpleDateFormat;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import uga.menik.csx370.models.User;
import uga.menik.csx370.models.ExpandedPost;
import uga.menik.csx370.models.Post;
import uga.menik.csx370.models.Comment;

@Service
@SessionScope
public class PostService {
    private final DataSource dataSource;
    private final User loggedUser;
    private int authUserId;

    @Autowired
    public PostService(DataSource dataSource, UserService userService) {
        this.dataSource = dataSource;
        this.loggedUser = userService.getLoggedInUser();
        this.authUserId = Integer.parseInt(loggedUser.getUserId());
    }


    public boolean newComment(int postId, String content) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement commStatement =
            conn.prepareStatement("INSERT INTO comments (commentText, postId, userId, commentDate) VALUES (?, ?, ?, ?)");
            commStatement.setString(1, content);
            commStatement.setInt(2, postId);
            commStatement.setInt(3, authUserId);
            commStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            int rowsAffected = commStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    // This function creates a new post with the given content. It also parses
    // the content for hashtags and inserts them into the hashtag table.
    public boolean newPost(String content) throws SQLException {
        // insert the new post into the posts table 
        final String sql = "INSERT INTO posts (postText, userID) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();// connect
            PreparedStatement postStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                postStatement.setString(1, content);
                postStatement.setInt(2, authUserId);
                int rowsAffected = postStatement.executeUpdate();
                ResultSet rs = postStatement.getGeneratedKeys(); // get userId of newly created post
                rs.next(); // move to first row
                String postID = rs.getInt(1) + ""; // get postId value

            ArrayList<String> hashtagWords = new ArrayList<>(); 
            for (String word : content.split("\\s+")) { // split content into words
                if (word.startsWith("#")) {
                    hashtagWords.add(word.substring(1)); // remove '#' character
                }
            }
            // insert hashtags into hashtag table
            String hashSql = "insert into hashtag values (?, ?)";
            try (Connection conn2 = dataSource.getConnection(); PreparedStatement hashStatement = conn.prepareStatement(hashSql)) {
                for (String tag : hashtagWords) { // loop through hashtags
                    hashStatement.setString(1, tag);
                    hashStatement.setString(2, postID);
                    hashStatement.executeUpdate();
                }
            }
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        

    }

    // This function hearts or unhearts a post based on the isAdd parameter.
    public boolean heartPost(int postId, boolean isAdd) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            if(isAdd) {
                PreparedStatement heart = conn.prepareStatement("INSERT INTO hearts VALUE (?, ?)");
                heart.setInt(1, postId);
                heart.setInt(2, authUserId);
                heart.executeUpdate();
            } else {
                PreparedStatement unheart = conn.prepareStatement("DELETE FROM hearts where postID = ? and userID = ?");
                unheart.setInt(1, postId);
                unheart.setInt(2, authUserId);
                unheart.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    
    // This function gets an expanded post by its ID, including comments, like count, comment count,
    public ExpandedPost getPostById(int postId) {
        ExpandedPost result = null;

        final String sqlPost =
            "SELECT p.postId, p.postText, p.postDate, " +
            "u.userId, u.firstName, u.lastName " +
            "FROM posts p " +
            "JOIN `user` u ON p.userId = u.userId " +
            "WHERE p.postId = ?";

        final String sqlComments =
            "SELECT c.commentId, c.commentText, c.commentDate, " +
            "u.userId, u.firstName, u.lastName " +
            "FROM comments c " +
            "JOIN `user` u ON c.userId = u.userId " +
            "WHERE c.postId = ? " +
            "ORDER BY c.commentDate ASC";

        final String sqlLikeCount    = "SELECT COUNT(*) AS cnt FROM hearts WHERE postId = ?";
        final String sqlCommentCount = "SELECT COUNT(*) AS cnt FROM comments WHERE postId = ?";
        final String sqlIsHearted    = "SELECT COUNT(*) AS cnt FROM hearts WHERE postId = ? AND userId = ?";
        final String sqlIsBookmarked = "SELECT COUNT(*) AS cnt FROM bookmarks WHERE postId = ? AND userId = ?";

    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(sqlPost)) {

        ps.setInt(1, postId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                // Base post info
                Timestamp ts = rs.getTimestamp("postDate");
                String formattedDate = new SimpleDateFormat("MMM dd, yyyy, hh:mm a").format(ts);
                User user = new User(
                    rs.getString("userId"),
                    rs.getString("firstName"),
                    rs.getString("lastName")
                );

                // Counts and flags
                int likeCount = 0, commentCount = 0;
                boolean hearted = false, bookmarked = false;

                try (PreparedStatement psLike = conn.prepareStatement(sqlLikeCount)) {
                    psLike.setInt(1, postId);
                    try (ResultSet lrs = psLike.executeQuery()) {
                        if (lrs.next()) likeCount = lrs.getInt("cnt");
                    }
                }
                try (PreparedStatement psCommCount = conn.prepareStatement(sqlCommentCount)) {
                    psCommCount.setInt(1, postId);
                    try (ResultSet crs = psCommCount.executeQuery()) {
                        if (crs.next()) commentCount = crs.getInt("cnt");
                    }
                }
                try (PreparedStatement psIsHearted = conn.prepareStatement(sqlIsHearted)) {
                    psIsHearted.setInt(1, postId);
                    psIsHearted.setInt(2, authUserId);
                    try (ResultSet hrs = psIsHearted.executeQuery()) {
                        if (hrs.next() && hrs.getInt("cnt") > 0) hearted = true;
                    }
                }
                try (PreparedStatement psIsBookmarked = conn.prepareStatement(sqlIsBookmarked)) {
                    psIsBookmarked.setInt(1, postId);
                    psIsBookmarked.setInt(2, authUserId);
                    try (ResultSet brs = psIsBookmarked.executeQuery()) {
                        if (brs.next() && brs.getInt("cnt") > 0) bookmarked = true;
                    }
                }

                // Comments
                List<Comment> comments = new ArrayList<>();
                try (PreparedStatement cps = conn.prepareStatement(sqlComments)) {
                    cps.setInt(1, postId);
                    try (ResultSet crs = cps.executeQuery()) {
                        while (crs.next()) {
                            Comment comment = new Comment(
                                crs.getString("commentId"),
                                crs.getString("commentText"),
                                crs.getString("commentDate"),
                                new User(
                                    crs.getString("userId"),
                                    crs.getString("firstName"),
                                    crs.getString("lastName")
                                )
                            );
                            comments.add(comment);
                        }
                    }
                }

                    // Combine into ExpandedPost
                    result = new ExpandedPost(
                        rs.getString("postId"),
                        rs.getString("postText"),
                        formattedDate,
                        user,
                        likeCount,
                        commentCount,
                        hearted,
                        bookmarked,
                        comments
                    );
                }
            }
        } catch (SQLException e) {
        System.out.println("Error in getPostById: " + e.getMessage());
        }

        return result;
    }

    // This function retrieves posts that contain a given hashtag.
    public List<ExpandedPost> getPostsByHashtag(String hashtag) {
        List<ExpandedPost> results = new ArrayList<>();

        final String sqlPosts =
            "SELECT p.postId, p.postText, p.postDate, " +
            "u.userId, u.firstName, u.lastName " +
            "FROM posts p " +
            "JOIN `user` u ON p.userId = u.userId " +
            "JOIN hashtag h ON h.postId = p.postId " +
            "WHERE h.content = ? " +
            "ORDER BY p.postDate DESC";

        final String sqlComments =
            "SELECT c.commentID, c.commentText, c.commentDate, " +
            "u.userID, u.firstName, u.lastName " +
            "FROM comments c " +
            "JOIN `user` u ON c.userID = u.userID " +
            "WHERE c.postID = ? " +
            "ORDER BY c.commentDate ASC";

        final String sqlLikeCount = "SELECT COUNT(userID) AS cnt FROM hearts WHERE postID = ? GROUP BY postID";

        final String sqlCommentCount = "SELECT COUNT(commentId) AS cnt FROM comments WHERE postId = ? GROUP BY postId";

        final String sqlIsHearted = "SELECT COUNT(*) AS cnt FROM hearts WHERE postID = ? AND userID = ?";

        final String sqlIsBookmarked = "SELECT COUNT(*) AS cnt FROM bookmarks WHERE postID = ? AND userID = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlPosts)) {
            ps.setString(1, hashtag);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String postId = rs.getString("postID");
                    int likeCount = 0;

                    try (PreparedStatement psLike = conn.prepareStatement(sqlLikeCount)) {
                        psLike.setString(1, postId);
                        try (ResultSet lrs = psLike.executeQuery()) {
                            if (lrs.next()) {
                                likeCount = lrs.getInt("cnt");
                            }
                        }
                    }

                    int commentCount = 0;
                    try (PreparedStatement psComment = conn.prepareStatement(sqlCommentCount)) {
                        psComment.setString(1, postId);
                        try (ResultSet crs = psComment.executeQuery()) {
                            if (crs.next()) {
                                commentCount = crs.getInt("cnt");
                            }
                        }
                    }

                    boolean hearted = false;
                    try (PreparedStatement psIsHearted = conn.prepareStatement(sqlIsHearted)) {
                        psIsHearted.setString(1, postId);
                        psIsHearted.setInt(2, authUserId);
                        try (ResultSet hrs = psIsHearted.executeQuery()) {
                            hearted = hrs.next();
                        }
                    }

                    boolean bookmarked = false;
                    try (PreparedStatement psIsBookmarked = conn.prepareStatement(sqlIsBookmarked)) {
                        psIsBookmarked.setString(1, postId);
                        psIsBookmarked.setInt(2, authUserId);
                        try (ResultSet brs = psIsBookmarked.executeQuery()) {
                            bookmarked = brs.next();
                        }
                    }

                    Timestamp ts = rs.getTimestamp("postDate");
                    String formattedDate = new SimpleDateFormat("MMM dd, yyyy, hh:mm a").format(ts);

                    User user = new User(
                        rs.getString("userID"),
                        rs.getString("firstName"),
                        rs.getString("lastName")
                    );

                    Post post = new Post(
                        rs.getString("postID"),
                        rs.getString("postText"),
                        formattedDate,
                        user,
                        likeCount,
                        commentCount,
                        hearted,
                        bookmarked
                    );

                    List<Comment> comments = new ArrayList<>();
                    try (PreparedStatement cps = conn.prepareStatement(sqlComments)) {
                        cps.setInt(1, Integer.parseInt(rs.getString("postID")));
                        try (ResultSet crs = cps.executeQuery()) {
                            while (crs.next()) {
                                Comment comment = new Comment(
                                    crs.getString("commentID"),
                                    crs.getString("commentText"),
                                    crs.getString("commentDate"),
                                    new User(crs.getString("userID"), crs.getString("firstName"), crs.getString("lastName"))
                                );
                                comments.add(comment);
                            }
                        }
                    }

                    results.add(new ExpandedPost(
                        post.getPostId(),
                        post.getContent(),
                        post.getPostDate(),
                        post.getUser(),
                        post.getHeartsCount(),
                        post.getCommentsCount(),
                        post.getHearted(),
                        post.isBookmarked(),
                        comments
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error with getPostByHashtag:" + e.getMessage());
        }

        return results;
    }

    // This function retrieves all posts.
    public List<Post> getAllPosts() {
        List<Post> results = new ArrayList<Post>();

        // SQL to retrieve all posts ordered by post timestamp desc.
        final String sqlPosts =
            "SELECT p.postID, p.postText, p.postDate, " +
            "u.userID, u.firstName, u.lastName " +
            "FROM posts p " +
            "JOIN `user` u ON p.userID = u.userID " +
            "ORDER BY p.postDate DESC";

        // SQL to retrieve comments for a specific post (ordered oldest -> newest)
        final String sqlComments =
            "SELECT c.commentID, c.commentText, c.commentDate, " +
            "u.userID, u.firstName, u.lastName " +
            "FROM comments c " +
            "JOIN `user` u ON c.userID = u.userID " +
            "WHERE c.postID = ? " +
            "ORDER BY c.commentDate ASC";

        final String sqlLikeCount = "SELECT COUNT(userID) AS cnt FROM hearts WHERE postID = ? GROUP BY postID";

        final String sqlCommentCount = "SELECT COUNT(commentId) AS cnt FROM comments WHERE postId = ? GROUP BY postId";

        final String sqlIsHearted = "SELECT COUNT(*) AS cnt FROM hearts WHERE postID = ? AND userID = ?";

        final String sqlIsBookmarked = "SELECT COUNT(*) AS cnt FROM bookmarks WHERE postID = ? AND userID = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlPosts)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String postId = rs.getString("postID");
                    int likeCount = 0;

                    try (PreparedStatement psLike = conn.prepareStatement(sqlLikeCount)) {
                        psLike.setString(1, postId);
                        try (ResultSet lrs = psLike.executeQuery()) {
                            if (lrs.next()) {
                                likeCount = lrs.getInt("cnt");
                            }
                        }
                    }

                    int commentCount = 0;
                    try (PreparedStatement psComment = conn.prepareStatement(sqlCommentCount)) {
                        psComment.setString(1, postId);
                        try (ResultSet crs = psComment.executeQuery()) {
                            if (crs.next()) {
                                commentCount = crs.getInt("cnt");
                            }
                        }
                    }

                    boolean hearted = false;
                    try (PreparedStatement psIsHearted = conn.prepareStatement(sqlIsHearted)) {
                        psIsHearted.setString(1, postId);
                        psIsHearted.setInt(2, authUserId);
                        try (ResultSet hrs = psIsHearted.executeQuery()) {
                            hearted = hrs.next();
                        }
                    }

                    boolean bookmarked = false;
                    try (PreparedStatement psIsBookmarked = conn.prepareStatement(sqlIsBookmarked)) {
                        psIsBookmarked.setString(1, postId);
                        psIsBookmarked.setInt(2, authUserId);
                        try (ResultSet brs = psIsBookmarked.executeQuery()) {
                            bookmarked = brs.next();
                        }
                    }

                    Timestamp ts = rs.getTimestamp("postDate");
                    String formattedDate = new SimpleDateFormat("MMM dd, yyyy, hh:mm a").format(ts);
                    
                    User user = new User(
                        rs.getString("userID"),
                        rs.getString("firstName"),
                        rs.getString("lastName")
                    );

                    // Process each post
                    Post post = new Post(
                        rs.getString("postID"),
                        rs.getString("postText"),
                        formattedDate,
                        user,
                        likeCount,
                        commentCount,
                        hearted,
                        bookmarked
                    );

                    // Load comments for this post
                    List<Comment> comments = new ArrayList<>();
                    try (PreparedStatement cps = conn.prepareStatement(sqlComments)) {
                        cps.setInt(1, Integer.parseInt(rs.getString("postID")));
                        try (ResultSet crs = cps.executeQuery()) {
                            while (crs.next()) {
                                Comment comment = new Comment(
                                    crs.getString("commentID"),
                                    crs.getString("commentText"),
                                    crs.getString("commentDate"),
                                    new User(crs.getString("userID"), crs.getString("firstName"), crs.getString("lastName"))
                                );
                                comments.add(comment);
                            }
                        }
                    }

                    // Combine into an ExpandedPost
                    results.add(new ExpandedPost(
                        post.getPostId(),
                        post.getContent(),
                        post.getPostDate(),
                        post.getUser(),
                        post.getHeartsCount(),
                        post.getCommentsCount(),
                        post.getHearted(),
                        post.isBookmarked(),
                        comments
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return results;
    }
     
    // This function retrieves posts from users that the logged-in user is following.
    // It loads each post, loads all comments for that post, and combines them into ExpandedPost objects.
    public List<Post> getPostsOfFollowing() {
        List<Post> results = new ArrayList<Post>();

        // SQL to retrieve posts from users the logged-in user follows, ordered by post timestamp desc.
        final String sqlPosts =
            "SELECT p.postID, p.postText, p.postDate, " +
            "u.userID, u.firstName, u.lastName " +
            "FROM posts p " +
            "JOIN `user` u ON p.userID = u.userID " +
            "JOIN follower f ON f.followeeId = p.userID " +   // followeeId is the user being followed
            "WHERE f.followerId = ? " +                      // followerId is the logged-in user
            "ORDER BY p.postDate DESC";

        // SQL to retrieve comments for a specific post (ordered oldest -> newest)
        final String sqlComments =
            "SELECT c.commentID, c.commentText, c.commentDate, " +
            "u.userID, u.firstName, u.lastName " +
            "FROM comments c " +
            "JOIN `user` u ON c.userID = u.userID " +
            "WHERE c.postID = ? " +
            "ORDER BY c.commentDate ASC";

        final String sqlLikeCount = "SELECT COUNT(userID) AS cnt FROM hearts WHERE postID = ? GROUP BY postID";

        final String sqlCommentCount = "SELECT COUNT(commentId) AS cnt FROM comments WHERE postId = ? GROUP BY postId";

        final String sqlIsHearted = "SELECT COUNT(*) AS cnt FROM hearts WHERE postID = ? AND userID = ?";

        final String sqlIsBookmarked = "SELECT COUNT(*) AS cnt FROM bookmarks WHERE postID = ? AND userID = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlPosts)) {
            ps.setInt(1, authUserId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String postId = rs.getString("postID");
                    int likeCount = 0;

                    try (PreparedStatement psLike = conn.prepareStatement(sqlLikeCount)) {
                        psLike.setString(1, postId);
                        try (ResultSet lrs = psLike.executeQuery()) {
                            if (lrs.next()) {
                                likeCount = lrs.getInt("cnt");
                            }
                        }
                    }

                    int commentCount = 0;
                    try (PreparedStatement psComment = conn.prepareStatement(sqlCommentCount)) {
                        psComment.setString(1, postId);
                        try (ResultSet crs = psComment.executeQuery()) {
                            if (crs.next()) {
                                commentCount = crs.getInt("cnt");
                            }
                        }
                    }

                    boolean hearted = false;
                    try (PreparedStatement psIsHearted = conn.prepareStatement(sqlIsHearted)) {
                        psIsHearted.setString(1, postId);
                        psIsHearted.setInt(2, authUserId);
                        try (ResultSet hrs = psIsHearted.executeQuery()) {
                            hearted = hrs.next();
                        }
                    }

                    boolean bookmarked = false;
                    try (PreparedStatement psIsBookmarked = conn.prepareStatement(sqlIsBookmarked)) {
                        psIsBookmarked.setString(1, postId);
                        psIsBookmarked.setInt(2, authUserId);
                        try (ResultSet brs = psIsBookmarked.executeQuery()) {
                            bookmarked = brs.next();
                        }
                    }

                    Timestamp ts = rs.getTimestamp("postDate");
                    String formattedDate = new SimpleDateFormat("MMM dd, yyyy, hh:mm a").format(ts);
                    
                    User user = new User(
                        rs.getString("userID"),
                        rs.getString("firstName"),
                        rs.getString("lastName")
                    );

                    // Process each post
                    Post post = new Post(
                        rs.getString("postID"),
                        rs.getString("postText"),
                        formattedDate,
                        user,
                        likeCount,
                        commentCount,
                        hearted,
                        bookmarked
                    );

                    // Load comments for this post
                    List<Comment> comments = new ArrayList<>();
                    try (PreparedStatement cps = conn.prepareStatement(sqlComments)) {
                        cps.setInt(1, Integer.parseInt(rs.getString("postID")));
                        try (ResultSet crs = cps.executeQuery()) {
                            while (crs.next()) {
                                Comment comment = new Comment(
                                    crs.getString("commentID"),
                                    crs.getString("commentText"),
                                    crs.getString("commentDate"),
                                    new User(crs.getString("userID"), crs.getString("firstName"), crs.getString("lastName"))
                                );
                                comments.add(comment);
                            }
                        }
                    }

                    // Combine into an ExpandedPost
                    results.add(new ExpandedPost(
                        post.getPostId(),
                        post.getContent(),
                        post.getPostDate(),
                        post.getUser(),
                        post.getHeartsCount(),
                        post.getCommentsCount(),
                        post.getHearted(),
                        post.isBookmarked(),
                        comments
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return results;
    }

// This function hearts or unhearts a post based on the isAdd parameter.
    public boolean bookmarkPost(int postId, boolean isAdd) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            if(isAdd) {
                PreparedStatement bookmark = conn.prepareStatement("INSERT INTO bookmarks VALUE (?, ?)");
                bookmark.setInt(1, postId);
                bookmark.setInt(2, authUserId);
                bookmark.executeUpdate();
            } else {
                PreparedStatement unbookmark = conn.prepareStatement("DELETE FROM bookmarks where postID = ? and userID = ?");
                unbookmark.setInt(1, postId);
                unbookmark.setInt(2, authUserId);
                unbookmark.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // This function retrieves posts that the logged-in user has bookmarked.
    public List<Post> getBookmarkedPosts() {
        // Create a list to hold the posts
        List<Post> results = new ArrayList<Post>();

        // SQL to retrieve posts from bookmarked posts, ordered by post timestamp desc.
        final String sqlPosts =
            "SELECT p.postID, p.postText, p.postDate, " +
            "u.userID, u.firstName, u.lastName " +
            "FROM posts p " +
            "JOIN `user` u ON p.userID = u.userID " +
            "JOIN bookmarks b on b.postID = p.postID " +  
            "WHERE b.userID = ? " +                     
            "ORDER BY p.postDate DESC";

        // SQL to retrieve comments for a specific post (ordered oldest -> newest)
        final String sqlComments =
            "SELECT c.commentID, c.commentText, c.commentDate, " +
            "u.userID, u.firstName, u.lastName " +
            "FROM comments c " +
            "JOIN `user` u ON c.userID = u.userID " +
            "WHERE c.postID = ? " +
            "ORDER BY c.commentDate ASC";

        final String sqlLikeCount = "SELECT COUNT(userID) AS cnt FROM hearts WHERE postID = ? GROUP BY postID";

        final String sqlCommentCount = "SELECT COUNT(commentId) AS cnt FROM comments WHERE postId = ? GROUP BY postId";

        final String sqlIsHearted = "SELECT COUNT(*) AS cnt FROM hearts WHERE postID = ? AND userID = ?";

        final String sqlIsBookmarked = "SELECT COUNT(*) AS cnt FROM bookmarks WHERE postID = ? AND userID = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlPosts)) {
            ps.setInt(1, authUserId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String postId = rs.getString("postID");
                    int likeCount = 0;

                    try (PreparedStatement psLike = conn.prepareStatement(sqlLikeCount)) {
                        psLike.setString(1, postId);
                        try (ResultSet lrs = psLike.executeQuery()) {
                            if (lrs.next()) {
                                likeCount = lrs.getInt("cnt");
                            }
                        }
                    }

                    int commentCount = 0;
                    try (PreparedStatement psComment = conn.prepareStatement(sqlCommentCount)) {
                        psComment.setString(1, postId);
                        try (ResultSet crs = psComment.executeQuery()) {
                            if (crs.next()) {
                                commentCount = crs.getInt("cnt");
                            }
                        }
                    }

                    boolean hearted = false;
                    try (PreparedStatement psIsHearted = conn.prepareStatement(sqlIsHearted)) {
                        psIsHearted.setString(1, postId);
                        psIsHearted.setInt(2, authUserId);
                        try (ResultSet hrs = psIsHearted.executeQuery()) {
                            hearted = hrs.next();
                        }
                    }

                    boolean bookmarked = false;
                    try (PreparedStatement psIsBookmarked = conn.prepareStatement(sqlIsBookmarked)) {
                        psIsBookmarked.setString(1, postId);
                        psIsBookmarked.setInt(2, authUserId);
                        try (ResultSet brs = psIsBookmarked.executeQuery()) {
                            bookmarked = brs.next();
                        }
                    }

                    Timestamp ts = rs.getTimestamp("postDate");
                    String formattedDate = new SimpleDateFormat("MMM dd, yyyy, hh:mm a").format(ts);
                    
                    User user = new User(
                        rs.getString("userID"),
                        rs.getString("firstName"),
                        rs.getString("lastName")
                    );

                    // Process each post
                    Post post = new Post(
                        rs.getString("postID"),
                        rs.getString("postText"),
                        formattedDate,
                        user,
                        likeCount,
                        commentCount,
                        hearted,
                        bookmarked
                    );

                    // Load comments for this post
                    List<Comment> comments = new ArrayList<>();
                    try (PreparedStatement cps = conn.prepareStatement(sqlComments)) {
                        cps.setInt(1, Integer.parseInt(rs.getString("postID")));
                        try (ResultSet crs = cps.executeQuery()) {
                            while (crs.next()) {
                                Comment comment = new Comment(
                                    crs.getString("commentID"),
                                    crs.getString("commentText"),
                                    crs.getString("commentDate"),
                                    new User(crs.getString("userID"), crs.getString("firstName"), crs.getString("lastName"))
                                );
                                comments.add(comment);
                            }
                        }
                    }

                    // Combine into an ExpandedPost
                    results.add(new ExpandedPost(
                        post.getPostId(),
                        post.getContent(),
                        post.getPostDate(),
                        post.getUser(),
                        post.getHeartsCount(),
                        post.getCommentsCount(),
                        post.getHearted(),
                        post.isBookmarked(),
                        comments
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return results;
    }

        

    // This function retrieves posts ordered by their creation date.
    public List<Post> getPostByDate(String userId) {
        List<Post> results = new ArrayList<Post>();

        // SQL to retrieve posts from users the logged-in user follows, ordered by post timestamp desc.
        final String sqlPosts =
            "SELECT p.postID, p.postText, p.postDate, " +
            "u.userID, u.firstName, u.lastName " +
            "FROM posts p " +
            "JOIN `user` u ON p.userID = u.userID " +
            "WHERE p.userID = ?"   +                   
            "ORDER BY p.postDate DESC";

        // SQL to retrieve comments for a specific post (ordered oldest -> newest)
        final String sqlComments =
            "SELECT c.commentID, c.commentText, c.commentDate, " +
            "u.userID, u.firstName, u.lastName " +
            "FROM comments c " +
            "JOIN `user` u ON c.userID = u.userID " +
            "WHERE c.postID = ? " +
            "ORDER BY c.commentDate ASC";

        final String sqlLikeCount = "SELECT COUNT(userID) AS cnt FROM hearts WHERE postID = ? GROUP BY postID";

        final String sqlCommentCount = "SELECT COUNT(commentId) AS cnt FROM comments WHERE postId = ? GROUP BY postId";

        final String sqlIsHearted = "SELECT COUNT(*) AS cnt FROM hearts WHERE postID = ? AND userID = ?";

        final String sqlIsBookmarked = "SELECT COUNT(*) AS cnt FROM bookmarks WHERE postID = ? AND userID = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlPosts)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String postId = rs.getString("postID");
                    int likeCount = 0;

                    try (PreparedStatement psLike = conn.prepareStatement(sqlLikeCount)) {
                        psLike.setString(1, postId);
                        try (ResultSet lrs = psLike.executeQuery()) {
                            if (lrs.next()) {
                                likeCount = lrs.getInt("cnt");
                            }
                        }
                    }

                    int commentCount = 0;
                    try (PreparedStatement psComment = conn.prepareStatement(sqlCommentCount)) {
                        psComment.setString(1, postId);
                        try (ResultSet crs = psComment.executeQuery()) {
                            if (crs.next()) {
                                commentCount = crs.getInt("cnt");
                            }
                        }
                    }

                    boolean hearted = false;
                    try (PreparedStatement psIsHearted = conn.prepareStatement(sqlIsHearted)) {
                        psIsHearted.setString(1, postId);
                        psIsHearted.setInt(2, authUserId);
                        try (ResultSet hrs = psIsHearted.executeQuery()) {
                            hearted = hrs.next();
                        }
                    }

                    boolean bookmarked = false;
                    try (PreparedStatement psIsBookmarked = conn.prepareStatement(sqlIsBookmarked)) {
                        psIsBookmarked.setString(1, postId);
                        psIsBookmarked.setInt(2, authUserId);
                        try (ResultSet brs = psIsBookmarked.executeQuery()) {
                            bookmarked = brs.next();
                        }
                    }

                    Timestamp ts = rs.getTimestamp("postDate");
                    String formattedDate = new SimpleDateFormat("MMM dd, yyyy, hh:mm a").format(ts);
                    
                    User user = new User(
                        rs.getString("userID"),
                        rs.getString("firstName"),
                        rs.getString("lastName")
                    );

                    // Process each post
                    Post post = new Post(
                        rs.getString("postID"),
                        rs.getString("postText"),
                        formattedDate,
                        user,
                        likeCount,
                        commentCount,
                        hearted,
                        bookmarked
                    );

                    // Load comments for this post
                    List<Comment> comments = new ArrayList<>();
                    try (PreparedStatement cps = conn.prepareStatement(sqlComments)) {
                        cps.setInt(1, Integer.parseInt(rs.getString("postID")));
                        try (ResultSet crs = cps.executeQuery()) {
                            while (crs.next()) {
                                Comment comment = new Comment(
                                    crs.getString("commentID"),
                                    crs.getString("commentText"),
                                    crs.getString("commentDate"),
                                    new User(crs.getString("userID"), crs.getString("firstName"), crs.getString("lastName"))
                                );
                                comments.add(comment);
                            }
                        }
                    }

                    // Combine into an ExpandedPost
                    results.add(new ExpandedPost(
                        post.getPostId(),
                        post.getContent(),
                        post.getPostDate(),
                        post.getUser(),
                        post.getHeartsCount(),
                        post.getCommentsCount(),
                        post.getHearted(),
                        post.isBookmarked(),
                        comments
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return results;
    }

}





