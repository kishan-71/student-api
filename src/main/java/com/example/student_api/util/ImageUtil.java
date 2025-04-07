package com.example.student_api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Utility class for image processing operations
 * Handles image resizing and Base64 encoding/decoding
 */
public class ImageUtil {

    private static final Logger logger = LoggerFactory.getLogger(ImageUtil.class);

    // Maximum dimensions for resized images
    private static final int MAX_WIDTH = 300;
    private static final int MAX_HEIGHT = 300;

    // Default image format
    private static final String DEFAULT_FORMAT = "jpg";

    // Private constructor to prevent instantiation
    private ImageUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Encode image bytes to Base64 string
     * Resizes the image before encoding if it exceeds maximum dimensions
     *
     * @param imageBytes The image bytes to encode
     * @return Base64 encoded string or null if input is null
     */
    public static String encodeToBase64(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            return null;
        }
        try {
            // Resize the image before encoding to Base64
            byte[] resizedImageBytes = resizeImage(imageBytes);
            return Base64.getEncoder().encodeToString(resizedImageBytes);
        } catch (IOException e) {
            logger.error("Error encoding image to Base64", e);
            // Fallback to original image if resizing fails
            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }

    /**
     * Decode Base64 string to image bytes
     *
     * @param base64Image The Base64 encoded string
     * @return Decoded image bytes or null if input is null or empty
     */
    public static byte[] decodeFromBase64(String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            return null;
        }
        try {
            return Base64.getDecoder().decode(base64Image);
        } catch (IllegalArgumentException e) {
            logger.error("Error decoding Base64 image", e);
            return null;
        }
    }

    /**
     * Resize image if it exceeds maximum dimensions
     * Maintains aspect ratio during resizing
     *
     * @param imageBytes The image bytes to resize
     * @return Resized image bytes or original bytes if resizing not needed/possible
     * @throws IOException If an I/O error occurs during image processing
     */
    private static byte[] resizeImage(byte[] imageBytes) throws IOException {
        // Read the image
        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes)) {
            BufferedImage originalImage = ImageIO.read(bis);

            if (originalImage == null) {
                logger.warn("Could not read image for resizing");
                return imageBytes; // Return original if can't read image
            }

            // Skip resizing if image is already smaller than max dimensions
            if (originalImage.getWidth() <= MAX_WIDTH && originalImage.getHeight() <= MAX_HEIGHT) {
                return imageBytes;
            }

            // Calculate new dimensions while maintaining aspect ratio
            int newWidth, newHeight;
            double aspectRatio = (double) originalImage.getWidth() / originalImage.getHeight();

            if (aspectRatio > 1) { // Width > Height
                newWidth = MAX_WIDTH;
                newHeight = (int) (MAX_WIDTH / aspectRatio);
            } else { // Height >= Width
                newHeight = MAX_HEIGHT;
                newWidth = (int) (MAX_HEIGHT * aspectRatio);
            }

            // Create a new resized image
            Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();

            // Convert back to byte array
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                ImageIO.write(resizedImage, DEFAULT_FORMAT, bos);
                return bos.toByteArray();
            }
        } catch (IOException e) {
            logger.error("Error resizing image", e);
            throw e;
        }
    }
}
