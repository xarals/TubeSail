package com.xaral.tubesail.domain;

import jakarta.persistence.*;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long date;
    private String username;
    private String password;
    private boolean activity;
    private String roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DownloadHistory> downloadHistory;

    public User() {}

    public User(String username, String password) {
        this.date = System.currentTimeMillis();
        this.username = username;
        this.password = password;
        this.activity = true;
        this.roles = "user";
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        String[] rolesList = roles.split(", ");
        StringBuilder newRoles = new StringBuilder();
        for (int i = 0; i < rolesList.length; i++) {
            if (i != 0)
                newRoles.append(", ");
           newRoles.append("ROLE_").append(rolesList[i].toUpperCase());
        }
        return newRoles.toString();
    }

    public String getHighestRole() {
        Set<String> rolesSet = Arrays.asList(roles.split(", ")).stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        if (rolesSet.contains("creator"))
            return "Creator";
        if (rolesSet.contains("admin"))
            return "Admin";
        if (rolesSet.contains("moderator"))
            return "Moderator";
        return "User";
    }

    public boolean canChange(String role) {
        List<String> roleOrder = Arrays.asList("Creator", "Admin", "Moderator", "User");
        String userRole = getHighestRole();
        return roleOrder.indexOf(userRole) < roleOrder.indexOf(role);
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public boolean getActivity() {
        return activity;
    }

    public void setActivity(boolean activity) {
        this.activity = activity;
    }

    public List<DownloadHistory> getDownloadHistory() {
        return downloadHistory;
    }

    public void setDownloadHistory(List<DownloadHistory> downloadHistory) {
        this.downloadHistory = downloadHistory;
    }

    public String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(this.date);
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
