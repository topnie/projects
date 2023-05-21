package webapp.bank.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Waluta {
    private Map<String, Long> wartosciWalut;

    public Waluta() {
        wartosciWalut = new HashMap<>();
        wartosciWalut.put("PLN", 1L);
        wartosciWalut.put("USD", 4L);
        wartosciWalut.put("EUR", 5L);
    }

    public long convertWaluta(String waluta1, String waluta2, long amount) {
        long val1 = wartosciWalut.get(waluta1);
        long val2 = wartosciWalut.get(waluta2);
        float x = (float) val1 / (float) val2;
        return (long)((float)amount * x);
    }

    public List<String> getAll() {
        return new LinkedList<>(wartosciWalut.keySet());
    }
}
