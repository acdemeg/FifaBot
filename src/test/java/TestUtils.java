import org.bot.GameInfo;
import org.bot.ImageAnalysis;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class TestUtils {

    public static GameInfo getGameInfo(Integer number) throws IOException {
        File file = new File("src/test/resources/screenshots", number + ".jpg");
        BufferedImage bufferedImage = ImageIO.read(file);
        ImageAnalysis imageAnalysis = new ImageAnalysis(bufferedImage);
        return imageAnalysis.analyse();
    }
}
