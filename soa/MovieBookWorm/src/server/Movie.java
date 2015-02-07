
package server;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.TextUtils;


public class Movie {
	/*  The back-end should not return the whole JSON given back by Rottentomatoes as is, but it has to simplify it,
	 *  keeping just the movie title, the year of publication, the name of the director(s), the two marks calculated
	 *  by Rottentomatoes (critics and audience), and the link to the movie's poster.*/
	private String title;
	private int year;
	private String posterUrl;
	private int criticsScore;
	private int audienceScore;
	private ArrayList<String> directorsList;
	private ArrayList<Book> booksList;

	public Movie(String title, int year) {
		this.title = title;
		this.year = year;
	}

	public Movie() {
	}
	
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getPosterUrl() {
		return posterUrl;
	}

	public void setPosterUrl(String posterUrl) {
		this.posterUrl = posterUrl;
	}

	public int getCriticsScore() {
		return criticsScore;
	}

	public void setCriticsScore(int criticsScore) {
		this.criticsScore = criticsScore;
	}

	public int getAudienceScore() {
		return audienceScore;
	}

	public void setAudienceScore(int audienceScore) {
		this.audienceScore = audienceScore;
	}

	public ArrayList<String> getDirectorsList() {
		return directorsList;
	}

	public void setDirectorsList(ArrayList<String> directorsList) {
		this.directorsList = directorsList;
	}
	public String getDirectorsListAsString() {
		return TextUtils.join(", ", directorsList);
	}

	public ArrayList<Book> getBooksList() {
		return booksList;
	}

	public void setBooksList(ArrayList<Book> booksList) {
		this.booksList = booksList;
	}
	
	

}
