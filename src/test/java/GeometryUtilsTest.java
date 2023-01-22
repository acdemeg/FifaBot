import org.example.GeometryUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;

class GeometryUtilsTest {

    @Test
    void triangleContainPointTest() {
        Point v1 = new Point(173, 74);
        Point v2 = new Point(216, 169);
        Point v3 = new Point(132, 168);
        Point pTrue = new Point(172, 137);
        Point pFalse = new Point(136, 101);
        Assertions.assertTrue(GeometryUtils.triangleContainPoint(v1, v2, v3, pTrue));
        Assertions.assertFalse(GeometryUtils.triangleContainPoint(v1, v2, v3, pFalse));
    }
}
