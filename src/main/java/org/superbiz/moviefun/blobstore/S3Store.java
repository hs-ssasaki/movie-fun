package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import java.io.IOException;
import java.util.Optional;

import static java.lang.String.format;

public class S3Store implements BlobStore {

    private final String BLOB_NAME_PREFIX = "image";

    private AmazonS3Client s3Client;
    private String storageBucket;

    public S3Store(AmazonS3Client s3Client, String storageBucket) {

        this.s3Client = s3Client;
        this.storageBucket = storageBucket;
    }

    @Override
    public void put(Blob blob) throws IOException {

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(blob.contentType);
        s3Client.putObject(storageBucket, blob.name, blob.inputStream, objectMetadata);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {

        S3Object s3Object = s3Client.getObject(storageBucket, format(BLOB_NAME_PREFIX + "/%d", name));

        s3Object.getObjectContent()

        return Optional.empty();
    }

    @Override
    public void deleteAll() {

    }
}
