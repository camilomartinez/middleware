package server.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import server.Book;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
//https://github.com/AsyncHttpClient/async-http-client

public class GoogleBooksClient {
	private final String API_KEY = "AIzaSyC9Mkq50x41mr4YZhVwReaxKU3GBSLUW1c";
	private final String API_BASE_URL = "https://www.googleapis.com/books/v1/";
	private AsyncHttpClient client;

	public GoogleBooksClient() {
		this.client = new AsyncHttpClient();
	}

	private String addToUrl(String url, String addedText) {
		return url + addedText;
	}

	private String getAbsoluteUrl(String relativeUrl) {
		return API_BASE_URL + relativeUrl;
	}

	private String prepareURLParameters(String url) {
		return url + "?";
	}

	private String addToUrlApiKey(String url) {
		return url + "apikey=" + API_KEY;
	}

	private String addToUrlSearchTerm(String url, String searchTerm) {
		try {
			return url + "&q=" + URLEncoder.encode(searchTerm, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "UnsupportedEncodingException";
		}
	}

	public ArrayList<Book> getBooks(String searchTerm) {
		// http://api.rottentomatoes.com/api/public/v1.0/movies.json
		String url = getAbsoluteUrl("volumes");
		// ?apikey=<key>&q={search-term}&page_limit={results-per-page}&page={page-number}
		url = prepareURLParameters(url);
		url = addToUrlApiKey(url);
		url = addToUrlSearchTerm(url, searchTerm);

		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		Future<Response> f;
		JSONArray itemsJsonArray = new JSONArray();
		ArrayList<Book> booksList = new ArrayList<Book>();
		try {
			f = asyncHttpClient.prepareGet(url).execute();
			Response r = f.get();
			String responseString = processInputStream(r
					.getResponseBodyAsStream());
			JSONObject body = new JSONObject(responseString);
			// Get the movies json array
			itemsJsonArray = body.getJSONArray("items");

		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
		// Parse itemsJsonArray into array of model objects
		// ArrayList<String> titlesList =
		// GoogleBooksClient.getLinksFromJson(items);
		// extract each book from json list result and add it to booksList
		for (int i = 0; i < itemsJsonArray.length(); i++) {
			try {
				JSONObject jobj = itemsJsonArray.getJSONObject(i);
				Book b = GoogleBooksClient.fromJson(jobj);
				booksList.add(b);
			} catch (Exception e) {

			}
		}
		// sort booksList
		ArrayList<Book> sortedList = sortByMatchingStringTokens(booksList, searchTerm);
		return sortedList;
	}

	public ArrayList<String> getBooksLinks(String searchTerm) {
		ArrayList<Book> booksList = getBooks(searchTerm);
		ArrayList<String> linksList = new ArrayList<String>();
		for (Book b : booksList) {
			linksList.add(b.getLink());
		}
		return linksList;
	}

	public ArrayList<String> getBooksLinks(ArrayList<Book> booksList) {
		ArrayList<String> linksList = new ArrayList<String>();
		for (Book b : booksList) {
			linksList.add(b.getLink());
		}
		return linksList;
	}

	public ArrayList<Book> sortByMatchingStringTokens(
			ArrayList<Book> booksList, String searchTerm) {
		ArrayList<String> titlesList = new ArrayList<String>();
		for (int i = 0; i < booksList.size(); i++) {
			titlesList.add(booksList.get(i).getTitle());
		}
		String s = searchTerm; // search string
		final ArrayList<String> matches = new ArrayList<String>();
		for (String tokens : s.split("\\s")) {
			matches.add(tokens.toLowerCase()); // convert the search string into
												// tokens
		}

		Comparator<String> customComparator = new Comparator<String>() {

			public int compare(String o1, String o2) {
				int scoreDiff = getScore(o1) - getScore(o2);
				if ((getScore(o1) == 0 && getScore(o2) == 0) || scoreDiff == 0) {
					return o1.compareTo(o2);
				}
				return -(getScore(o1) - getScore(o2));
			}

			private int getScore(String s) {
				int score = 0;
				for (String match : matches) {
					if (s.toLowerCase().contains(match)) {
						score++;
					}
				}
				return score;
			}
		};
		Collections.sort(titlesList, customComparator);

		ArrayList<Book> sortedList = new ArrayList<Book>();
		for (String title : titlesList) {
			for (Book bk : booksList) {
				if (bk.getTitle().equals(title)) {
					sortedList.add(bk);
				}
			}
		}
		sortedList.removeAll(Collections.singleton(null));
		return sortedList;

	}

	public static Book fromJson(org.json.JSONObject jsonObject) {
		Book b = new Book();
		try {
			b.setTitle(jsonObject.getJSONObject("volumeInfo")
					.getString("title"));
		} catch (Exception e) {
			b.setTitle("Not found");
		}
		try {
			b.setPublisher(jsonObject.getJSONObject("volumeInfo").getString(
					"publisher"));
		} catch (Exception e) {
			b.setPublisher("Not found");
		}
		try {
			b.setPublishedDate(jsonObject.getJSONObject("volumeInfo")
					.getString("publishedDate"));
		} catch (Exception e) {
			b.setPublishedDate("Not found");
		}
		try {
			b.setPageCount(jsonObject.getJSONObject("volumeInfo").getInt(
					"pageCount"));
		} catch (Exception e) {
			b.setPageCount(-1);
		}
		try {
			b.setLink(jsonObject.getJSONObject("volumeInfo").getString(
					"previewLink"));
		} catch (Exception e) {
			b.setLink("Not found");
		}
		try {
			ArrayList<String> authList = new ArrayList<String>();
			JSONArray authorsJsonArray = jsonObject.getJSONObject("volumeInfo")
					.getJSONArray("authors");
			for (int i = 0; i < authorsJsonArray.length(); i++) {
				authList.add(authorsJsonArray.get(i).toString());
			}
			b.setAuthorsList(authList);
		} catch (org.json.JSONException e) {
			b.setAuthorsList(new ArrayList<String>());
		}

		// Return new object
		return b;
	}

	public String processInputStream(InputStream in) {
		try {

			BufferedReader streamReader = new BufferedReader(
					new InputStreamReader(in, "UTF-8"));
			StringBuilder responseStrBuilder = new StringBuilder();

			String inputStr;
			while ((inputStr = streamReader.readLine()) != null)
				responseStrBuilder.append(inputStr);
			return responseStrBuilder.toString();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "InputStream error";
	}
	/*
	 * public static void main(String[] args) { GoogleBooksClient client = new
	 * GoogleBooksClient(); ArrayList<Book> booksList =
	 * client.getBooks("The lord of the rings");
	 * client.getBooksLinks(booksList);
	 * 
	 * System.exit(0); }
	 */
}