package fr.kaddath.apps.fluxx.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.kaddath.apps.fluxx.service.CategoryService;

@WebServlet(name = "AutoCompleteCategory", urlPatterns = { "/AutoCompleteCategory" })
public class AutoCompleteCategory extends HttpServlet {

	private static final long serialVersionUID = -5595823845823527972L;

	@Inject
	CategoryService categoryService;

	@Resource(lookup = "fluxx/autocomplete/category")
	private Integer numCategories;

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		try {
			String query = request.getParameter("query");
			List<String> categories = categoryService.findCategoryNamesInLowerCaseWithLike(query, numCategories);
			for (String categorie : categories) {
				out.println(categorie);
			}

		} finally {
			out.close();
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		processRequest(request, response);
	}

	@Override
	public String getServletInfo() {
		return "Use to build suggest list";
	}

}
