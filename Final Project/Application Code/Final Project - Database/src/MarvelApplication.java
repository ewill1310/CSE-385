
/**
 * Purpose: Create a GUI application that interacts with a MySQL database that contains at 
 * least two large tables, user will be able to interact with the tables, add and remove data at will
 * Author: Evan Williams
 * For: CSE 385D Final Fall 2023
 * Due: 12/10/2023 
 * 
 * Data sets used for this projects
 * 1: Marvel Comic Books Data set: https://www.kaggle.com/datasets/deepcontractor/marvel-comic-books
 * 2: Comic Character (Marvel Data set): https://www.kaggle.com/datasets/danoozy44/comic-characters/?select=marvel-wikia-data.csv
 * 
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EventObject;

public class MarvelApplication {

	private JFrame frame;
	private JTable comicTable;
	private JTable heroTable;
	private JButton addButton;
	private JButton deleteButton;
	private JButton searchButton;
	private JButton homeButton;
	private JTextField searchBox;
	private JComboBox<String> dropDown;

	// Heroes Text Boxes
	private JTextField name = new JTextField(10);
	private JTextField id = new JTextField(10);
	private JTextField align = new JTextField(10);
	private JTextField eye = new JTextField(10);
	private JTextField hair = new JTextField(10);
	private JTextField sex = new JTextField(10);
	private JTextField alive = new JTextField(10);
	private JTextField appearances = new JTextField(10);
	private JTextField fAppearance = new JTextField(10);
	private JTextField year = new JTextField(10);

	// Comic Text Boxes
	private JTextField comic = new JTextField(10);
	private JTextField active = new JTextField(10);
	private JTextField issue = new JTextField(10);
	private JTextField publish = new JTextField(10);
	private JTextField description = new JTextField(10);
	private JTextField penciler = new JTextField(10);
	private JTextField writer = new JTextField(10);
	private JTextField cover = new JTextField(10);
	private JTextField imprint = new JTextField(10);
	private JTextField format = new JTextField(10);
	private JTextField rating = new JTextField(10);
	private JTextField price = new JTextField(10);

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			Font font = new Font(Font.SANS_SERIF, Font.BOLD, 14);
			UIManager.put("Label.font", font);
			UIManager.put("Button.font", font);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MarvelApplication().start();
			}
		});
	}

	private void start() {
		frame = new JFrame("Marvel Database Application");
		JPanel main = new JPanel(new BorderLayout());
		JPanel home = new JPanel(new FlowLayout());
		JButton heroes = new JButton("Marvel Hero Catalogue");
		heroes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.remove(main);
				comicHeroes(frame);
			}
		});
		JButton comics = new JButton("Marvel Comics Catalogue");
		comics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.remove(main);
				comicBooks(frame);
			}
		});
		home.add(heroes);
		home.add(comics);
		JPanel descPane = new JPanel(new FlowLayout());
		JTextArea desc = new JTextArea(10, 45);
		desc.setLineWrap(true);
		desc.setWrapStyleWord(true);
		desc.setText(
				"This is an application that will allow the user to view the information from the comic book database on MySQL, This database contains all of the marvel super heroes and all of the marvel comics that have been printed. The user can search through the respective database tables to find specific results and the user can add entries into and remove entries from the database. To do this the user must type into the Hero Information or Comic Information area then click the button that corresponds to there wanted action. The add feature will add even if the boxes are empty. The delete feature will only delete entries that match what is written in all of the boxes, so make sure each box is filled out properly to ensure you remove whar you want.");
		desc.setEditable(false);
		descPane.add(desc);
		main.add(home, BorderLayout.CENTER);
		main.add(descPane, BorderLayout.SOUTH);
		home.setBackground(new Color(237, 29, 36));
		descPane.setBackground(new Color(237, 29, 36));
		frame.getContentPane().add(main, BorderLayout.NORTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 292);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();
		int x = (screenSize.width - frameSize.width) / 2;
		int y = (screenSize.height - frameSize.height) / 2;
		frame.setLocation(x, y);
		frame.setVisible(true);
		frame.repaint();
	}

	private void comicBooks(JFrame frame) {
		JPanel panel = new JPanel(new BorderLayout());
		JPanel comicPanel = new JPanel(new GridLayout(4, 13));
		JPanel searchPanel = new JPanel(new FlowLayout());
		JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
		comicTable = new JTable();
		comicTable.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { "Comic Name", "Active Years", "Issue Title", "Publish Date", "Issue Description",
						"Penciler", "Writer", "Cover Artist", "Imprint", "Format", "Rating", "Price" }));
		comicTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		// set up buttons
		addButton = new JButton("Add Comic");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addComic();
			}
		});
		deleteButton = new JButton("Delete Comic");
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteComic();
			}
		});
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comicSearch();
			}
		});
		homeButton = new JButton("Home");
		homeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				start();
			}
		});
		// set up rest
		String[] columnNames = { "comic_name", "active_years", "issue_title", "publish_date", "issue_description",
				"penciler", "writer", "cover_artist", "imprint", "format", "rating", "price" };
		dropDown = new JComboBox<>(columnNames);
		setUpComicPanel(comicPanel);
		JPanel temp = new JPanel();
		JPanel temp2 = new JPanel();
		temp.setBackground(new Color(237, 29, 36));
		temp2.setBackground(new Color(237, 29, 36));
		buttonPanel.add(temp);
		buttonPanel.add(addButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(temp2);
		buttonPanel.setSize(1600, 50);
		panel.add(comicPanel, BorderLayout.NORTH);
		panel.add(new JScrollPane(comicTable), BorderLayout.SOUTH);
		panel.add(buttonPanel, BorderLayout.CENTER);
		JLabel searchTypeLabel = new JLabel("Search Type");
		searchBox = new JTextField(20);
		searchPanel.add(homeButton);
		searchPanel.add(searchTypeLabel);
		searchPanel.add(dropDown);
		searchPanel.add(searchBox);
		searchPanel.add(searchButton);
		frame.setTitle("Comic Book Database");
		searchPanel.setBackground(new Color(237, 29, 36));
		comicPanel.setBackground(new Color(237, 29, 36));
		buttonPanel.setBackground(new Color(237, 29, 36));
		panel.setBackground(new Color(237, 29, 36));
		frame.getContentPane().add(searchPanel, BorderLayout.NORTH);
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1600, 650);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();
		int x = (screenSize.width - frameSize.width) / 2;
		int y = (screenSize.height - frameSize.height) / 2;
		frame.setLocation(x, y);
		frame.setVisible(true);
		frame.repaint();
		retrieveComics();
	}

	private void comicHeroes(JFrame frame) {
		heroTable = new JTable();
		heroTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Name", "Identity", "Alignment",
				"Eye", "Hair", "Sex", "Alive", "Appearances", "First Appearance", "Year" }));
		heroTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		// set up buttons
		addButton = new JButton("Add Hero");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addHero();
			}
		});
		deleteButton = new JButton("Delete Hero");
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteHero();
			}
		});
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				heroSearch();
			}
		});
		homeButton = new JButton("Home");
		homeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				start();
			}
		});
		// set up rest
		JPanel panel = new JPanel(new BorderLayout());
		JPanel heroPanel = new JPanel(new GridLayout(4, 11));
		String[] columnNames = { "name", "ID", "ALIGN", "EYE", "HAIR", "SEX", "ALIVE", "APPEARANCES",
				"FIRST_APPEARANCE", "Year" };
		dropDown = new JComboBox<>(columnNames);
		setUpHeroPanel(heroPanel);
		JPanel searchPanel = new JPanel(new FlowLayout());
		JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
		JPanel temp = new JPanel();
		JPanel temp2 = new JPanel();
		temp.setBackground(new Color(237, 29, 36));
		temp2.setBackground(new Color(237, 29, 36));
		buttonPanel.add(temp);
		buttonPanel.add(addButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(temp2);
		buttonPanel.setSize(1600, 80);
		buttonPanel.setBackground(new Color(237, 29, 36));
		panel.add(heroPanel, BorderLayout.NORTH);
		panel.add(new JScrollPane(heroTable), BorderLayout.SOUTH);
		panel.add(buttonPanel, BorderLayout.CENTER);
		JLabel searchTypeLabel = new JLabel("Search Type");
		searchBox = new JTextField(20);
		searchPanel.add(homeButton);
		searchPanel.add(searchTypeLabel);
		searchPanel.add(dropDown);
		searchPanel.add(searchBox);
		searchPanel.add(searchButton);
		searchPanel.setBackground(new Color(237, 29, 36));
		panel.setBackground(new Color(237, 29, 36));
		frame.setTitle("Marvel Hero Database");
		frame.getContentPane().add(searchPanel, BorderLayout.NORTH);
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1600, 650);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();
		int x = (screenSize.width - frameSize.width) / 2;
		int y = (screenSize.height - frameSize.height) / 2;
		frame.setLocation(x, y);
		frame.setVisible(true);
		frame.repaint();
		retrieveHeroes();
	}

	private void setUpComicPanel(JPanel comicPanel) {
		JLabel comicInfo = new JLabel("Comic Info:");
		comicInfo.setHorizontalAlignment(JLabel.CENTER);
		comicPanel.add(comicInfo);
		for (int i = 0; i < 12; i++) {
			JPanel temp = new JPanel();
			temp.setBackground(new Color(237, 29, 36));
			comicPanel.add(temp);
		}
		JLabel comicNameL = new JLabel("Comic Name:");
		comicNameL.setHorizontalAlignment(JLabel.CENTER);
		comicPanel.add(comicNameL);
		comicPanel.add(comic);
		JLabel activeYearsL = new JLabel("Active Years:");
		activeYearsL.setHorizontalAlignment(JLabel.CENTER);
		comicPanel.add(activeYearsL);
		comicPanel.add(active);
		JLabel issueTitleL = new JLabel("Issue Title:");
		issueTitleL.setHorizontalAlignment(JLabel.CENTER);
		comicPanel.add(issueTitleL);
		comicPanel.add(issue);
		JLabel publishL = new JLabel("Publish Date:");
		publishL.setHorizontalAlignment(JLabel.CENTER);
		comicPanel.add(publishL);
		comicPanel.add(publish);
		JLabel descL = new JLabel("Issue Description:");
		descL.setHorizontalAlignment(JLabel.CENTER);
		comicPanel.add(descL);
		comicPanel.add(description);
		JLabel penL = new JLabel("Penciler:");
		penL.setHorizontalAlignment(JLabel.CENTER);
		comicPanel.add(penL);
		comicPanel.add(penciler);
		JPanel temp = new JPanel();
		temp.setBackground(new Color(237, 29, 36));
		comicPanel.add(temp);
		JLabel writerL = new JLabel("Writer:");
		writerL.setHorizontalAlignment(JLabel.CENTER);
		comicPanel.add(writerL);
		comicPanel.add(writer);
		JLabel coverL = new JLabel("Cover Artist:");
		coverL.setHorizontalAlignment(JLabel.CENTER);
		comicPanel.add(coverL);
		comicPanel.add(cover);
		JLabel imprintL = new JLabel("Imprint:");
		imprintL.setHorizontalAlignment(JLabel.CENTER);
		comicPanel.add(imprintL);
		comicPanel.add(imprint);
		JLabel formatL = new JLabel("Format:");
		formatL.setHorizontalAlignment(JLabel.CENTER);
		comicPanel.add(formatL);
		comicPanel.add(format);
		JLabel ratingL = new JLabel("Rating:");
		ratingL.setHorizontalAlignment(JLabel.CENTER);
		comicPanel.add(ratingL);
		comicPanel.add(rating);
		JLabel priceL = new JLabel("Price:");
		priceL.setHorizontalAlignment(JLabel.CENTER);
		comicPanel.add(priceL);
		comicPanel.add(price);
		for (int i = 0; i < 14; i++) {
			JPanel temp3 = new JPanel();
			temp3.setBackground(new Color(237, 29, 36));
			comicPanel.add(temp3);
		}
	}

	private void setUpHeroPanel(JPanel heroPanel) {
		heroPanel.setBackground(new Color(237, 29, 36));
		JLabel heroInfo = new JLabel("Hero Info:");
		heroInfo.setBackground(new Color(237, 29, 36));
		heroInfo.setHorizontalAlignment(JLabel.CENTER);
		heroPanel.add(heroInfo);
		for (int i = 0; i < 10; i++) {
			JPanel temp = new JPanel();
			temp.setBackground(new Color(237, 29, 36));
			heroPanel.add(temp);
		}
		JLabel heroName = new JLabel("Name:");
		heroName.setHorizontalAlignment(JLabel.CENTER);
		heroPanel.add(heroName);
		heroPanel.add(name);
		JLabel heroId = new JLabel("Identity:");
		heroId.setHorizontalAlignment(JLabel.CENTER);
		heroPanel.add(heroId);
		heroPanel.add(id);
		JLabel heroAlign = new JLabel("Alignment:");
		heroAlign.setHorizontalAlignment(JLabel.CENTER);
		heroPanel.add(heroAlign);
		heroPanel.add(align);
		JLabel heroEye = new JLabel("Eye:");
		heroEye.setHorizontalAlignment(JLabel.CENTER);
		heroPanel.add(heroEye);
		heroPanel.add(eye);
		JLabel heroHair = new JLabel("Hair:");
		heroHair.setHorizontalAlignment(JLabel.CENTER);
		heroPanel.add(heroHair);
		heroPanel.add(hair);
		JPanel temp3 = new JPanel();
		temp3.setBackground(new Color(237, 29, 36));
		heroPanel.add(temp3);
		JLabel heroSex = new JLabel("Sex:");
		heroSex.setHorizontalAlignment(JLabel.CENTER);
		heroPanel.add(heroSex);
		heroPanel.add(sex);
		JLabel heroAlive = new JLabel("Alive:");
		heroAlive.setHorizontalAlignment(JLabel.CENTER);
		heroPanel.add(heroAlive);
		heroPanel.add(alive);
		JLabel heroApp = new JLabel("Appearances:");
		heroApp.setHorizontalAlignment(JLabel.CENTER);
		heroPanel.add(heroApp);
		heroPanel.add(appearances);
		JLabel heroFApp = new JLabel("First Appearance:");
		heroFApp.setHorizontalAlignment(JLabel.CENTER);
		heroPanel.add(heroFApp);
		heroPanel.add(fAppearance);
		JLabel heroYear = new JLabel("Year:");
		heroYear.setHorizontalAlignment(JLabel.CENTER);
		heroPanel.add(heroYear);
		heroPanel.add(year);
		for (int i = 0; i < 12; i++) {
			JPanel temp = new JPanel();
			temp.setBackground(new Color(237, 29, 36));
			heroPanel.add(temp);
		}
	}

	private void retrieveHeroes() {
		retrieveHeroes("SELECT * FROM heroes ORDER BY name ASC;");
	}

	private void retrieveHeroes(String query) {
		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/comicbooks", "root",
					"Luke1015!");
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				String name = resultSet.getString("name");
				String id = resultSet.getString("ID");
				String align = resultSet.getString("ALIGN");
				String eye = resultSet.getString("EYE");
				String hair = resultSet.getString("HAIR");
				String sex = resultSet.getString("SEX");
				String alive = resultSet.getString("ALIVE");
				int appearances = resultSet.getInt("APPEARANCES");
				String first_appearance = resultSet.getString("FIRST_APPEARANCE");
				int year = resultSet.getInt("Year");
				((DefaultTableModel) heroTable.getModel()).addRow(
						new Object[] { name, id, align, eye, hair, sex, alive, appearances, first_appearance, year });
			}
			connection.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e);
		}
	}

	private void retrieveComics() {
		retrieveComics("SELECT * FROM comics ORDER BY comic_name ASC");
	}

	private void retrieveComics(String query) {
		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/comicbooks", "root",
					"Luke1015!");
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				String comic_name = resultSet.getString("comic_name");
				String active_years = resultSet.getString("active_years");
				String issue_title = resultSet.getString("issue_title");
				String publish_date = resultSet.getString("publish_date");
				String issue_description = resultSet.getString("issue_description");
				String penciler = resultSet.getString("penciler");
				String writer = resultSet.getString("writer");
				String cover_artist = resultSet.getString("cover_artist");
				String imprint = resultSet.getString("imprint");
				String format = resultSet.getString("format");
				String rating = resultSet.getString("rating");
				String price = resultSet.getString("price");
				((DefaultTableModel) comicTable.getModel())
						.addRow(new Object[] { comic_name, active_years, issue_title, publish_date, issue_description,
								penciler, writer, cover_artist, imprint, format, rating, price });
			}
			connection.close();
			frame.repaint();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e);
		}
	}

	private void addComic() {
		String insert = "INSERT INTO comics VALUES ('" + comic.getText() + "', '" + active.getText() + "', '"
				+ issue.getText() + "', '" + publish.getText() + "', '" + description.getText() + "', '"
				+ penciler.getText() + "', '" + writer.getText() + "', '" + cover.getText() + "', '" + imprint.getText()
				+ "', '" + format.getText() + "', '" + rating.getText() + "', '" + price.getText() + "');";
		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/comicbooks", "root",
					"Luke1015!");
			Statement statement = connection.createStatement();
			statement.execute(insert);
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DefaultTableModel model = (DefaultTableModel) comicTable.getModel();
		model.setRowCount(0);
		retrieveComics();
	}

	private void deleteComic() {
		String insert = "DELETE FROM comics WHERE comic_name = '" + comic.getText() + "' AND active_years = '"
				+ active.getText() + "' AND issue_title = '" + issue.getText() + "' AND publish_date = '"
				+ publish.getText() + "' AND issue_description = '" + description.getText() + "' AND penciler = '"
				+ penciler.getText() + "' AND writer = '" + writer.getText() + "' AND cover_artist = '"
				+ cover.getText() + "' AND imprint = '" + imprint.getText() + "' AND format = '" + format.getText()
				+ "' AND rating = '" + rating.getText() + "' AND price = '" + price.getText() + "';";
		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/comicbooks", "root",
					"Luke1015!");
			Statement statement = connection.createStatement();
			statement.execute(insert);
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DefaultTableModel model = (DefaultTableModel) comicTable.getModel();
		model.setRowCount(0);
		retrieveComics();
	}

	private void addHero() {
		String insert = "INSERT INTO heroes (name, ID, ALIGN, EYE, HAIR, SEX, ALIVE, APPEARANCES, FIRST_APPEARANCE, Year) VALUES ('"
				+ name.getText() + "', '" + id.getText() + "', '" + align.getText() + "', '" + eye.getText() + "', '"
				+ hair.getText() + "', '" + sex.getText() + "', '" + alive.getText() + "', '" + appearances.getText()
				+ "', '" + fAppearance.getText() + "', '" + year.getText() + "');";
		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/comicbooks", "root",
					"Luke1015!");
			Statement statement = connection.createStatement();
			statement.execute(insert);
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DefaultTableModel model = (DefaultTableModel) heroTable.getModel();
		model.setRowCount(0);
		retrieveHeroes();
	}

	private void deleteHero() {
		String insert = "DELETE FROM heroes WHERE name = '" + name.getText() + "' AND ID = '" + id.getText()
				+ "' AND ALIGN = '" + align.getText() + "' AND EYE = '" + eye.getText() + "' AND HAIR = '"
				+ hair.getText() + "' AND SEX = '" + sex.getText() + "' AND ALIVE = '" + alive.getText()
				+ "' AND APPEARANCES = '" + appearances.getText() + "' AND FIRST_APPEARANCE = '" + fAppearance.getText()
				+ "' AND Year = '" + year.getText() + "';";
		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/comicbooks", "root",
					"Luke1015!");
			Statement statement = connection.createStatement();
			statement.execute(insert);
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DefaultTableModel model = (DefaultTableModel) heroTable.getModel();
		model.setRowCount(0);
		retrieveHeroes();
	}

	private void comicSearch() {
		String query = "SELECT * FROM comics WHERE " + dropDown.getSelectedItem() + " LIKE '%" + searchBox.getText()
				+ "%' ORDER BY comic_name ASC;";
		DefaultTableModel model = (DefaultTableModel) comicTable.getModel();
		model.setRowCount(0);
		retrieveComics(query);
	}

	private void heroSearch() {
		String query = "SELECT * FROM heroes WHERE " + dropDown.getSelectedItem() + " LIKE '%" + searchBox.getText()
				+ "%' ORDER BY name ASC;";
		DefaultTableModel model = (DefaultTableModel) heroTable.getModel();
		model.setRowCount(0);
		retrieveHeroes(query);
	}
}