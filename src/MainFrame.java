import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

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


    /**
     * Configures and displays the main layout for the Point-of-Sale GUI interface.
     * <p>
     * This function runs at the start of the main function for the MainFrame class.
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

        // configure summary pane and add styles
        JTextArea orderSummary = new JTextArea();
        orderSummary.setFont(paragraphFont);
        orderSummary.setLineWrap(true);
        orderSummary.setWrapStyleWord(true);
        orderSummary.setOpaque(false);
        orderSummary.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // configure order confirm pane and add styles
        JPanel orderConfirm = new JPanel();
        orderConfirm.setOpaque(false);
        orderConfirm.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.black));

        // initialize order confirm pane elements
        JLabel orderTotalLabel = new JLabel(String.format("Total: $%.2f", total));
        orderTotalLabel.setFont(titleFont);
        JButton confirmButton = new JButton("Confirm");
        confirmButton.setFont(titleFont);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(titleFont);

        // TODO: add functionality for buttons

        // add confirm pane elements
        orderConfirm.setLayout(new GridLayout(1, 3, 2, 2));
        orderConfirm.add(orderTotalLabel);
        orderConfirm.add(confirmButton);
        orderConfirm.add(cancelButton);
        
        // add all panes to summary pane
        summaryPanel.add(summaryTitle, BorderLayout.NORTH);
        summaryPanel.add(orderSummary, BorderLayout.CENTER);
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
     * This function is triggered by the "server" button included in the navigation panel at the 
     * top of the GUI. The panel is automatically cleared and re-displayed outside of this function.
     */
    void serverLayout() {
        int numSmallSections = 3;
        int numBigSections = 3;
        int numButtonCols = 5;
        orderPanel.setLayout(new GridLayout(numSmallSections + numBigSections, 1, 0, 5));

        for (int i = 0; i < numSmallSections + numBigSections; i++) {
            JPanel selectionPanel = new JPanel();
            JPanel selectionButtons = new JPanel();

            selectionButtons.setOpaque(false);
            if (i < numSmallSections) {
                selectionButtons.setLayout(new GridLayout(1, numButtonCols, 4, 4));
                for (int j = 0; j < numButtonCols; j++) {
                    JButton btn = new JButton();
                    btn.setText("Button " + j);
                    selectionButtons.add(btn);
                }
            }
            else {
                selectionButtons.setLayout(new GridLayout(2, numButtonCols, 4, 4));
                for (int j = 0; j < 2 * numButtonCols; j++) {
                    JButton btn = new JButton();
                    btn.setText("Button " + j);
                    selectionButtons.add(btn);
                }
            }

            JLabel categoryName = new JLabel("Small Section " + i);
            categoryName.setForeground(Color.white);
            categoryName.setFont(subtitleFont);

            selectionPanel.setLayout(new BorderLayout());
            selectionPanel.setOpaque(false);
            selectionPanel.add(categoryName, BorderLayout.NORTH);
            selectionPanel.add(selectionButtons, BorderLayout.CENTER);

            orderPanel.add(selectionPanel, BorderLayout.CENTER);
        }
    }


    /**
     * Configures the functionality and layout for the main panel for the manager view of the GUI.
     * <p>
     * This function is triggered by the "manager" button included in the navigation panel at the 
     * top of the GUI. The panel is automatically cleared and re-displayed outside of this function.
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
        // restockCategories.add(restockButtons, BorderLayout.WEST);

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

    public static void main(String[] args) {
        MainFrame myFrame = new MainFrame();
        myFrame.initialize();
        myFrame.managerLayout();
        myFrame.setVisible(true);
    }
}