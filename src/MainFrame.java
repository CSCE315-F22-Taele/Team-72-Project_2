import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.List;

import javax.imageio.ImageReader;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

/**
 * Class for the GUI of the Point-of-Scale system.
 * <p>
 * This class extends JFrame and incorporates new methods for the purpose of the POS system.
 * 
 * @author Carson Duffy
 */
public class MainFrame extends JFrame {
    final private Font titleFont = new Font("Helvetica", Font.BOLD, 18);
    final private Font subtitleFont = new Font("Helvetica", Font.BOLD, 16);
    final private Font paragraphFont = new Font("Helvetica", Font.PLAIN, 14);

    private double customerTotal = 0;
    private double restockTotal = 0;
    private int numButtonCols = 6;
    private boolean serverPage;
    private static ArrayList<Item> customerOrderItems = new ArrayList<>();
    private static LinkedHashMap<Item, Integer> restockOrderItems = new LinkedHashMap<>();
    private static Employee currentEmployee;
    private static ExecQuery eq;
    private String restockCategoryName;
    private static List<Item> itemsList;

    private static Map<String, List<Item>> itemTypeMap;
    private static LinkedHashMap<String, List<String>> typeGroupMap = new LinkedHashMap<>();
    
    // elements that are modified outside of initialize()
    private JPanel orderPanel;
    private JPanel orderSummary;
    private JPanel spreadsheetCells;
    private static JLabel orderTotalLabel;


    /**
     * Configures and displays the main layout for the Point-of-Sale GUI interface.
     * <p>
     * This method runs at the start of the main method for the MainFrame class.
     * It adds all of the panels, text, and buttons necessary for the GUI's operation, 
     * and it also configures the main button behavior for the buttons that are included
     * in both the server and the manager pages of the GUI.
     */
    public void initialize() {
        JPanel mainWindow = new JPanel();
        
        orderPanel = new JPanel();
        orderPanel.setBackground(new Color(87, 87, 87));


        /**************************** Navigation Panel ****************************/

        // styles
        JPanel navPanel = new JPanel();
        navPanel.setBackground(new Color(122, 122, 122));
        navPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.black));

        // configure button to switch to server page
        JButton serverButton = new JButton("Server");
        serverButton.setFont(titleFont);
        serverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                orderPanel.removeAll();
                orderPanel.revalidate();
                orderPanel.repaint();
                loginLayout(false);
                //serverLayout();
                setVisible(true);
            }
        });

        // configure button to switch to manager page
        JButton managerButton = new JButton("Manager");
        managerButton.setFont(titleFont);
        managerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                orderPanel.removeAll();
                orderPanel.revalidate();
                orderPanel.repaint();
                loginLayout(true);
                //managerLayout();
                setVisible(true);
            }
        });

        // buttons layout
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 4, 0, 0));
        buttonsPanel.add(serverButton);
        buttonsPanel.add(managerButton);

        // add buttons to naviagtion
        navPanel.setLayout(new BorderLayout());
        navPanel.add(buttonsPanel, BorderLayout.WEST);


        /**************************** Summary Panel ****************************/

        // styles
        JPanel summaryPanel = new JPanel();
        summaryPanel.setBackground(new Color(122, 122, 122));
        summaryPanel.setLayout(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        // configure header pane and add styles
        JLabel summaryTitle = new JLabel("Order Summary:", SwingConstants.CENTER);
        summaryTitle.setFont(titleFont);
        summaryTitle.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));

        // configure container for order summary panel
        JPanel orderSummaryContainer = new JPanel();
        orderSummaryContainer.setOpaque(false);
        orderSummaryContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // configure summary pane and add styles
        orderSummary = new JPanel();
        orderSummary.setOpaque(false);
        orderSummaryContainer.add(orderSummary, BorderLayout.NORTH);

        // configure order confirm pane and add styles
        JPanel orderConfirm = new JPanel();
        orderConfirm.setOpaque(false);
        orderConfirm.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.black));

        // initialize order confirm pane elements
        orderTotalLabel = new JLabel();
        orderTotalLabel.setFont(titleFont);
        JButton confirmButton = new JButton("Confirm");
        confirmButton.setFont(titleFont);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(titleFont);

        // add functionality to confirm orders
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (serverPage) {
                    eq.confirmCustomerOrder(customerOrderItems, currentEmployee);
                    customerTotal = 0;
                    customerOrderItems.clear();
                    listCustomerOrderItems();
                }
                else {
                    eq.confirmRestockOrder(restockOrderItems, currentEmployee);
                    restockTotal = 0;
                    restockOrderItems.clear();
                    listRestockOrderItems();
                    displayRestockTable(restockCategoryName);
                }
            }
        });

        // add functionality to cancel orders
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (serverPage) {
                    customerTotal = 0;
                    customerOrderItems.clear();
                    listCustomerOrderItems();
                }
                else {
                    restockTotal = 0;
                    restockOrderItems.clear();
                    listRestockOrderItems();
                }
            }
            
        });

        // add confirm pane elements
        orderConfirm.setLayout(new GridLayout(1, 3, 2, 2));
        orderConfirm.add(orderTotalLabel);
        orderConfirm.add(confirmButton);
        orderConfirm.add(cancelButton);
        
        // add all panes to summary pane
        summaryPanel.add(summaryTitle, BorderLayout.NORTH);
        summaryPanel.add(orderSummaryContainer, BorderLayout.CENTER);
        summaryPanel.add(orderConfirm, BorderLayout.SOUTH);


        /**************************** Main Window ****************************/

        // collect navigation and order panels in group to display together
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(navPanel, BorderLayout.NORTH);
        leftPanel.add(orderPanel, BorderLayout.CENTER);

        // arrange group of panels and summary panel
        mainWindow.setLayout(new BorderLayout());
        mainWindow.add(leftPanel, BorderLayout.CENTER);
        mainWindow.add(summaryPanel, BorderLayout.EAST);

        add(mainWindow);

        // set remaining styles
        setTitle("Cabo Point-of-Sale System");
        setSize(1280, 720);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        // close database on window exit
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                eq.close();
            }
        });
    }
    

    /**
     * Configures the functionality and layout for the main panel for the server view of the GUI.
     * <p>
     * This method is triggered by the "server" button included in the navigation panel at the 
     * top of the GUI. The panel is automatically cleared and re-displayed outside of this method.
     */
    void serverLayout() {
        serverPage = true;

        // set grid layout for number of item categories
        orderPanel.setLayout(new GridLayout(typeGroupMap.size(), 1, 0, 5));

        // iterate through each item category and add to GUI
        for (Map.Entry<String, List<String>> entry : typeGroupMap.entrySet()) {
            JPanel selectionPanel = new JPanel();
            JPanel selectionButtons = new JPanel();

            // basic styles
            selectionButtons.setOpaque(false);
            selectionPanel.setOpaque(false);

            // title for category
            JLabel categoryNameLabel = new JLabel(entry.getKey());
            categoryNameLabel.setForeground(Color.white);
            categoryNameLabel.setFont(subtitleFont);

            // get list of all items in the current item category
            ArrayList<Item> sectionItems = new ArrayList<Item>();
            for (String type: entry.getValue()) {
                List<Item> items = itemTypeMap.get(type);
                sectionItems.addAll(items);
            }
            
            // determine grid layout parameters and create layout
            int numButtons = sectionItems.size();
            int numRows = numButtons / numButtonCols + 1;
            selectionButtons.setLayout(new GridLayout(numRows, numButtonCols, 4, 4));

            // add elements to grid layout
            for (int j = 0; j < numRows * numButtonCols; j++) {
                if (j < numButtons) {   // add button
                    Item currentItem = sectionItems.get(j);

                    JButton btn = new JButton();
                    btn.setText(currentItem.getName());

                    // when button is clicked, add it to order and order summary panel
                    btn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            customerOrderItems.add(currentItem);
                            customerTotal += currentItem.getCustomerPrice();
                            listCustomerOrderItems();   // re-list order summary items
                        }
                    });

                    selectionButtons.add(btn);
                }
                else {      // all buttons have been placed ~ add blank panel
                    JPanel empty = new JPanel();
                    empty.setOpaque(false);
                    selectionButtons.add(empty);
                }
            }

            // configure layout for item category panel
            selectionPanel.setLayout(new BorderLayout());
            selectionPanel.add(categoryNameLabel, BorderLayout.NORTH);
            selectionPanel.add(selectionButtons, BorderLayout.CENTER);

            // add to main panel grid
            orderPanel.add(selectionPanel, BorderLayout.CENTER);
        }

        listCustomerOrderItems();   // redisplay summary
    }



    void loginLayout(boolean isManagerPanel){
        String ttl = "";
        if (isManagerPanel){
            ttl = "Manager Page Login";
        }
        else{
            ttl = "Server Page Login";
        }
        JLabel title = new JLabel(ttl);
        title.setFont(new Font("Helvetica", Font.BOLD, 22));
        title.setForeground(Color.black);
        title.setHorizontalAlignment(JLabel.CENTER);


        JLabel lbUsername = new JLabel("Username:");
        lbUsername.setFont(titleFont);
        lbUsername.setForeground(Color.black);
        JTextField username = new JTextField();

        JLabel lbPassword = new JLabel("Password:");
        lbPassword.setFont(titleFont);
        lbPassword.setForeground(Color.black);
        JTextField password = new JTextField();
        
        JPanel titlePanel = new JPanel();
        titlePanel.add(title);
        titlePanel.setBackground(new Color(87,87,87));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(2,2, 1, 5));
        formPanel.setBorder(new EmptyBorder(20,20, 20, 20));
        formPanel.add(lbUsername);
        formPanel.add(username);
        formPanel.add(lbPassword);
        formPanel.add(password);
        formPanel.setBackground(new Color(87,87,87));


        //Confirm
        JLabel lbgrant = new JLabel();
        lbgrant.setFont(subtitleFont);
        lbgrant.setForeground(new Color(200,0,0));
        lbgrant.setHorizontalAlignment(JLabel.CENTER);

        JButton btnEnter = new JButton("Enter");
        btnEnter.setFont(subtitleFont);
        btnEnter.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e){
                if (isManagerPanel){
                    if (eq.verifyManager(username.getText(), password.getText())){
                        orderPanel.removeAll();
                        orderPanel.revalidate();
                        orderPanel.repaint();
                        managerLayout();
                    }else{
                        lbgrant.setText("ERROR: Invalid Username or Password");  
                    }
                }else{
                    if (eq.verifyServer(username.getText(), password.getText())){
                        orderPanel.removeAll();
                        orderPanel.revalidate();
                        orderPanel.repaint();
                        serverLayout();
                    }else{
                        lbgrant.setText("ERROR: Invalid Username or Password");  
                    }
                }
                
            }

        
        });

        
        JButton btnClear = new JButton("Clear");
        btnClear.setFont(subtitleFont);
        btnClear.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e){
                username.setText("");
                password.setText("");
            }

        });

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1,2,2,2));
        buttonsPanel.setBorder(new EmptyBorder(25,25, 25, 25));
        buttonsPanel.setBackground(new Color(87,87,87));
        buttonsPanel.add(btnEnter);
        buttonsPanel.add(btnClear);

        //login.setBackground(new Color(128,128,128));
        orderPanel.setLayout(new GridLayout(typeGroupMap.size(), 1, 0, 5));

        orderPanel.add(titlePanel, BorderLayout.NORTH);
        orderPanel.add(formPanel, BorderLayout.CENTER);
        orderPanel.add(lbgrant, BorderLayout.CENTER);
        orderPanel.add(buttonsPanel, BorderLayout.SOUTH);

    }


    /**
     * Configures the functionality and layout for the main panel for the manager view of the GUI.
     * <p>
     * This method is triggered by the "manager" button included in the navigation panel at the 
     * top of the GUI. The panel is automatically cleared and re-displayed outside of this method.
     */
    void managerLayout() {
        serverPage = false;

        orderPanel.setLayout(new BorderLayout());

        JPanel restockCategories = new JPanel();
        restockCategories.setLayout(new BorderLayout());

        // buttons for switching between category tables
        JPanel restockButtons = new JPanel();
        restockButtons.setLayout(new GridLayout(1, typeGroupMap.size(), 2, 2));
        restockButtons.setOpaque(false);

        // add category buttons to change tabes
        for (String categoryName: typeGroupMap.keySet()) {
            JButton btn = new JButton(categoryName);
            btn.setFont(subtitleFont);
            restockButtons.add(btn);

            // button shows restock table for category
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    displayRestockTable(categoryName);
                }
            });
        }

        // container for table of items and information
        JPanel spreadsheet = new JPanel();
        spreadsheet.setLayout(new BorderLayout());
        spreadsheet.setOpaque(false);

        // top row of table
        JPanel spreadsheetTitle = new JPanel();
        spreadsheetTitle.setLayout(new GridLayout(1, 7, 2, 2));
        spreadsheetTitle.setOpaque(false);

        // add column titles to spreadsheet table
        String[] tableLabels = {"Name", "Price", "Change Price", "Current Inv.", "PPU", "Unit", "Added Inv.", "Added Total", "Confirm"};
        for (String tl: tableLabels) {
            JLabel tableLabel = new JLabel(tl, SwingConstants.CENTER);
            tableLabel.setFont(subtitleFont);
            tableLabel.setForeground(Color.white);
            spreadsheetTitle.add(tableLabel);
        }

        JPanel btnContainer = new JPanel();
        btnContainer.setLayout(new BorderLayout());
        btnContainer.setOpaque(false);

        JPanel reportContainer = new JPanel();
        reportContainer.setLayout(new GridLayout(1, 3, 0, 0));
        reportContainer.setOpaque(false);

        // button to add item to item database
        JButton addItemBtn = new JButton("Add Item");
        addItemBtn.setFont(paragraphFont);

        // report buttons
        JButton salesReport = new JButton("Sales Report");
        salesReport.setFont(paragraphFont);
        reportContainer.add(salesReport);

        JButton excessReport = new JButton("Excess Report");
        excessReport.setFont(paragraphFont);
        reportContainer.add(excessReport);

        JButton restockReport = new JButton("Restock Report");
        restockReport.setFont(paragraphFont);
        reportContainer.add(restockReport);

        // add buttons to layout
        btnContainer.add(reportContainer, BorderLayout.EAST);
        btnContainer.add(addItemBtn, BorderLayout.WEST);
        spreadsheet.add(btnContainer, BorderLayout.SOUTH);

        // configure all button functionalities
        addItemBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItemFromGUI();
            }
        });
        salesReport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displaySalesReport();
            }
        });
        excessReport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayExcessReport();
            }
        });
        restockReport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayRestockReport();
            }
        });

        // configure layout for table cells container
        JPanel spreadsheetCellsContainer = new JPanel();
        spreadsheetCellsContainer.setLayout(new BorderLayout());
        spreadsheetCellsContainer.setOpaque(false);
        
        // configure layout for table cells
        spreadsheetCells = new JPanel();
        spreadsheetCells.setOpaque(false);

        // configure layout of all panels
        spreadsheetCellsContainer.add(spreadsheetCells, BorderLayout.NORTH);
        spreadsheet.add(spreadsheetTitle, BorderLayout.NORTH);
        spreadsheet.add(spreadsheetCellsContainer, BorderLayout.CENTER);
        orderPanel.add(restockButtons, BorderLayout.NORTH);
        orderPanel.add(spreadsheet, BorderLayout.CENTER);

        // set spreadsheet to category and redisplay order summary
        restockCategoryName = typeGroupMap.keySet().toArray()[0].toString();
        displayRestockTable(restockCategoryName);
        listRestockOrderItems();
    }


    /**
     * Displays a table of items on the main panel of the GUI based on the item category name.
     * <p>
     * This method will find all items within a category and list their information in a table 
     * layout. Also included is a text field to enter the quantity of the item that needs to be 
     * restocked and on option to add the item to the order. The manager of the restaurant can
     * enter a desired quantity to restock and add it to the order.
     * 
     * @param categoryName specifies the category whose items are displayed in the table.
     */
    void displayRestockTable(String categoryName) {
        restockCategoryName = categoryName;

        // reconfigure spreadsheet panel
        spreadsheetCells.removeAll();
        spreadsheetCells.revalidate();
        spreadsheetCells.repaint();

        // get all items in the specified category
        ArrayList<Item> sectionItems = new ArrayList<Item>();
        for (String type: typeGroupMap.get(categoryName)) {
            List<Item> items = itemTypeMap.get(type);
            sectionItems.addAll(items);
        }

        // initialize and configure spreadsheet layout
        spreadsheetCells.setLayout(new GridLayout(sectionItems.size(), 9, 2, 2));

        // add each item's properties to the spreadsheet table
        for (Item it: sectionItems) {
            JLabel ingName = new JLabel(it.getName(), SwingConstants.CENTER);
            JTextField cPrice = new JTextField();
            JButton chgBtn = new JButton("Change");
            JLabel inv = new JLabel(Double.toString(it.getInventory()), SwingConstants.CENTER);
            JLabel ppu = new JLabel(String.format("$%.2f", it.getRestockPrice()), SwingConstants.CENTER);
            JLabel u = new JLabel(it.getOrderUnit(), SwingConstants.CENTER);
            JTextField addInv = new JTextField();
            JLabel addTot = new JLabel("", SwingConstants.CENTER);
            JButton confBtn = new JButton("Add");

            // format each element
            ingName.setFont(paragraphFont);
            ingName.setForeground(Color.white);
            cPrice.setFont(paragraphFont);
            cPrice.setText(String.format("%.2f", it.getCustomerPrice()));
            chgBtn.setFont(paragraphFont);
            inv.setFont(paragraphFont);
            inv.setForeground(Color.white);
            ppu.setFont(paragraphFont);
            ppu.setForeground(Color.white);
            u.setFont(paragraphFont);
            u.setForeground(Color.white);
            addInv.setFont(paragraphFont);
            addTot.setFont(paragraphFont);
            addTot.setForeground(Color.white);
            confBtn.setFont(paragraphFont);

            // add each element to the row
            spreadsheetCells.add(ingName);
            spreadsheetCells.add(cPrice);
            spreadsheetCells.add(chgBtn);
            spreadsheetCells.add(inv);
            spreadsheetCells.add(ppu);
            spreadsheetCells.add(u);
            spreadsheetCells.add(addInv);
            spreadsheetCells.add(addTot);
            spreadsheetCells.add(confBtn);

            // add changePrice functionality
            chgBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        double price = Double.parseDouble(cPrice.getText());
                        it.setCustomerPrice(price);
                        eq.changeItemPrice(it, price);
                        displayRestockTable(categoryName);
                    }
                    catch (NumberFormatException err) {
                        System.out.println("ERROR: Please enter a number");
                    }
                }
            });

            // configure confirm button functionality
            confBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {   // add price of item to total and re-list items in summary
                        int amount = Integer.parseInt(addInv.getText());
                        if (amount > 0) {
                            double price = amount * it.getRestockPrice();
                            restockTotal += price;
                            restockOrderItems.put(it, amount);
                            listRestockOrderItems();
                        }
                    }
                    catch (NumberFormatException err) {
                        System.out.println("ERROR: Please enter a number");
                    }
                }
            });

            // configure text box functionality (triggers when text changes)
            addInv.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                  updatePrice();
                }
                public void removeUpdate(DocumentEvent e) {
                  updatePrice();
                }
                public void insertUpdate(DocumentEvent e) {
                  updatePrice();
                }
              
                public void updatePrice() {     // updates the price of the item at the amount in the text box
                    try {
                        int amount = Integer.parseInt(addInv.getText());
                        double price = amount * it.getRestockPrice();
                        addTot.setText(String.format("$%.2f", price));
                    }
                    catch (NumberFormatException err) { 
                        addTot.setText("");
                    }
                }
            });
        }
    }


    /**
     * List all of the items included in the customer order on the order summary panel.
     * <p>
     * This method is run every time an item is added to the order. Because of this,
     * the order summary panel needs to be cleared and re-painted at the start of the 
     * method in case any of the items are removed. For each item, the method lists the
     * item name, price (if there is one), and a button to remove it.
     */
    void listCustomerOrderItems() {
        // reset order summary panel and reconfigure layout
        orderSummary.removeAll();
        orderSummary.revalidate();
        orderSummary.repaint();
        orderSummary.setLayout(new GridLayout(customerOrderItems.size(), 3, 4, 4));

        // add elements to grid layout
        for (Item it: customerOrderItems) {
            JLabel itName = new JLabel(it.getName());
            JLabel itPrice = new JLabel("", SwingConstants.CENTER);
            if (it.getCustomerPrice() > 0) {
                itPrice.setText(String.format("$%.2f", it.getCustomerPrice()));
            }
            JButton removeBtn = new JButton("Remove");

            // button removes item from summary panel
            removeBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    customerOrderItems.remove(it);
                    customerTotal -= it.getCustomerPrice();
                    listCustomerOrderItems();
                }
            });

            itName.setFont(paragraphFont);
            itPrice.setFont(paragraphFont);
            removeBtn.setFont(paragraphFont);

            // add elements
            orderSummary.add(itName);
            orderSummary.add(itPrice);
            orderSummary.add(removeBtn);
        }

        // reset total text
        orderTotalLabel.setText(String.format("Total: $%.2f", customerTotal));
    }


    /**
     * List all of the items included in the restock order on the order summary panel.
     * <p>
     * This method is run every time an item is added to the order. Because of this,
     * the order summary panel needs to be cleared and re-painted at the start of the 
     * method in case any of the items are removed. For each item, the method lists the
     * item name, item amount, price, and a button to remove it.
     */
    void listRestockOrderItems() {
        // reset order summary panel and reconfigure layout
        orderSummary.removeAll();
        orderSummary.revalidate();
        orderSummary.repaint();
        orderSummary.setLayout(new GridLayout(restockOrderItems.size(), 4, 4, 4));

        // add elements to grid layout

        for (Map.Entry<Item, Integer> entry: restockOrderItems.entrySet()) {
            Item it = entry.getKey();
            int amount = entry.getValue();
            double price = amount * it.getRestockPrice();

            JLabel itName = new JLabel(it.getName(), SwingConstants.CENTER);
            JLabel itAmount = new JLabel(Integer.toString(amount), SwingConstants.CENTER);
            JLabel itPrice = new JLabel(String.format("$%.2f", price), SwingConstants.CENTER);
            JButton removeBtn = new JButton("Remove");

            // button removes item from summary panel
            removeBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    restockTotal -= price;
                    restockOrderItems.remove(it);
                    listRestockOrderItems();
                }
            });

            itName.setFont(paragraphFont);
            itAmount.setFont(paragraphFont);
            itPrice.setFont(paragraphFont);
            removeBtn.setFont(paragraphFont);

            // add elements
            orderSummary.add(itName);
            orderSummary.add(itAmount);
            orderSummary.add(itPrice);
            orderSummary.add(removeBtn);
        }

        // reset total text
        orderTotalLabel.setText(String.format("Total: $%.2f", restockTotal));
    }


    /**
     * Adds an item to the items database from the GUI.
     * <p>
     * This method will open a window asking the user to input the attributes of
     * a new Item object to be added to the itemsList and to the database. The \
     * method will create the Item object and add it to both entities
     */
    void addItemFromGUI() {
        // configure panel
        JFrame newItemWindow = new JFrame("Add Item");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // configure grid layout
        JPanel attGrid = new JPanel();
        attGrid.setLayout(new GridLayout(9, 2, 4, 4));

        // configure all elements to be added to the grid
        JLabel nameLab = new JLabel("Name");
        nameLab.setFont(paragraphFont);
        JTextField nameField = new JTextField();
        nameField.setFont(paragraphFont);

        JLabel cpLab = new JLabel("Customer Price");
        cpLab.setFont(paragraphFont);
        JTextField cpField = new JTextField();
        cpField.setFont(paragraphFont);

        JLabel rpLab = new JLabel("Restock Price");
        rpLab.setFont(paragraphFont);
        JTextField rpField = new JTextField();
        rpField.setFont(paragraphFont);

        JLabel caLab = new JLabel("Customer Amount");
        caLab.setFont(paragraphFont);
        JTextField caField = new JTextField();
        caField.setFont(paragraphFont);

        JLabel raLab = new JLabel("Restock Amount");
        raLab.setFont(paragraphFont);
        JTextField raField = new JTextField();
        raField.setFont(paragraphFont);

        JLabel unitLab = new JLabel("Unit of Measure");
        unitLab.setFont(paragraphFont);
        JTextField unitField = new JTextField();
        unitField.setFont(paragraphFont);

        JLabel invLab = new JLabel("Inventory Amount");
        invLab.setFont(paragraphFont);
        JTextField invField = new JTextField();
        invField.setFont(paragraphFont);

        JLabel typeLab = new JLabel("Item Type");
        typeLab.setFont(paragraphFont);
        JTextField typeField = new JTextField();
        typeField.setFont(paragraphFont);

        JLabel minAmtLab = new JLabel("Minumum Stock Amount");
        typeLab.setFont(paragraphFont);
        JTextField minAmtField = new JTextField();
        typeField.setFont(paragraphFont);

        // add each element to grid layout
        attGrid.add(nameLab);
        attGrid.add(nameField);
        attGrid.add(cpLab);
        attGrid.add(cpField);
        attGrid.add(rpLab);
        attGrid.add(rpField);
        attGrid.add(caLab);
        attGrid.add(caField);
        attGrid.add(raLab);
        attGrid.add(raField);
        attGrid.add(unitLab);
        attGrid.add(unitField);
        attGrid.add(invLab);
        attGrid.add(invField);
        attGrid.add(typeLab);
        attGrid.add(typeField);
        attGrid.add(minAmtLab);
        attGrid.add(minAmtField);

        // configure layout for button wrapper
        JPanel buttonWrapper = new JPanel();
        buttonWrapper.setLayout(new BorderLayout());

        // configure elements for adding item to database
        JButton confBtn = new JButton();
        confBtn.setText("Add Item");
        confBtn.setFont(paragraphFont);

        // add configure button functionality 
        confBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get item values
                String name = nameField.getText();
                double customerPrice = Double.parseDouble(cpField.getText());
                double restockPrice = Double.parseDouble(rpField.getText());
                double customerAmount = Double.parseDouble(caField.getText());
                double restockAmount = Double.parseDouble(raField.getText());
                double inventory = Double.parseDouble(invField.getText());
                String orderUnit = unitField.getText();
                String type = typeField.getText();
                double minAmount = Double.parseDouble(minAmtField.getText());

                // create item object
                Item item = new Item(-1, name, customerPrice, restockPrice, customerAmount, 
                                        restockAmount, orderUnit, inventory, type, minAmount);

                // add item to entities
                eq.addItem(item);
                itemsList = Arrays.asList(eq.getItems());
                itemTypeMap = itemsList.stream().collect(Collectors.groupingBy(i -> i.getType()));

                // close window gracefully
                newItemWindow.setVisible(false);
                newItemWindow.dispose();
            }
        });

        // configure window layout
        buttonWrapper.add(confBtn, BorderLayout.WEST);
        mainPanel.add(attGrid, BorderLayout.NORTH);
        mainPanel.add(buttonWrapper, BorderLayout.SOUTH);
        newItemWindow.add(mainPanel);

        // edit remoaning styles
        newItemWindow.setMinimumSize(new Dimension(300, 400));
        newItemWindow.setVisible(true);
    }


    // TODO: finish function
    void displaySalesReport() {
        // frame and panel initialization
        JFrame reportWindow = new JFrame("Sales Report");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // configure layout of main panel 
        reportWindow.add(mainPanel);

        // edit remoaning styles
        reportWindow.setMinimumSize(new Dimension(720, 480));
        reportWindow.setVisible(true);
    }
    // TODO: finish function
    void displayExcessReport() {
        // frame and panel initialization
        JFrame reportWindow = new JFrame("Excess Report");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // configure layout of main panel 
        reportWindow.add(mainPanel);

        // edit remoaning styles
        reportWindow.setMinimumSize(new Dimension(720, 480));
        reportWindow.setVisible(true);
    }
    // TODO: finish function
    void displayRestockReport() {
        // frame and panel initialization
        JFrame reportWindow = new JFrame("Restock Report");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // configure layout of main panel 
        reportWindow.add(mainPanel);

        // edit remoaning styles
        reportWindow.setMinimumSize(new Dimension(720, 480));
        reportWindow.setVisible(true);
    }


    public static void main(String[] args) {
        eq = new ExecQuery("duffy", "930006481");

        // collect items and group by type
        itemsList = Arrays.asList(eq.getItems());
        itemTypeMap = itemsList.stream().collect(Collectors.groupingBy(i -> i.getType()));

        // add entries to hash map that groups item types into categories (represented by keys)
        typeGroupMap.put("Base", Arrays.asList("Entree Base"));
        typeGroupMap.put("Protein", Arrays.asList("Protein"));
        typeGroupMap.put("Rice/Beans", Arrays.asList("Rice", "Beans"));
        typeGroupMap.put("Toppings", Arrays.asList("Cheese", "Toppings"));
        typeGroupMap.put("Sauces", Arrays.asList("Sauces"));
        typeGroupMap.put("Extras", Arrays.asList("Sides", "Drinks"));

        currentEmployee = new Employee(2,"Conrad", "Krueger", "CKrueg", "730001845", "manager");
        
        // initialize main frame and display
        MainFrame myFrame = new MainFrame();
        myFrame.initialize();
        myFrame.loginLayout(false);     // initial layout
        myFrame.setVisible(true);
    }
}