package net.h4bbo.http.kepler.util;

import java.util.concurrent.ThreadLocalRandom;

public class HomeUtil {
    public static String getRandomAd() {
        String[] advertisements = new String[] {
                "habbo_banner_1.gif",
                "Habbohome_phold_160x600.gif",
                "Habbohome_phold_160x600.gif",
                "Habbohome_phold_160x600.gif",
                "bb2_placeholder.gif",
                "SciFi_spaceholder_160x600_001.gif",
                "HC2_placeh_160x600.gif",
                "battleball_reddevil_br.gif",
                "HC_Promo_160x600_GIF01.gif"

        };

        if (advertisements.length > 0) {
            return advertisements[ThreadLocalRandom.current().nextInt(advertisements.length)];
        }

        return null;
    }

    public static String getRandomValentinesImage() {
        String[] advertisements = new String[] {
                "valentines_1_chanaho.png",
                "valentines_2_ultra.png",
                "valentines_3_santi13.png",
                "valentines_4_santi13.png",
                "valentines_5_rasta.png"

        };

        if (advertisements.length > 0) {
            return advertisements[ThreadLocalRandom.current().nextInt(advertisements.length)];
        }

        return null;
    }

    public static int getStickerLimit(boolean hasClubSubscription) {
        return hasClubSubscription ? 350 : 200;
    }
}
