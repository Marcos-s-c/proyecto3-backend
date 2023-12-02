package com.sistema.venus.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.venus.domain.Post;
import com.sistema.venus.domain.UserPreferences;
import com.sistema.venus.repo.PostRepository;
import com.sistema.venus.util.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.ValidationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Value("${temp.folder}")
    private String tempFolder;
    @Value("${cloudinary.upload.preset}")
    private String cloudinaryPreset;
    @Value("${cloudinary.url}")
    private String cloudinaryUrl;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserPreferenceService userPreferenceService;

    private void createPost(MultipartFile file, String subject, String content)
            throws IOException, ValidationException {
        if (postRepository.findAll().stream().noneMatch(post -> post.getSubject().equals(subject))) {
            postRepository.save(Post.builder()
                    .subject(subject)
                    .content(content)
                    .imageUrl(getImageUrl(file, null))
                    .date(Utils.getDateCurrentTimezone())
                    .build());
        } else {
            throw new ValidationException("Ya existe un post con ese asunto.");
        }
    }

    private void updatePost(String postId, MultipartFile file, String subject, String content)
            throws IOException, ValidationException {
        if (postRepository.findAll().stream().noneMatch(post -> (post.getSubject().equals(subject) && !post.getPostId().equals(Long.valueOf(postId))))) {
            postRepository.getPostByPostId(Long.parseLong(postId));
            postRepository.save(Post.builder()
                    .postId(Long.valueOf(postId))
                    .subject(subject)
                    .content(content)
                    .date(postRepository.getPostByPostId(Long.parseLong(postId)).getDate())
                    .imageUrl(getImageUrl(file, postId))
                    .build());
        } else {
            throw new ValidationException("Ya existe un post con ese asunto.");
        }
    }

    public void upsertPost(String postId, MultipartFile file, String subject, String content) throws ValidationException, IOException {
        if(postId==null){
            createPost(file,subject,content);
            return;
        }
        updatePost(postId,file,subject,content);
    }

    public Post getPostById(Long postId) {
        return postRepository.getPostByPostId(postId);
    }

    private String getImageUrl(MultipartFile file, String postId) throws IOException {
        try {
            if (postId != null && file == null) {
                return postRepository.getPostByPostId(Long.valueOf(postId)).getImageUrl();
            }
            return getCloudinaryUrl(file);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private String getCloudinaryUrl(MultipartFile file) throws IOException {
        if (file == null)
            return null;
        File tempFile = new File(
                String.format("%s\\%s-%s", tempFolder, System.currentTimeMillis(), file.getOriginalFilename()));
        Files.write(tempFile.toPath(), file.getBytes());

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.addBinaryBody("file", tempFile, ContentType.DEFAULT_BINARY, LocalDateTime.now().toString());
        entityBuilder.addPart("upload_preset", new StringBody(cloudinaryPreset, ContentType.TEXT_PLAIN));

        HttpPost request = new HttpPost(cloudinaryUrl);
        request.setEntity(entityBuilder.build());
        HttpResponse response = new DefaultHttpClient().execute(request);

        tempFile.delete();
        String url = objectMapper.readValue(EntityUtils.toString(response.getEntity(), "UTF-8"), JsonNode.class)
                .get("secure_url").toString();
        return url.substring(1, url.length() - 1);
    }

    public List<Post> getAllPosts(String searchParam, String sortBy) {
        List<Post> posts = StringUtils.isBlank(searchParam) ? getSortedPosts(sortBy): getSortedPosts(sortBy)
                .stream().filter(post -> post.getSubject().contains(searchParam))
                .collect(Collectors.toList());
        setLikes(posts);
        return posts;
    }

    private void setLikes(List<Post> posts) {
        posts.forEach(post -> {
            post.setLikeAmount(post.getLikes().size());
            if(post.getLikes().stream().anyMatch(userPreferences -> userPreferences.getEmailId().equals(userService.getLoggedUser().getEmail()))){
                post.setLikedByLoggedUser(true);
            }
        });
    }

    private List<Post> getSortedPosts(String sortBy) {
        List<Post> posts = postRepository.findAll();
        Comparator<Post> postComparator;
        if("likes".equals(sortBy)){
            postComparator = Comparator.comparing(post -> post.getLikes().size(), Comparator.reverseOrder());
        }else{
            postComparator = Comparator.comparing(Post::getDate, Comparator.reverseOrder());
        }

        posts.sort(postComparator);
        return posts;
    }

    public void likePost(Long postId){
        Post post = postRepository.getPostByPostId(postId);
        UserPreferences userPreferences = userPreferenceService.getPreferenciaNotificacionByEmail(userService.getLoggedUser().getEmail());
        if(post.getLikes().stream().noneMatch(up -> up.getUserPreferenceId().equals(userPreferences.getUserPreferenceId()))){
            post.getLikes().add(userPreferences);
        }else{
            post.getLikes().removeIf(up -> up.getUserPreferenceId().equals(userPreferences.getUserPreferenceId()));
        }
        postRepository.save(post);
    }

    public void borrarPost(Long postId) {
        postRepository.deleteById(postId);
    }
}
