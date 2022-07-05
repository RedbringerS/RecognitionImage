package main.java;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Recognition {
    private final BufferedImage img;
    private final int[] topLeftCornerX = {149,219,289,364,439};
    private final int[] topLeftCornerY = {589, 617};
    private final int[] rankSize = {41, 31};
    private final int[] suitSize = {23, 21};

    public Recognition(BufferedImage img) {
        this.img = img;
    }

    public String getRankOrSuit(int number, char type) {
        String rankSuit = "";
        boolean isCard = false;
        BufferedImage getImage = null;
        if (type == 'r') {
            getImage = img.getSubimage(topLeftCornerX[number], topLeftCornerY[0], rankSize[0], rankSize[1]);
        } else if (type == 's') {
            getImage = img.getSubimage(topLeftCornerX[number], topLeftCornerY[1], suitSize[0], suitSize[1]);
        }
        boolean isGray = false;

        if (getImage != null && getImage.getRGB(getImage.getWidth() - 1, getImage.getHeight() - 1) == -8882056) {
            isGray = true;
            getImage = getBinaryImage(getImage, 's');
        }

        String originalString =  getImageAsString(getImage);

        if (number > 2) {
            Color c = new Color(getImage.getRGB(getImage.getWidth()-1, getImage.getHeight()-1));
            isCard = isFourThorFifthCard(c.getRed(), c.getGreen(), c.getRed());
        }

        if (number < 3 || isCard) {
            File dir = null;
            if (type == 'r') {
                dir = new File("src/main/resources/image/ranks/"); //13 значений
            } else if (type == 's') {
                dir = new File("src/main/resources/image/suits/"); //4 масти
            }

            int min = 1000000;
            for(final File imgFile : dir.listFiles()) {
                BufferedImage target = null;
                try {
                    target = ImageIO.read(imgFile);
                    if (isGray) {
                        target = getBinaryImage(target, 't');
                    }
                } catch (IOException e) {
                    System.out.println("Картинку " + imgFile.getName() + " невозможно открыть!");
                }
                String targetString =  getImageAsString(target);

                int levenshtein = levenshtein(originalString, targetString, 1000);

                if (levenshtein < min) {
                    min = levenshtein;
                    rankSuit = imgFile.getName().replaceFirst("[.][^.]+$", "");
                }
            }
        }
        return rankSuit;
    }

    public boolean isFourThorFifthCard(int r, int g, int b) {
        return (r == 255 && g == 255 && b == 255) || (r == 120 && g == 120 && b == 120);
    }

    public int levenshtein(String source, String target, int threshold) {

        int N1 = source.length();
        int N2 = target.length();

        int[] p = new int[N1 + 1];
        int[] d = new int[N1 + 1];
        int[] temp;

        final int boundary = Math.min(N1, threshold) + 1;
        for (int i = 0; i < boundary; i++) {
            p[i] = i;
        }

        Arrays.fill(p, boundary, p.length, Integer.MAX_VALUE);
        Arrays.fill(d, Integer.MAX_VALUE);

        for (int j = 1; j <= N2; j++) {
            char t_j = target.charAt(j - 1);
            d[0] = j;

            int min = Math.max(1, j - threshold);
            int max = (j > Integer.MAX_VALUE - threshold) ? N1 : Math.min(N1, j + threshold);

            if (min > max) {
                return -1;
            }

            if (min > 1) {
                d[min - 1] = Integer.MAX_VALUE;
            }

            for (int i = min; i <= max; i++) {
                d[i] = (source.charAt(i - 1) == t_j) ? p[i - 1] : 1 + Math.min(Math.min(d[i - 1], p[i]), p[i - 1]);
            }

            temp = p;
            p = d;
            d = temp;
        }

        if (p[N1] <= threshold) {
            return p[N1];
        }
        return -1;
    }

    public String getImageAsString(BufferedImage symbol) {
        short whiteBg = -1;
        StringBuilder binaryString = new StringBuilder();
        for (short y = 1; y < symbol.getHeight(); y++) {
            for (short x = 1; x < symbol.getWidth(); x++) {
                int rgb = symbol.getRGB(x, y);
                binaryString.append(rgb == whiteBg ? " " : "*");
            }
        }
        return binaryString.toString();
    }
    public BufferedImage getBinaryImage(BufferedImage img, char type) {
        for (int i = 0; i< img.getWidth(); i++) {
            for (int j = 0; j< img.getHeight(); j++) {
                if (type == 's') {
                    if (img.getRGB(i, j) == -8882056) {
                        img.setRGB(i, j, -1);
                    } else {
                        img.setRGB(i, j, 0);
                    }
                }
                else if (type == 't' && img.getRGB(i, j) != -1) {
                    img.setRGB(i, j, 0);
                }
            }
        }
        return img;
    }
}