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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
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

    public void savePost(MultipartFile file, String subject, String content) throws IOException {
        postRepository.save(Post.builder()
                .subject(subject)
                .content(content)
                .imageUrl(getImageUrl(file))
                .build());
    }

    private String getImageUrl(MultipartFile file) throws IOException {
        try{
            if(file==null) return null;
            File tempFile =  new File(String.format("%s\\%s-%s",tempFolder,System.currentTimeMillis(), file.getOriginalFilename()));
            Files.write(tempFile.toPath(), file.getBytes());

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.addBinaryBody("file",tempFile, ContentType.DEFAULT_BINARY, LocalDateTime.now().toString());
            entityBuilder.addPart("upload_preset",new StringBody(cloudinaryPreset,ContentType.TEXT_PLAIN));

            HttpPost request = new HttpPost(cloudinaryUrl);
            request.setEntity(entityBuilder.build());
            HttpResponse response = new DefaultHttpClient().execute(request);

            tempFile.delete();
            String url = objectMapper.readValue(EntityUtils.toString(response.getEntity(),"UTF-8"), JsonNode.class).get("secure_url").toString();
            return url.substring(1,url.length()-1);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }
}
