package server.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import server.Book;
import server.Movie;

public class MoviesUtility {
	
	
	public MoviesUtility() {
	}

	public ArrayList<String> getMovies(String searchTerm){
		RottenTomatoesClient client = new RottenTomatoesClient();
		ArrayList<String> movieList = client.getMovies(searchTerm);
		return movieList;
	}
	
	public Movie getMovie(String title){
		RottenTomatoesClient client = new RottenTomatoesClient();
		Movie mov = client.getMovie(title);
		GoogleBooksClient client2 = new GoogleBooksClient();
		ArrayList<Book> booksList = client2.getBooks(title);
		mov.setBooksList(booksList);
		return mov;
	}
}
