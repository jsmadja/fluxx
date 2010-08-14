package fr.kaddath.apps.fluxx.customtag;

import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.service.FeedService;
import fr.kaddath.apps.fluxx.service.Services;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(value = "tagline")
public class TagLine extends UIComponentBase {

    private FeedService feedService;

    public TagLine() {
        super();
        feedService = Services.getFeedService();
    }

    @Override
    public String getFamily() {
        return "fluxx";
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        Feed feed = (Feed) getAttributes().get("feed");
        List<String> categories = feedService.findCategoriesByFeedId(feed.getId().toString());
        Collections.sort(categories);
        for (String category:categories) {
            responseWriter.startElement("img", this);
            responseWriter.writeAttribute("src", context.getExternalContext().getRequestContextPath()+"/myimages/tag.png", "src");
            responseWriter.endElement("img");
            responseWriter.write(category);
            responseWriter.write("&nbsp;&nbsp;&nbsp;");
        }
    }

}
