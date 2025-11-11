/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.csx370.services;

import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import uga.menik.csx370.models.FollowableUser;

/**
 * This service contains people related functions.
 */
@Service
public class PeopleService {
    // define fields for dependencies
	private final DataSource dataSource;
	private final UserService userService;
	// inject dependencies
	@Autowired
	public PeopleService(DataSource dataSource, UserService userService) {
		this.dataSource = dataSource;
		this.userService = userService;
	}
	
    /**
     * This function should query and return all users that 
     * are followable. The list should not contain the user 
     * with id userIdToExclude.
     */
    public List<FollowableUser> getFollowableUsers(String userIdToExclude) throws SQLException{
        // Write an SQL query to find the users that are not the current user.
		List<FollowableUser> followableUsers = new ArrayList<>();
		List<Integer> followList = new ArrayList<Integer>();
		
		if (!userService.isAuthenticated()) { // make sure user is logged in
			return followableUsers; // no one logged in, return empty list
		}
		
		try (Connection conn = dataSource.getConnection()) {
			PreparedStatement followers = conn.prepareStatement("SELECT followeeId from follower where followerId = ?");
			followers.setString(1, userIdToExclude);
			try (ResultSet rs = followers.executeQuery()) {
				while (rs.next()) {
					followList.add(rs.getInt("followeeId"));
				}
			}
		
		String sql = "SELECT * FROM `user`\n" +
			 "LEFT JOIN ( \n" +
			 "SELECT posts.userId, posts.postDate FROM posts \n" +
			 "JOIN ( \n" +
			 "SELECT max(postDate) AS postDate, userId FROM posts \n" +
			 "GROUP BY userId \n" +
			 "ORDER BY postDate DESC \n" +
			 ") AS latestPosts \n" +
			 "ON posts.postDate = latestPosts.postDate AND posts.userId = latestPosts.userId \n" +
			 "GROUP BY userId \n" +
			 ") AS userPosts \n" +
			 "ON `user`.userId = userPosts.userId \n" +
			 "WHERE `user`.userId != ? \n";
        // Run the query with a datasource.
        // See UserService.java to see how to inject DataSource instance and
        // use it to run a query.
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userIdToExclude);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					String activateStatus = "Never";
					if (rs.getTimestamp("postDate") != null) {
						activateStatus = rs.getTimestamp("postDate").toString();
					}
					followableUsers.add(new FollowableUser(
						rs.getString("userId"),
						rs.getString("firstName"),
						rs.getString("lastName"),
						followList.contains(rs.getInt("userId")),
						activateStatus
						 // check if user is followed
					)
				);
			}
		}	
	}catch (SQLException e) {
		System.out.println(e.getMessage());
	}	
        // Use the query result to create a list of followable users.
        // See UserService.java to see how to access rows and their attributes
        // from the query result.
        // Check the following createSampleFollowableUserList function to see 
        // how to create a list of FollowableUsers.

        // Replace the following line and return the list you created.
        return followableUsers; 
    }

}

