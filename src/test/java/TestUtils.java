import org.example.GameInfo;
import org.example.ImageAnalysis;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.example.Main.IMAGE_FORMAT;

public class TestUtils {

    public static GameInfo getGameInfo(Integer number) throws IOException {
        File file = new File("src/test/resources/screenshots", number + "." + IMAGE_FORMAT);
        BufferedImage bufferedImage = ImageIO.read(file);
        ImageAnalysis imageAnalysis = new ImageAnalysis(bufferedImage);
        return imageAnalysis.analyse();
    }
}
