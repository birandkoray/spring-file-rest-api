package com.example.demo.rest;

import com.example.demo.data.CategoryType;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import static com.example.demo.data.CategoryType.*;

@RestController
@CrossOrigin(value = "http://localhost:3000")
public class FileUploadRestController {

    @Autowired
    private GridFsOperations gridFsOperations;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    private static final Logger logger = Logger.getLogger(FileUploadRestController.class.getName());

    @PostMapping("/upload")
    public ResponseEntity<String> uploadData(@RequestParam("cv") MultipartFile resume,
                                             @RequestParam("coverLetter") MultipartFile coverLetter) throws Exception {


        String files = "";

        files += getString(resume, RESUME);

        files += getString(coverLetter, COVER_LETTER);

        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    private String getString(MultipartFile file, CategoryType categoryType) throws IOException {

        gridFsOperations.delete(new Query(Criteria.where("metadata.nominee").is(456).and("metadata.type").is(categoryType)));

        if (file != null) {
            InputStream inputStream = file.getInputStream();
            String originalName = file.getOriginalFilename();
            String name = file.getName();
            String contentType = file.getContentType();
            long size = file.getSize();

            DBObject resumeMetadata = new BasicDBObject();
            resumeMetadata.put("nominee", 456);
            resumeMetadata.put("type", categoryType);
            gridFsOperations.store(inputStream, originalName, resumeMetadata);
            return originalName;
        }

        return "";
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("type")CategoryType categoryType) throws Exception {

        GridFSFile c = gridFsOperations.findOne(new Query(Criteria.where("metadata.nominee").is(456).and("metadata.type").is(categoryType)));
        GridFsResource resource = gridFsTemplate.getResource(c);
        String fileName = c.getFilename();
        byte[] fileData = StreamUtils.copyToByteArray(resource.getInputStream());

        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentLength(fileData.length);
        respHeaders.setContentType(MediaType.APPLICATION_PDF);
        respHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");

        return new ResponseEntity<byte[]>(fileData, respHeaders, HttpStatus.OK);
    }

}
