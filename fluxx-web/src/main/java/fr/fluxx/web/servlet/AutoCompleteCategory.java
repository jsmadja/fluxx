/**
 * Copyright (C) 2010 Julien SMADJA <julien.smadja@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.fluxx.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.fluxx.core.domain.Category;
import fr.fluxx.core.service.CategoryService;

@WebServlet(name = "AutoCompleteCategory", urlPatterns = { "/AutoCompleteCategory" })
public class AutoCompleteCategory extends HttpServlet {

	private static final long serialVersionUID = -5595823845823527972L;

	@Inject
	CategoryService categoryService;

	// @Resource(lookup = "fluxx/autocomplete/category")
	private final Integer numCategories = 10;

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		try {
			String query = request.getParameter("query");
			List<Category> categories = categoryService.findCategoriesByName(query, numCategories);
			for (Category category : categories) {
				out.println(category.getName());
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
		return "Used to build suggest list";
	}

}
