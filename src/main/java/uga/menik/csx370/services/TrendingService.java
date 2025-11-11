package uga.menik.csx370.services;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uga.menik.csx370.models.TrendingTag;

@Service
public class TrendingService {
    private final DataSource dataSource;

    @Autowired
    public TrendingService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Returns top hashtags used in posts within the last 7 days.
     * Ordered by usage count desc, then by most recent usage.
     */
    public List<TrendingTag> getTrendingTagsLast7Days(int limit) {
        List<TrendingTag> tags = new ArrayList<>();
        final String sql = 
            "SELECT h.content AS hashtag, COUNT(DISTINCT h.postId) AS postCount, " +
            "       MAX(p.postDate) AS lastUsed " +
            "FROM hashtag h " +
            "JOIN posts p ON h.postId = p.postId " +
            "WHERE p.postDate >= NOW() - INTERVAL 7 DAY " +
            "GROUP BY h.content " +
            "ORDER BY postCount DESC, lastUsed DESC " +
            "LIMIT ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("lastUsed");
                    String lastUsedFmt = ts == null ? "Unknown"
                        : new SimpleDateFormat("MMM dd, yyyy, hh:mm a").format(ts);
                    tags.add(new TrendingTag(
                        rs.getString("hashtag"),
                        rs.getInt("postCount"),
                        lastUsedFmt
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching trending hashtags: " + e.getMessage());
        }
        return tags;
    }

    
}
