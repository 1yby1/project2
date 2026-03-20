package org.project.model;

import lombok.Data;

@Data
public class UserWithRole {
    private Long id;
    private String username;
    private String roleName;
    private String email; // Optional field, can be removed if not needed

    // Add no-arg constructor so MyBatis can use setter-based population when SQL returns fewer columns
    public UserWithRole() {}

    public UserWithRole(Long id, String username, String roleName,String email) {
        this.id = id;
        this.username = username;
        this.roleName = roleName;
        this.email = email;
    }
}
