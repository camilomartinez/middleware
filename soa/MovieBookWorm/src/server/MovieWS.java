package server;

import java.util.ArrayList;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface MovieWS {
	@WebMethod 
	public ArrayList<String> getMovies(@WebParam(name="SearchTerm") String searchTerm);
	@WebMethod 
	public Movie getMovie(@WebParam(name="MovieName") String name);
}
