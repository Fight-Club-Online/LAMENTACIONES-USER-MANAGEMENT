package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * Servicio para manejar la subida de avatares a Azure Blob Storage.
 */
@Service
public class AzureBlobService {

    private final BlobContainerClient containerClient;
    private final boolean enabled;

    public AzureBlobService(
        @Value("${azure.storage.connection-string:}") String connectionString,
        @Value("${azure.storage.container-name:avatars}") String containerName
    ) {
        this.enabled = connectionString != null && !connectionString.isBlank() 
                       && !connectionString.equals("placeholder");
        
        if (this.enabled) {
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
            this.containerClient = blobServiceClient.getBlobContainerClient(containerName);
        } else {
            this.containerClient = null;
        }
    }

    public String uploadAvatar(MultipartFile file, String userId) throws IOException {
        if (!enabled || containerClient == null) {
            throw new RuntimeException("Azure Storage no está configurado");
        }
        String extension = getExtension(file.getOriginalFilename());
        String blobName = "avatars/" + userId + "/" + UUID.randomUUID() + "." + extension;
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.upload(file.getInputStream(), file.getSize(), true);
        return blobClient.getBlobUrl();
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}