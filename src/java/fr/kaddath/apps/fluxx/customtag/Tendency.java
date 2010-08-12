package fr.kaddath.apps.fluxx.customtag;

import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Item;
import fr.kaddath.apps.fluxx.resource.FluxxMessage;
import fr.kaddath.apps.fluxx.service.ItemService;
import fr.kaddath.apps.fluxx.service.Services;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(value = "tendency")
public class Tendency extends UIComponentBase {

    public static final int DAYS_IN_A_WEEK = 7;
    public static final int DAYS_IN_A_MONTH = 30;
    public static final int DAYS_IN_A_YEAR = 365;
    public static final int MILLISEC_IN_A_DAY = 1000 * 60 * 60 * 24;
    private static final Logger LOG = Logger.getLogger(Tendency.class.getName());
    private ItemService itemService;

    public Tendency() {
        super();
        itemService = Services.getItemService();
    }

    @Override
    public String getFamily() {
        return "fluxx";
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        Feed feed = (Feed) getAttributes().get("feed");

        float numItems = itemService.getNumItems(feed);

        int periodInDays = computePeriod(feed);
        if (periodInDays == 0) {
            periodInDays = Integer.MAX_VALUE;
        }

        String tendency = "";
        String img = "";

        // per day
        double itemsPerDay = round(numItems / periodInDays, 1);
        double itemsPerWeek = round(numItems * DAYS_IN_A_WEEK / periodInDays, 1);
        double itemsPerMonth = round((numItems * DAYS_IN_A_MONTH) / periodInDays, 1);
        double itemsPerYear = round((numItems * DAYS_IN_A_YEAR) / periodInDays, 1);
        if (itemsPerDay >= 1) {
            tendency = FluxxMessage.m("publication_per_day", itemsPerDay);
            img = "2-green.png";
        } else if (itemsPerWeek >= 1) {
            tendency = FluxxMessage.m("publication_per_week", itemsPerWeek);
            img = "1-green.png";
        } else if (itemsPerMonth >= 1) {
            tendency = FluxxMessage.m("publication_per_month", itemsPerMonth);
            img = "2-black.png";
        } else if (itemsPerYear >= 1) {
            tendency = FluxxMessage.m("publication_per_year", itemsPerMonth);
            img = "1-red.png";
        } else {
            img = ""+itemsPerDay;
        }

        responseWriter.startElement("img", this);
        responseWriter.writeAttribute("src", context.getExternalContext().getRequestContextPath()+"/myimages/tendency/"+img, "src");
        responseWriter.writeAttribute("title", tendency, "src");
        responseWriter.endElement("img");
    }

    public double round(double what, int howmuch) {
        return (double) ((int) (what * Math.pow(10, howmuch) + .5)) / Math.pow(10, howmuch);
    }

    private int computePeriod(Feed feed) {

        Item lastItem = itemService.findFirstItem(feed);
        if (lastItem == null) {
            return 0;
        }
        long t = System.currentTimeMillis();
        Date date = lastItem.getPublishedDate();
        if (date == null) {
            date = lastItem.getUpdatedDate();
        }

        if (date == null) {
            return 0;
        }

        long time = date.getTime();
        return (int) (((float) (t - time)) / MILLISEC_IN_A_DAY);
    }
}
