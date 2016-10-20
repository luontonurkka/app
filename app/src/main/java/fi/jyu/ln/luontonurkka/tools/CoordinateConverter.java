package fi.jyu.ln.luontonurkka.tools;

/**
 * http://koivu.luomus.fi/projects/coord/kartta/mapping_functions_luomus.php.txt
 *
 * Created by Jarno on 20.10.2016
 */

public class CoordinateConverter {

    public static int[] WGSToYKJ(double n, double e) {
        double lambda = e, phi = n;
        double keskimerd = 3500000;
        double keskimerdlamda = 27;
        double keskimerdlamdarad = Math.toRadians(keskimerdlamda);
        double lamdarad = Math.toRadians(lambda);
        double phirad = Math.toRadians(phi);
        double v2 = 1 + 0.00676717019722 * Math.cos(phirad) * Math.cos(phirad);
        double l = lamdarad - keskimerdlamdarad;
        double tanxhi = Math.tan(phirad) / (Math.cos(Math.sqrt(v2) * l));
        double atan = Math.atan(tanxhi);
        double p = 6367654.5 * atan - 16107.0347*Math.sin(2 * atan) +
                16.9762 * Math.sin(4 * atan) - 0.0223 * Math.sin(6 * atan);
        double v22 = 1+0.00676817019722 * Math.cos(atan) * Math.cos(atan);
        double sinhyc = Math.cos(atan) * Math.tan(l) / Math.sqrt(v22);
        double y = Math.log(sinhyc + Math.sqrt(sinhyc * sinhyc + 1)) * 6399936.608;
        double i = keskimerd + y;
        int[] result = {(int)Math.round(p - 47), (int)Math.round(i + 175)};
        return result;
    }

    public static void YKJToWGS(int p, int i) {

    }

}
