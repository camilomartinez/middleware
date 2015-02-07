package server;

import java.util.ArrayList;

import utils.TextUtils;

public class Book {
	private String title;
	private ArrayList<String> authorsList;
	private String publisher;
	private String publishedDate;
	private String link;
	private int pageCount;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getPublishedDate() {
		return publishedDate;
	}
	public void setPublishedDate(String publishedDate) {
		this.publishedDate = publishedDate;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public ArrayList<String> getAuthorsList() {
		return authorsList;
	}
	public void setAuthorsList(ArrayList<String> authorsList) {
		this.authorsList = authorsList;
	}

	public String getAuthorsListAsString() {
		return TextUtils.join(", ", authorsList);
	}
	
	
	
}
