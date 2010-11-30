package fr.kaddath.apps.fluxx.webservice;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import fr.kaddath.apps.fluxx.service.Services;

@Path("rss/{username}/{category}")
public class FeedResource {
	@Context
	private HttpServletRequest request;

	private final String feedEncoding = "UTF-8";

	@GET
	@Produces("application/xml")
	public String getXml(@PathParam(value = "username") String username, @PathParam(value = "category") String category) {
		return Services.getRssService().getRssFeedByUsernameAndCategory(username, category, request, feedEncoding);
	}

}
