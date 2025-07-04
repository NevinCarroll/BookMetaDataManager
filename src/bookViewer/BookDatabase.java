package bookViewer;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/*
 * File Name: BookDatabase.java
 * Author: Nevin Fullerton
 * Date: 12/8/2024
 * Description: This class stores all the books in a Map, and has functionality to sort ISBN arrays given to it, and search through the database to find all 
 * books that match the string the user gave the program. The database uses the Jackson library to read and edit JSON files, which are used to store the book
 * information after the user closes the program. The class reads the JSON file and stores all the book from it into a Map that uses the book ISBN (International
 * Standard Book Number) as the key. The class has a function to get books from the Map using the book's ISBN, as well return all ISBN keys if the GUI needs
 * to present all books at once. The class is able to add a book to a JSON file when given the book information from the GUI. The database is also able to 
 * edit a book in the JSON file given new information from the GUI. Finally the database is able to remove a book from the JSON file when given an ISBN from
 * the GUI. The database can search through the Map to find any book that has a string that the user gives it. It can search through Titles, Authors, Genres, and
 * ISBNs. The database is also able to sort an array of ISBN given to it, so that it is the correct order to display to the user. It can sort using titles,
 * authors, publications dates, and ISBNs.
 */

public class BookDatabase {

	private Map<String, Book> bookHolder = new HashMap<>(); // ISBN used as key, holds all books
	
	BookDatabase() {
		readJSONFile();
	}
	
	public void readJSONFile() {
		ObjectMapper jsonBookMapper = new ObjectMapper(); // Reads JSON file
		
		try (FileReader jsonReader = new FileReader("src/bookViewer/data/BookData.json")) {
			ArrayNode bookArrayNode = (ArrayNode) jsonBookMapper.readTree(jsonReader); // Gets the array in the JSON file that stores the book info
			
			for (JsonNode book : bookArrayNode) { // Goes through each book in the JSON file and adds it to the Map
				ArrayNode jsonBookGenres = (ArrayNode) book.get("genres"); // Get the genres as an array
				String[] translatedGenres = new String[jsonBookGenres.size()];
				
				for (int i = 0; i < jsonBookGenres.size(); i++) // Gets the genres and put them into a string array
					translatedGenres[i] = jsonBookGenres.get(i).asText();
				
				// get the book information from the JSON file and put it into a Book object
				Book newBook = new Book(book.get("title").asText(), book.get("author").asText(), translatedGenres, book.get("publicationDate").asText(), book.get("description").asText(), book.get("isbn").asText());
				bookHolder.put(newBook.getISBN(), newBook);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 
	 */
	public int addBookToDataBase(String title, String author, String genres, String publicationDate, String description, String ISBN) {
		
		if (bookHolder.containsKey(ISBN)) // Can't have duplicate ISBNs in the database
			return 3;
		
		// Removes white space form beginning and end of Strings
		title = title.trim();
		author = author.trim();
		publicationDate = publicationDate.trim();
		description = description.trim();
		ISBN = ISBN.trim();
		
		String[] genresSplit = genres.split(","); // Split genres 
		for (int i = 0; i < genresSplit.length; i++) // Remove white space from genre strings
			genresSplit[i] = genresSplit[i].trim();
		
		// Verifying Publication Date is correctly formatted
		String[] publicationDateSplit = publicationDate.split("/");
		
		if (!publicationDate.equals("")) { // Publication Date can be empty, but if it is filled, then must be in proper format
			if (publicationDateSplit.length != 3) 
				return 1; // Error with publication date
			if (publicationDateSplit[0].length() != 2 || publicationDateSplit[1].length() != 2 || publicationDateSplit[2].length() != 4) // Ensure the size of each one is correct MM/DD/YYYY
				return 1;
			
			try {
				if(Integer.parseInt(publicationDateSplit[0]) > 12 || Integer.parseInt(publicationDateSplit[1]) > 31) // Upper bound of these numbers
					return 1;
				if(Integer.parseInt(publicationDateSplit[0]) < 1 || Integer.parseInt(publicationDateSplit[1]) < 1 || Integer.parseInt(publicationDateSplit[2]) < 1) // Lower bound of these numbers
					return 1;
			} catch (NumberFormatException e) {
				return 1; // If the month, day, or year are not an integer
			}
		}
		
		String[] ISBNSplit = ISBN.split("-"); // ISBN must be 978-xxxxxxxxxx or 979-xxxxxxxxxx
		
		if (ISBNSplit.length != 2) // Should only be two parts
			return 2; // Error with ISBN
			
		if(ISBNSplit[0].equals("978") || ISBNSplit[0].equals("979")) { // There has to be an ISBN because that is how the program searches for books
			if(ISBNSplit[1].length() != 10) // Second part should be a number 10 digits in length
				return 2;

			try {
				Long.parseLong(ISBNSplit[1]); // Checks if the second part of the ISBN is an integer, no other characters
			} catch (NumberFormatException e) {
				return 2;
			}
			
		} else 
			return 2; // First three numbers must equal 978 or 979
			
		Book newBook = new Book(title, author, genresSplit, publicationDate, description, ISBN); // Put new book in Map
		bookHolder.put(newBook.getISBN(), newBook);
		
		// Add book to Json file
		
		ObjectMapper jsonMapper = new ObjectMapper(); // Reads JSON file
		
		try (FileReader jsonReader = new FileReader("src/bookViewer/data/BookData.json")) {
			Path jsonFile = Path.of("src/bookViewer/data/BookData.json"); // Path to JSON File
			ArrayNode bookArrayNode = (ArrayNode) jsonMapper.readTree(jsonReader); // Get the array from the JSON file that stores the book information
			
			JsonNode newJSONBook = jsonMapper.valueToTree(newBook); // converts the object's variables into JSON notation
			bookArrayNode.add(newJSONBook); // Adds the new book to the JSON file
			
			// JSON generator will overwrite the current JSON file, or if missing will create a new one
			JsonGenerator jsonGen = jsonMapper.getFactory().createGenerator(Files.newBufferedWriter(jsonFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE));
			jsonMapper.writerWithDefaultPrettyPrinter().writeValue(jsonGen, bookArrayNode); // Writes to the file with formatting, so that it is easier to read
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return 0; // Successful addition to database
	}
	
	/*
	 * Edit book in the database 
	 */
	public int editBookFromDatabase(String title, String author, String genres, String publicationDate, String description, String ISBN, String orginalISBN) {
		// Removes white space form beginning and end of Strings
		title = title.trim();
		author = author.trim();
		publicationDate = publicationDate.trim();
		description = description.trim();
		ISBN = ISBN.trim();
		
		String[] genresSplit = genres.split(","); // Split genres 
		for (int i = 0; i < genresSplit.length; i++) // Remove white space from genre strings
			genresSplit[i] = genresSplit[i].trim();
		
		// Verifying Publication Date is correctly formatted
		String[] publicationDateSplit = publicationDate.split("/");
		
		if (!publicationDate.equals("")) { // Publication Date can be empty, but if it is filled, then must be in proper format
			if (publicationDateSplit.length != 3) 
				return 1; // Error with publication date
			if (publicationDateSplit[0].length() != 2 || publicationDateSplit[1].length() != 2 || publicationDateSplit[2].length() != 4) // Ensure the size of each one is correct MM/DD/YYYY
				return 1;
			
			try {
				if(Integer.parseInt(publicationDateSplit[0]) > 12 || Integer.parseInt(publicationDateSplit[1]) > 31) // Upper bound of these numbers
					return 1;
				if(Integer.parseInt(publicationDateSplit[0]) < 1 || Integer.parseInt(publicationDateSplit[1]) < 1 || Integer.parseInt(publicationDateSplit[2]) < 1) // Lower bound of these numbers
					return 1;
			} catch (NumberFormatException e) {
				return 1; // If the month, day, or year are not an integer
			}
		}
		
		String[] ISBNSplit = ISBN.split("-"); // ISBN must be 978-xxxxxxxxxx or 979-xxxxxxxxxx
		
		if (ISBNSplit.length != 2) // Should only be two parts
			return 2; // Error with ISBN
			
		if(ISBNSplit[0].equals("978") || ISBNSplit[0].equals("979")) { // There has to be an ISBN because that is how the program searches for books
			if(ISBNSplit[1].length() != 10) // Second part should be a number 10 digits in length
				return 2;

			try {
				Long.parseLong(ISBNSplit[1]); // Checks if the second part of the ISBN is a long, no other characters
			} catch (NumberFormatException e) {
				return 2;
			}
			
		} else 
			return 2; // First three numbers must equal 978 or 979
			
		Book editedBook = new Book(title, author, genresSplit, publicationDate, description, ISBN); // Overwrite Key in Map

		// Check if ISBN was changed by user
		if (ISBN.equals(orginalISBN)) {
			bookHolder.put(editedBook.getISBN(), editedBook); // Puts edited book as a new entry in the Map
			ObjectMapper jsonMapper = new ObjectMapper(); // Reads JSON file
			
			try (FileReader jsonReader = new FileReader("src/bookViewer/data/BookData.json")) {
				Path jsonFile = Path.of("src/bookViewer/data/BookData.json"); //
				ArrayNode bookArrayNode = (ArrayNode) jsonMapper.readTree(jsonReader); // An array of the books stored in the JSON file
				
				JsonNode editedJSONBook = jsonMapper.valueToTree(editedBook); // converts the object's variables into JSON notation
				
				for (int i = 0; i < bookArrayNode.size(); i++) { // Simple sequential search to remove book from JSON file
					if (bookArrayNode.get(i).get("isbn").asText().equals(editedBook.getISBN())) { 
						bookArrayNode.remove(i);
						bookArrayNode.add(editedJSONBook);
						break;
					}
				}
				
				// JSON generator will overwrite the current JSON file, or if missing will create a new one
				JsonGenerator jsonGen = jsonMapper.getFactory().createGenerator(Files.newBufferedWriter(jsonFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE));
				jsonMapper.writerWithDefaultPrettyPrinter().writeValue(jsonGen, bookArrayNode); // Writes to the file with formatting, so that it is easier to read
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		} else { // If ISBN not changed, then remove old book from Map
			removeBookFromDatabase(orginalISBN);
			
			String bookGenres = ""; // Get genres and put them into a single string
			for (int i = 0; i < editedBook.getGenres().length; i++) {
				if (i == 0)
					bookGenres += editedBook.getGenres()[i];
				else 
					bookGenres += ", " + editedBook.getGenres()[i];
			}
			
			addBookToDataBase(editedBook.getTitle(), editedBook.getAuthor(), bookGenres, editedBook.getPublicationDate(), editedBook.getDescription(), editedBook.getISBN());
		}
		return 0; // Successful addition to database
	}
	
	public void removeBookFromDatabase(String ISBNOfBook) {
		ObjectMapper jsonBookMapper = new ObjectMapper(); 
		
		try (FileReader jsonReader = new FileReader("src/bookViewer/data/BookData.json")) {
			ArrayNode bookArrayNode = (ArrayNode) jsonBookMapper.readTree(jsonReader); // get the array in the JSON file that stores all the https://mkyong.com/java/jackson-how-to-parse-json/
			
			for (int i = 0; i < bookArrayNode.size(); i++) { // Simple sequential search
				if (bookArrayNode.get(i).get("isbn").asText().equals(ISBNOfBook)) { 
					bookArrayNode.remove(i);
					bookHolder.remove(ISBNOfBook);
					break;
				}
			}
			
			Path jsonFile = Path.of("src/bookViewer/data/BookData.json"); // Path of json file
			// JSON generator will overwrite the current JSON file, or if missing will create a new one
			JsonGenerator jsonGen = jsonBookMapper.getFactory().createGenerator(Files.newBufferedWriter(jsonFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING));
			jsonBookMapper.writerWithDefaultPrettyPrinter().writeValue(jsonGen, bookArrayNode); // writes it formatted so it is easier to read 
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Searches through database and returns results, uses sequential search
	 */ 
	public List<String> searchByAuthor(String authorToSearch) {
		List<String> booksFound = new LinkedList<String>(); // All books with the string in them
		
		authorToSearch = authorToSearch.toLowerCase(); // ensure that the search is not case sensitive
		
		for (String isbn: bookHolder.keySet()) {
			if (bookHolder.get(isbn).getAuthor().toLowerCase().indexOf(authorToSearch) != -1){ // Checks if any part of the Author contains the string passed to the method
				booksFound.add(bookHolder.get(isbn).getISBN());
			}
		}
		
		return booksFound;
	}
	
	public List<String> searchByTitle(String titleToSearch) {
		List<String> booksFound = new LinkedList<String>(); // All books with the string in them
		
		titleToSearch = titleToSearch.toLowerCase(); // ensure that the search is not case sensitive
		
		for (String isbn: bookHolder.keySet()) {
			if (bookHolder.get(isbn).getTitle().toLowerCase().indexOf(titleToSearch) != -1){ // Checks if any part of the Title contains the string passed to the method
				booksFound.add(bookHolder.get(isbn).getISBN());
			}
		}
		
		return booksFound;
	}
	
	public List<String> searchByGenre(String genreToSearch) {
		List<String> booksFound = new LinkedList<String>(); // All books with the string in them
		
		genreToSearch = genreToSearch.toLowerCase(); // ensure that the search is not case sensitive
		
		for (String isbn: bookHolder.keySet()) {
			for (String genre: bookHolder.get(isbn).getGenres())
				if (genre.toLowerCase().indexOf(genreToSearch) != -1){ // Checks each genre in the book to see if it is
					booksFound.add(bookHolder.get(isbn).getISBN());
					break;
				}
		}
		
		return booksFound;
	}
	
	public List<String> searchByISBN(String isbnToSearch) {
		List<String> booksFound = new LinkedList<String>(); // All books with the string in them
		
		for (String isbn: bookHolder.keySet()) {
			if (bookHolder.get(isbn).getISBN().indexOf(isbnToSearch) != -1) { // Checks if any part of the ISBN contains the string passed to the method
				booksFound.add(bookHolder.get(isbn).getISBN());
			}
		}
		
		return booksFound;		
	}
	
	// Sorting algorithms below sort the books currently displayed to the user, uses bubble sort for all sorting methods
	
	public void sortByISBN(List<String> keys) {
		List<String> isbns = keys; // ISBN keys that have to be sorted
		
		for (int i = 0; i < isbns.size() - 1; i++) {
			for (int j = 0; j < isbns.size() - i - 1; j++) {
				if (bookHolder.get(isbns.get(j)).compareToISBN(bookHolder.get(isbns.get(j + 1))) == 1) { // If current book is greater than next book, swap
					String temp = isbns.get(j); // Swap values
					isbns.set(j, isbns.get(j + 1));
					isbns.set(j + 1, temp);
				}
			}
		}
	}

	public void sortByTitle(List<String> keys) {
		List<String> isbns = keys; // ISBN keys that have to be sorted
		
		for (int i = 0; i < isbns.size() - 1; i++) {
			for (int j = 0; j < isbns.size() - i - 1; j++) {
				if (bookHolder.get(isbns.get(j)).compareToTitle(bookHolder.get(isbns.get(j + 1))) == 1) { // If current book is greater than next book, swap
					String temp = isbns.get(j); // Swap values
					isbns.set(j, isbns.get(j + 1));
					isbns.set(j + 1, temp);
				}
			}
		}
	}
	
	public void sortByAuthor(List<String> keys) {
		List<String> isbns = keys; // ISBN keys that have to be sorted
		
		for (int i = 0; i < isbns.size() - 1; i++) {
			for (int j = 0; j < isbns.size() - i - 1; j++) {
				if (bookHolder.get(isbns.get(j)).compareToAuthor(bookHolder.get(isbns.get(j + 1))) == 1) { // If current book is greater than next book, swap
					String temp = isbns.get(j); // Swap values
					isbns.set(j, isbns.get(j + 1));
					isbns.set(j + 1, temp);
				}
			}
		}
	}
	
	public void sortByPublicationDate(List<String> keys) {
		List<String> isbns = keys; // ISBN keys that have to be sorted
		
		for (int i = 0; i < isbns.size() - 1; i++) {
			for (int j = 0; j < isbns.size() - i - 1; j++) {
				if (bookHolder.get(isbns.get(j)).compareToPublicationDate(bookHolder.get(isbns.get(j + 1))) == 1) { // If current book is greater than next book, swap
					String temp = isbns.get(j); // Swap values
					isbns.set(j, isbns.get(j + 1));
					isbns.set(j + 1, temp);
				}
			}
		}
	}
	
	
	/*
	 * Get a book from the Map
	 */
	public Book getBook(String isbn) {
		return bookHolder.get(isbn);
	}
	
	// Used to display all books in database
	public List<String> allISBNs() {
		List<String> copyOfKeys = new LinkedList<String>();
		for (int i = 0; i < bookHolder.keySet().toArray().length; i++) 
			copyOfKeys.add((String) bookHolder.keySet().toArray()[i]) ;
		
		return copyOfKeys;
	}
	
	
}
