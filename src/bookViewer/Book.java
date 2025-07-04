package bookViewer;

import java.util.Arrays;

/*
 * File: Book.java
 * Author: Nevin Fullerton
 * Date: 12/8/2024
 * Description: This class stores the meta data of a book which includes title, author, genres, publication date, description, and ISBN (International Standard
 * Book Number). The class has getters and setters for all these fields. The class has built in methods to compare its title, author, publication date, and
 * ISBN to other books for easier sorting.
 */

public class Book {

	private String title;
	private String author; 
	private String[] genres; 
	private String publicationDate; // Month/Day/Year EX: 09/12/1992
	private String description;
	private String ISBN; // (International Standard Book Number) Uses ISBN-13, a 13 digit number that starts with 978 or 979 EX: 978-0345339683
	
	public Book(String title, String authorName, String[] genres, String publicationDate, String description, String ISBN) {
		// Initialize all values
		this.title = title;
		this.author = authorName;
		this.genres = Arrays.copyOf(genres, genres.length); 
		this.publicationDate = publicationDate;
		this.description = description;
		this.ISBN = ISBN;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String authorFirstName) {
		this.author = authorFirstName;
	}
	
	public String[] getGenres() {
		return genres;
	}
	
	public void setGenres(String[] genres) {
		this.genres = genres;
	}
	
	public String getPublicationDate() {
		return publicationDate;
	}
	
	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getISBN() {
		return ISBN;
	}

	public void setISBN(String ISBN) {
		this.ISBN = ISBN;
	}

	public int compareToAuthor(Book bookToCompare) {
		// Checks how to alphabetically order the two books
		if (author.compareTo(bookToCompare.getAuthor()) == 0) // Same order 
			return 0;
		else if (author.compareTo(bookToCompare.getAuthor()) > 0) // This book goes after the other book alphabetically
			return 1;
		else  // This book goes before the other book
			return -1;
	}
	
	public int compareToPublicationDate(Book bookToCompare) {
		//Check to see if this book and other book have a publication date
		if (publicationDate.equals("") && bookToCompare.getPublicationDate().equals(""))
			return 0;
		else if (publicationDate.equals(""))
			return -1;
		else if (bookToCompare.getPublicationDate().equals(""))
			return 1;
		
		// Splits both publication dates for easier comparison
		String[] thisBookPublicationDate = publicationDate.split("/"); 
		String[] otherBookPublicationDate = bookToCompare.getPublicationDate().split("/");

		
		if (Integer.parseInt(thisBookPublicationDate[2]) > Integer.parseInt(otherBookPublicationDate[2])) // Compare year
			return 1;
		else if (Integer.parseInt(thisBookPublicationDate[2]) < Integer.parseInt(otherBookPublicationDate[2]))
			return -1;
		else if (Integer.parseInt(thisBookPublicationDate[0]) > Integer.parseInt(otherBookPublicationDate[0])) // Compare month
			return 1;
		else if (Integer.parseInt(thisBookPublicationDate[0]) < Integer.parseInt(otherBookPublicationDate[0]))
			return -1;
		else if (Integer.parseInt(thisBookPublicationDate[1]) > Integer.parseInt(otherBookPublicationDate[1])) // Compare day
			return 1;
		else if (Integer.parseInt(thisBookPublicationDate[1]) < Integer.parseInt(otherBookPublicationDate[1]))
			return -1;
		else 
			return 0;
		
	}
	
	public int compareToTitle(Book bookToCompare) {
		// Checks how to alphabetically order the two books
		if (title.compareTo(bookToCompare.getTitle()) == 0) // Don't change order
			return 0;
		else if (title.compareTo(bookToCompare.getTitle()) > 0) // This book goes after the other book alphabetically
			return 1;
		else // This book goes before the other book alphabetically
			return -1;
	}
	
	public int compareToISBN(Book bookToCompare) {
		// Checks which ISBN is greater
		long otherISBN = Long.parseLong(bookToCompare.getISBN().replaceAll("-", "")); // Get other ISBN and remove hyphen to make it easier to compare results
		long thisISBN = Long.parseLong(getISBN().replaceAll("-", ""));
		
		if (thisISBN == otherISBN)
			return 0;
		else if (thisISBN > otherISBN)
			return 1;
		else // thisISBN < otherISBN
			return -1;
	}
	
	public String toString() {
		return "Title: " + title + ", Author: " + author + ", Publication Date: " + publicationDate + ", description: " + description + ", ISBN: " + ISBN;
	}
	
}
