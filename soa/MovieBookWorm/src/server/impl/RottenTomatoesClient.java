package server.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import server.Movie;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

public class RottenTomatoesClient {
    private final String API_KEY = "jpn4dayjxng962r47j4dt4fe";
    private final String API_BASE_URL = "http://api.rottentomatoes.com/api/public/v1.0/";
    private AsyncHttpClient client;

    public RottenTomatoesClient() {
        this.client = new AsyncHttpClient();
    }
    private String addToUrl(String url, String addedText){
    	return url + addedText;
    }
    private String getAbsoluteUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }
    private String prepareURLParameters(String url){
    	return url+"?";
    }
    private String addToUrlApiKey(String url){
    	return url+"apikey="+API_KEY;
    }
    private String addToUrlSearchTerm(String url, String searchTerm){
    	try {
			return url+"&q="+URLEncoder.encode(searchTerm, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "UnsupportedEncodingException";
		}
    }
    private String addToUrlPageLimit(String url, Integer pageLimit){
    	return url+"&page_limit="+pageLimit.toString();
    }
    private String addToUrlPageNumber(String url, Integer pageNumber){
    	return url+"&page="+pageNumber.toString();
    }
    
    public ArrayList<String> getMovies(String searchTerm){
    	// http://api.rottentomatoes.com/api/public/v1.0/movies.json
        String url = getAbsoluteUrl("movies.json");
        //?apikey=<key>&q={search-term}&page_limit={results-per-page}&page={page-number}
        url = prepareURLParameters(url);
        url = addToUrlApiKey(url);
        url = addToUrlSearchTerm(url, searchTerm);
        url = addToUrlPageLimit(url, 30);
        url = addToUrlPageNumber(url, 1);
        
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        Future<Response> f;
		try {
			f = asyncHttpClient.prepareGet(url).execute();
	        Response r = f.get();
	        String responseString = processInputStream(r.getResponseBodyAsStream());
	        JSONObject body = new JSONObject(responseString);
	        //Get the movies json array
	        JSONArray items = body.getJSONArray("movies");
	        //Parse json array into array of model objects
	        ArrayList<String> titlesList = RottenTomatoesClient.getTitlesFromJson(items);
	        return titlesList;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
    }
    public Movie getMovie(String title) {
   	 // http://api.rottentomatoes.com/api/public/v1.0/movies.json
       String url = getAbsoluteUrl("movies.json");
       url = prepareURLParameters(url);
       url = addToUrlApiKey(url);
       //?apikey=<key>&q={search-term}&page_limit={results-per-page}&page={page-number}
       url = addToUrlSearchTerm(url, title);
       url = addToUrlPageLimit(url, 30);
       url = addToUrlPageNumber(url, 1);
       
       AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
       Future<Response> f;
		try {
			f = asyncHttpClient.prepareGet(url).execute();
	        Response r = f.get();
	        String responseString = processInputStream(r.getResponseBodyAsStream());
	        JSONObject body = new JSONObject(responseString);
	        //Get the movies json array
	        JSONArray items = body.getJSONArray("movies");
	        //parse id here from json response
	        ArrayList<String> idList = RottenTomatoesClient.getIdsFromJson(items);
	        
	        ArrayList<Movie> movieList = new ArrayList<Movie>();
	        Movie mov = null;
	        //create another asyncclient that accesses url+id.json for detailed movie info
	        for (int i = 0; i < idList.size(); i++) {
		        String url2 = getAbsoluteUrl("movies/");
		        url2 = addToUrl(url2, idList.get(i));
		        url2 = addToUrl(url2, ".json");
		        url2 = prepareURLParameters(url2);
		        url2 = addToUrlApiKey(url2);
		        //http request for all movies from previous search result
		        AsyncHttpClient asyncHttpClient2 = new AsyncHttpClient();
		        Future<Response> f2;
		        f2 = asyncHttpClient2.prepareGet(url2).execute();
		        Response r2 = f2.get();
		        String responseString2 = processInputStream(r2.getResponseBodyAsStream());
		        JSONObject body2 = new JSONObject(responseString2);
		        //Get the movie from json object
		        mov = RottenTomatoesClient.fromJson(body2);
		        if(mov.getTitle().equals(title)){
		        	System.out.println("Movie found: "+mov.getTitle());
		        	return mov; 
		        }
		        System.out.println(title+" != "+mov.getTitle());
		        movieList.add(mov);//movieList saved for debugging purposes
			}//otherwise, if movie doesn't exist with precise "title" return first movie in the list
			return movieList.get(0);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
   }
    public static Movie fromJson(org.json.JSONObject jsonObject) {
		Movie mov = new Movie();
            // Deserialize json into object fields
        	try {
                mov.setTitle(jsonObject.getString("title"));
            } catch (Exception e) {
            	mov.setTitle("Not found");
            } try {
                mov.setYear(jsonObject.getInt("year"));
            } catch (Exception e) {
            	mov.setYear(-1);
            } try {
                mov.setPosterUrl(jsonObject.getJSONObject("posters").getString("thumbnail"));
            } catch (Exception e) {
            	mov.setPosterUrl("Not found");
            } try {
                mov.setCriticsScore(jsonObject.getJSONObject("ratings").getInt("critics_score"));
            } catch (Exception e) {
            	mov.setCriticsScore(-1);
            }  try {
                mov.setAudienceScore(jsonObject.getJSONObject("ratings").getInt("audience_score"));
            } catch (Exception e) {
            	mov.setAudienceScore(-1);
            }  
            // Construct simple array of directors' names
            ArrayList<String> dirList = new ArrayList<String>();
            //directors are in link of movie id.
            //http://developer.rottentomatoes.com/docs
            JSONArray abridgedDirectors = new JSONArray();
            try { 
                abridgedDirectors = jsonObject.getJSONArray("abridged_directors");
                for (int i = 0; i < abridgedDirectors.length(); i++) {
                    dirList.add(abridgedDirectors.getJSONObject(i).getString("name"));
                }
                mov.setDirectorsList(dirList);
            } catch (org.json.JSONException e) {
                mov.setDirectorsList(dirList);
            }
        // Return new object
        return mov;
    }
	// Decodes array of movie json results into business model objects
    public static ArrayList<String> getIdsFromJson(JSONArray jsonArray) {
        ArrayList<String> idList = new ArrayList<String>(jsonArray.length());
        // Process each result in json array, decode and convert to business
        // object
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject businessJson = null;
            try {
                businessJson = jsonArray.getJSONObject(i);
                //extract id from businessJson, and add to idList
                String id = businessJson.getString("id");
                idList.add(id);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        return idList;
    }
    public static ArrayList<String> getTitlesFromJson(JSONArray jsonArray) {
        ArrayList<String> titlesList = new ArrayList<String>(jsonArray.length());
        // Process each result in json array, decode and convert to business
        // object
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject businessJson = null;
            try {
                businessJson = jsonArray.getJSONObject(i);
                //extract title from businessJson, and add it to titlesList
                String id = businessJson.getString("title");
                titlesList.add(id);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        return titlesList;
    }
    public String processInputStream(InputStream in){
		try {
			
			BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
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
}