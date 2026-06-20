/**
 * Multithread
 */
public class Multithread {

  public static void main(String[] args) {
    String nombre[] = {
        "Gabriel",
        "Laura",
        "Ana",
        "Leo",
        "Francisca"
    };
    for (int i = 0; i < nombre.length; i++) {
      try {
        Thread.sleep(1000);
        System.out.println(nombre[i]);

      } catch (Exception e) {
        System.out.println(e.getLocalizedMessage());
      }
    }
  }
}
