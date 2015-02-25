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

import support.Book;
import support.Movie;
import support.MovieWSImpl;
import support.MovieWSImplService;

/**
 * Servlet implementation class MovieClient
 */
@WebServlet("/MovieClient")
public class MovieClient extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MovieClient() {
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
		response.getWriter().write("<html><body>");
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
		String getMovie = request.getParameter("getMovie");
		if (getMovie == null) {
			out.write("<form action=\"MovieClient\" method=\"Post\">Movie title: <input type=\"text\" name=\"movieTitle\" size=\"20\"><br><br><input type=\"submit\" value=\"Search\" onclick=\"form.action='MovieClient';\"></form>");
		} else {
			Movie mov = null;
			try {
				mov = port.getMovie(getMovie);
			} catch (Exception e) {
			} 
			if (mov == null) {
				out.write("Server error, go back and try again.");
			} else {
				//<img src="mov.getPosterUrl()" height="42" width="42">
				out.write("<img src=\""+mov.getPosterUrl()+"\" height=\"81\" width=\"54\" />");
				out.write("</br></br>");
				out.write("Movie info -> " + mov.getTitle() + " ("
						+ mov.getYear() + ")");
				out.write("</br> >>Directors: </br>");
				List<String> directors = mov.getDirectorsList();
				for (int i = 0; i < directors.size(); i++) {
					out.write(directors.get(i).toString() + "; ");
					out.write("</br>");
				}
				out.write(" >>Audience score: " + mov.getAudienceScore());
				out.write("</br> >>Critics score: " + mov.getCriticsScore());
				out.write("</br> >>Books: </br>");
				for (Book b : mov.getBooksList()) {
					// <a href="linkurl">Link name</a>
					out.write("<a href='" + b.getLink() + "' target=\"_blank\">" + b.getTitle()
							+ "</a>");
					out.write("</br>");
				}
				out.write("</br>");
			}
		}
		out.write("</body></html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response);
	}

	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		String movieTitle = request.getParameter("movieTitle");
		if (movieTitle == null) {
			out.write("<form action=\"MovieClient\" method=\"Post\">Movie title: <input type=\"text\" name=\"movieTitle\" size=\"20\"><br><br><input type=\"submit\" value=\"Search\" onclick=\"form.action='MovieClient';\"></form>");
		} else {
			// handling doPost, call getMovies for movieTitle
			out.write("<html><body>");
			MovieWSImplService service = new MovieWSImplService();
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo portInfo) {
					// TODO Auto-generated method stub
					List<Handler> handlers = new ArrayList<Handler>();
					handlers.add(new ClientLogHandler());
					return handlers;
				}
			});
			// getMovieWSImplPort method yields a stub of type MovieWSImpl
			// through which we invoke the service
			MovieWSImpl port = service.getMovieWSImplPort();
			List<String> movieList = port.getMovies(movieTitle);
			out.write("<form action=\"MovieClient\" method=\"Post\">Movie title: <input type=\"text\" name=\"movieTitle\" value=\""
					+ movieTitle
					+ "\" size=\"20\"><br><br><input type=\"submit\" value=\"Search\" onclick=\"form.action='MovieClient';\"></form>");
			out.write("</br></br>");
			for (int i = 0; i < movieList.size(); i++) {
				String title = movieList.get(i).toString();
				String titleEncoded = title;
				try {
					titleEncoded = URLEncoder.encode(title, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				// make it "a" tag link to servlet that
				// takes as a parameter movie title, calls getMovie(title), and
				// shows the full movie info
				// <a href="MovieClient?getMovie=movietitle">movietitle</a>
				out.write("<a href=\"MovieClient?getMovie=");
				out.write(titleEncoded);
				out.write("\">" + title + "</a>");
				out.write("</br>");
			}
			out.write("</br></br>");
			out.write("</body></html>");
		}
		out.close();
	}

}
