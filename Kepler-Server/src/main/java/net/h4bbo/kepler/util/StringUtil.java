package net.h4bbo.kepler.util;

import com.google.gson.Gson;
import net.h4bbo.kepler.dao.mysql.TagDao;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.util.config.GameConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class StringUtil {
    public static Gson GSON = new Gson();

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
     * @param input        the input
     * @param filerNewline if new lines (ENTER) should be filtered
     * @return the string
     */
    public static String filterInput(String input, boolean filerNewline) {
        input = input.replace((char) 1, ' ');
        input = input.replace((char) 2, ' ');
        input = input.replace((char) 9, ' ');
        input = input.replace((char) 10, ' ');
        input = input.replace((char) 12, ' ');

        if (filerNewline) {
            input = input.replace((char) 13, ' ');
        }

        if (GameConfiguration.getInstance().getBoolean("normalise.input.strings")) {
            input = Normalizer.normalize(input, Normalizer.Form.NFD);
        }

        if (input.contains("∂") && input.contains("∫") && input.contains("å") && input.contains("æ")) {
            input = input.replace("∂", "");
            input = input.replace("∫", "");
            input = input.replace("å", "");
            input = input.replace("æ", "");
        }

        return input;
    }

    /**
     * Paginate a list of items.
     *
     * @param <T>          the generic type
     * @param originalList the original list
     * @param chunkSize    the chunk size
     * @return the list
     */
    public static <T> Map<Integer, List<T>> paginate(List<T> originalList, int chunkSize) {
        return paginate(originalList, chunkSize, false);
    }

    public static <T> Map<Integer, List<T>> paginate(List<T> originalList, int chunkSize, boolean emptyFirstPage) {
        Map<Integer, List<T>> chunks = new ConcurrentHashMap<>();
        List<List<T>> listOfChunks = new CopyOnWriteArrayList<>();

        for (int i = 0; i < originalList.size() / chunkSize; i++) {
            listOfChunks.add(originalList.subList(i * chunkSize, i * chunkSize + chunkSize));
        }

        if (originalList.size() % chunkSize != 0) {
            listOfChunks.add(originalList.subList(originalList.size() - originalList.size() % chunkSize, originalList.size()));
        }

        for (int i = 0; i < listOfChunks.size(); i++) {
            chunks.put(i, listOfChunks.get(i));
        }

        if (emptyFirstPage && chunks.isEmpty()) {
            chunks.put(0, new ArrayList<>());
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
     * @param str   the string
     * @param delim the delimiter
     * @return the list
     */
    public static List<String> split(String str, String delim) {
        return new ArrayList<>(Arrays.asList(str.split(delim)));
    }

    /**
     * Get words in a string
     *
     * @param s the string to get the list for
     * @return the list of words
     */
    public static String[] getWords(String s) {
        String[] words = s.split("\\s+");

        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].replaceAll("[^\\w]", "");
        }

        return words;
    }

    /**
     * Get encoding for strings
     *
     * @return the encoding
     */
    public static Charset getCharset() {
        return StandardCharsets.UTF_8;
    }

    public static String isValidTag(String tag, int userId, int roomId, int groupId) {
        String formatTag = StringUtils.normalizeSpace((StringUtil.filterInput(tag, false))).replaceAll("\\<[^>]*>", "").replace(",", "").toLowerCase();

        if (tag.length() <= 1 || tag.trim().isEmpty() || tag.length() > 20 || TagDao.hasTag(userId, roomId, groupId, tag)) {
            return null;
        }

        return formatTag;
    }

    public static String toAlphabetic(int i) {
        i = i - 1;

        if (i < 0) {
            return "-" + toAlphabetic(-i - 1);
        }

        int quot = i / 26;
        int rem = i % 26;
        char letter = (char) ((int) 'A' + rem);
        if (quot == 0) {
            return "" + letter;
        } else {
            return toAlphabetic(quot - 1) + letter;
        }
    }

    public static void addTag(String tag, int userId, int roomId, int groupId) {
        boolean checkAgain = false;

        if (tag.equalsIgnoreCase("br") || tag.equalsIgnoreCase("brasil")) {
            tag = "brazil";
            checkAgain = true;
        }

        if (tag.equalsIgnoreCase("spanish") || tag.equalsIgnoreCase("es")) {
            tag = "español";
            checkAgain = true;
        }

        if (checkAgain) {
            if (TagDao.hasTag(userId, roomId, groupId, tag)) {
                return;
            }
        }

        TagDao.addTag(userId, roomId, groupId, tag);
    }

    public static boolean hasValue(List<String> firstList, List<String> secondList) {
        for (var str : firstList) {
            if (secondList.contains(str)) {
                return true;
            }
        }

        return false;
    }

    public static String replaceAlertMessage(String message, Player player) {
        String newString = message;
        newString = newString.replace("\r\n", "<br>");
        newString = newString.replace("\r", "<br>");
        newString = newString.replace("\n", "<br>");

        /*
        if (player.getNetwork().isFlashConnection()) {
            newString = newString.replace("<br>", "\r");
        }*/

        newString = newString.replace("%username%", player.getDetails().getName());
        return newString;
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
