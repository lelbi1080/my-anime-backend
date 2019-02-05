package be.ben;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {


    public static int hammingDistance(String s1, String s2) {
        int cpt = Math.abs(s1.length() - s2.length());
        int shortest = s1.length() < s2.length() ? s1.length() : s2.length();

        for (int i = 0; i < shortest; i++) {
            if (s1.charAt(i) != s2.charAt(i))
                cpt++;
        }

        return cpt;
    }

    public static String sanitize(String s) {
        return s.toLowerCase()
                .replaceAll("(vostfr|tv|vf|vo|oav|film|movie)", "")
                .replaceAll("[-=_/ ()\\\\]", "");
    }

    public static void main(String[] args) throws Exception {
       /* String name = "DRagon BaLlz";
        String[] db = new String[]{"Dragon Ball Z", "Dragon BaLl-- kaI", "Dragon Ronpa", "Naruto", "Ajin (TV)", "Bleach", "Ajin vostfr"};
        ArrayList<Integer> distances = new ArrayList<>();
        for(int i = 0; i < db.length; i++) {
            distances.add(hammingDistance(name.toLowerCase(), sanitize(db[i])));
            System.out.println("La distance de Hamming entre " + name + " et " + db[i] + " (sanitized = " + sanitize(db[i]) + ") " + " est de : " + distance);
        }
        Integer min = Collections.min(distances);

        Integer[] elts = (Integer[]) distances.stream().filter(value -> {
            return min.equals(value);
        }).toArray();

        System.out.println("Les mangas à link sont : " + Arrays.toString(elts));*/
        new SpringApplicationBuilder(Application.class).profiles("dev").build().run(args);

    }

}
