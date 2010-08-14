package fr.kaddath.apps.fluxx.servlet;

import fr.kaddath.apps.fluxx.domain.AggregatedFeed;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Fluxxer;
import fr.kaddath.apps.fluxx.service.AggregatedFeedService;
import fr.kaddath.apps.fluxx.service.OpmlService;
import fr.kaddath.apps.fluxx.service.UserService;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AggregatedFeedOpmlServlet extends HttpServlet {
   
    @Inject
    private OpmlService opmlService;

    @Inject
    private UserService userService;

    @Inject
    private AggregatedFeedService aggregatedFeedService;

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("application/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String username = request.getParameter("username");
            Fluxxer fluxxer = userService.findByUsername(username);
            out.println(opmlService.createOpml(toFeeds(fluxxer.getAggregatedFeeds(), request)));
        } finally { 
            out.close();
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private List<Feed> toFeeds(List<AggregatedFeed> aggregatedFeeds, HttpServletRequest request) {
        List<Feed> feeds = new ArrayList<Feed>();
        for(AggregatedFeed agFeed:aggregatedFeeds) {
            Feed feed = new Feed();
            feed.setTitle(agFeed.getName());
            feed.setUrl(aggregatedFeedService.createUrl(request, agFeed));
            feeds.add(feed);
        }
        return feeds;
    }

}
