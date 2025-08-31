package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.wordfilter.WordfilterWord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class WordfilterDao {
    public static List<WordfilterWord> getBadWords() {
        List<WordfilterWord> word = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM wordfilter", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                word.add(new WordfilterWord(resultSet.getString("word"), resultSet.getBoolean("is_bannable"), resultSet.getBoolean("is_filterable")));
            }
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return word;
    }
}
