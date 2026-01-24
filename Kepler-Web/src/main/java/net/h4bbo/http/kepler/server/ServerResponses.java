package net.h4bbo.http.kepler.server;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.alexdev.duckhttpd.exceptions.NoServerResponseException;
import org.alexdev.duckhttpd.response.ResponseBuilder;
import org.alexdev.duckhttpd.response.WebResponses;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.http.kepler.log.Log;
import net.h4bbo.http.kepler.template.TwigTemplate;

import java.io.IOException;

public class ServerResponses implements WebResponses {
    @Override
    public FullHttpResponse getErrorResponse(WebConnection client, Throwable throwable) {
        if (throwable != null) {
            if (throwable instanceof NoServerResponseException) {
                Log.getErrorLogger().error("Server did not send response for: " + client.getRouteRequest());
            }

            Log.getErrorLogger().error("Error occurred: ", throwable);
        }

        client.session().delete("page");

        TwigTemplate tpl = (TwigTemplate) client.template("overrides/default");
        try {
            return ResponseBuilder.create(tpl.renderHTML());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public FullHttpResponse getResponse(HttpResponseStatus status, WebConnection webConnection) {
        webConnection.session().delete("page");

        if (status == HttpResponseStatus.FORBIDDEN) {
            return ResponseBuilder.create(HttpResponseStatus.FORBIDDEN, "\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n" + "   <h1>Forbidden</h1>\n" + "<body>\n" + "</html>");
        }

        if (status == HttpResponseStatus.NOT_FOUND) {
            TwigTemplate twigTemplate = new TwigTemplate(webConnection);
            twigTemplate.start("overrides/default");
            try {
                return ResponseBuilder.create(HttpResponseStatus.NOT_FOUND, twigTemplate.renderHTML());//"\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n" + "   <h1>Not Found</h1>\n" + "<body>\n" + "</html>");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //if (status == HttpResponseStatus.BAD_REQUEST) {
        return ResponseBuilder.create(HttpResponseStatus.BAD_REQUEST, "\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n" + "   <h1>Bad Request</h1>\n" + "<body>\n" + "</html>");
        //}

        //return null;
    }
}
