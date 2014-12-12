package john.pavlik;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Compliance {

	private JFrame frame;
	private final Action aboutAction = new AboutAction();
	private final Action adAction = new ImportADAction();
	private JTable table;
	private UserTableModel tableModel = new UserTableModel();
	private final Action atcAction = new ATCTS_Action();
	private final Action exportAction = new ExportAction();
	private final Action newListAction = new NewListAction();
	private final Action createEmailListAction = new EmailListAction();
	private final Action datastoreAction = new DataStoreAction();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Compliance window = new Compliance();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Compliance() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		mntmAbout.setAction(aboutAction);
		mnHelp.add(mntmAbout);

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);

		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(panel, popupMenu);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton btnImportAd = new JButton();
		btnImportAd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});

		JButton btnNewList = new JButton("");
		btnNewList.setAction(newListAction);
		panel.add(btnNewList);

		JButton btnImportDataStore = new JButton();
		btnImportDataStore.setAction(datastoreAction);
		panel.add(btnImportDataStore);
		btnImportAd.setAction(adAction);
		panel.add(btnImportAd);

		JButton btnImportAtcts = new JButton();
		btnImportAtcts.setAction(atcAction);
		panel.add(btnImportAtcts);

		JButton btnShowEmails = new JButton();
		btnShowEmails.setAction(createEmailListAction);
		panel.add(btnShowEmails);

		JButton btnExport = new JButton();
		btnExport.setAction(exportAction);
		panel.add(btnExport);

		table = new JTable(tableModel);
		table.setAutoCreateRowSorter(true);
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);

		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
	}

	private class AboutAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public AboutAction() {
			putValue(NAME, "About");
			putValue(SHORT_DESCRIPTION, "Displays about dialog");
		}

		public void actionPerformed(ActionEvent e) {
			JOptionPane
					.showMessageDialog(
							frame,
							"Created by CPT John Pavlik of the 4th Special Troops Battalion S6 for the 4th Sustainment Brigade");
		}
	}

	private class ImportADAction extends FileChooseAction {
		private static final long serialVersionUID = 1L;

		public ImportADAction() {
			putValue(NAME, "Import AD File");
			putValue(
					SHORT_DESCRIPTION,
					"AD file must be tab delimited, the first column must be the username, and it must contain the EDIPI somewhere.");
		}

		private User[] readUsers(File file) {
			ArrayList<User> list = new ArrayList<>();
			try (BufferedReader fr = new BufferedReader(new FileReader(file))) {
				String currentLine;
				while ((currentLine = fr.readLine()) != null) {
					try {
						list.add(parseUser(currentLine));
					} catch (UnableToParseException e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return list.toArray(new User[0]);
		}

		private User parseUser(String line) throws UnableToParseException {
			String[] currentUserStrings;
			currentUserStrings = line.split("\\t");
			String username = "", displayName = "", akoEmail = "", enterpriseEmail = "", edipi = "";
			for (String s : currentUserStrings) {
				if (s.contains("@mil"))
					edipi = s.replace("@mil", "");
				else if (s.contains("@us.army.mil"))
					akoEmail = s;
				else if (s.contains("@mail.mil"))
					enterpriseEmail = s;
				else if (s.contains("MIL USA"))
					displayName = s;
				else if (username.isEmpty())
					username = s;
			}
			if (edipi.isEmpty())
				throw new UnableToParseException(
						"Failed to parse line, EDIPI is empty: " + line);
			return new User(username, displayName, akoEmail, enterpriseEmail,
					edipi, false, false, false, null, null, "");
		}

		private class UnableToParseException extends Exception {
			private static final long serialVersionUID = -1L;

			public UnableToParseException(String value) {
				super(value);
			}
		}

		public void actionPerformed(ActionEvent event) {
			File file = chooseOpenFile(new FileNameExtensionFilter("Text File",
					"txt"));
			if (file != null) {
				User[] users = readUsers(file);
				tableModel.mergeUsers(users);
			}
		}
	}

	private class UserTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		final int userCol = 0;
		final int displayCol = 1;
		final int akoEmailCol = 2;
		final int enterpriseEmailCol = 3;
		final int edipiCol = 4;
		final int atcCol = 5;
		final int aupCol = 6;
		final int awareCol = 7;
		final int lastAwareCol = 8;
		final int dateAUPCol = 9;
		final int expiresCol = 10;
		final int unitCol = 11;
		private String[] columnNames = { "AD Username", "Display Name",
				"AKO Email", "Enterprise Email", "EDIPI", "Exists in ATC",
				"AUP", "Awareness", "Last Cyber Awareness Training",
				"Date AUP Signed", "Expires 30 Days", "Unit" };

		private ArrayList<User> data = new ArrayList<>();

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public String getValueAt(int row, int col) {
			switch (col) {
			case userCol:
				return data.get(row).getUsername();
			case displayCol:
				return data.get(row).getDisplayName();
			case akoEmailCol:
				return data.get(row).getAkoEmail();
			case enterpriseEmailCol:
				return data.get(row).getEnterpriseEmail();
			case edipiCol:
				return data.get(row).getEdipi();
			case atcCol:
				return data.get(row).getAtc() ? "Yes" : "No";
			case aupCol:
				return data.get(row).getAup() ? "Yes" : "No";
			case awareCol:
				return data.get(row).getAwareness() ? "Yes" : "No";
			case lastAwareCol:
				Date d = data.get(row).getLastCyber();
				if (d == null)
					return "";
				return DateFormat.getDateInstance(DateFormat.SHORT).format(d);
			case dateAUPCol:
				d = data.get(row).getAupSigned();
				if (d == null)
					return "";
				return DateFormat.getDateInstance(DateFormat.SHORT).format(d);
			case expiresCol:
				Date lastCyberDate = data.get(row).getLastCyber();
				if (lastCyberDate == null)
					return "No";
				Calendar lastCyberCal = new GregorianCalendar();
				lastCyberCal.setTime(lastCyberDate);
				lastCyberCal.add(Calendar.YEAR, 1);
				Calendar cal = new GregorianCalendar();
				cal.add(Calendar.DATE, 30);
				if (lastCyberCal.getTime().after(new Date())
						&& lastCyberCal.before(cal))
					return "Yes";
				else
					return "No";
			case unitCol:
				return data.get(row).getUnit();
			default:
				return "EXCEPTION, OUT OF RANGE";
			}
		}

		public Class<?> getColumnClass(int c) {
			return "".getClass();
		}

		/*
		 * Don't need to implement this method unless your table's editable.
		 */
		public boolean isCellEditable(int row, int col) {
			return false;
		}

		// public void addUser(User user) {
		// data.add(user);
		// fireTableRowsInserted(data.size() - 1, data.size() - 1);
		// }

		public void mergeUsers(User[] users) {
			int totalAddedCount = 0;
			ArrayList<Integer> list = new ArrayList<>();
			for (User mergeUser : users) {
				int index = data.indexOf(mergeUser);
				if (index == -1) {
					data.add(mergeUser);
					totalAddedCount += 1;
				} else {
					User existingUser = data.get(index);
					if ("".equals(existingUser.getUsername())
							&& !"".equals(mergeUser.getUsername()))
						existingUser.setUsername(mergeUser.getUsername());
					if ("".equals(existingUser.getDisplayName())
							&& !"".equals(mergeUser.getDisplayName()))
						existingUser.setDisplayName(mergeUser.getDisplayName());
					if ("".equals(existingUser.getAkoEmail())
							&& !"".equals(mergeUser.getAkoEmail()))
						existingUser.setAkoEmail(mergeUser.getAkoEmail());
					if ("".equals(existingUser.getEnterpriseEmail())
							&& !"".equals(mergeUser.getEnterpriseEmail()))
						existingUser.setEnterpriseEmail(mergeUser
								.getEnterpriseEmail());
					if ("".equals(existingUser.getEdipi())
							&& !"".equals(mergeUser.getEdipi()))
						existingUser.setEdipi(mergeUser.getEdipi());
					existingUser.setAtc(mergeUser.getAtc()
							|| existingUser.getAtc());
					existingUser.setAUP(mergeUser.getAup()
							|| existingUser.getAup());
					existingUser.setAwareness(mergeUser.getAwareness()
							|| existingUser.getAwareness());
					if (mergeUser.getLastCyber() != null
							&& existingUser.getLastCyber() == null)
						existingUser.setLastCyber(mergeUser.getLastCyber());
					if (!"".equals(mergeUser.getUnit())
							&& "".equals(existingUser.getUnit()))
						existingUser.setUnit(mergeUser.getUnit());
					list.add(index);
				}
			}
			if (totalAddedCount > 0)
				tableModel.fireTableRowsInserted(data.size() - 1
						- totalAddedCount, data.size() - 1);
			for (Integer i : list) {
				fireTableRowsUpdated(i, i);
			}
		}

		public void wipe() {
			data.clear();
			fireTableDataChanged();
		}
	}

	private abstract class FileChooseAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		protected File chooseOpenFile(FileNameExtensionFilter filter) {
			return chooseFile(true, filter);
		}

		protected File chooseSaveFile(FileNameExtensionFilter filter) {
			return chooseFile(false, filter);
		}

		private File chooseFile(boolean open, FileNameExtensionFilter filter) {
			final JFileChooser fc = new JFileChooser();
			if (filter != null)
				fc.setFileFilter(filter);
			int returnVal = -1;
			if (open) {
				returnVal = fc.showOpenDialog(frame);
			} else {
				returnVal = fc.showSaveDialog(frame);
			}
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				return file;
			} else {
				return null;
			}
		}

	}

	private class ATCTS_Action extends FileChooseAction {
		private static final long serialVersionUID = 1L;

		public ATCTS_Action() {
			putValue(NAME, "Import ATCTS Report File");
			putValue(
					SHORT_DESCRIPTION,
					"Columns in the report should include Name, AUP Uploaded, Awareness Trained, "
							+ "E-mail, Enterprise Email, EDIPI, Last DoD Cyber Awareness Challenge Training, and HQ Alignment Subunit in any order.  "
							+ "Any other columns will be ignored.  Use Export to spreadsheet option in ATCTS.");
		}

		public void actionPerformed(ActionEvent e) {
			File file = chooseOpenFile(new FileNameExtensionFilter(
					"Excel Spreadsheet", "xls"));
			if (file == null)
				return;
			User[] users = ATC_XML_Parser.Parse(file);
			tableModel.mergeUsers(users);
		}
	}

	private class ExportAction extends FileChooseAction {
		private static final long serialVersionUID = 1L;

		public ExportAction() {
			putValue(NAME, "Export to Excel");
			putValue(SHORT_DESCRIPTION, "Exports table below to excel file");
		}

		public void actionPerformed(ActionEvent event) {
			File file = chooseSaveFile(new FileNameExtensionFilter(
					"Excel XML Spreadsheet", "xml"));
			if (file == null)
				return;
			if (!file.getPath().toLowerCase().endsWith(".xml")) {
				file = new File(file.getPath() + ".xml");
			}
			if (file.exists()) {
				if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(
						frame,
						"File already exists.  Do you want to overwrite?",
						"File already exists.", JOptionPane.YES_NO_OPTION))
					return;
			}
			String nl = System.getProperty("line.separator");
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
				bw.write("<?xml version='1.0'?>" + nl);
				bw.write("<?mso-application progid='Excel.Sheet'?>" + nl);
				bw.write("<Workbook xmlns='urn:schemas-microsoft-com:office:spreadsheet' xmlns:o='urn:schemas-microsoft-com:office:office' "
						+ "xmlns:x='urn:schemas-microsoft-com:office:excel' xmlns:ss='urn:schemas-microsoft-com:office:spreadsheet' "
						+ "xmlns:html='http://www.w3.org/TR/REC-html40'>" + nl);
				bw.write("<DocumentProperties xmlns='urn:schemas-microsoft-com:office:office'><Version>12.00</Version></DocumentProperties>");
				bw.write("<Worksheet ss:Name='HR Report'>" + nl);
				bw.write("<Names><NamedRange ss:Name=\"_FilterDatabase\" ss:RefersTo=\"='HR Report'!R1C1:R1C12\" ss:Hidden=\"1\"/></Names>");
				bw.write("<Table>");
				bw.write("<Column ss:Width='168'/>");
				bw.write("<Column ss:Width='208.5'/>");
				bw.write("<Column ss:Width='231'/>");
				bw.write("<Column ss:Width='211.5'/>");
				bw.write("<Column ss:Width='57.75'/>");
				bw.write("<Column ss:Width='36.75'/>");
				bw.write("<Column ss:Width='35.25'/>");
				bw.write("<Column ss:Width='35.25'/>");
				bw.write("<Column ss:Width='52.5'/>");
				bw.write("<Column ss:Width='52.5'/>");
				bw.write("<Column ss:Width='101.25'/>");
				bw.write("<Column ss:Width='116.25'/>");
				bw.write("<Row>");
				bw.write("<Cell><Data ss:Type=\"String\">AD Username</Data><NamedCell");
				bw.write("  ss:Name=\"_FilterDatabase\"/></Cell>");
				bw.write("<Cell><Data ss:Type=\"String\">Display Name</Data><NamedCell");
				bw.write("  ss:Name=\"_FilterDatabase\"/></Cell>");
				bw.write("<Cell><Data ss:Type=\"String\">AKO</Data><NamedCell ss:Name=\"_FilterDatabase\"/></Cell>");
				bw.write("<Cell><Data ss:Type=\"String\">Enterprise Email</Data><NamedCell ss:Name=\"_FilterDatabase\"/></Cell>");
				bw.write("<Cell><Data ss:Type=\"String\">EDIPI</Data><NamedCell");
				bw.write("   ss:Name=\"_FilterDatabase\"/></Cell>");
				bw.write("<Cell><Data ss:Type=\"String\">ATC</Data><NamedCell");
				bw.write("  ss:Name=\"_FilterDatabase\"/></Cell>");
				bw.write("<Cell><Data ss:Type=\"String\">AUP</Data><NamedCell");
				bw.write("  ss:Name=\"_FilterDatabase\"/></Cell>");
				bw.write("<Cell><Data ss:Type=\"String\">Cyber</Data><NamedCell");
				bw.write("  ss:Name=\"_FilterDatabase\"/></Cell>");
				bw.write("<Cell><Data ss:Type=\"String\">Last Cyber</Data><NamedCell");
				bw.write("  ss:Name=\"_FilterDatabase\"/></Cell>");
				bw.write("<Cell><Data ss:Type=\"String\">AUP Signed</Data><NamedCell");
				bw.write("  ss:Name=\"_FilterDatabase\"/></Cell>");
				bw.write("<Cell><Data ss:Type=\"String\">30 days expiration</Data><NamedCell");
				bw.write("  ss:Name=\"_FilterDatabase\"/></Cell>");
				bw.write("<Cell><Data ss:Type=\"String\">Unit</Data><NamedCell");
				bw.write("  ss:Name=\"_FilterDatabase\"/></Cell>");
				bw.write("</Row>");
				for (int row = 0; row < tableModel.getRowCount(); ++row) {
					bw.write("<Row>");
					for (int col = 0; col < tableModel.getColumnCount(); ++col) {
						bw.write("<Cell><Data ss:Type='String'>");
						bw.write(tableModel.getValueAt(row, col));
						bw.write("</Data></Cell>");
					}
					bw.write("</Row>");
				}
				bw.write("</Table><AutoFilter x:Range='R1C1:R1C12' xmlns='urn:schemas-microsoft-com:office:excel'></AutoFilter></Worksheet></Workbook>"
						+ nl);
				bw.flush();
				bw.close();
				JOptionPane
						.showMessageDialog(frame, "File successfully saved.");
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, e.getMessage());
			}
		}
	}

	private class NewListAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public NewListAction() {
			putValue(NAME, "New List");
			putValue(SHORT_DESCRIPTION, "Wipes the table below clean");
		}

		public void actionPerformed(ActionEvent e) {
			tableModel.wipe();
		}
	}

	private class EmailListAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public EmailListAction() {
			putValue(NAME, "Display Selected Emails");
			putValue(SHORT_DESCRIPTION,
					"Generates semi-colon separated list of emails for every selected user.");
		}

		public void actionPerformed(ActionEvent e) {
			int[] selectedRows = table.getSelectedRows();

			StringBuilder emailList = new StringBuilder();
			for (Integer viewRowIndex : selectedRows) {
				int modelRowIndex = table.convertRowIndexToModel(viewRowIndex);
				String enterprise = tableModel.getValueAt(modelRowIndex,
						tableModel.enterpriseEmailCol);
				String ako = tableModel.getValueAt(modelRowIndex,
						tableModel.akoEmailCol);
				if ("".equals(enterprise)) {
					if ("".equals(ako)) {
						JOptionPane.showMessageDialog(frame, "User at row "
								+ modelRowIndex + " has no email address.");
					} else {
						emailList.append(ako);
					}
				} else {
					emailList.append(enterprise);
				}
				emailList.append(";");
			}
			if (emailList.length() > 0) {
				emailList.deleteCharAt(emailList.length() - 1);
				JOptionPane.showInputDialog(frame, "", "Selected Emails",
						JOptionPane.INFORMATION_MESSAGE, null, null,
						emailList.toString());
			}
		}
	}

	private class DataStoreAction extends FileChooseAction {
		private static final long serialVersionUID = 1L;

		public DataStoreAction() {
			putValue(NAME, "Import Units");
			putValue(
					SHORT_DESCRIPTION,
					"Imports a comma separated value file (use Excel Save As feature) that includes "
							+ "DOD_EDI_PN_ID and HOME_UIC_CD from EMILPO Datastore.  Also can optionally include NAME_REPORTING_FORMAT.");
		}

		private User[] readDatastore(File file) {
			ArrayList<User> list = new ArrayList<>();
			try (BufferedReader fr = new BufferedReader(new FileReader(file))) {
				String currentLine = fr.readLine();
				if (currentLine == null)
					return new User[0];
				else if (currentLine.contains("DOD_EDI_PN_ID")
						&& currentLine.contains("HOME_UIC_CD")) {
					String[] headers = currentLine.split(",");
					int edipiColumn = -1;
					int uicColumn = -1;
					int displayColumn = -1;
					for (int i = 0; i < headers.length; ++i) {
						String header = headers[i];
						if ("DOD_EDI_PN_ID".equalsIgnoreCase(header)) {
							edipiColumn = i;
						} else if ("HOME_UIC_CD".equalsIgnoreCase(header)) {
							uicColumn = i;
						} else if ("NAME_REPORTING_FORMAT"
								.equalsIgnoreCase(header)) {
							displayColumn = i;
						}
					}
					while ((currentLine = fr.readLine()) != null) {
						try {
							list.add(parseDatastoreUser(currentLine,
									edipiColumn, uicColumn, displayColumn));
						} catch (UnableToParseException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return list.toArray(new User[0]);
		}

		private User parseDatastoreUser(String line, int edipiColumn,
				int uicColumn, int displayColumn) throws UnableToParseException {
			if (edipiColumn < 0 || uicColumn < 0)
				throw new UnableToParseException(
						"No EDIPI and/or UIC columns defined. " + edipiColumn
								+ " " + uicColumn);

			String displayName = "", edipi = "", uic = "";

			Pattern pattern = Pattern.compile(",?\\s*(\"[^\"]*\"|[^,]*)\\s*");
			Matcher matcher = pattern.matcher(line);
			int index = 0;

			while (matcher.find()) {
				String string = matcher.group(1).replaceAll("\"", "");
				if (index == displayColumn)
					displayName = string;
				if (index == edipiColumn)
					edipi = string;
				if (index == uicColumn)
					uic = string;
				index += 1;
			}

			if (edipi.isEmpty())
				throw new UnableToParseException(
						"Failed to parse line, EDIPI is empty: " + line);
			return new User("", displayName, "", "", edipi, false, false,
					false, null, null, uic);
		}

		private class UnableToParseException extends Exception {
			private static final long serialVersionUID = -1L;

			public UnableToParseException(String value) {
				super(value);
			}
		}

		public void actionPerformed(ActionEvent event) {
			File file = chooseOpenFile(new FileNameExtensionFilter(
					"Comma Separated Values", "csv"));
			if (file != null) {
				User[] users = readDatastore(file);
				tableModel.mergeUsers(users);
			}
		}
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
