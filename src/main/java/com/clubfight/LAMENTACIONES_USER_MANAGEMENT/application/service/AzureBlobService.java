package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * Servicio para manejar la subida de avatares a Azure Blob Storage con métricas de negocio integradas.
 */
@Service
public class AzureBlobService {

    private final BlobContainerClient containerClient;
    private final boolean enabled;
    private final MeterRegistry meterRegistry;

    public AzureBlobService(
        @Value("${azure.storage.connection-string:}") String connectionString,
        @Value("${azure.storage.container-name:avatars}") String containerName,
        MeterRegistry meterRegistry
    ) {
        this.meterRegistry = meterRegistry;
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
            trackUploadError("config_error");
            throw new RuntimeException("Azure Storage no está configurado");
        }

        String extension = getExtension(file.getOriginalFilename());
        String blobName = "avatars/" + userId + "/" + UUID.randomUUID() + "." + extension;
        
        try {
            BlobClient blobClient = containerClient.getBlobClient(blobName);
            blobClient.upload(file.getInputStream(), file.getSize(), true);

            Counter.builder("avatar_uploads_total")
                .description("Total de avatares de usuario subidos exitosamente")
                .tag("extension", extension)
                .register(meterRegistry)
                .increment();

            Counter.builder("avatar_upload_bytes_total")
                .description("Total de bytes transferidos y almacenados en Azure Blob Storage")
                .register(meterRegistry)
                .increment(file.getSize());

            return blobClient.getBlobUrl();

        } catch (IOException | RuntimeException e) {
            trackUploadError("upload_failed");
            throw e;
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    private void trackUploadError(String errorType) {
        Counter.builder("avatar_upload_errors_total")
            .description("Total de fallos al intentar subir avatares")
            .tag("type", errorType)
            .register(meterRegistry)
            .increment();
    }
}