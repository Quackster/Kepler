package net.h4bbo.http.kepler.controllers.site;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.alexdev.duckhttpd.response.ResponseBuilder;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.MimeType;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.dao.mysql.PlayerStatisticsDao;
import net.h4bbo.kepler.dao.mysql.ReferredDao;
import net.h4bbo.kepler.game.misc.figure.FigureManager;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.kepler.util.FigureUtil;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.http.kepler.dao.RegisterDao;
import net.h4bbo.http.kepler.util.*;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class RegisterController {
    public static void register(WebConnection webConnection) throws Exception {
        if (webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/me");
            return;
        }

        int maxConnectionsPerIp = GameConfiguration.getInstance().getInteger("max.connections.per.ip");
        String ipAddress = webConnection.getIpAddress();

        if (PlayerDao.countIpAddress(ipAddress) >= maxConnectionsPerIp) {
            webConnection.session().set("alertMessage", "You already have enough accounts registered");
            webConnection.redirect("/");
            return;
        }

        if (webConnection.cookies().exists(SessionUtil.MACHINE_ID)) {
            var machineId = webConnection.cookies().get(SessionUtil.MACHINE_ID);

            if (PlayerDao.countMachineId("#" + machineId) >= maxConnectionsPerIp) {
                webConnection.session().set("alertMessage", "You already have enough accounts registered");
                webConnection.redirect("/");
                return;
            }
        }

        if (GameConfiguration.getInstance().getBoolean("registration.disabled")) {
            var template = webConnection.template("register_disabled");
            template.render();
            return;
        }

        int referral = 0;

        try {
            referral = webConnection.get().contains("referral") ? webConnection.get().getInt("referral") : 0;
        } catch (Exception ignored) {

        }

        if (referral > 0) {
            webConnection.session().set("referral", referral);
        }

        if (webConnection.post().queries().size() > 3) {
            String[] checkEmpty = new String[]{"bean.avatarName", "bean.captchaResponse", "retypedPassword", "bean.email"};

            for (var field : checkEmpty) {
                if (webConnection.post().queries().containsKey(field) && webConnection.post().getString(field).isBlank()) {
                    webConnection.session().set("captcha.invalid", false);
                    webConnection.redirect("/register?errorCode=blank_fields");
                    break;
                }
            }

            String username = "";//HtmlUtil.removeHtmlTags(webConnection.session().getString("registerUsername"));
            String email = "";//HtmlUtil.removeHtmlTags(webConnection.session().getString("registerEmail"));

            if (webConnection.post().contains("registerUsername")) {
                username = HtmlUtil.removeHtmlTags(webConnection.session().getString("registerUsername"));
            }

            if (webConnection.post().contains("registerEmail")) {
                email = HtmlUtil.removeHtmlTags(webConnection.session().getString("registerEmail"));
            }

            if (webConnection.post().contains("bean.avatarName")) {
                username = HtmlUtil.removeHtmlTags(webConnection.post().getString("bean.avatarName"));
            }

            if (webConnection.post().contains("bean.email")) {
                email = HtmlUtil.removeHtmlTags(webConnection.post().getString("bean.email"));
            }

            //String username = HtmlUtil.removeHtmlTags(webConnection.post().getString("bean.avatarName"));
            //String email = HtmlUtil.removeHtmlTags(webConnection.post().getString("bean.email"));

            if (webConnection.post().queries().size() > 10) {
                String password = HtmlUtil.removeHtmlTags(webConnection.post().getString("retypedPassword"));
                String day = HtmlUtil.removeHtmlTags(webConnection.post().getString("bean.day"));
                String month = HtmlUtil.removeHtmlTags(webConnection.post().getString("bean.month"));
                String year = HtmlUtil.removeHtmlTags(webConnection.post().getString("bean.year"));
                String figure = null;
                String gender = null;

                if (webConnection.post().contains("randomFigure")) {
                    String temp = HtmlUtil.removeHtmlTags(webConnection.post().getString("randomFigure"));
                    figure = temp.substring(2);
                    gender = temp.substring(0, 1);
                } else {
                    figure = HtmlUtil.removeHtmlTags(webConnection.post().getString("bean.figure"));
                    gender = HtmlUtil.removeHtmlTags(webConnection.post().getString("bean.gender"));
                }

                webConnection.session().set("registerUsername", username);
                webConnection.session().set("registerPassword", password);
                webConnection.session().set("registerShowPassword", password.replaceAll("(?s).", "*"));
                webConnection.session().set("registerFigure", figure);
                webConnection.session().set("registerGender", gender);
                webConnection.session().set("registerEmail", email);
                webConnection.session().set("registerDay", day);
                webConnection.session().set("registerMonth", month);
                webConnection.session().set("registerYear", year);

                if (!FigureManager.getInstance().validateFigure(figure, gender, false)) {
                    webConnection.redirect("/register?error=bad_look");
                    return;
                }

                if (!RegisterUtil.isValidName(username)) {
                    webConnection.redirect("/register?error=bad_username");
                    return;
                }

                if (!RegisterUtil.isValidEmail(email)) {
                    webConnection.session().set("email.invalid", true);
                    webConnection.redirect("/register?error=bad_email");
                    return;
                }
            }

            String captchaResponse = HtmlUtil.removeHtmlTags(webConnection.post().getString("bean.captchaResponse"));

            if (!captchaResponse.equals(webConnection.session().getString("captcha-text"))) {
                webConnection.session().set("captcha.invalid", true);
                webConnection.redirect("/register?error=bad_captcha");
                return;
            } else {
                if (webConnection.post().contains("bean.email")) {
                    email = HtmlUtil.removeHtmlTags(webConnection.post().getString("bean.email"));
                    webConnection.session().set("registerEmail", email);
                }

                if (!RegisterUtil.isValidEmail(webConnection.session().getString("registerEmail"))) {
                    webConnection.session().set("email.invalid", true);
                    webConnection.redirect("/register?error=bad_email");
                    return;
                }

                String hashedPassword = PlayerManager.getInstance().createPassword(webConnection.session().getString("registerPassword"));
                int userId = RegisterDao.newUser(
                        webConnection.session().getString("registerUsername"),
                        hashedPassword,
                        webConnection.session().getString("registerFigure"),
                        webConnection.session().getString("registerGender"),
                        webConnection.session().getString("registerEmail"));

                String activationCode = UUID.randomUUID().toString();
                PlayerStatisticsDao.newStatistics(userId, activationCode);

                if (GameConfiguration.getInstance().getBoolean("email.smtp.enable")) {
                    EmailUtil.send(webConnection, webConnection.session().getString("registerEmail"), "Activate your account at Classic Habbo",
                            EmailUtil.renderRegistered(
                                    userId,
                                    webConnection.session().getString("registerUsername"),
                                    webConnection.session().getString("registerEmail"),
                                    activationCode
                            )
                    );
                }

                var latestIpAddress = PlayerDao.getLatestIp(userId);

                if (latestIpAddress == null || !latestIpAddress.equals(ipAddress)) {
                    PlayerDao.logIpAddress(userId, ipAddress);
                }

                referral = webConnection.session().getInt("referral");

                if (referral > 0) {
                    ReferredDao.addReferred(referral, userId);
                }

                webConnection.session().delete("referral");
                webConnection.session().delete("captcha.invalid");

                webConnection.session().set("user.id", userId);
                webConnection.session().set("authenticated", true);

                /*
                if (GameConfiguration.getInstance().getBoolean("free.month.hc.registration")) {
                    PlayerDao.saveSubscription(userId, 0, DateUtil.getCurrentTimeSeconds() + TimeUnit.DAYS.toSeconds(30));
                }
                 */

                /*if (webConnection.cookies().exists(SessionUtil.MACHINE_ID)) {
                    var machineId = webConnection.cookies().get(SessionUtil.MACHINE_ID);

                    if (PlayerDao.countMachineId("#" + machineId) > 0) {
                        PlayerDao.setMachineId(userId, "#" + machineId);
                        webConnection.cookies().set(SessionUtil.MACHINE_ID, machineId.replace("#", ""), 2, TimeUnit.DAYS);
                    }
                }*/

                //AlertsDao.createAlert(userId, AlertType.CREDIT_DONATION, "<strong>A reminder</strong><br>If you play for 5 minutes daily, you will receive 120 credits!");
                webConnection.redirect("/welcome");
            }

            //webConnection.session().delete("captcha-text");
        } else {
            var template = webConnection.template("register");

            if (webConnection.session().getBoolean("captcha.invalid") || webConnection.session().getBoolean("email.invalid")) {
                if (webConnection.session().getBoolean("captcha.invalid")) {
                    template.set("registerCaptchaInvalid", true);
                }

                if (webConnection.session().getBoolean("email.invalid")) {
                    template.set("registerEmailInvalid", true);
                }

                template.set("registerUsername", webConnection.session().getString("registerUsername"));
                template.set("registerShowPassword", webConnection.session().getString("registerPassword").replaceAll("(?s).", "*"));
                template.set("registerFigure", webConnection.session().getString("registerFigure"));
                template.set("registerGender", webConnection.session().getString("registerGender"));
                template.set("registerEmail", webConnection.session().getString("registerEmail"));
                template.set("registerDay", webConnection.session().getString("registerDay"));
                template.set("registerMonth", webConnection.session().getString("registerMonth"));
                template.set("registerYear", webConnection.session().getString("registerYear"));
            }

            template.set("randomNum", ThreadLocalRandom.current().nextInt(0, 10000));
            template.set("randomFemaleFigure1", FigureUtil.getRandomFigure("F", false));
            template.set("randomFemaleFigure2", FigureUtil.getRandomFigure("F", false));
            template.set("randomFemaleFigure3", FigureUtil.getRandomFigure("F", false));

            template.set("randomMaleFigure1", FigureUtil.getRandomFigure("M", false));
            template.set("randomMaleFigure2", FigureUtil.getRandomFigure("M", false));
            template.set("randomMaleFigure3", FigureUtil.getRandomFigure("M", false));

            template.set("referral", webConnection.session().getInt("referral"));

            template.render();
        }
    }

    public static void registerCancelled(WebConnection webConnection) {
        webConnection.session().delete("referral");
        webConnection.session().delete("captcha.invalid");
        webConnection.redirect("/");
    }

    public static void captcha(WebConnection webConnection) {
        String captchaText = Captcha.generateText(7);
        webConnection.session().set("captcha-text", captchaText);

        FullHttpResponse response = ResponseBuilder.create(
                HttpResponseStatus.OK, MimeType.getContentType("png"), Captcha.generateImage(captchaText)
        );

        webConnection.send(response);
    }
}
