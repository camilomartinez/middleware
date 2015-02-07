package server.impl;

import java.util.ArrayList;

import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import server.Movie;
import server.MovieWS;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

@WebService//(endpointInterface="server.MovieWS")
@HandlerChain(file="handler-chain-service.xml")
public class MovieWSImpl implements MovieWS {

	private MoviesUtility utils;
	
	
	public MovieWSImpl() {
		utils = new MoviesUtility();
	}

	@WebMethod
	public Movie getMovie(@WebParam(name="MovieName") String name) {
		return utils.getMovie(name);
	}

	@WebMethod
	public ArrayList<String> getMovies(@WebParam(name="SearchTerm")String searchTerm) {
		return utils.getMovies(searchTerm);
	}

}
