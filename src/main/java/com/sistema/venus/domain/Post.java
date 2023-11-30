package com.sistema.venus.domain;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    private String imageUrl;
    private String subject;
    private String content;
    @Transient
    private Boolean likedByLoggedUser;
    @Transient
    private Integer likeAmount;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;

    @JsonManagedReference
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "user_likes", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "user_preference_id"))
   @JsonIgnore
    private Set<UserPreferences> likes;
    @Override
    public String toString() {
        return "Post{" +
                "postId=" + postId +
                ", imageUrl='" + imageUrl + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ", likedByLoggedUser=" + likedByLoggedUser +
                ", date=" + date +
                '}';
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getLikedByLoggedUser() {
        return likedByLoggedUser;
    }

    public void setLikedByLoggedUser(Boolean likedByLoggedUser) {
        this.likedByLoggedUser = likedByLoggedUser;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Set<UserPreferences> getLikes() {
        return likes;
    }

    public void setLikes(Set<UserPreferences> likes) {
        this.likes = likes;
    }

    public Integer getLikeAmount() {
        return likeAmount;
    }

    public void setLikeAmount(Integer likeAmount) {
        this.likeAmount = likeAmount;
    }
}
