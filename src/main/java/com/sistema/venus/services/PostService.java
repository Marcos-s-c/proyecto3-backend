package com.sistema.venus.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.venus.domain.Post;
import com.sistema.venus.repo.PostRepository;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    public void savePost(String postId, MultipartFile file, String subject, String content)
            throws IOException, ValidationException {
        if (postRepository.findAll().stream().noneMatch(post -> (postId == null && post.getSubject().equals(subject))
                || (post.getSubject().equals(subject) && !post.getPostId().equals(Long.valueOf(postId))))) {
            postRepository.save(Post.builder()
                    .postId(postId != null ? Long.valueOf(postId) : null)
                    .subject(subject)
                    .content(content)
                    .imageUrl(getImageUrl(file, postId))
                    .build());
        } else {
            throw new ValidationException("Ya existe un post con ese asunto.");
        }
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

    public List<Post> getAllPosts() {
        return postRepository.getSortedPosts();
    }
}
