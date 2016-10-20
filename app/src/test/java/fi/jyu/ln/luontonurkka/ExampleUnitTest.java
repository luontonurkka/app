package fi.jyu.ln.luontonurkka;

import org.junit.Test;

import fi.jyu.ln.luontonurkka.tools.CoordinateConverter;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void coordinate_test() throws Exception {
        int[] testCoords = CoordinateConverter.WGSToYKJ(65.03889, 26.027133);
        assertEquals(7216161, testCoords[0]);
        assertEquals(3454345, testCoords[1]);
    }

    @Test
    public void coordinate_test2() throws Exception {
        int[] testCoords = CoordinateConverter.WGSToYKJ(62.240404, 25.743398);
        int n = testCoords[0] / 10000;
        int e = testCoords[1] / 10000;
        assertEquals(690, n);
        assertEquals(343, e);
    }

    @Test
    public void coordinate_test3() throws Exception {
        double[] testCoords = CoordinateConverter.YKJToWGS(7216161, 3454345);
        assertEquals(65.03889, testCoords[0], 0.1);
        assertEquals(26.027133, testCoords[1], 0.1);
    }
}