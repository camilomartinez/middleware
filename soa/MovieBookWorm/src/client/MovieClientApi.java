package client;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import support.Book;
import support.Movie;
import support.MovieWSImpl;
import support.MovieWSImplService;

/**
 * Servlet implementation class MovieClientApi
 */
@WebServlet("/MovieClientApi")
public class MovieClientApi extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MovieClientApi() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		MovieWSImplService service = new MovieWSImplService();
		service.setHandlerResolver(new HandlerResolver() {
			public List<Handler> getHandlerChain(PortInfo portInfo) {
				// TODO Auto-generated method stub
				List<Handler> handlers = new ArrayList<Handler>();
				handlers.add(new ClientLogHandler());
				return handlers;
			}
		});
		// getPort method yields a stub of type MovieWS through which you invoke
		// the service
		MovieWSImpl port = service.getMovieWSImplPort();
		PrintWriter out = response.getWriter();
		//out.write("<html><body>");
		String getMovie = request.getParameter("getMovie");
		String searchTerm = request.getParameter("getMovies"); 
		response.setContentType("application/json");
		if (getMovie == null && searchTerm==null) {
			JSONObject json = new JSONObject();
			try {
				json.put("error", "Invalid url");
				json.put("usageExample1","http://localhost:8080/MovieBookWorm/MovieClientApi?getMovies=%22Avatar%22");
				json.put("usageExample2","http://localhost:8080/MovieBookWorm/MovieClientApi?getMovie=%22Avatar%22");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			out.write(json.toString());
		} else {
			if(searchTerm !=null){

				//call getMovies for searchTerm
				MovieWSImplService service2 = new MovieWSImplService();
				service2.setHandlerResolver(new HandlerResolver() {
					public List<Handler> getHandlerChain(PortInfo portInfo) {
						// TODO Auto-generated method stub
						List<Handler> handlers = new ArrayList<Handler>();
						handlers.add(new ClientLogHandler());
						return handlers;
					}
				});
				// getMovieWSImplPort method yields a stub of type MovieWSImpl
				// through which we invoke the service
				MovieWSImpl port2 = service2.getMovieWSImplPort();
				List<String> movieList = port2.getMovies(searchTerm);
				JSONObject json = new JSONObject();
				JSONArray moviesJsonArray = new JSONArray();
				try {
					for (int i = 0; i < movieList.size(); i++) {
						String title = movieList.get(i).toString();
						JSONObject jObjMovie = new JSONObject();
						jObjMovie.put("title", movieList.get(i).toString());
						moviesJsonArray.put(jObjMovie);
					}
					json.put("movies", moviesJsonArray);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				out.write(json.toString());
			}
			else if (getMovie != null) {
				Movie mov = null;
				try {
					mov = port.getMovie(getMovie);
				} catch (Exception e) {
				}
				if (mov == null) {
					JSONObject json = new JSONObject();
					try {
						json.put("error", "Server error, try again.");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					out.write(json.toString());
				} else {
					JSONObject json = new JSONObject();
					try {
						json.put("posterUrl", mov.getPosterUrl());
						json.put("title", mov.getTitle());
						json.put("year", mov.getYear());

						JSONArray directorsJsonArray = new JSONArray();
						List<String> directors = mov.getDirectorsList();
						for (int i = 0; i < directors.size(); i++) {
							JSONObject jObjDirector = new JSONObject();
							jObjDirector.put("name", directors.get(i)
									.toString());
							directorsJsonArray.put(jObjDirector);
						}
						json.put("directors", directorsJsonArray);

						json.put("audienceScore", mov.getAudienceScore());
						json.put("criticsScore", mov.getCriticsScore());

						JSONArray booksJsonArray = new JSONArray();
						List<Book> books = mov.getBooksList();
						for (Book b : books) {
							JSONObject jObjBook = new JSONObject();
							jObjBook.put("bookTitle", b.getTitle());
							jObjBook.put("bookLink", b.getLink());
							booksJsonArray.put(jObjBook);
						}
						json.put("books", booksJsonArray);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					out.write(json.toString());
				}
			} 
		}
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		//handleRequest(request, response);
	}



}
