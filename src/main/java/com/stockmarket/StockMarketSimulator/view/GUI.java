package com.stockmarket.StockMarketSimulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.stockmarket.StockMarketSimulator.services.AsyncService;
import com.stockmarket.StockMarketSimulator.services.CompanyService;
import com.stockmarket.StockMarketSimulator.services.InvestorService;
import com.stockmarket.StockMarketSimulator.services.SimulationService;
import com.stockmarket.StockMarketSimulator.services.TransactionService;
import com.stockmarket.StockMarketSimulator.setup.CompanyGenerator;
import com.stockmarket.StockMarketSimulator.setup.InvestorGenerator;
import com.stockmarket.StockMarketSimulator.view.report.ReportType;


@SpringBootApplication
public class GUI extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;

	private JTextArea text;
	private JButton companiesHighestCapital;
	private JButton companiesLowestCapital;
	private JButton investorsWithTheHighestNumberOfShares;
	private JButton investorsThatHaveInvestedInTheMostCompanies;
	private JButton investorsWithTheLowestNumberOfShares;
	private JButton investorsLeastNumberOfCompanies;
	private JButton totalNumberOfTransactions;
	private JButton getAllInvestors;
	private JButton fullReport;
	private JButton savePDFFile;
	private JButton saveTxtFile;
	private JButton saveDocsFile;
	private JButton transactions;
	private JButton reRun;
	private JButton disableButtons;

	private JPanel mainPanel;
	private JPanel panel2;
	private JPanel panel3;
	private JPanel panel4;
	private JPanel loadingPanel;
	private JPanel outputPanel;
	private JLabel loadingLabel;

	private List<JButton> buttonsList;

	private String reportContent; // hold the current information to be saved to file

	@Autowired 
	private SimulationService simulation;

	@Autowired
	private InvestorService investorService;

	@Autowired
	private CompanyService companyService;

	@Autowired 
	private TransactionService transactionService;

	private JTextArea consoleText;

	@Autowired
	private AsyncService asyncService;



	public GUI() {
		buttonsList = new ArrayList<>(); // create new list for buttons

		// set basic config
		this.setTitle("Stock Market Simulator");
		setSize(900,555);
		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		this.setLayout(null);

		// set main panel
		mainPanel = new JPanel();
		mainPanel.validate();
		mainPanel.setBounds(0, 2, 610, 460);

		this.add(mainPanel);

		// create save file panel
		JPanel saveFile = new JPanel();
		saveFile.validate();
		saveFile.setBounds(50, 470, 500, 50 );
		this.add(saveFile);

		// Button to save PDF file
		savePDFFile = new JButton("Save PDF File");
		buttonsList.add(savePDFFile);
		savePDFFile.addActionListener(this);
		savePDFFile.setActionCommand("savePDF");
		saveFile.add(savePDFFile);

		// Button to save TXT file
		saveTxtFile = new JButton("Save TXT File");
		buttonsList.add(saveTxtFile);
		saveTxtFile.setActionCommand("textFile");
		saveTxtFile.addActionListener(this);
		saveFile.add(saveTxtFile);

		// Button to save DOCX File
		saveDocsFile = new JButton("Save DOCX File");
		buttonsList.add(saveDocsFile);
		saveDocsFile.setActionCommand("saveDoc");
		saveDocsFile.addActionListener(this);
		saveFile.add(saveDocsFile);

		// create panel for loading message
		loadingPanel = new JPanel();
		loadingPanel.setLayout(new BorderLayout());
		loadingPanel.setBounds(3, 5, 550, 550);
		loadingLabel = new JLabel("RUNNING SIMULATION, PLEASE WAIT...");
		loadingLabel.setFont(loadingLabel.getFont().deriveFont(25.0f));
		loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
		loadingLabel.setVerticalAlignment(SwingConstants.CENTER);
		loadingPanel.add(loadingLabel, BorderLayout.NORTH);

		consoleText = new JTextArea(24,30);
		consoleText.setEditable(false);
		JScrollPane scroll = new JScrollPane(consoleText);
		consoleText.setVisible(false);
		loadingPanel.add(scroll, BorderLayout.CENTER);


		loadingPanel.setVisible(true);
		loadingPanel.validate();

		// create panel for all the outputs
		outputPanel = new JPanel();
		outputPanel.setBorder(BorderFactory.createTitledBorder("Simulation Report"));
		outputPanel.validate();
		outputPanel.setBounds(3, 5, 550, 550);


		// create text area
		text = new JTextArea(24, 30);
		text.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(text);
		outputPanel.add(scrollPane);
		mainPanel.add(this.loadingPanel);


		// create panel for companies options
		panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel2.setBorder(BorderFactory.createTitledBorder("Companies"));
		panel2.validate();
		panel2.setVisible(true);
		panel2.setBounds(630, 5, 250, 135);
		this.add(panel2);

		// create button for highest capital
		companiesHighestCapital = new JButton("Companies Highest Capital");
		buttonsList.add(companiesHighestCapital);
		companiesHighestCapital.setBounds(630, 15, 40, 5);
		companiesHighestCapital.setActionCommand("companiesHighestCapital");
		companiesHighestCapital.addActionListener(this);
		panel2.add(companiesHighestCapital);

		// create button for lowest capital
		companiesLowestCapital = new JButton("Companies Lowest Capital");
		buttonsList.add(companiesLowestCapital);
		companiesLowestCapital.setActionCommand("companiesLowestCapital");
		companiesLowestCapital.addActionListener(this);
		panel2.add(companiesLowestCapital);
		companiesLowestCapital.setBounds(630, 35, 40, 5);

		// create button for all companies
		JButton button = new JButton("Display All Companies");
		buttonsList.add(button);
		button.setActionCommand("companies");
		button.addActionListener(this);
		panel2.add(button);

		// create panel for investors
		panel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel3.setBorder(BorderFactory.createTitledBorder("Investors"));
		panel3.validate();
		panel3.setBounds(630, 150, 250, 195);
		this.add(panel3);

		// create button for highest number of shares
		investorsWithTheHighestNumberOfShares = new JButton("Highest Steakholder");
		buttonsList.add(investorsWithTheHighestNumberOfShares);
		investorsWithTheHighestNumberOfShares.setActionCommand("investorsWithTheHighestNumberOfShares");
		investorsWithTheHighestNumberOfShares.addActionListener(this);
		panel3.add(investorsWithTheHighestNumberOfShares);

		// create button for highest number of companies
		investorsThatHaveInvestedInTheMostCompanies = new JButton("Most Companies Invested In");
		buttonsList.add(investorsThatHaveInvestedInTheMostCompanies);
		investorsThatHaveInvestedInTheMostCompanies.setActionCommand("investorsThatHaveInvestedInTheMostCompanies");
		investorsThatHaveInvestedInTheMostCompanies.addActionListener(this);
		panel3.add(investorsThatHaveInvestedInTheMostCompanies);


		// create button for lowest number of shares
		investorsWithTheLowestNumberOfShares = new JButton("Lowest Stakeholder");
		buttonsList.add(investorsWithTheLowestNumberOfShares);
		investorsWithTheLowestNumberOfShares.setActionCommand("investorsWithTheLowestNumberOfShares");
		investorsWithTheLowestNumberOfShares.addActionListener(this);
		panel3.add(investorsWithTheLowestNumberOfShares);


		// create button for lowest number of companies		
		investorsLeastNumberOfCompanies = new JButton("Least Companies Invested In");
		buttonsList.add(investorsLeastNumberOfCompanies);
		investorsLeastNumberOfCompanies.setActionCommand("investorsLeastNumberOfCompanies");
		investorsLeastNumberOfCompanies.addActionListener(this);
		panel3.add(investorsLeastNumberOfCompanies);

		// create button for all investors information
		getAllInvestors = new JButton("Display All Investors");
		buttonsList.add(getAllInvestors);
		getAllInvestors.addActionListener(this);
		getAllInvestors.setActionCommand("investors");
		panel3.add(getAllInvestors);

		// create panel for simulation
		panel4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel4.setBorder(BorderFactory.createTitledBorder("Simulation"));
		panel4.validate();
		panel4.setBounds(630, 350, 250, 165);
		this.add(panel4);

		// create button for full report
		fullReport = new JButton("Full report");
		buttonsList.add(fullReport);
		fullReport.setActionCommand("fullReport");
		fullReport.addActionListener(this);
		panel4.add(fullReport);

		// create button for total number of transactions
		totalNumberOfTransactions = new JButton("Total Transactions");
		buttonsList.add(totalNumberOfTransactions);
		totalNumberOfTransactions.setActionCommand("totalNumberOfTransactions");
		totalNumberOfTransactions.addActionListener(this);
		panel4.add(totalNumberOfTransactions);

		// button to show transactions
		transactions = new JButton("Display All Transactions");
		buttonsList.add(transactions);
		transactions.addActionListener(this);
		transactions.setActionCommand("transactions");
		panel4.add(transactions);

		// button to restart simulation
		reRun = new JButton("Restart Simulation");
		buttonsList.add(reRun);
		reRun.addActionListener(this);
		reRun.setActionCommand("rerun");
		panel4.add(reRun);

		disableButtons = new JButton();

		disableButtons.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				simulationFinished(false);  
			}  
		});



		this.validate();
		this.repaint();
		this.setVisible (true);
		this.setButtonsActive(false);


	}

	public void setConsoleText(String s) {
		consoleText.setVisible(true);
		consoleText.append("\n"+s);
		consoleText.setCaretPosition(consoleText.getDocument().getLength());
	}

	public boolean simulationFinished(boolean state) {
		this.mainPanel.removeAll();
		if(state) {
			this.mainPanel.add(this.outputPanel);
			setButtonsActive(true);
			displayFullReport();
		} else {
			this.mainPanel.add(this.loadingPanel);
			setButtonsActive(false);
		}

		this.validate();
		this.repaint();
		return true;
	}

	private boolean showParametersPane() {

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2,3));
		JLabel compLabel = new JLabel("Companies");
		JSlider compSlider = new JSlider(1, 200, 100);
		JLabel compValue = new JLabel("100");
		compSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				compValue.setText(String.valueOf(((JSlider) ce.getSource()).getValue()));
			}
		});
		panel.add(compLabel);
		panel.add(compSlider);
		panel.add(compValue);



		JLabel invLabel = new JLabel("Investors");
		JSlider invSlider = new JSlider(1, 200, 100);
		JLabel invValue = new JLabel("100");
		invSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				invValue.setText(String.valueOf(((JSlider) ce.getSource()).getValue()));
			}
		});

		panel.add(invLabel);
		panel.add(invSlider);
		panel.add(invValue);

		int response = JOptionPane.showConfirmDialog(null, panel,"Choose simulation parameters",JOptionPane.OK_OPTION);

		if(response == 0) {
			try {
				CompanyGenerator.numberOfCompanies = Integer.parseInt(compValue.getText());
				InvestorGenerator.numberOfInvestors = Integer.parseInt(invValue.getText());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			return false;
		}

	}

	private void setButtonsActive(boolean state) {
		for(JButton button : buttonsList) {
			button.setEnabled(state);
		}
	}
	/**
	 * This method is responsible to return all the companies and all the details into a JTable
	 */
	private JPanel getCompanies() {

		JPanel companiesPanel =new JPanel();
		companiesPanel.setBorder(BorderFactory.createTitledBorder("Companies"));	
		companiesPanel.setBounds(5, 5, 550, 450);
		companiesPanel.validate();
		companiesPanel.repaint();
		companiesPanel.setVisible(true);

		Object[][] data = getCompaniesData();
		Object[] columns = {"ID",
				"Name",
				"Capital (€)",
				"Initial Shares",
				"Shares Sold",
				"Initial Share Price (€)",
				"Final Share Price (€)"
		};


		DefaultTableModel companiesModel = new DefaultTableModel(data, columns) {
			public boolean isCellEditable(int row, int column){
				return false;
			}
			@Override
			public Class getColumnClass(int column) {
				switch (column) {
				case 0:
					return Integer.class;
				case 1:
					return String.class;
				case 2:
					return Double.class;
				case 3:
					return Integer.class;
				case 4:
					return Integer.class;
				case 5:
					return Integer.class;
				case 6:
					return Double.class;
				case 7:
					return Double.class;
				default:
					return String.class;
				}
			}

		};



		JTable companiesTable = new JTable(companiesModel);
		companiesTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		companiesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		companiesTable.getColumnModel().getColumn(2).setPreferredWidth(95);
		companiesTable.setPreferredScrollableViewportSize(new Dimension(500, 350));
		companiesTable.setAutoCreateRowSorter(true);
		setCellsAlignment(companiesTable, SwingConstants.CENTER);


		JScrollPane scrollPane = new JScrollPane(companiesTable);
		companiesPanel.add(scrollPane);


		return companiesPanel;

	}

	private Object[][] getTransactionsData(){


		Object[][] toReturn = new Object[transactionService.getAllTransactions().size()][6];

		for(int i = 0; i < transactionService.getAllTransactions().size(); i++) {
			Object[] row = {

					new Integer(transactionService.getAllTransactions().get(i).getTransactionId()),
					new Integer(transactionService.getAllTransactions().get(i).getInvestor().getId()),
					new Integer(transactionService.getAllTransactions().get(i).getCompany().getId()),
					transactionService.getAllTransactions().get(i).getDate().toString(),
			};
			toReturn[i] = row;

		};	

		return toReturn;
	}

	private Object[][] getCompaniesData(){


		Object[][] toReturn = new Object[companyService.getAllCompanies().size()][6];

		for(int i = 0; i < companyService.getAllCompanies().size(); i++) {
			Object[] row = {
					new Integer(companyService.getAllCompanies().get(i).getId()),
					companyService.getAllCompanies().get(i).getName(),
					new Double(companyService.getAllCompanies().get(i).getCapital()),
					new Integer(companyService.getAllCompanies().get(i).getInitialShares()),
					new Integer(companyService.getAllCompanies().get(i).getSharesSold()),
					new Double(companyService.getAllCompanies().get(i).getInitialSharePrice()),
					new Double(companyService.getAllCompanies().get(i).getSharePrice())
			};
			toReturn[i] = row;

		};	

		return toReturn;
	}


	private Object[][] getInvestorsData(){

		Object[][] toReturn = new Object[investorService.getAllInvestors().size()][6];

		for(int i = 0; i < investorService.getAllInvestors().size(); i++) {
			Object[] row = {
					new Integer(investorService.getAllInvestors().get(i).getId()),
					investorService.getAllInvestors().get(i).getName(),
					new Double(investorService.getAllInvestors().get(i).getInitialBudget()),
					new Double(investorService.getAllInvestors().get(i).getBudget()),
					new Integer(investorService.getAllInvestors().get(i).getNumberOfCompaniesInvestedIn()),
					new Integer(investorService.getAllInvestors().get(i).getTotalNumberOfSharesBought())
			};
			toReturn[i] = row;

		};	

		return toReturn;
	}

	private JPanel getInvestors() {


		JPanel investorsPanel = new JPanel();
		investorsPanel.setBorder(BorderFactory.createTitledBorder("Investors"));	
		investorsPanel.setBounds(5, 5, 550, 450);
		investorsPanel.validate();
		investorsPanel.setVisible(true);
		investorsPanel.repaint();

		Object[][] data = getInvestorsData();
		Object[] columns = {"ID",
				"Name",
				"Initial Budget (€)",
				"Final Budget (€)",
				"Companies Invested In",
		"Shares Bought"};

		DefaultTableModel investorsModel = new DefaultTableModel(data, columns) {
			public boolean isCellEditable(int row, int column){
				return false;
			}
			@Override
			public Class getColumnClass(int column) {
				switch (column) {
				case 0:
					return Integer.class;
				case 1:
					return String.class;
				case 2:
					return Double.class;
				case 3:
					return Double.class;
				case 4:
					return Integer.class;
				case 5:
					return Integer.class;
				default:
					return String.class;
				}
			}

		};

		JTable investorsTable = new JTable(investorsModel);

		investorsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		investorsTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		investorsTable.getColumnModel().getColumn(2).setPreferredWidth(95);
		investorsTable.setPreferredScrollableViewportSize(new Dimension(500, 350));
		setCellsAlignment(investorsTable, SwingConstants.CENTER);

		JScrollPane scrollPane = new JScrollPane(investorsTable);
		investorsPanel.add(scrollPane);

		investorsTable.setAutoCreateRowSorter(true);


		return investorsPanel;

	}

	/**
	 * This method is responsible to create a transaction table, and returns all the transactions made by the simulation.
	 * @return
	 */
	private JPanel getAllTransactions() {

		JPanel transactionsPanel =new JPanel();
		transactionsPanel.setBorder(BorderFactory.createTitledBorder("Transactions"));	
		transactionsPanel.setBounds(5, 5, 550, 500);
		transactionsPanel.validate();
		transactionsPanel.repaint();
		transactionsPanel.setVisible(true);

		Object[][] data = getTransactionsData();
		Object[] columns = {
				"Transaction ID",
				"Investor ID",
				"Company ID",
				"Date"
		};

		DefaultTableModel transactionsModel = new DefaultTableModel(data, columns) {
			public boolean isCellEditable(int row, int column){
				return false;
			}
			@Override
			public Class getColumnClass(int column) {
				switch (column) {
				case 0:
					return Integer.class;
				case 1:
					return Integer.class;
				case 2:
					return Integer.class;
				default:
					return String.class;
				}
			}

		};

		JTable transactionsTable = new JTable(transactionsModel);
		transactionsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		transactionsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		//		transactionsTable.getColumnModel().getColumn(2).setPreferredWidth(95);
		//		transactionsTable.getColumnModel().getColumn(1).
		transactionsTable.setPreferredScrollableViewportSize(new Dimension(400, 350));
		transactionsTable.setAutoCreateRowSorter(true);

		setCellsAlignment(transactionsTable, SwingConstants.CENTER);

		JScrollPane scrollPane = new JScrollPane(transactionsTable);
		transactionsPanel.add(scrollPane);


		return transactionsPanel;

	}

	/**
	 * Sets the alignment of all cells of a JTable to the desired SwingConstants option.
	 * @param table JTable
	 * @param alignment SwingConstant enum.
	 */
	private static void setCellsAlignment(JTable table, int alignment){
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer(); // create renderer
		renderer.setHorizontalAlignment(alignment); // set alignment

		TableModel tableModel = table.getModel(); // get JTable's model

		for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++){ // for each column
			table.getColumnModel().getColumn(columnIndex).setCellRenderer(renderer); // set the cell renderer
		}
	}


	//	public void start() {
	//		new GUI();
	//	}


	@Override
	public void actionPerformed(ActionEvent e) {

		this.saveDocsFile.setEnabled(true);
		this.savePDFFile.setEnabled(true);

		if(e.getActionCommand().equals("companiesHighestCapital")){

			setContentAndText(simulation.highestCapital());


		}else if(e.getActionCommand().equals("companiesLowestCapital")) {

			setContentAndText(simulation.lowestCapital());


		}else if(e.getActionCommand().equals("investorsWithTheHighestNumberOfShares")) {

			setContentAndText(simulation.highestNumberOfShares());


		}else if(e.getActionCommand().equals("investorsThatHaveInvestedInTheMostCompanies")) {

			setContentAndText(simulation.highestNumberOfCompanies());


		}else if(e.getActionCommand().equals("investorsWithTheLowestNumberOfShares")) {

			setContentAndText(simulation.lowestNumberOfShares());


		}else if(e.getActionCommand().equals("investorsLeastNumberOfCompanies")) {

			setContentAndText(simulation.lowestNumberOfCompanies());


		}else if(e.getActionCommand().equals("totalNumberOfTransactions")) {

			setContentAndText(simulation.totalTransactions());


		}else if(e.getActionCommand().equals("fullReport")) {

			displayFullReport();


		}else if(e.getActionCommand().equals("companies")) {
			setContentAndPanel(getCompanies(), simulation.allCompanies());


		}else if(e.getActionCommand().equals("investors")){
			setContentAndPanel(getInvestors(), simulation.allInvestors());


		}else if(e.getActionCommand().equals("transactions")) {

			this.saveDocsFile.setEnabled(false);
			this.savePDFFile.setEnabled(false);

			setContentAndPanel(getAllTransactions(), simulation.allTransactions());


		}else if(e.getActionCommand().equals("savePDF")) {
			saveFile(ReportType.PDF);

		}else if(e.getActionCommand().equals("textFile")) {
			saveFile(ReportType.TXT);

		}else if(e.getActionCommand().equals("saveDoc")) {
			saveFile(ReportType.DOCX);
		}
		else if(e.getActionCommand().equals("rerun")) {
			boolean shouldReRun = showParametersPane();

			if (shouldReRun) {
				
				//simulationFinished(false);

				this.consoleText.setText("Running simulation on the background...");
				this.consoleText.append("\nPlease wait...");

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						simulation.restart();
					}
				});

			}
		}

		this.revalidate();
		this.repaint();

	}

	private void setContentAndPanel(JPanel panel, String content) {
		mainPanel.removeAll();
		JPanel companies = panel;
		companies.validate();
		companies.setVisible(true);
		companies.repaint();
		mainPanel.add(companies);
		reportContent = content;
	}

	private void setContentAndText(String content) {
		showOutputPanel();
		reportContent = content;
		text.setText(content);
		text.setCaretPosition(0);
	}

	private void showOutputPanel() {
		mainPanel.removeAll();
		mainPanel.add(outputPanel);
		outputPanel.setVisible(true);
	}

	private void saveFile(ReportType type) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Choose filename and folder");   

		int userSelection = fileChooser.showSaveDialog(this);

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			System.out.println("Save as file: " + fileToSave.getAbsolutePath());

			if(type == ReportType.PDF) {
				simulation.generatePdfReport(reportContent, fileToSave.getAbsolutePath());
			} else if(type == ReportType.DOCX) {
				simulation.generateDocxReport(reportContent, fileToSave.getAbsolutePath());
			} else if(type == ReportType.TXT) {
				simulation.generateTxtReport(reportContent, fileToSave.getAbsolutePath());
			}
		}
	}

	private void displayFullReport() {
		showOutputPanel();
		reportContent = simulation.fullReport();
		text.setText(simulation.fullReport());
		text.setCaretPosition(0);
	}

}
