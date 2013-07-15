
package com.huanghua.util;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtil {

    public static BufferedImage getImage(String imagePath) {
        try {
            return ImageIO.read(ImageUtil.class.getClassLoader().getResource(imagePath));
        } catch (IOException e) {
            return null;
        }
    }
}
