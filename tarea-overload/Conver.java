/**
 * Conver
 */
public class Conver {
  public static Integer convI(String x) {
    return Integer.parseInt(x);
  }

  public static Double convD(String x) {
    return Double.parseDouble(x);
  }

  public static String convS(Integer x) {
    return x.toString();
  }

  public static String convS(Double x) {
    return x.toString();
  }

  public static Integer redondeo(Double x) {
    double x0 = x.doubleValue();
    return ((int) Math.round(x0));
  }

  public static Double redondeo(Double x, int d) {
    double x0 = x.doubleValue();
    return ((double) Math.round(x0));
  }

  public static Integer raiz(Integer x) {
    return ((int) Math.sqrt(x));
  }

  public static Double raiz(Double x, int d) {
    return ((double) Math.round(x));
  }
}
