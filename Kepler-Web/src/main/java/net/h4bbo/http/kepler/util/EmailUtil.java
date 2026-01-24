package net.h4bbo.http.kepler.util;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.http.kepler.KeplerWeb;
import net.h4bbo.http.kepler.dao.EmailDao;
import net.h4bbo.http.kepler.template.TwigTemplate;
import org.apache.commons.validator.routines.EmailValidator;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.util.Properties;

public class EmailUtil {
    private static final long EMAIL_COOLDOWN = 2*60;

    public static boolean send(WebConnection webConnection, String targetEmail, String subject, String renderedHTML) {
        if (!GameConfiguration.getInstance().getBoolean("email.smtp.enable")) {
            return true;
        }

        if (!EmailValidator.getInstance().isValid(targetEmail)) {
            return false;
        }

        if (webConnection.session().contains("lastEmailTime")) {
            long lastEmailTime = Long.parseLong(webConnection.session().getString("lastEmailTime")) + EMAIL_COOLDOWN;

            if (lastEmailTime > DateUtil.getCurrentTimeSeconds()) {
                webConnection.session().set("alertMessage", "Please wait a few minutes before sending an email again");
                webConnection.session().set("alertColour", "red");
                return false;
            }
        }

        webConnection.session().set("lastEmailTime", String.valueOf(DateUtil.getCurrentTimeSeconds()));

        KeplerWeb.getExecutor().execute(() -> {
            try {
                Properties prop = new Properties();
                prop.put("mail.smtp.auth", true);
                prop.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
                prop.put("mail.smtp.socketFactory.fallback","false");
                prop.put("mail.smtp.socketFactory.port", "465");
                prop.put("mail.smtp.host", GameConfiguration.getInstance().getString("email.smtp.host"));
                prop.put("mail.smtp.port", GameConfiguration.getInstance().getString("email.smtp.port"));
                prop.put("mail.smtp.connectiontimeout", "5000");
                prop.put("mail.smtp.timeout", "5000");

                Session session = Session.getInstance(prop, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        String username = GameConfiguration.getInstance().getString("email.smtp.login.username");
                        String password = GameConfiguration.getInstance().getString("email.smtp.login.password");
                        return new PasswordAuthentication(username, password);
                    }
                });
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(GameConfiguration.getInstance().getString("email.smtp.from.email"), GameConfiguration.getInstance().getString("email.smtp.from.name")));
                message.setRecipients(
                        Message.RecipientType.TO, InternetAddress.parse(targetEmail));
                message.setSubject(subject);

                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setContent(renderedHTML, "text/html");

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(mimeBodyPart);

                message.setContent(multipart);
                Transport.send(message);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        return true;
    }

    public static String renderRegistered(int playerId, String playerName, String playerEmail, String activationCode) {
        var tpl = new TwigTemplate(null);
        tpl.start("account/email/email_registered");
        tpl.set("playerId", playerId);
        tpl.set("playerName", playerName);
        tpl.set("playerEmail", playerEmail);
        tpl.set("activationCode", activationCode);
        try {
            return tpl.renderHTML();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String renderActivate(int playerId, String playerName, String playerEmail, String activationCode) {
        var tpl = new TwigTemplate(null);
        tpl.start("account/email/email_activate");
        tpl.set("playerId", playerId);
        tpl.set("playerName", playerName);
        tpl.set("playerEmail", playerEmail);
        tpl.set("activationCode", activationCode);
        try {
            return tpl.renderHTML();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String renderPasswordRecovery(int playerId, String playerName, String recoveryCode) {
        var tpl = new TwigTemplate(null);
        tpl.start("account/email/email_recovery");
        tpl.set("playerId", playerId);
        tpl.set("playerName", playerName);
        tpl.set("recoveryCode", recoveryCode);
        try {
            return tpl.renderHTML();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean isAlreadyTradePass(int userId, String email) {
        return EmailDao.hasUserTradePass(userId, email);
    }
}
