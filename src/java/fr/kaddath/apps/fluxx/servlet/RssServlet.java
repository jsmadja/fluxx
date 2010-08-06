package fr.kaddath.apps.fluxx.servlet;

import fr.kaddath.apps.fluxx.domain.AggregatedFeed;
import fr.kaddath.apps.fluxx.service.AggregatedFeedService;
import fr.kaddath.apps.fluxx.service.RssService;
import java.io.IOException;
import java.io.PrintWriter;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

@WebServlet(name = "RssServlet", urlPatterns = {"/rss"})
public class RssServlet extends HttpServlet {

    @EJB
    private RssService rssService;
    @EJB
    private AggregatedFeedService aggregatedFeedService;
    @Resource(lookup="fluxx/feed/encoding")
    private String feedEncoding;
    
    private static final Logger LOG = Logger.getLogger("fluxx");
    private static final Logger STACK = Logger.getLogger("fluxx.stack");

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/xml; charset="+feedEncoding);
        PrintWriter out = response.getWriter();
        try {
            String aggregatedFeedId = request.getParameter("id");
                AggregatedFeed feed = aggregatedFeedService.findByAggregatedFeedId(aggregatedFeedId);
                if (feed != null) {
                    LOG.info("Try to create aggregated rss : "+feed);
                    String url = aggregatedFeedService.createUrl(request, feed);
                    String xml = rssService.createRssFeed(feed, url);
                    out.write(xml);
                } else {
                out.write("Invalid feed id!");
            }

        } catch (Exception e) {
            LOG.warn(e.getMessage());
            STACK.error(e);
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

}
