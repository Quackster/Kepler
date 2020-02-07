package org.alexdev.http.template;

import org.alexdev.duckhttpd.response.ResponseBuilder;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import org.alexdev.duckhttpd.util.config.Settings;
import org.alexdev.http.template.binders.AlertBinder;
import org.alexdev.http.template.binders.SessionBinder;
import org.alexdev.http.template.binders.SiteBinder;
import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.util.config.ServerConfiguration;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class TwigTemplate extends Template {
    private File file;
    private String view;

    private JtwigModel model;
    private JtwigTemplate template;

    private Map<String, Object> keyValueMap;

    public TwigTemplate(WebConnection session) {
        super(session);
        this.keyValueMap = new HashMap<>();
    }

    @Override
    public void start(String view) {
        this.view = view;

        try {
            File file = Paths.get(
                            ServerConfiguration.getString("template.directory"),
                            ServerConfiguration.getString("template.name"), view + ".tpl")
                        .toFile();

            if (file.exists() &&
                file.isFile()) {

                this.file = file;
                this.template = JtwigTemplate.fileTemplate(file);
                this.model = JtwigModel.newModel();
            } else {
                throw new Exception("The template view " + view + " does not exist!\nThe path: " + file.getCanonicalPath());
            }

            if (this.webConnection.session().getBoolean("authenticated")) {
                PlayerDetails playerDetails = PlayerDao.getDetails(this.webConnection.session().getInt("user.id"));

                if (playerDetails == null) {
                    this.webConnection.session().delete("authenticated");
                    this.webConnection.redirect("/");
                    return;
                }

                this.set("playerDetails", PlayerDao.getDetails(this.webConnection.session().getInt("user.id")));
            }

        } catch (Exception ex) {
            Settings.getInstance().getDefaultResponses().getErrorResponse(this.webConnection, ex);
        }
    }

    @Override
    public void set(String name, Object value) {
        this.model.with(name, value);
        this.keyValueMap.put(name, value);
    }

    @Override
    public Object get(String s) {
        if (this.keyValueMap.containsKey(s)) {
            return this.keyValueMap.get(s);
        }

        return null;
    }

    public String renderHTML() {
        this.registerBinder(new SessionBinder());
        this.registerBinder(new SiteBinder());
        this.registerBinder(new AlertBinder(this.webConnection.session().getString("alertMessage"), this.webConnection.session().getString("alertColour")));
        return this.template.render(this.model);
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
