import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.List;

import javax.swing.*;

import org.w3c.dom.views.DocumentView;

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

    private double total = 0;
    JPanel orderPanel;  // panel that configures order, changes based on server/manager

    private int numButtonCols = 6;

    private static Map<String, List<Item>> itemTypeMap;
    private static LinkedHashMap<String, List<String>> typeGroupMap = new LinkedHashMap<>();
    private static JLabel orderTotalLabel;

    private static ArrayList<Item> orderItems = new ArrayList<>();
    JPanel orderSummary;


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
        orderTotalLabel = new JLabel(String.format("Total: $%.2f", total));
        orderTotalLabel.setFont(titleFont);
        JButton confirmButton = new JButton("Confirm");
        confirmButton.setFont(titleFont);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(titleFont);

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: send items list to backend
                // completeCustomerOrder(orderItems, employee)
                orderItems.clear();
                total = 0;
                listCustomerOrderItems();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                orderItems.clear();
                total = 0;
                listCustomerOrderItems();
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
    }
    

    /**
     * Configures the functionality and layout for the main panel for the server view of the GUI.
     * <p>
     * This method is triggered by the "server" button included in the navigation panel at the 
     * top of the GUI. The panel is automatically cleared and re-displayed outside of this method.
     */
    void serverLayout() {
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
                            orderItems.add(currentItem);
                            total += currentItem.getCustomerPrice();
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
    }


    /**
     * Configures the functionality and layout for the main panel for the manager view of the GUI.
     * <p>
     * This method is triggered by the "manager" button included in the navigation panel at the 
     * top of the GUI. The panel is automatically cleared and re-displayed outside of this method.
     */
    void managerLayout() {
        orderPanel.setLayout(new BorderLayout());

        JPanel restockCategories = new JPanel();
        restockCategories.setLayout(new BorderLayout());

        ArrayList<String> categories = new ArrayList<String>();
        categories.add("Bases");
        categories.add("Protein");
        categories.add("Rice/Beans");
        categories.add("Toppings/Sauces");
        categories.add("Sides");
        categories.add("Other");

        JPanel restockButtons = new JPanel();
        restockButtons.setLayout(new GridLayout(1, categories.size(), 2, 2));
        restockButtons.setOpaque(false);

        for (String cat: categories) {
            JButton btn = new JButton(cat);
            btn.setFont(subtitleFont);
            restockButtons.add(btn);
        }

        JPanel spreadsheet = new JPanel();
        spreadsheet.setLayout(new BorderLayout());
        spreadsheet.setOpaque(false);

        JPanel spreadsheetTitle = new JPanel();
        spreadsheetTitle.setLayout(new GridLayout(1, 7, 2, 2));
        spreadsheetTitle.setOpaque(false);

        JLabel ingredientName = new JLabel("Ingredient Name", SwingConstants.CENTER);
        ingredientName.setFont(subtitleFont);
        JLabel inventory = new JLabel("Current Inv.", SwingConstants.CENTER);
        inventory.setFont(subtitleFont);
        JLabel pricePerUnit = new JLabel("Price Per Unit", SwingConstants.CENTER);
        pricePerUnit.setFont(subtitleFont);
        JLabel unit = new JLabel("Unit", SwingConstants.CENTER);
        unit.setFont(subtitleFont);
        JLabel addedInventory = new JLabel("Added Inv.", SwingConstants.CENTER);
        addedInventory.setFont(subtitleFont);
        JLabel addedTotal = new JLabel("Added Total", SwingConstants.CENTER);
        addedTotal.setFont(subtitleFont);
        JLabel confirm = new JLabel("Confirm", SwingConstants.CENTER);
        confirm.setFont(subtitleFont);

        spreadsheetTitle.add(ingredientName);
        spreadsheetTitle.add(inventory);
        spreadsheetTitle.add(pricePerUnit);
        spreadsheetTitle.add(unit);
        spreadsheetTitle.add(addedInventory);
        spreadsheetTitle.add(addedTotal);
        spreadsheetTitle.add(confirm);

        JPanel spreadsheetCellsContainer = new JPanel();
        spreadsheetCellsContainer.setLayout(new BorderLayout());
        spreadsheetCellsContainer.setOpaque(false);
        
        JPanel spreadsheetCells = new JPanel();
        spreadsheetCells.setLayout(new GridLayout(3, 7, 2, 2));
        spreadsheetCells.setOpaque(false);
        for (int i = 0; i < 3; i++) {
            JLabel ingName = new JLabel("Ingredient " + i);
            JLabel inv = new JLabel("XXX");
            JLabel ppu = new JLabel("$X.XX");
            JLabel u = new JLabel("u");
            JTextField addInv = new JTextField();
            JLabel addTot = new JLabel("$XX.XX");
            JButton confBtn = new JButton("Add to Order");

            spreadsheetCells.add(ingName);
            spreadsheetCells.add(inv);
            spreadsheetCells.add(ppu);
            spreadsheetCells.add(u);
            spreadsheetCells.add(addInv);
            spreadsheetCells.add(addTot);
            spreadsheetCells.add(confBtn);
        }
        spreadsheetCellsContainer.add(spreadsheetCells, BorderLayout.NORTH);

        spreadsheet.add(spreadsheetTitle, BorderLayout.NORTH);
        spreadsheet.add(spreadsheetCellsContainer, BorderLayout.CENTER);

        orderPanel.add(restockButtons, BorderLayout.NORTH);
        orderPanel.add(spreadsheet, BorderLayout.CENTER);
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
        orderSummary.removeAll();
        orderSummary.revalidate();
        orderSummary.repaint();

        orderSummary.setLayout(new GridLayout(orderItems.size(), 3, 4, 4));

        for (Item it: orderItems) {
            JLabel itName = new JLabel(it.getName());
            JLabel itPrice = new JLabel("", SwingConstants.CENTER);
            if (it.getCustomerPrice() > 0) {
                itPrice.setText(String.format("$%.2f", it.getCustomerPrice()));
            }
            JButton removeBtn = new JButton("Remove");

            removeBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    orderItems.remove(it);
                    total -= it.getCustomerPrice();
                    listCustomerOrderItems();
                }
            });

            orderSummary.add(itName);
            orderSummary.add(itPrice);
            orderSummary.add(removeBtn);
        }

        orderTotalLabel.setText(String.format("Total: $%.2f", total));
    }


    public static void main(String[] args) {
        ExecQuery eq = new ExecQuery("duffy", "930006481");

        List<Item> itemsList = Arrays.asList(eq.getItems());
        itemTypeMap = itemsList.stream().collect(Collectors.groupingBy(i -> i.getType()));

        typeGroupMap.put("Base", Arrays.asList("Entree Base"));
        typeGroupMap.put("Protein", Arrays.asList("Protein"));
        typeGroupMap.put("Rice/Beans", Arrays.asList("Rice", "Beans"));
        typeGroupMap.put("Toppings", Arrays.asList("Cheese", "Toppings"));
        typeGroupMap.put("Sauces", Arrays.asList("Sauces"));
        typeGroupMap.put("Extras", Arrays.asList("Sides", "Drinks"));
        
        MainFrame myFrame = new MainFrame();
        myFrame.initialize();
        myFrame.serverLayout();
        myFrame.setVisible(true);

        eq.close();
    }
}