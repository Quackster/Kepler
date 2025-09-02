package net.h4bbo.http.kepler.template;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.alexdev.duckhttpd.response.ResponseBuilder;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import org.alexdev.duckhttpd.util.config.Settings;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.util.StringUtil;
import net.h4bbo.kepler.util.config.ServerConfiguration;
import net.h4bbo.http.kepler.log.Log;
import net.h4bbo.http.kepler.template.binders.AlertBinder;
import net.h4bbo.http.kepler.template.binders.SessionBinder;
import net.h4bbo.http.kepler.template.binders.SiteBinder;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class TwigTemplate extends Template {
    private File file;
    private String view;

    private PebbleEngine engine;
    private PebbleTemplate compiledTemplate;
    private HashMap<String, Object> context;

    public TwigTemplate(WebConnection session) {
        super(session);
        this.context = new HashMap<>();
    }

    @Override
    public void start(String view) {
        this.view = view;

        try {
            File file = Paths.get(ServerConfiguration.getString("template.directory"), ServerConfiguration.getString("template.name"), view + ".tpl").toFile();

            if (file.exists() && file.isFile()) {
                this.file = file;
                String prefix = Path.of(ServerConfiguration.getString("template.directory"), ServerConfiguration.getString("template.name")).toAbsolutePath().toString();

                this.engine = new PebbleEngine.Builder()
                        .strictVariables(false)
                        .autoEscaping(false)
                        .extension(new PresentTest())
                        .build();

                this.engine.getLoader().setCharset(StringUtil.getCharset().toString());
                this.engine.getLoader().setPrefix(prefix);

                this.compiledTemplate = this.engine.getTemplate(view + ".tpl");
            } else {
                throw new Exception("The template view " + view + " does not exist!\nThe path: " + file.getCanonicalPath());
            }

            if (this.webConnection != null) {
                if (this.webConnection.session().getBoolean("authenticated") ||
                        this.webConnection.session().getBoolean("authenticatedHousekeeping")) {
                    PlayerDetails playerDetails = PlayerDao.getDetails(this.webConnection.session().getInt("user.id"));

                    if (playerDetails == null) {
                        this.webConnection.session().delete("authenticated");
                        this.webConnection.redirect("/");
                        return;
                    }

                    this.set("playerDetails", PlayerDao.getDetails(this.webConnection.session().getInt("user.id")));
                }
            }

        } catch (Exception ex) {
            if (this.webConnection != null) {
                Settings.getInstance().getDefaultResponses().getErrorResponse(this.webConnection, ex);
            } else {
                Log.getErrorLogger().error("Error: ", ex);
            }
        }
    }

    @Override
    public void set(String name, Object value) {
        this.context.put(name, value);
    }

    @Override
    public Object get(String s) {
        if (this.context.containsKey(s)) {
            return this.context.get(s);
        }

        return null;
    }

    private void attachBinders() {
        this.registerBinder(new SessionBinder());
        this.registerBinder(new SiteBinder());

        if (this.webConnection != null) {
            this.registerBinder(new AlertBinder(
                    this.webConnection.session().getString("alertMessage"),
                    this.webConnection.session().getString("alertColour"))
            );
        }
    }

    public String renderHTML() throws IOException {
        this.attachBinders();

        for (var key : context.keySet()) {
            //System.out.println(key);
        }

        Writer writer = new StringWriter();
        compiledTemplate.evaluate(writer, context);
        String html = writer.toString();
        writer.close();

        return html;
    }

    @Override
    public void render() {
        try {
            var html = this.renderHTML();
            var response = ResponseBuilder.create(html);

            for (var entry : this.webConnection.headers().entrySet()) {
                response.headers().add(entry.getKey(), entry.getValue());
            }

            this.webConnection.send(response);
            this.webConnection.headers().clear();
        } catch (Exception ex) {
            Settings.getInstance().getDefaultResponses().getErrorResponse(this.webConnection, ex);
        }
    }
}
