package client;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

import support.Movie;
import server.MovieWS;
import support.Book;
import support.MovieWSImpl;
import support.MovieWSImplService;

public class TestClient {

	public static void main(String[] args) {
		
		MovieWSImplService service = new MovieWSImplService();
		service.setHandlerResolver(new HandlerResolver() {
			public List<Handler> getHandlerChain(PortInfo portInfo) {
				// TODO Auto-generated method stub
				List<Handler> handlers = new ArrayList<Handler>();
				handlers.add(new ClientLogHandler());
				return handlers;
			}
		});
		//getPort method yields a stub of type MovieWS through which you invoke the service
		MovieWSImpl port = service.getMovieWSImplPort();
		List<String> movieList = port.getMovies("Lord of the rings");
		System.out.println("");System.out.println("");
		for (int i = 0; i < movieList.size(); i++) {
			System.out.println(movieList.get(i).toString());
		}
		System.out.println("");System.out.println("");
		Movie mov = port.getMovie("The Lord of the Rings");
		System.out.println("");System.out.println("");
		System.out.println("Movie received -> " + mov.getTitle() + " (" + mov.getYear()+ ")");
		System.out.println(">>Directors: ");
		List<String> directors = mov.getDirectorsList();
		for (int i = 0; i < directors.size(); i++) {
			System.out.println(directors.get(i).toString()+"; ");
		}
		System.out.println(">>Audience score: "+mov.getAudienceScore());
		System.out.println(">>Critics score: "+mov.getCriticsScore());
		System.out.println(">>Books: ");
		for(Book b : mov.getBooksList()){
			System.out.println(b.getTitle()+" "+b.getLink()+";");
		}
		System.out.println("");
		
	}

}
