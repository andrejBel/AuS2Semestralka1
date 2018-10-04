package Utils;

import java.text.SimpleDateFormat;
import java.util.Random;

public class Helper {

    private static Random GENERATOR = new Random();
    private static SimpleDateFormat SIMPLE_DATE_FORMASTTER =  new SimpleDateFormat("dd.MM.yyyy");

    private Helper() {}

    public static String GetNahodneRodneCislo() {
        String abeceda = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder rodneCislo = new StringBuilder();

        while (rodneCislo.length() < 16) { // length of the random string.
            int index = (int) (GENERATOR.nextFloat() * abeceda.length());
            rodneCislo.append(abeceda.charAt(index));
        }
        return rodneCislo.toString();
    }

    public static long GetNahodnyDatumNarodenia() {
        return ((long)(GENERATOR.nextDouble()*(1262304000000L)));
    }

    String FormatujDatum(long date) {
        return SIMPLE_DATE_FORMASTTER.format(date);
    }

}
