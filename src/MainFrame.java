import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.List;

import javax.swing.*;
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
                serverLayout();
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
                managerLayout();
                setVisible(true);
            }
        });

        // buttons layout
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 2, 0, 0));
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

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: send items list to backend
                if (serverPage) {
                    eq.confirmCustomerOrder(customerOrderItems, currentEmployee);
                    customerTotal = 0;
                    customerOrderItems.clear();
                    listCustomerOrderItems();
                }
                else {
                    // completeRestockOrder(restockOrderItems, employee)
                    restockTotal = 0;
                    restockOrderItems.clear();
                    listRestockOrderItems();
                }
            }
        });

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
        String[] tableLabels = {"Ingredient Name", "Current Inv.", "Price Per Unit", "Unit", "Added Inv.", "Added Total", "Confirm"};
        for (String tl: tableLabels) {
            JLabel tableLabel = new JLabel(tl, SwingConstants.CENTER);
            tableLabel.setFont(subtitleFont);
            tableLabel.setForeground(Color.white);
            spreadsheetTitle.add(tableLabel);
        }

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
        String firstCategory = typeGroupMap.keySet().toArray()[0].toString();
        displayRestockTable(firstCategory);
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
        spreadsheetCells.setLayout(new GridLayout(sectionItems.size(), 7, 2, 2));

        // add each item's properties to the spreadsheet table
        for (Item it: sectionItems) {
            JLabel ingName = new JLabel(it.getName(), SwingConstants.CENTER);
            JLabel inv = new JLabel(Double.toString(it.getInventory()), SwingConstants.CENTER);
            JLabel ppu = new JLabel(String.format("$%.2f", it.getRestockPrice()), SwingConstants.CENTER);
            JLabel u = new JLabel(it.getOrderUnit(), SwingConstants.CENTER);
            JTextField addInv = new JTextField();
            JLabel addTot = new JLabel("", SwingConstants.CENTER);
            JButton confBtn = new JButton("Add to Order");

            ingName.setFont(paragraphFont);
            ingName.setForeground(Color.white);
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
            spreadsheetCells.add(inv);
            spreadsheetCells.add(ppu);
            spreadsheetCells.add(u);
            spreadsheetCells.add(addInv);
            spreadsheetCells.add(addTot);
            spreadsheetCells.add(confBtn);

            // configure button functionality
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


    public static void main(String[] args) {
        eq = new ExecQuery("duffy", "930006481");

        // collect items and group by type
        List<Item> itemsList = Arrays.asList(eq.getItems());
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
        myFrame.managerLayout();     // initial layout
        myFrame.setVisible(true);
    }
}