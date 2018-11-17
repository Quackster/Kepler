package org.alexdev.kepler.util;

import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.util.config.GameConfiguration;

import java.text.Normalizer;
import java.util.*;

public class StringUtil {
    /**
     * Checks if is null or empty.
     *
     * @param param the param
     * @return true, if is null or empty
     */
    public static boolean isNullOrEmpty(String param) {
        return param == null || param.trim().length() == 0;
    }

    /**
     * Filter input.
     *
     * @param input the input
     * @param filerNewline if new lines (ENTER) should be filtered
     * @return the string
     */
    public static String filterInput(String input, boolean filerNewline) {
        input = input.replace((char)1, ' ');
        input = input.replace((char)2, ' ');
        input = input.replace((char)9, ' ');
        input = input.replace((char)10, ' ');
        input = input.replace((char)12, ' ');

        if (filerNewline) {
            input = input.replace((char)13, ' ');
        }

        if (GameConfiguration.getInstance().getBoolean("normalise.input.strings")) {
            input = Normalizer.normalize(input, Normalizer.Form.NFD);
        }
        
        return input;
    }

    /**
     * Paginate a list of items.
     *
     * @param <T> the generic type
     * @param originalList the original list
     * @param chunkSize the chunk size
     * @return the list
     */
    public static <T> Map<Integer, List<T>> paginate(List<T> originalList, int chunkSize) {
        Map<Integer, List<T>> chunks = new LinkedHashMap<>();
        List<List<T>> listOfChunks = new ArrayList<>();

        for (int i = 0; i < originalList.size() / chunkSize; i++) {
            listOfChunks.add(originalList.subList(i * chunkSize, i * chunkSize + chunkSize));
        }

        if (originalList.size() % chunkSize != 0) {
            listOfChunks.add(originalList.subList(originalList.size() - originalList.size() % chunkSize, originalList.size()));
        }

        for (int i = 0; i < listOfChunks.size(); i++) {
            chunks.put(i, listOfChunks.get(i));
        }

        return chunks;
    }

    /**
     * Round to two decimal places.
     *
     * @param decimal the decimal
     * @return the double
     */
    public static double format(double decimal) {
        return Math.round(decimal * 100.0) / 100.0;
    }

    /**
     * Split.
     *
     * @param str the string
     * @param delim the delimiter
     * @return the list
     */
    public static List<String> split(String str, String delim) {
        return new ArrayList<>(Arrays.asList(str.split(delim)));
    }
}
