package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.unit.usecases;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service.AzureBlobService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AzureBlobServiceTest {

    @Mock
    private BlobContainerClient containerClient;

    @Mock
    private BlobClient blobClient;
    private MeterRegistry meterRegistry; 

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
    }

    private AzureBlobService buildDisabled() {
        return new AzureBlobService("", "avatars", meterRegistry);
    }

    private AzureBlobService buildEnabled() {
        AzureBlobService service = buildDisabled();
        ReflectionTestUtils.setField(service, "enabled", true);
        ReflectionTestUtils.setField(service, "containerClient", containerClient);
        return service;
    }

    @Test
    void shouldBeDisabledWhenConnectionStringIsEmpty() {
        AzureBlobService service = new AzureBlobService("", "avatars", meterRegistry);

        boolean enabled = (boolean) ReflectionTestUtils.getField(service, "enabled");
        assertFalse(enabled); 
        assertNull(ReflectionTestUtils.getField(service, "containerClient"));
    }

    @Test
    void shouldBeDisabledWhenConnectionStringIsNull() {
        AzureBlobService service = new AzureBlobService(null, "avatars", meterRegistry);

        boolean enabled = (boolean) ReflectionTestUtils.getField(service, "enabled");
        assertFalse(enabled);
    }

    @Test
    void shouldBeDisabledWhenConnectionStringIsPlaceholder() {
        AzureBlobService service = new AzureBlobService("placeholder", "avatars", meterRegistry);

        boolean enabled = (boolean) ReflectionTestUtils.getField(service, "enabled");
        assertFalse(enabled);
    }

    @Test
    void shouldThrowWhenServiceIsDisabled() {
        AzureBlobService service = buildDisabled();

        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", "image/jpeg", "bytes".getBytes());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.uploadAvatar(file, "user-123"));

        assertEquals("Azure Storage no está configurado", ex.getMessage());
        
        assertEquals(1.0, meterRegistry.counter("avatar_upload_errors_total", "type", "config_error").count());
    }

    @Test
    void shouldUploadAndReturnBlobUrl() throws IOException {
        AzureBlobService service = buildEnabled();

        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "image-content".getBytes());

        String expectedUrl = "https://mystorage.blob.core.windows.net/avatars/user-1/uuid.jpg";

        when(containerClient.getBlobClient(anyString())).thenReturn(blobClient);
        doNothing().when(blobClient).upload(any(), anyLong(), eq(true));
        when(blobClient.getBlobUrl()).thenReturn(expectedUrl);

        String result = service.uploadAvatar(file, "user-1");

        assertEquals(expectedUrl, result);
        verify(containerClient)
                .getBlobClient(argThat(name -> name.startsWith("avatars/user-1/") && name.endsWith(".jpg")));
        verify(blobClient).upload(any(ByteArrayInputStream.class), eq((long) "image-content".length()), eq(true));
        verify(blobClient).getBlobUrl();
        assertEquals(1.0, meterRegistry.counter("avatar_uploads_total", "extension", "jpg").count());
    }

    @Test
    void shouldUsePngExtensionWhenFilenameIsPng() throws IOException {
        AzureBlobService service = buildEnabled();

        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png", "png-bytes".getBytes());

        when(containerClient.getBlobClient(anyString())).thenReturn(blobClient);
        doNothing().when(blobClient).upload(any(), anyLong(), eq(true));
        when(blobClient.getBlobUrl()).thenReturn("https://url/avatar.png");

        service.uploadAvatar(file, "user-2");

        verify(containerClient).getBlobClient(argThat(name -> name.endsWith(".png")));
        assertEquals(1.0, meterRegistry.counter("avatar_uploads_total", "extension", "png").count());
    }

    @Test
    void shouldUseJpgExtensionWhenFilenameHasNoExtension() throws IOException {
        AzureBlobService service = buildEnabled();

        MockMultipartFile file = new MockMultipartFile(
                "file", "avatarsinextension", "image/jpeg", "bytes".getBytes());

        when(containerClient.getBlobClient(anyString())).thenReturn(blobClient);
        doNothing().when(blobClient).upload(any(), anyLong(), eq(true));
        when(blobClient.getBlobUrl()).thenReturn("https://url/blob.jpg");

        service.uploadAvatar(file, "user-3");

        verify(containerClient).getBlobClient(argThat(name -> name.endsWith(".jpg")));
        assertEquals(1.0, meterRegistry.counter("avatar_uploads_total", "extension", "jpg").count());
    }

    @Test
    void shouldUseJpgExtensionWhenFilenameIsNull() throws IOException {
        AzureBlobService service = buildEnabled();

        MockMultipartFile file = new MockMultipartFile(
                "file", null, "image/jpeg", "bytes".getBytes());

        when(containerClient.getBlobClient(anyString())).thenReturn(blobClient);
        doNothing().when(blobClient).upload(any(), anyLong(), eq(true));
        when(blobClient.getBlobUrl()).thenReturn("https://url/blob.jpg");

        service.uploadAvatar(file, "user-4");

        verify(containerClient).getBlobClient(argThat(name -> name.endsWith(".jpg")));
        assertEquals(1.0, meterRegistry.counter("avatar_uploads_total", "extension", "jpg").count());
    }

    @Test
    void shouldIncludeUserIdInBlobPath() throws IOException {
        AzureBlobService service = buildEnabled();
        String userId = "abc-xyz-789";

        MockMultipartFile file = new MockMultipartFile(
                "file", "img.jpg", "image/jpeg", "data".getBytes());

        when(containerClient.getBlobClient(anyString())).thenReturn(blobClient);
        doNothing().when(blobClient).upload(any(), anyLong(), eq(true));
        when(blobClient.getBlobUrl()).thenReturn("https://url/img.jpg");

        service.uploadAvatar(file, userId);

        verify(containerClient).getBlobClient(argThat(name -> name.contains("avatars/" + userId + "/")));
    }

    @Test
    void shouldPropagateIOExceptionFromInputStream() throws IOException {
        AzureBlobService service = buildEnabled();
        MockMultipartFile file = spy(new MockMultipartFile(
                "file", "img.jpg", "image/jpeg", "data".getBytes()));
        doThrow(new IOException("stream error")).when(file).getInputStream();

        when(containerClient.getBlobClient(anyString())).thenReturn(blobClient);

        assertThrows(IOException.class, () -> service.uploadAvatar(file, "user-5"));

        verify(blobClient, never()).upload(any(), anyLong(), anyBoolean());
        
        assertEquals(1.0, meterRegistry.counter("avatar_upload_errors_total", "type", "upload_failed").count());
    }
}