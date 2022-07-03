package main.java;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CardRecognition {
    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        Recognition RF;
        final File dir = new File(args[0]);
        if(dir.isDirectory())
        {
            for(File item : dir.listFiles(ImageFilter.IMAGE_FILTER)){
               BufferedImage image = null;
                try {
                    image = ImageIO.read(item);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                StringBuilder results = new StringBuilder();
                RF = new Recognition(image);
                for (int i=0; i<5; i++) {
                    results.append(RF.getRankOrSuit(i, 'r'))
                                .append(RF.getRankOrSuit(i, 's'));
                }
                System.out.println(item.getName() + " - " + results);
            }
        }
        System.out.println("Программа выполнилась за " + ((double) System.currentTimeMillis() - time));
    }
}