package net.h4bbo.http.kepler.controllers.api;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.alexdev.duckhttpd.response.ResponseBuilder;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.MimeType;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.http.kepler.server.Watchdog;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.net.HttpURLConnection;

public class ImagerController {
    public static void imager_redirect(WebConnection webConnection) {
        boolean sentFurniResponse = false;

        if (Watchdog.IS_IMAGER_ONLINE) {
            var reqConfig = RequestConfig.custom()
                .setConnectTimeout(GameConfiguration.getInstance().getInteger("site.imaging.endpoint.timeout"))
                .build();

            try (final var httpClient = HttpClientBuilder.create()
                    .setDefaultRequestConfig(reqConfig)
                    .build()) {

                String requestUri = webConnection.request().uri();

                // Ignore all the weird shit from the promo_habbos_v2 swf (it has stuff like figure,s-0.g-1.d-3.h-3.a-0,7e9a0bafa6863e224cdbb2a3b53dcec0 (hash))
                if (requestUri.contains("/avatar/") && requestUri.contains(",")) {
                    requestUri = "/habbo-imaging/avatarimage?figure=" + requestUri.replace("/habbo-imaging/avatar/", "").split(",")[0]
                            + "&size=b&direction=3&head_direction=3&gesture=sml&frame=1";
                }

                HttpGet request = new HttpGet(GameConfiguration.getInstance().getString("site.imaging.endpoint") + requestUri);
                request.addHeader(HttpHeaders.USER_AGENT, "Imager");

                try (var r = httpClient.execute(request)) {
                    HttpEntity entity = r.getEntity();

                    if (entity != null) {
                        if (r.getStatusLine().getStatusCode() == HttpResponseStatus.OK.code()) {
                            FullHttpResponse response = ResponseBuilder.create(
                                    HttpResponseStatus.OK, entity.getContentType().getValue(), EntityUtils.toByteArray(entity)
                            );

                            webConnection.send(response);
                            sentFurniResponse = true;
                        }
                    }
                }
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }

        }

        if (!sentFurniResponse) {
            FullHttpResponse response = ResponseBuilder.create(
                    HttpResponseStatus.NO_CONTENT, MimeType.getContentType("png"), new byte[0]
            );
            webConnection.send(response);
        }
    }
}
