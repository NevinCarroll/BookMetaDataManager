package bookViewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/*
 * File Name: GUI.java
 * Author: Nevin Fullerton
 * Date: 12/8/2024
 * Description: This is the GUI that allows the user to view, search, sort, add, edit, and remove from the book database. The books information is displayed
 * to the user using a table, each row displaying either the title, author, genres, publication date, description, or ISBN-13 (International Standard Book 
 * Number). When a row in the table is selected by the user, the info panel on the left will be filled with information from that row to present the book
 * info in a more readable way. Above the table is a panel with a text field that allows the user to select how they want to search, and a button to perform
 * the search after they are done typing. The program will then search through the database and present the results in the table by showing only the books
 * found. The user can also sort through a variety of ways including author, title, publication date, and ISBN, which will then sort the table from least to
 * greatest. The user is also able to add a book by pressing a button, which will open a dialog box that requests the user fill in the information of the book 
 * they want to add. The user can edit a book that they selected from the table, and a dialog box will appear with the book information filled out, allowing the
 * user to change it and edit the book. Finally the user is able to remove a book by selecting in the table and pressing the remove button, the program will
 * prompt the user to ensure they really want to delete the book. If the user presses yes, the book will be removed from the database.
 */

public class GUI extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel mainPanel; // Holds the book table that displays book information
	private JPanel bookInfoPanel; // Displays information of the currently selected book in the table
	private JPanel searchPanel; // Has options for the user to search, sort, and edit the database
	private JTable bookTable; // Displays books to user in a table
	private JScrollPane bookScrollPane;  // Makes the table have a scroll wheel
	JComboBox<String> sortBox; // Allows program to see which sorting method is being used by user

	private BookDatabase bookDatabase = new BookDatabase(); // Class that stores all the book data
	
	private List<String> booksBeingDisplayed; // ISBN of Books currently being displayed, used to know what book to display on side panel, sorted and searched using bookDatabase class
	
	public GUI() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Book Database");
		this.setLayout(new BorderLayout());
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		bookTable = new JTable(); // Used to display all books
		
		bookScrollPane = new JScrollPane(bookTable);
		
		mainPanel.add(bookScrollPane, BorderLayout.CENTER); // Holds the book table
		
		this.add(mainPanel, BorderLayout.CENTER);
		
		bookInfoPanel = new JPanel(); // Shows information of the book selected in the table
		bookInfoPanel.setLayout(new BoxLayout(bookInfoPanel, BoxLayout.PAGE_AXIS));
		bookInfoPanel.setPreferredSize(new Dimension(200, 100)); // Fixes the size of the panel, so that the table is not constantly shifting left and right
		bookInfoPanel.setMinimumSize(new Dimension(200, 100));
		bookInfoPanel.setMaximumSize(new Dimension(200, 100));

		// Book info
		JLabel bookTitleLabel = new JLabel("Title: ");
		bookInfoPanel.add(bookTitleLabel);
		
		JLabel bookAuthorLabel = new JLabel("Author: ");
		bookInfoPanel.add(bookAuthorLabel);
		
		JLabel bookGenresLabel = new JLabel("Genres: ");
		bookInfoPanel.add(bookGenresLabel);
		
		JLabel bookPublicationDateLabel = new JLabel("Publication Date: ");
		bookInfoPanel.add(bookPublicationDateLabel);
		
		JLabel bookDescriptionLabel = new JLabel("Description: ");
		bookInfoPanel.add(bookDescriptionLabel);
		
		JLabel bookIsbnLabel = new JLabel("ISBN: ");
		bookInfoPanel.add(bookIsbnLabel);
		
		this.add(bookInfoPanel, BorderLayout.WEST); // Left side of program
		
		// Holds all book searching, sorting, and editing 
		searchPanel = new JPanel();
		searchPanel.setLayout(new BorderLayout());
		JPanel layoutSearchComponentsPanel = new JPanel(); // Makes components horizontal
		layoutSearchComponentsPanel.setLayout(new BoxLayout(layoutSearchComponentsPanel, BoxLayout.LINE_AXIS));
		
		JLabel searchLabel = new JLabel("Search: "); 
		layoutSearchComponentsPanel.add(searchLabel);
		
		String focusText = "Type here to search"; 
		JTextField searchTextField = new JTextField(focusText); 
		searchTextField.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (searchTextField.getText().trim().equals(focusText)) { // Will remove the focusText string if user clicks on text field
					searchTextField.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) { // Will only bring back focusText string if user has not entered anything
				if (searchTextField.getText().trim().equals("")) {
					searchTextField.setText(focusText);
				}
			}
			
		});
		
		
		String[] searchOptions = {"Author", "Title", "Genres", "ISBN"};
		JComboBox<String> searchBox = new JComboBox<String>(searchOptions); // Allows user to select which search option they want
		layoutSearchComponentsPanel.add(searchBox);
		
		JButton searchButton = new JButton("Search"); // Will search depending on what the user has selected
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (searchBox.getSelectedItem() == "Author") {
					List<String> booksFound = bookDatabase.searchByAuthor(searchTextField.getText().trim());
					displayBooks(booksFound);
				} else if (searchBox.getSelectedItem() == "Title") {
					List<String> booksFound = bookDatabase.searchByTitle(searchTextField.getText().trim());
					displayBooks(booksFound);
				} else if (searchBox.getSelectedItem() == "Genres") {
					List<String> booksFound = bookDatabase.searchByGenre(searchTextField.getText().trim());
					displayBooks(booksFound);
				} else if (searchBox.getSelectedItem() == "ISBN") {
					List<String> booksFound = bookDatabase.searchByISBN(searchTextField.getText().trim());
					displayBooks(booksFound);
				}
			}
		});
		
		layoutSearchComponentsPanel.add(searchButton);
		layoutSearchComponentsPanel.add(searchTextField);
		
		JButton displayAllBooksButton = new JButton("Display All Books");
		displayAllBooksButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				displayAllBooks();
			}
			
		});
		layoutSearchComponentsPanel.add(displayAllBooksButton);
		
		JLabel sortLabel = new JLabel("Sort: ");
		layoutSearchComponentsPanel.add(sortLabel);
		
		String[] sortOptions = {"Author", "Title", "Publication Date", "ISBN"};
		sortBox = new JComboBox<String>(sortOptions);
		layoutSearchComponentsPanel.add(sortBox);
		
		sortBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				sortBooks(booksBeingDisplayed);
			}
			
		});
		
		JButton addButton = new JButton("Add Book");
		layoutSearchComponentsPanel.add(addButton);
		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addBook();
			}
			
		});
		
		JButton editButton = new JButton("Edit Book");
		layoutSearchComponentsPanel.add(editButton);
		editButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				editBook();
			}
			
		});
		
		JButton removeButton = new JButton("Remove Book");
		layoutSearchComponentsPanel.add(removeButton);
		removeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				removeBook();
			}
			
		});
		
		searchPanel.add(layoutSearchComponentsPanel, BorderLayout.CENTER);
		this.add(searchPanel, BorderLayout.NORTH);
		this.setSize(new Dimension(1100, 500));
		
		displayAllBooks(); // Displays all books to user
	}
	
	/*
	 * Displays the given book in the book table
	 */
	public void displayBooks(List<String> ISBNsToDisplay) {
		// Display books in table
		String[] columnNames = {"Title", "Author", "Genres", "Publication Date", "Description", "ISBN"};
		String[][] allBookData = new String[ISBNsToDisplay.size()][columnNames.length]; 

		booksBeingDisplayed = new LinkedList<String>(); // Clears list so that it displays only books being put into method
		
		//Take any array of strings (ISBNs) and display them to User
		
		int bookCounter = 0; 
		for(String bookISBN: ISBNsToDisplay) {
			Book thisBook = bookDatabase.getBook(bookISBN); // Gets book from database
			
			String bookGenres = ""; // Get genres and put them into a single string
			for (int i = 0; i < thisBook.getGenres().length; i++) {
				if (i == 0) // No comma on first genre
					bookGenres += thisBook.getGenres()[i];
				else 
					bookGenres += ", " + thisBook.getGenres()[i];
			}
			
			//One row in the table
			String[] bookMetaData = {thisBook.getTitle(), thisBook.getAuthor(), bookGenres ,thisBook.getPublicationDate(), thisBook.getDescription(), thisBook.getISBN()};
			allBookData[bookCounter] = Arrays.copyOf(bookMetaData, bookMetaData.length); // Puts the meta data to a row in the array
			booksBeingDisplayed.add(thisBook.getISBN()); // Stores isbn to be used later
			bookCounter++;
		}

		bookTable.setModel(new DefaultTableModel(allBookData, columnNames)); // Will automatically put the data in rows and columns
		bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only allow the user to select one row at a time
		bookTable.setDefaultEditor(Object.class, null); // Prevents editing of tabled directly by user
		bookTable.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) { // When user clicks on a row, display book info on the side panel
				if (bookTable.getSelectedRow() != -1) {
					displayBookInfo(bookTable.getSelectedRow());
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// Ignore, unused
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// Ignore, unused
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// Ignore, unused
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// Ignore, unused
			}
			
		});
	}
	
	/*
	 * Displays the book information on the side panel
	 */
	private void displayBookInfo(int bookIndex) {
		bookInfoPanel.removeAll(); // Reset all text in info panel
		
		Book bookToDisplay = bookDatabase.getBook(booksBeingDisplayed.get(bookIndex));
		
		// The label text is wrapped in html because that will allow the text to wrap when it gets to the end of the panel, allowing it to not get cut off
		JLabel bookTitleLabel = new JLabel("<html> Title: " + bookToDisplay.getTitle() + "</html>");
		bookInfoPanel.add(bookTitleLabel);
		
		JLabel bookAuthorLabel = new JLabel("<html> Author: " + bookToDisplay.getAuthor() + "</html>");
		bookInfoPanel.add(bookAuthorLabel);
		
		String bookGenres = ""; // Convert the genre array from the book into a single string
		for (int i = 0; i < bookToDisplay.getGenres().length; i++) {
			if (i == 0)
				bookGenres += bookToDisplay.getGenres()[i];
			else
				bookGenres += ", " + bookToDisplay.getGenres()[i];
		}
		
		JLabel bookGenresLabel = new JLabel("<html> Genres: " + bookGenres +"</html>");
		bookInfoPanel.add(bookGenresLabel);
		
		JLabel bookPublicationDateLabel = new JLabel("<html> Publication Date: " + bookToDisplay.getPublicationDate() + "</html>");
		bookInfoPanel.add(bookPublicationDateLabel);
		
		JLabel bookDescriptionLabel = new JLabel("<html> Description: " + bookToDisplay.getDescription() + "</html>");
		bookInfoPanel.add(bookDescriptionLabel);
		
		JLabel bookIsbnLabel = new JLabel("<html> ISBN: " + bookToDisplay.getISBN() + "</html>");
		bookInfoPanel.add(bookIsbnLabel);
		
		bookInfoPanel.revalidate(); // Refresh panel
		bookInfoPanel.repaint();
	}

	private void addBook() {
		JDialog addingDialog = new JDialog(this, "Adding Book");
		addingDialog.setResizable(false); // Makes user unable to resize dialog box
		JPanel dialogContent = new JPanel(); // Had to create another panel, because using boxLayout directly with JDialog does not work
		dialogContent.setLayout(new BoxLayout(dialogContent, BoxLayout.Y_AXIS));
		
		JLabel instructionLabel = new JLabel("Fill out the following information, then click the 'add' button at the bottom to add the book to the database.");
		dialogContent.add(instructionLabel);
		
		JPanel titlePanel = new JPanel(); // Panel that stores the label and text field
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS)); // Layouts out horizontal
		JLabel titleLabel = new JLabel("Title: ");
		JTextField titleTextField =  new JTextField();
		titlePanel.add(titleLabel);
		titlePanel.add(titleTextField);
		dialogContent.add(titlePanel);
		
		JPanel authorPanel = new JPanel();
		authorPanel.setLayout(new BoxLayout(authorPanel, BoxLayout.X_AXIS));
		JLabel authorLabel = new JLabel("Author: ");
		JTextField authorTextField =  new JTextField();
		authorPanel.add(authorLabel);
		authorPanel.add(authorTextField);
		dialogContent.add(authorPanel);
		
		JPanel genresPanel = new JPanel();
		genresPanel.setLayout(new BoxLayout(genresPanel, BoxLayout.X_AXIS));
		JLabel genresLabel = new JLabel("Genres (separte genres with ' , ') : ");
		JTextField genresTextField =  new JTextField();
		genresPanel.add(genresLabel);
		genresPanel.add(genresTextField);
		dialogContent.add(genresPanel);
		
		JPanel publicationDatePanel = new JPanel(); 
		publicationDatePanel.setLayout(new BoxLayout(publicationDatePanel, BoxLayout.X_AXIS));
		JLabel publicationDateLabel = new JLabel("Publication Date (MM/DD/YYYY) : ");
		JTextField publicationDateTextField =  new JTextField();
		publicationDatePanel.add(publicationDateLabel);
		publicationDatePanel.add(publicationDateTextField);
		dialogContent.add(publicationDatePanel);
		
		JPanel descriptionPanel = new JPanel();
		descriptionPanel.setLayout(new BoxLayout(descriptionPanel, BoxLayout.X_AXIS));
		JLabel descriptionLabel = new JLabel("Description: ");
		JTextField descriptionTextField =  new JTextField();
		descriptionPanel.add(descriptionLabel);
		descriptionPanel.add(descriptionTextField);
		dialogContent.add(descriptionPanel);
		
		JPanel ISBNPanel = new JPanel();
		ISBNPanel.setLayout(new BoxLayout(ISBNPanel, BoxLayout.X_AXIS));
		JLabel ISBNLabel = new JLabel("ISBN ({978 or 979}-xxxxxxxxxx) REQUIRED : ");
		JTextField ISBNTextField =  new JTextField();
		ISBNPanel.add(ISBNLabel);
		ISBNPanel.add(ISBNTextField);
		dialogContent.add(ISBNPanel);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		JButton addButton = new JButton("Add Book");
		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int result = bookDatabase.addBookToDataBase(titleTextField.getText(), authorTextField.getText(), genresTextField.getText(), publicationDateTextField.getText(), descriptionTextField.getText(), ISBNTextField.getText());
				
				if (result == 0) { // Success
					JOptionPane.showMessageDialog(dialogContent, "Book succesfully added!", "Success", JOptionPane.INFORMATION_MESSAGE);
					booksBeingDisplayed.add(ISBNTextField.getText()); // Adds to books being displayed
					displayBooks(booksBeingDisplayed); 
				} else if (result == 1) // Publication date incorrectly formatted
					JOptionPane.showMessageDialog(dialogContent, "There was an fomating error with the publication date, ensure it is in the correct format. EX: 'MM/DD/YYYY -> 09/04/1998'", "Error", JOptionPane.ERROR_MESSAGE);
				else if (result == 2) // ISBN incorrectly formatted
					JOptionPane.showMessageDialog(dialogContent, "There was an fomating error with the ISBN or it does not exists. Ensure it is in the correct format or filled out. It starts with '978' or '979' folled by a hypen '-' then 10 numbers. EX: '978-0345339683'", "Error", JOptionPane.ERROR_MESSAGE);
				else if (result == 3) // ISBN already exists
					JOptionPane.showMessageDialog(dialogContent, "That ISBN already exists, please change it to one that does not exist in the database.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		JButton cancelButton =  new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addingDialog.setVisible(false);
				addingDialog.dispose();
			}
			
		});
		
		buttonPanel.add(addButton);
		buttonPanel.add(cancelButton);
		dialogContent.add(buttonPanel);
		
		addingDialog.add(dialogContent);
		addingDialog.pack();
		addingDialog.setVisible(true);
	}
	
	private void editBook() {
		if (bookTable.getSelectedRow() == -1) { // Ensure a book is selected before counting
			JOptionPane.showMessageDialog(this, "Please click the book in the table you want to edit, then press the edit button again.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		Book selectedBook = bookDatabase.getBook(booksBeingDisplayed.get(bookTable.getSelectedRow()));
		String bookOrginalISBN = selectedBook.getISBN(); // Used to check if user changed ISBN of book
		
		JDialog editDialog = new JDialog(this, "Editing Book");
		editDialog.setResizable(false); // Makes user unable to resize dialog box
		JPanel dialogContent = new JPanel(); // Had to create another panel, because using boxLayout directly with JDialog does not work
		dialogContent.setLayout(new BoxLayout(dialogContent, BoxLayout.Y_AXIS));
		
		JLabel instructionLabel = new JLabel("Edit the following information, then click the 'edit' button at the bottom to edit the book.");
		dialogContent.add(instructionLabel);
		
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
		JLabel titleLabel = new JLabel("Title: ");
		JTextField titleTextField =  new JTextField(selectedBook.getTitle()); // Displays information already in book, so that user is able to edit it
		titlePanel.add(titleLabel);
		titlePanel.add(titleTextField);
		dialogContent.add(titlePanel);
		
		JPanel authorPanel = new JPanel();
		authorPanel.setLayout(new BoxLayout(authorPanel, BoxLayout.X_AXIS));
		JLabel authorLabel = new JLabel("Author: ");
		JTextField authorTextField =  new JTextField(selectedBook.getAuthor());
		authorPanel.add(authorLabel);
		authorPanel.add(authorTextField);
		dialogContent.add(authorPanel);
		
		String bookGenres = ""; // Get genres and put them into a single string
		for (int i = 0; i < selectedBook.getGenres().length; i++) {
			if (i == 0)
				bookGenres += selectedBook.getGenres()[i];
			else 
				bookGenres += ", " + selectedBook.getGenres()[i];
		}
		
		JPanel genresPanel = new JPanel();
		genresPanel.setLayout(new BoxLayout(genresPanel, BoxLayout.X_AXIS));
		JLabel genresLabel = new JLabel("Genres (separte genres with ' , ') : ");
		JTextField genresTextField =  new JTextField(bookGenres);
		genresPanel.add(genresLabel);
		genresPanel.add(genresTextField);
		dialogContent.add(genresPanel);
		
		JPanel publicationDatePanel = new JPanel(); 
		publicationDatePanel.setLayout(new BoxLayout(publicationDatePanel, BoxLayout.X_AXIS));
		JLabel publicationDateLabel = new JLabel("Publication Date (MM/DD/YYYY) : ");
		JTextField publicationDateTextField =  new JTextField(selectedBook.getPublicationDate());
		publicationDatePanel.add(publicationDateLabel);
		publicationDatePanel.add(publicationDateTextField);
		dialogContent.add(publicationDatePanel);
		
		JPanel descriptionPanel = new JPanel();
		descriptionPanel.setLayout(new BoxLayout(descriptionPanel, BoxLayout.X_AXIS));
		JLabel descriptionLabel = new JLabel("Description: ");
		JTextField descriptionTextField =  new JTextField(selectedBook.getDescription());
		descriptionPanel.add(descriptionLabel);
		descriptionPanel.add(descriptionTextField);
		dialogContent.add(descriptionPanel);
		
		JPanel ISBNPanel = new JPanel();
		ISBNPanel.setLayout(new BoxLayout(ISBNPanel, BoxLayout.X_AXIS));
		JLabel ISBNLabel = new JLabel("ISBN ({978 or 979}-xxxxxxxxxx) REQUIRED : ");
		JTextField ISBNTextField =  new JTextField(selectedBook.getISBN());
		ISBNPanel.add(ISBNLabel);
		ISBNPanel.add(ISBNTextField);
		dialogContent.add(ISBNPanel);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		JButton editButton = new JButton("Edit Book");
		editButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int result = bookDatabase.editBookFromDatabase(titleTextField.getText(), authorTextField.getText(), genresTextField.getText(), publicationDateTextField.getText(), descriptionTextField.getText(), ISBNTextField.getText(), bookOrginalISBN);
				
				if (result == 0) { // Success
					JOptionPane.showMessageDialog(dialogContent, "Book succesfully edited!", "Success", JOptionPane.INFORMATION_MESSAGE);
					booksBeingDisplayed.set(booksBeingDisplayed.indexOf(bookOrginalISBN), ISBNTextField.getText()); // Switch out ISBNs
					displayBooks(booksBeingDisplayed); // Will update table with new information
				} else if (result == 1) // Publication date incorrectly formatted
					JOptionPane.showMessageDialog(dialogContent, "There was an fomating error with the publication date, ensure it is in the correct format. EX: 'MM/DD/YYYY -> 09/04/1998'", "Error", JOptionPane.ERROR_MESSAGE);
				else if (result == 2) // ISBN incorrectly formatted
					JOptionPane.showMessageDialog(dialogContent, "There was an fomating error with the ISBN or it does not exists. Ensure it is in the correct format or filled out. It starts with '978' or '979' folled by a hypen '-' then 10 numbers. EX: '978-0345339683'", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		JButton cancelButton =  new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				editDialog.setVisible(false); // Gets rid of dialog box
				editDialog.dispose();
			}
			
		});
		
		buttonPanel.add(editButton);
		buttonPanel.add(cancelButton);
		dialogContent.add(buttonPanel);
		
		editDialog.add(dialogContent);
		editDialog.pack();
		editDialog.setVisible(true);
	}
	
	/*
	 * If a row is selected in the JTable, prompt the user to remove that book from the database
	 */
	private void removeBook() {
		if (bookTable.getSelectedRow() == -1) { // Makes sure user has selected a book before continuing
			JOptionPane.showMessageDialog(this, "Please click the book in the table you want to delete, then press the remove button again.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		String selectedBook = booksBeingDisplayed.get(bookTable.getSelectedRow());
		
		int userChoice = JOptionPane.showConfirmDialog(this, "Do you wish to delete the currently selected book with the title '" + bookTable.getValueAt(bookTable.getSelectedRow(), 0) + "'?");
	
		if (userChoice == 0) { // Remove book if user clicks yes
			bookDatabase.removeBookFromDatabase(selectedBook);
			booksBeingDisplayed.remove(bookTable.getSelectedRow()); // Remove ISBN from books being displayed to prevent it from being displayed
			displayBooks(booksBeingDisplayed);
			JOptionPane.showMessageDialog(this, "Book succesfully removed!", "Book Removed", JOptionPane.INFORMATION_MESSAGE);
		}
		// Do nothing if user clicks no, cancel, or exits dialog box
		
	}
	
	/*
	 * Allows the program to sort the books at any time
	 */
	private void sortBooks(List<String> booksToSort) {
		if (sortBox.getSelectedItem() == "Author") { // Gets what the user has selected in the combo box and sort the table in that way
			bookDatabase.sortByAuthor(booksToSort);
		} else if (sortBox.getSelectedItem() == "Title") {
			bookDatabase.sortByTitle(booksToSort);
		} else if (sortBox.getSelectedItem() == "Publication Date") {
			bookDatabase.sortByPublicationDate(booksToSort);
		} else if (sortBox.getSelectedItem() == "ISBN") {
			bookDatabase.sortByISBN(booksToSort);
		}
		
		displayBooks(booksToSort);
	}
	
	private void displayAllBooks() {
		List<String> allBooks = bookDatabase.allISBNs(); // Gets all books, sorts them, then displays them
		sortBooks(allBooks);
		displayBooks(allBooks);
	}
		
	
	public static void main(String[] args) {
		GUI bookGUI = new GUI();
		
		bookGUI.setLocationRelativeTo(null); // Centers window to middle of screen
		bookGUI.setVisible(true);
	}
	
}
