package main.java;
import java.io.FilenameFilter;
public class ImageFilter {
    static final String[] EXTENSIONS = new String[]{
            "jpeg", "png", "bmp"
    };
    static final FilenameFilter IMAGE_FILTER = (dir, name) -> {
        for (final String ext : EXTENSIONS) {
            if (name.endsWith("." + ext)) {
                return (true);
            }
        }
        System.out.println("Неправильное расширение файла");
        return (false);
    };
}