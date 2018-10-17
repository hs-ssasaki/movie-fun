package org.superbiz.moviefun.albums;

import org.apache.tika.io.IOUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.blobstore.Blob;
import org.superbiz.moviefun.blobstore.BlobStore;

import java.io.*;
import java.net.URISyntaxException;

import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    private final AlbumsBean albumsBean;
    private BlobStore blobStore;
    private final String blobNamePrefix = "image";

    public AlbumsController(AlbumsBean albumsBean, BlobStore blobStore) {
        this.albumsBean = albumsBean;
        this.blobStore = blobStore;
    }


    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable long albumId, Map<String, Object> model) {
        model.put("album", albumsBean.find(albumId));
        return "albumDetails";
    }

    @PostMapping("/{albumId}/cover")
    public String uploadCover(@PathVariable long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {
        saveUploadToFile(albumId, uploadedFile, getCoverFile(albumId));
        return format("redirect:/albums/%d", albumId);
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException, URISyntaxException {
        Optional<Blob> blob = blobStore.get(format(blobNamePrefix + "/%d", albumId));
        Blob imageBlob = blob.orElseGet(this::buildDefaultImageName);

        byte[] imageBytes = IOUtils.toByteArray(imageBlob.inputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(imageBlob.contentType));
        headers.setContentLength(imageBytes.length);

        return new HttpEntity<>(imageBytes, headers);
    }

    @DeleteMapping("/covers")
    public String deleteCovers() {
        blobStore.deleteAll();
        return "redirect:/albums";
    }

    private Blob buildDefaultImageName() {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream input = classLoader.getResourceAsStream("default-cover.jpg");

        return new Blob("default-cover", input, MediaType.IMAGE_JPEG_VALUE);
    }


    private void saveUploadToFile(long albumId, @RequestParam("file") MultipartFile uploadedFile, File targetFile) throws IOException {
        Blob blob = new Blob(
                format(blobNamePrefix + "/%d", albumId),
                uploadedFile.getInputStream(),
                uploadedFile.getContentType()
        );

        blobStore.put(blob);
    }

//    private HttpHeaders createImageHttpHeaders(Path coverFilePath, byte[] imageBytes) throws IOException {
//        String contentType = new Tika().detect(coverFilePath);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.parseMediaType(contentType));
//        headers.setContentLength(imageBytes.length);
//        return headers;
//    }
//
    private File getCoverFile(@PathVariable long albumId) {
        String coverFileName = format(blobNamePrefix + "/%d", albumId);
        return new File(coverFileName);
    }

//    private Path getExistingCoverPath(@PathVariable long albumId) throws URISyntaxException {
//        File coverFile = getCoverFile(albumId);
//        Path coverFilePath;
//
//        if (coverFile.exists()) {
//            coverFilePath = coverFile.toPath();
//        } else {
//            coverFilePath = Paths.get(getSystemResource("default-cover.jpg").toURI());
//        }
//
//        return coverFilePath;
//    }
}
