package net.h4bbo.http.kepler.controllers.site;

import net.h4bbo.kepler.dao.mysql.TransactionDao;
import net.h4bbo.kepler.game.item.Transaction;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.PlayerRank;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.http.kepler.util.XSSUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class CreditsController {
    public static void credits(WebConnection webConnection) {
        XSSUtil.createKey(webConnection, "/credits");

        var template = webConnection.template("credits");
        webConnection.session().set("page", "credits");
        template.render();
    }

    public static void transactions(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        Calendar presentDayCalendar = Calendar.getInstance();
        Calendar currentCalendar = Calendar.getInstance();
        Calendar futureCalendar = Calendar.getInstance();
        Calendar previousCalendar = Calendar.getInstance();
        
        var template = webConnection.template("credits_history");
        var details = (PlayerDetails) template.get("playerDetails");

        int userId = details.getId();
        boolean viewAll = false;

        if (details.getRank().getRankId() >= PlayerRank.MODERATOR.getRankId()) {
            viewAll = true;

            try {
                if (webConnection.get().contains("userId"))
                    userId = webConnection.get().getInt("userId");
            } catch (Exception ex) {

            }
        }

        boolean hasDateParameter = webConnection.get().contains("period") && DateUtil.getFromFormat("yyyy-MM-dd", webConnection.get().getString("period")) > 0;
        List<Transaction> transactionsThisMonth = null;


        if (hasDateParameter) {
            long time = DateUtil.getFromFormat("yyyy-MM-dd", webConnection.get().getString("period"));

            currentCalendar.setTimeInMillis(time * 1000);
            futureCalendar.setTimeInMillis(time * 1000);
            previousCalendar.setTimeInMillis(time * 1000);
        }
        
        previousCalendar.add(Calendar.MONTH, -1);
        futureCalendar.add(Calendar.MONTH, 1);

        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH) + 1;
        transactionsThisMonth = TransactionDao.getTransactions(userId, month, year, viewAll);

        template.set("canGoNext", currentCalendar.get(Calendar.MONTH) != presentDayCalendar.get(Calendar.MONTH) || currentCalendar.get(Calendar.YEAR) != (presentDayCalendar.get(Calendar.YEAR)));

        String previousMonth = new SimpleDateFormat("MMMM").format(previousCalendar.getTime());
        int previousYear = previousCalendar.get(Calendar.YEAR);
        int previousNumericalMonth = previousCalendar.get(Calendar.MONTH) + 1;

        template.set("previousYear", previousYear);
        template.set("previousMonth", previousMonth);
        template.set("previousNumericalMonth", previousNumericalMonth);

        String futureMonth = new SimpleDateFormat("MMMM").format(futureCalendar.getTime());
        int futureYear = futureCalendar.get(Calendar.YEAR);
        int futureNumericalMonth = futureCalendar.get(Calendar.MONTH) + 1;

        template.set("futureYear", futureYear);
        template.set("futureMonth", futureMonth);
        template.set("futureNumericalMonth", futureNumericalMonth);

        webConnection.session().set("page", "credits");

        template.set("currentYear", year);
        template.set("currentMonth", new SimpleDateFormat("MMMM").format(currentCalendar.getTime()));

        template.set("transactions", transactionsThisMonth);
        template.render();
    }
}