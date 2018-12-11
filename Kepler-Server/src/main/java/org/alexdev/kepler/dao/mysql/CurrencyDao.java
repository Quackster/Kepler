package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.player.PlayerDetails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CurrencyDao {

    /**
     * Atomically increase credits.
     */
    public static void increaseCredits(Map<PlayerDetails, Integer> playerMap) {
        Connection conn = null;
        PreparedStatement updateQuery = null;
        PreparedStatement fetchQuery = null;
        ResultSet row = null;

        try {
            conn = Storage.getStorage().getConnection();

            // We disable autocommit to make sure the following queries share the same atomic transaction
            conn.setAutoCommit(false);

            // Increase credits
            updateQuery = Storage.getStorage().prepare("UPDATE users SET credits = credits + ? WHERE id = ?", conn);

            // Batch increase update queries
            for (var kvp : playerMap.entrySet()) {
                PlayerDetails playerDetails = kvp.getKey();
                int increaseAmount = kvp.getValue();

                updateQuery.setInt(1, increaseAmount);
                updateQuery.setInt(2, playerDetails.getId());

                updateQuery.addBatch();
            }

            // Execute update queries in batch
            updateQuery.executeBatch();

            List<String> playerIds = new ArrayList<>();

            for (var player : playerMap.keySet()) {
                playerIds.add(Integer.toString(player.getId()));
            }

            // String of structure 1, 2, 3 containing playerIds to be used in query below
            String rawPlayerBind = String.join(",", playerIds);

            // Fetch increased amount
            // TODO: find better way to write the IN clause
            // TODO: use temporary table when playerIds list is above max arguments of SQL IN function or size above max_allowed_packet MariaDB configuration setting
            fetchQuery = Storage.getStorage().prepare("SELECT id,credits FROM users WHERE id IN (" + rawPlayerBind + ")", conn);

            // Execute fetch query
            row = fetchQuery.executeQuery();

            // Commit these queries
            conn.commit();

            // Map that holds user id and updated credit amount
            Map<Integer, Integer> updatedAmounts = new HashMap<>();

            // Get all values, we do this to not block the database driver for too long
            while (row != null && row.next()) {
                updatedAmounts.put(row.getInt("id"), row.getInt("credits"));
            }

            for (var kvp : updatedAmounts.entrySet()) {
                var userId = kvp.getKey();
                var amount = kvp.getValue();

                for (var details : playerMap.keySet()) {
                    if (details.getId() != userId) {
                        continue;
                    }

                    details.setCredits(amount);
                }
            }

        } catch (Exception e) {
            try {
                // Rollback these queries
                conn.rollback();
            } catch(SQLException re) {
                Storage.logError(re);
            }

            Storage.logError(e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ce) {
                Storage.logError(ce);
            }

            Storage.closeSilently(row);
            Storage.closeSilently(updateQuery);
            Storage.closeSilently(fetchQuery);
            Storage.closeSilently(conn);
        }
    }

    /**
     * Atomically increase credits.
     *
     * @param details the player details
     */
    public static void increaseCredits(PlayerDetails details, int amount) {
        Connection conn = null;
        PreparedStatement updateQuery = null;
        PreparedStatement fetchQuery = null;
        ResultSet row = null;

        try {
            conn = Storage.getStorage().getConnection();

            // We disable autocommit to make sure the following queries share the same atomic transaction
            conn.setAutoCommit(false);

            // Increase credits
            updateQuery = Storage.getStorage().prepare("UPDATE users SET credits = credits + ? WHERE id = ?", conn);
            updateQuery.setInt(1, amount);
            updateQuery.setInt(2, details.getId());
            updateQuery.execute();

            // Fetch increased amount
            fetchQuery = Storage.getStorage().prepare("SELECT credits FROM users WHERE id = ?", conn);
            fetchQuery.setInt(1, details.getId());
            row = fetchQuery.executeQuery();

            // Commit these queries
            conn.commit();

            // Set amount
            if (row != null && row.next()) {
                int updatedAmount = row.getInt("credits");
                details.setCredits(updatedAmount);
            }

        } catch (Exception e) {
            try {
                // Rollback these queries
                conn.rollback();
            } catch(SQLException re) {
                Storage.logError(re);
            }

            Storage.logError(e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ce) {
                Storage.logError(ce);
            }

            Storage.closeSilently(row);
            Storage.closeSilently(updateQuery);
            Storage.closeSilently(fetchQuery);
            Storage.closeSilently(conn);
        }
    }

    /**
     * Atomically decrease credits.
     *
     * @param details the player details
     */
    public static void decreaseCredits(PlayerDetails details, int amount) {
        if (details.getCredits() <= 0) {
            amount = 0;
        }

        Connection conn = null;
        PreparedStatement updateQuery = null;
        PreparedStatement fetchQuery = null;
        ResultSet row = null;

        try {
            conn = Storage.getStorage().getConnection();

            // We disable autocommit to make sure the following queries share the same atomic transaction
            conn.setAutoCommit(false);

            // Decrease credits
            updateQuery = Storage.getStorage().prepare("UPDATE users SET credits = credits - ? WHERE id = ?", conn);
            updateQuery.setInt(1, amount);
            updateQuery.setInt(2, details.getId());
            updateQuery.execute();

            // Fetch increased amount
            fetchQuery = Storage.getStorage().prepare("SELECT credits FROM users WHERE id = ?", conn);
            fetchQuery.setInt(1, details.getId());
            row = fetchQuery.executeQuery();

            // Commit these queries
            conn.commit();

            // Set amount
            if (row != null && row.next()) {
                int updatedAmount = row.getInt("credits");

                if (updatedAmount < 0) {
                    updatedAmount = 0;
                }

                details.setCredits(updatedAmount);
            }

        } catch (Exception e) {
            try {
                // Rollback these queries
                conn.rollback();
            } catch(SQLException re) {
                Storage.logError(re);
            }

            Storage.logError(e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ce) {
                Storage.logError(ce);
            }

            Storage.closeSilently(row);
            Storage.closeSilently(updateQuery);
            Storage.closeSilently(fetchQuery);
            Storage.closeSilently(conn);
        }
    }

    /**
     * Atomically increase tickets.
     *
     * @param details the player details
     */
    public static void increaseTickets(PlayerDetails details, int amount) {
        Connection conn = null;
        PreparedStatement updateQuery = null;
        PreparedStatement fetchQuery = null;
        ResultSet row = null;

        try {
            conn = Storage.getStorage().getConnection();

            // We disable autocommit to make sure the following queries share the same atomic transaction
            conn.setAutoCommit(false);

            // Increase credits
            updateQuery = Storage.getStorage().prepare("UPDATE users SET tickets = tickets + ? WHERE id = ?", conn);
            updateQuery.setInt(1, amount);
            updateQuery.setInt(2, details.getId());
            updateQuery.execute();

            // Fetch increased amount
            fetchQuery = Storage.getStorage().prepare("SELECT tickets FROM users WHERE id = ?", conn);
            fetchQuery.setInt(1, details.getId());
            row = fetchQuery.executeQuery();

            // Commit these queries
            conn.commit();

            // Set amount
            if (row != null && row.next()) {
                int updatedAmount = row.getInt("tickets");
                details.setTickets(updatedAmount);
            }

        } catch (Exception e) {
            try {
                // Rollback these queries
                conn.rollback();
            } catch(SQLException re) {
                Storage.logError(re);
            }

            Storage.logError(e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ce) {
                Storage.logError(ce);
            }

            Storage.closeSilently(row);
            Storage.closeSilently(updateQuery);
            Storage.closeSilently(fetchQuery);
            Storage.closeSilently(conn);
        }
    }

    /**
     * Atomically decrease tickets.
     *
     * @param details the player details
     */
    public static void decreaseTickets(PlayerDetails details, int amount) {
        if (details.getTickets() <= 0) {
            amount = 0;
        }

        Connection conn = null;
        PreparedStatement updateQuery = null;
        PreparedStatement fetchQuery = null;
        ResultSet row = null;

        try {
            conn = Storage.getStorage().getConnection();

            // We disable autocommit to make sure the following queries share the same atomic transaction
            conn.setAutoCommit(false);

            // Decrease credits
            updateQuery = Storage.getStorage().prepare("UPDATE users SET tickets = tickets - ? WHERE id = ?", conn);
            updateQuery.setInt(1, amount);
            updateQuery.setInt(2, details.getId());
            updateQuery.execute();

            // Fetch increased amount
            fetchQuery = Storage.getStorage().prepare("SELECT tickets FROM users WHERE id = ?", conn);
            fetchQuery.setInt(1, details.getId());
            row = fetchQuery.executeQuery();

            // Commit these queries
            conn.commit();

            // Set amount
            if (row != null && row.next()) {
                int updatedAmount = row.getInt("tickets");

                if (updatedAmount < 0) {
                    updatedAmount = 0;
                }

                details.setTickets(updatedAmount);
            }

        } catch (Exception e) {
            try {
                // Rollback these queries
                conn.rollback();
            } catch(SQLException re) {
                Storage.logError(re);
            }

            Storage.logError(e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ce) {
                Storage.logError(ce);
            }

            Storage.closeSilently(row);
            Storage.closeSilently(updateQuery);
            Storage.closeSilently(fetchQuery);
            Storage.closeSilently(conn);
        }
    }

    /**
     * Atomically increase tickets.
     *
     * @param details the player details
     */
    public static void increaseFilm(PlayerDetails details, int amount) {
        Connection conn = null;
        PreparedStatement updateQuery = null;
        PreparedStatement fetchQuery = null;
        ResultSet row = null;

        try {
            conn = Storage.getStorage().getConnection();

            // We disable autocommit to make sure the following queries share the same atomic transaction
            conn.setAutoCommit(false);

            // Increase credits
            updateQuery = Storage.getStorage().prepare("UPDATE users SET film = film + ? WHERE id = ?", conn);
            updateQuery.setInt(1, amount);
            updateQuery.setInt(2, details.getId());
            updateQuery.execute();

            // Fetch increased amount
            fetchQuery = Storage.getStorage().prepare("SELECT film FROM users WHERE id = ?", conn);
            fetchQuery.setInt(1, details.getId());
            row = fetchQuery.executeQuery();

            // Commit these queries
            conn.commit();

            // Set amount
            if (row != null && row.next()) {
                int updatedAmount = row.getInt("film");
                details.setFilm(updatedAmount);
            }

        } catch (Exception e) {
            try {
                // Rollback these queries
                conn.rollback();
            } catch(SQLException re) {
                Storage.logError(re);
            }

            Storage.logError(e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ce) {
                Storage.logError(ce);
            }

            Storage.closeSilently(row);
            Storage.closeSilently(updateQuery);
            Storage.closeSilently(fetchQuery);
            Storage.closeSilently(conn);
        }
    }

    /**
     * Atomically decrease film.
     *
     * @param details the player details
     */
    public static void decreaseFilm(PlayerDetails details, int amount) {
        if (details.getFilm() <= 0) {
            amount = 0;
        }

        Connection conn = null;
        PreparedStatement updateQuery = null;
        PreparedStatement fetchQuery = null;
        ResultSet row = null;

        try {
            conn = Storage.getStorage().getConnection();

            // We disable autocommit to make sure the following queries share the same atomic transaction
            conn.setAutoCommit(false);

            // Increase credits
            updateQuery = Storage.getStorage().prepare("UPDATE users SET film = film - ? WHERE id = ?", conn);
            updateQuery.setInt(1, amount);
            updateQuery.setInt(2, details.getId());
            updateQuery.execute();

            // Fetch increased amount
            fetchQuery = Storage.getStorage().prepare("SELECT film FROM users WHERE id = ?", conn);
            fetchQuery.setInt(1, details.getId());
            row = fetchQuery.executeQuery();

            // Commit these queries
            conn.commit();

            // Set amount
            if (row != null && row.next()) {
                int updatedAmount = row.getInt("film");

                if (updatedAmount < 0) {
                    updatedAmount = 0;
                }

                details.setFilm(updatedAmount);
            }

        } catch (Exception e) {
            try {
                // Rollback these queries
                conn.rollback();
            } catch(SQLException re) {
                Storage.logError(re);
            }

            Storage.logError(e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ce) {
                Storage.logError(ce);
            }

            Storage.closeSilently(row);
            Storage.closeSilently(updateQuery);
            Storage.closeSilently(fetchQuery);
            Storage.closeSilently(conn);
        }
    }
}
