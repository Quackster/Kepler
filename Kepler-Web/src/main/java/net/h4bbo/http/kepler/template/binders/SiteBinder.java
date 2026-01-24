package net.h4bbo.http.kepler.template.binders;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import org.alexdev.duckhttpd.template.TemplateBinder;
import net.h4bbo.http.kepler.Routes;
import net.h4bbo.http.kepler.server.Watchdog;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.http.kepler.util.Captcha;

import java.text.NumberFormat;
import java.util.Locale;

public class SiteBinder implements TemplateBinder {
    private String siteName;
    private String sitePath;

    private String loaderGameIp;
    private String loaderGamePort;

    private String loaderMusIp;
    private String loaderMusPort;

    private String loaderDcr;
    private String loaderVariables;
    private String loaderTexts;

    private int usersOnline;
    private boolean serverOnline;
    private String formattedUsersOnline;

    private int visits;
    private String housekeepingPath;
    private String staticContentPath;

    private String loaderFlashBase;
    private String loaderFlashSwf;
    private String loaderFlashTexts;
    private String loaderFlashVariables;

    private String loaderFlashBetaBase;
    private String loaderFlashBetaSwf;
    private String loaderFlashBetaTexts;
    private String loaderFlashBetaVariables;

    private String emailSiteName;
    private String emailHotelName;
    private String emailStaticPath;
    private String furniImagerPath;

    private Captcha captcha;

    @Override
    public void onRegister(Template template, WebConnection webConnection) {
        this.captcha = new Captcha();

        this.siteName = GameConfiguration.getInstance().getString("site.name");
        this.sitePath = GameConfiguration.getInstance().getString("site.path");
        this.staticContentPath = GameConfiguration.getInstance().getString("static.content.path");
        this.furniImagerPath = "https://classichabbo.com/imager/furni";

        this.emailStaticPath = GameConfiguration.getInstance().getString("email.static.content.path");
        this.emailHotelName = GameConfiguration.getInstance().getString("site.path").replace("https://", "").replace("http://", "").replace("/", "").toUpperCase();

        this.loaderGameIp = GameConfiguration.getInstance().getString("loader.game.ip");
        this.loaderGamePort = GameConfiguration.getInstance().getString("loader.game.port");

        this.loaderMusIp = GameConfiguration.getInstance().getString("loader.mus.ip");
        this.loaderMusPort = GameConfiguration.getInstance().getString("loader.mus.port");

        this.loaderDcr = GameConfiguration.getInstance().getString("loader.dcr");
        this.loaderVariables = GameConfiguration.getInstance().getString("loader.external.variables");
        this.loaderTexts = GameConfiguration.getInstance().getString("loader.external.texts");

        this.loaderFlashBase = GameConfiguration.getInstance().getString("loader.flash.base");
        this.loaderFlashSwf = GameConfiguration.getInstance().getString("loader.flash.swf");
        this.loaderFlashTexts = GameConfiguration.getInstance().getString("loader.flash.external.texts");
        this.loaderFlashVariables = GameConfiguration.getInstance().getString("loader.flash.external.variables");

        this.loaderFlashBetaBase = GameConfiguration.getInstance().getString("loader.flash.beta.base");
        this.loaderFlashBetaSwf = GameConfiguration.getInstance().getString("loader.flash.beta.swf");
        this.loaderFlashBetaTexts = GameConfiguration.getInstance().getString("loader.flash.beta.external.texts");
        this.loaderFlashBetaVariables = GameConfiguration.getInstance().getString("loader.flash.beta.external.variables");

        this.serverOnline = Watchdog.IS_SERVER_ONLINE;
        this.usersOnline = Watchdog.USERS_ONLNE;
        this.formattedUsersOnline = NumberFormat.getNumberInstance(Locale.US).format(this.usersOnline);

        this.visits = Watchdog.LAST_VISITS;
        this.housekeepingPath = Routes.HOUSEKEEPING_PATH;

        template.set("site", this);
        template.set("gameConfig", GameConfiguration.getInstance());
    }

    public String getSiteName() {
        return siteName;
    }

    public String getSitePath() {
        return sitePath;
    }

    public String getLoaderGameIp() {
        return loaderGameIp;
    }

    public String getLoaderGamePort() {
        return loaderGamePort;
    }

    public String getLoaderMusIp() {
        return loaderMusIp;
    }

    public String getLoaderMusPort() {
        return loaderMusPort;
    }

    public String getLoaderDcr() {
        return loaderDcr;
    }

    public String getLoaderVariables() {
        return loaderVariables;
    }

    public String getLoaderTexts() {
        return loaderTexts;
    }

    public int getUsersOnline() {
        return usersOnline;
    }

    public boolean isServerOnline() {
        return serverOnline;
    }

    public String getFormattedUsersOnline() {
        return formattedUsersOnline;
    }

    public int getVisits() {
        return visits;
    }

    public String getHousekeepingPath() {
        return housekeepingPath;
    }

    public String getStaticContentPath() {
        return staticContentPath;
    }

    public String getLoaderFlashBase() {
        return loaderFlashBase;
    }

    public String getLoaderFlashSwf() {
        return loaderFlashSwf;
    }

    public String getLoaderFlashTexts() {
        return loaderFlashTexts;
    }

    public String getLoaderFlashVariables() {
        return loaderFlashVariables;
    }

    public String getLoaderFlashBetaBase() {
        return loaderFlashBetaBase;
    }

    public String getLoaderFlashBetaSwf() {
        return loaderFlashBetaSwf;
    }

    public String getLoaderFlashBetaTexts() {
        return loaderFlashBetaTexts;
    }

    public String getLoaderFlashBetaVariables() {
        return loaderFlashBetaVariables;
    }

    public String getEmailSiteName() {
        return emailSiteName;
    }

    public String getEmailHotelName() {
        return emailHotelName;
    }

    public String getEmailStaticPath() {
        return emailStaticPath;
    }

    public String getFurniImagerPath() {
        return furniImagerPath;
    }

    public Captcha getCaptcha() {
        return captcha;
    }
}
