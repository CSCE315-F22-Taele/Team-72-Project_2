import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainFrame extends JFrame {
    final private Font titleFont = new Font("Helvetica", Font.BOLD, 18);
    final private Font paragraphFont = new Font("Helvetica", Font.PLAIN, 14);

    private double total = 0;
    JPanel orderPanel;  // panel that configures order, changes based on server/manager

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
        JLabel totalLabel = new JLabel("Total:");
        totalLabel.setFont(titleFont);
        JLabel orderTotalLabel = new JLabel(String.format("$%.2f", total));
        orderTotalLabel.setFont(titleFont);
        JButton confirmButton = new JButton("Confirm");
        confirmButton.setFont(titleFont);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(titleFont);

        // TODO: add functionality for buttons

        // add confirm pane elements
        orderConfirm.setLayout(new GridLayout(1, 4, 2, 2));
        orderConfirm.add(totalLabel);
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

    // reconfigures main panel for customer orders
    void serverLayout() {
        orderPanel.setLayout(new BorderLayout());
        JLabel test = new JLabel("Server");
        orderPanel.add(test, BorderLayout.NORTH);
    }

    // reconfigures main panel for restock orders
    void managerLayout() {
        orderPanel.setLayout(new BorderLayout());
        JLabel test = new JLabel("Manager");
        orderPanel.add(test, BorderLayout.NORTH);
    }

    public static void main(String[] args) {
        MainFrame myFrame = new MainFrame();
        myFrame.initialize();
    }
}