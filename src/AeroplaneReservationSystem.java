import com.toedter.calendar.JDateChooser;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

public class AeroplaneReservationSystem {
    private static int ticketCount = 0;
    private static final ArrayList<String> ticketList = new ArrayList<>();

    // Enhanced color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);      // Blue
    private static final Color SECONDARY_COLOR = new Color(236, 240, 241);   // Light Gray
    private static final Color ACCENT_COLOR = new Color(231, 76, 60);        // Red
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);      // Green
    private static final Color WARNING_COLOR = new Color(241, 196, 15);      // Yellow
    private static final Color DARK_COLOR = new Color(52, 73, 94);           // Dark Blue
    private static final Color LIGHT_COLOR = new Color(236, 240, 241);       // Light Gray
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);  // Off White
    private static final Color GRADIENT_START = new Color(41, 128, 185);     // Blue
    private static final Color GRADIENT_END = new Color(52, 152, 219);       // Lighter Blue
    private static final Color TICKET_HEADER_COLOR = new Color(155, 89, 182); // Purple
    private static final Color TICKET_BORDER_COLOR = new Color(142, 68, 173); // Darker Purple
    private static final Color TICKET_BG_COLOR = new Color(250, 250, 250);    // Almost White
    private static final Color TICKET_TEXT_COLOR = new Color(44, 62, 80);     // Dark Gray
    private static final Color HOVER_COLOR = new Color(26, 188, 156);         // Turquoise

    // Email Configuration
    private static final String FROM_EMAIL = "ahsansaeed1993@gmail.com"; // Replace with your email
    private static final String EMAIL_PASSWORD = "hzzi lyks ztwc tvix"; // Replace with your app password

    // Enhanced fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TICKET_FONT = new Font("Consolas", Font.PLAIN, 16);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);

    // Form components
    private static JTextField nameField, dobField, emailField, phoneField;
    private static JComboBox<String> departureCity, destinationCity, classSelection;

    private static JComboBox<String> returnFlightTimeSelection;
    private static JRadioButton oneWayBtn, returnBtn;
    private static JDateChooser departureDateChooser, returnDateChooser;
    // New components for airline selection
    private static JComboBox<String> airlineSelection;
    private static JComboBox<String> flightTimeSelection;
    private static JLabel priceLabel;
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");

        JFrame frame = new JFrame("Pakistan Airlines Reservation System");
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));
        frame.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel titlePanel = createTitlePanel();
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        JPanel inputPanel = createInputPanel();
        JScrollPane scrollPane = new JScrollPane(inputPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JButton bookButton = createBookButton(frame);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bookButton, BorderLayout.SOUTH);

        frame.add(titlePanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(PRIMARY_COLOR, 2, true),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Add flight type selection
        JPanel flightTypePanel = createFlightTypePanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        inputPanel.add(flightTypePanel, gbc);

        // Reset gridwidth
        gbc.gridwidth = 1;

        String[] cities = {"Karachi", "Lahore", "Islamabad", "Peshawar", "Multan"};
        departureCity = createStyledComboBox(cities);
        destinationCity = createStyledComboBox(cities);
        classSelection = createStyledComboBox(new String[]{"Economy", "Business", "First Class"});

        // Add airline selection
        String[] airlines = {"AirBlue (PA-406)", "AirBlue (PA-402)", "Fly Jinnah (9P-846)"};
        airlineSelection = createStyledComboBox(airlines);
        flightTimeSelection = createStyledComboBox(new String[]{});
        returnFlightTimeSelection = createStyledComboBox(new String[]{}); // Add return flight time selection
        returnFlightTimeSelection.setEnabled(false); // Initially disabled

        priceLabel = new JLabel("Price: PKR 0");
        priceLabel.setFont(LABEL_FONT);
        priceLabel.setForeground(DARK_COLOR);

        // Create text fields
        nameField = createStyledTextField("Enter passenger name");
        dobField = createStyledTextField("YYYY-MM-DD");
        emailField = createStyledTextField("Enter email address");
        phoneField = createStyledTextField("Enter phone number (03XXXXXXXXX)");

        // Create date choosers
        departureDateChooser = new JDateChooser();
        returnDateChooser = new JDateChooser();
        styleDateChooser(departureDateChooser);
        styleDateChooser(returnDateChooser);
        returnDateChooser.setEnabled(false);

        // Set minimum date as today for departure and return
        departureDateChooser.setMinSelectableDate(new Date());
        returnDateChooser.setMinSelectableDate(new Date());

        // Add components
        int row = 1;
        addLabelAndComponent(inputPanel, "Departure City:", departureCity, row++, gbc);
        addLabelAndComponent(inputPanel, "Destination City:", destinationCity, row++, gbc);
        addLabelAndComponent(inputPanel, "Airline:", airlineSelection, row++, gbc);
        addLabelAndComponent(inputPanel, "Departure Flight Time:", flightTimeSelection, row++, gbc);
        addLabelAndComponent(inputPanel, "Departure Date:", departureDateChooser, row++, gbc);
        addLabelAndComponent(inputPanel, "Return Date:", returnDateChooser, row++, gbc);
        addLabelAndComponent(inputPanel, "Return Flight Time:", returnFlightTimeSelection, row++, gbc);
        addLabelAndComponent(inputPanel, "Class:", classSelection, row++, gbc);
        addLabelAndComponent(inputPanel, "Estimated Price:", priceLabel, row++, gbc);
        addLabelAndComponent(inputPanel, "Passenger Full Name:", nameField, row++, gbc);
        addLabelAndComponent(inputPanel, "Date of Birth:", dobField, row++, gbc);
        addLabelAndComponent(inputPanel, "Email:", emailField, row++, gbc);
        addLabelAndComponent(inputPanel, "Phone:", phoneField, row++, gbc);

        // Add listeners for dynamic updates
        departureCity.addActionListener(e -> {
            updateFlightTimes((String)departureCity.getSelectedItem(),
                    (String)destinationCity.getSelectedItem(),
                    flightTimeSelection);
            if (returnBtn.isSelected()) {
                updateFlightTimes((String)destinationCity.getSelectedItem(),
                        (String)departureCity.getSelectedItem(),
                        returnFlightTimeSelection);
            }
        });

        destinationCity.addActionListener(e -> {
            updateFlightTimes((String)departureCity.getSelectedItem(),
                    (String)destinationCity.getSelectedItem(),
                    flightTimeSelection);
            if (returnBtn.isSelected()) {
                updateFlightTimes((String)destinationCity.getSelectedItem(),
                        (String)departureCity.getSelectedItem(),
                        returnFlightTimeSelection);
            }
        });

        airlineSelection.addActionListener(e -> updatePrice());
        classSelection.addActionListener(e -> updatePrice());

        return inputPanel;
    }
    private static void updateFlightTimes(String departure, String destination, JComboBox<String> timeSelection) {
        timeSelection.removeAllItems();

        if (departure.equals("Karachi") && destination.equals("Lahore")) {
            timeSelection.addItem("08:00 PM - 09:50 PM (1h 50m)");
            timeSelection.addItem("12:00 PM - 01:50 PM (1h 50m)");
            timeSelection.addItem("08:35 PM - 10:15 PM (1h 40m)");
        } else if (departure.equals("Lahore") && destination.equals("Karachi")) {
            timeSelection.addItem("09:00 AM - 10:50 AM (1h 50m)");
            timeSelection.addItem("02:00 PM - 03:50 PM (1h 50m)");
            timeSelection.addItem("06:35 PM - 08:15 PM (1h 40m)");
        } else if (departure.equals("Islamabad") && destination.equals("Karachi")) {
            timeSelection.addItem("07:30 AM - 09:30 AM (2h 00m)");
            timeSelection.addItem("01:30 PM - 03:30 PM (2h 00m)");
            timeSelection.addItem("05:30 PM - 07:30 PM (2h 00m)");
        } else if (departure.equals("Karachi") && destination.equals("Islamabad")) {
            timeSelection.addItem("10:30 AM - 12:30 PM (2h 00m)");
            timeSelection.addItem("03:30 PM - 05:30 PM (2h 00m)");
            timeSelection.addItem("08:30 PM - 10:30 PM (2h 00m)");
        } else {
            timeSelection.addItem("10:00 AM - 11:45 AM (1h 45m)");
            timeSelection.addItem("03:00 PM - 04:45 PM (1h 45m)");
            timeSelection.addItem("07:00 PM - 08:45 PM (1h 45m)");
        }
    }

    private static int calculatePrice(int age, String travelClass, String airline, boolean isReturn) {
        int basePrice;
        switch (airline) {
            case "AirBlue (PA-406)" -> basePrice = 33500;
            case "AirBlue (PA-402)" -> basePrice = 36045;
            case "Fly Jinnah (9P-846)" -> basePrice = 25943;
            default -> basePrice = 30000;
        }

        // Apply class multiplier
        switch (travelClass) {
            case "First Class" -> basePrice *= 2.5;
            case "Business" -> basePrice *= 1.5;
            default -> basePrice *= 1.0;
        }

        // Apply age discounts
        if (age <= 2) {
            basePrice = (int)(basePrice * 0.1); // 90% discount for infants
        } else if (age <= 12) {
            basePrice = (int)(basePrice * 0.5); // 50% discount for children
        } else if (age >= 65) {
            basePrice = (int)(basePrice * 0.7); // 30% discount for seniors
        }

        // Double the price for return tickets
        if (isReturn) {
            basePrice *= 2;
        }

        return basePrice;
    }

    private static JPanel createFlightTypePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panel.setBackground(Color.WHITE);

        oneWayBtn = new JRadioButton("One Way");
        returnBtn = new JRadioButton("Return");

        ButtonGroup group = new ButtonGroup();
        group.add(oneWayBtn);
        group.add(returnBtn);

        oneWayBtn.setSelected(true);

        styleRadioButton(oneWayBtn);
        styleRadioButton(returnBtn);

        // Updated oneWayBtn listener with price update
        oneWayBtn.addActionListener(e -> {
            returnDateChooser.setEnabled(false);
            returnDateChooser.setDate(null);
            returnFlightTimeSelection.setEnabled(false);
            returnFlightTimeSelection.removeAllItems();
            updatePrice(); // Add price update for one-way selection
        });

        // Updated returnBtn listener with price update
        returnBtn.addActionListener(e -> {
            returnDateChooser.setEnabled(true);
            returnFlightTimeSelection.setEnabled(true);

            // Set minimum date for return as the departure date
            if (departureDateChooser.getDate() != null) {
                returnDateChooser.setMinSelectableDate(departureDateChooser.getDate());
            }

            // Update return flight times
            updateFlightTimes(
                    (String)destinationCity.getSelectedItem(),
                    (String)departureCity.getSelectedItem(),
                    returnFlightTimeSelection
            );
            updatePrice(); // Add price update for return selection
        });

        panel.add(oneWayBtn);
        panel.add(returnBtn);

        return panel;
    }

    private static void styleRadioButton(JRadioButton button) {
        button.setFont(LABEL_FONT);
        button.setForeground(DARK_COLOR);
        button.setBackground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(PRIMARY_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(DARK_COLOR);
            }
        });
    }

    private static void styleDateChooser(JDateChooser dateChooser) {
        dateChooser.setPreferredSize(new Dimension(300, 35));
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setFont(INPUT_FONT);
        ((JTextField) dateChooser.getDateEditor().getUiComponent()).setBorder(
                new LineBorder(PRIMARY_COLOR, 1));

        // Add property change listener for date validation
        dateChooser.getDateEditor().addPropertyChangeListener(e -> {
            if ("date".equals(e.getPropertyName()) && returnBtn.isSelected()) {
                Date departureDate = departureDateChooser.getDate();
                if (departureDate != null) {
                    returnDateChooser.setMinSelectableDate(departureDate);
                    // If return date is before departure date, clear it
                    if (returnDateChooser.getDate() != null &&
                            returnDateChooser.getDate().before(departureDate)) {
                        returnDateChooser.setDate(null);
                    }
                }
            }
        });
    }
    private static JPanel createTitlePanel() {
        JPanel titlePanel = new GradientPanel(GRADIENT_START, GRADIENT_END);
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel titleLabel = new JLabel("Pakistan Airlines Reservation", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createCompoundBorder(
                        new ShadowBorder(),
                        BorderFactory.createEmptyBorder(5, 15, 5, 15)
                )
        ));

        JButton viewTicketsButton = createViewTicketsButton();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(viewTicketsButton);

        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(buttonPanel, BorderLayout.EAST);

        return titlePanel;
    }
    private static JButton createViewTicketsButton() {
        JButton viewTicketsButton = new JButton("View Booked Tickets") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };

        viewTicketsButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewTicketsButton.setForeground(Color.WHITE);
        viewTicketsButton.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.WHITE, 1, true),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        viewTicketsButton.setContentAreaFilled(false);
        viewTicketsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewTicketsButton.addActionListener(e -> showTicketsWindow());

        viewTicketsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                viewTicketsButton.setForeground(Color.YELLOW);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                viewTicketsButton.setForeground(Color.WHITE);
            }
        });

        return viewTicketsButton;
    }

    private static JButton createBookButton(JFrame frame) {
        JButton bookButton = new JButton("Book Ticket") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, SUCCESS_COLOR,
                        0, getHeight(), SUCCESS_COLOR.darker()
                );
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        bookButton.setFont(BUTTON_FONT);
        bookButton.setForeground(Color.WHITE);
        bookButton.setOpaque(false);
        bookButton.setContentAreaFilled(false);
        bookButton.setBorderPainted(false);
        bookButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookButton.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));

        bookButton.addActionListener(e -> handleBooking(frame));

        return bookButton;
    }

    private static void handleBooking(JFrame frame) {
        String departure = (String) departureCity.getSelectedItem();
        String destination = (String) destinationCity.getSelectedItem();
        String airline = (String) airlineSelection.getSelectedItem();
        String flightTime = (String) flightTimeSelection.getSelectedItem();
        String returnFlightTime = returnBtn.isSelected() ?
                (String) returnFlightTimeSelection.getSelectedItem() : "N/A";
        String travelClass = (String) classSelection.getSelectedItem();
        String name = nameField.getText().trim();
        String dobText = dobField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        // Remove placeholder text if present
        if (name.equals("Enter passenger name")) name = "";
        if (dobText.equals("YYYY-MM-DD")) dobText = "";
        if (email.equals("Enter email address")) email = "";
        if (phone.equals("Enter phone number (+92XXXXXXXXXX)")) phone = "";

        // Validation
        if (name.isEmpty() || dobText.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            showError(frame, "Please fill in all required fields!", "Validation Error");
            return;
        }

        if (departure.equals(destination)) {
            showError(frame, "Departure and destination cities cannot be the same!", "Invalid Selection");
            return;
        }

        if (flightTimeSelection.getSelectedItem() == null) {
            showError(frame, "Please select a departure flight time!", "Invalid Selection");
            return;
        }

        // Return flight validation
        if (returnBtn.isSelected()) {
            if (returnFlightTimeSelection.getSelectedItem() == null) {
                showError(frame, "Please select a return flight time!", "Invalid Selection");
                return;
            }
        }

        // Date validations
        if (departureDateChooser.getDate() == null) {
            showError(frame, "Please select a departure date!", "Date Error");
            return;
        }

        if (returnBtn.isSelected() && returnDateChooser.getDate() == null) {
            showError(frame, "Please select a return date!", "Date Error");
            return;
        }

        if (returnBtn.isSelected() &&
                returnDateChooser.getDate().before(departureDateChooser.getDate())) {
            showError(frame, "Return date cannot be before departure date!", "Date Error");
            return;
        }

        // Email validation
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError(frame, "Please enter a valid email address!", "Invalid Email");
            return;
        }

        if (!phone.matches("^(\\+92|92|0)(3[0-9]{2}[0-9]{7})$") || phone.length() > 11) {
            showError(frame, """
            Please enter a valid Pakistani phone number!
            Format: 03xxxxxxxxx
            - Should start with 03
            - Must be exactly 11 digits
            - Only numbers allowed""",
                    "Invalid Phone Number");
            return;
        }

        phone = phone.replaceAll("^\\+92|^92", "0"); // Convert +92 or 92 to 0

        // Date validation
        LocalDate dob;
        try {
            dob = LocalDate.parse(dobText);
            if (dob.isAfter(LocalDate.now())) {
                showError(frame, "Date of birth cannot be in the future!", "Invalid Date");
                return;
            }
        } catch (Exception ex) {
            showError(frame, "Please enter date in YYYY-MM-DD format!", "Invalid Date Format");
            return;
        }

        // Calculate price and generate ticket
        int age = Period.between(dob, LocalDate.now()).getYears();
        int price = calculatePrice(age, travelClass, airline);
        if (returnBtn.isSelected()) {
            price *= 2; // Double the price for return tickets
        }
        String ticketNo = generateTicketNumber();

        // Format dates for ticket
        String departureDate = formatDate(departureDateChooser.getDate());
        String returnDate = returnBtn.isSelected() ? formatDate(returnDateChooser.getDate()) : "N/A";

        String ticketDetails = formatTicketDetails(
                ticketNo, departure, destination, travelClass,
                name, dobText, phone, departureDate, returnDate,
                airline, flightTime, returnFlightTime, price
        );

        // Add ticket to list and send email
        ticketList.add(ticketDetails);
        sendEmail(email, "Your Pakistan Airlines Ticket Confirmation", createEmailContent(ticketDetails));

        // Show success message and reset fields
        showSuccess(frame);
        resetFields();
    }
    private static JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(INPUT_FONT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(DARK_COLOR);
        comboBox.setBorder(new LineBorder(PRIMARY_COLOR, 1));
        comboBox.setPreferredSize(new Dimension(300, 35));
        ((JComponent) comboBox.getRenderer()).setBackground(Color.WHITE);
        return comboBox;
    }

    private static JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder);
        textField.setFont(INPUT_FONT);
        textField.setForeground(Color.GRAY);
        textField.setBorder(new LineBorder(PRIMARY_COLOR, 1));
        textField.setPreferredSize(new Dimension(300, 35));

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(DARK_COLOR);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });

        return textField;
    }

    private static void addLabelAndComponent(JPanel panel, String labelText,
                                             JComponent component, int row,
                                             GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(DARK_COLOR);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(component, gbc);
    }

    private static void showError(JFrame frame, String message, String title) {
        JOptionPane.showMessageDialog(frame,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
    }

    private static void showSuccess(JFrame frame) {
        JOptionPane.showMessageDialog(frame,
                "✈ Ticket booked successfully!\nCheck your email for confirmation.",
                "Booking Successful",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private static void resetFields() {
        nameField.setText("Enter passenger name");
        nameField.setForeground(Color.GRAY);
        dobField.setText("YYYY-MM-DD");
        dobField.setForeground(Color.GRAY);
        emailField.setText("Enter email address");
        emailField.setForeground(Color.GRAY);
        phoneField.setText("Enter phone number (03XXXXXXXXX)");
        phoneField.setForeground(Color.GRAY);
        departureCity.setSelectedIndex(0);
        destinationCity.setSelectedIndex(0);
        airlineSelection.setSelectedIndex(0);
        classSelection.setSelectedIndex(0);
        departureDateChooser.setDate(null);
        returnDateChooser.setDate(null);
        oneWayBtn.setSelected(true);
        returnDateChooser.setEnabled(false);
        updateFlightTimes((String)departureCity.getSelectedItem(),
                (String)destinationCity.getSelectedItem());
        updatePrice();
    }

    private static void updateFlightTimes(String selectedItem, String selectedItem1) {
    }

    private static String createEmailContent(String ticketDetails) {
        return String.format("""
    <html>
        <body style='font-family: Courier New, monospace; color: #000000; padding: 20px;'>
            <div style='max-width: 400px; margin: 0 auto; background-color: #ffffff; 
                      padding: 20px;'>
                <!-- Logo and Title Section -->
                <div style='text-align: center; margin-bottom: 20px;'>
                    <div style='color: #0078d4; font-size: 24px; margin-bottom: 10px;'>
                        ✈ PAKISTAN AIRLINES
                    </div>
                    <div style='font-size: 20px; color: #333333; margin-top: 20px;'>
                        Ticket Confirmation
                    </div>
                </div>
                
                <!-- Ticket Details Section -->
                <div style='border: 2px solid #000000;'>
                    <pre style='margin: 0;'>
╔════════════════════════════════╗
║         PAKISTAN AIRLINES      ║
╠════════════════════════════════╣
%s
╚════════════════════════════════╝
                    </pre>
                </div>
                
                <!-- Footer Section -->
                <div style='text-align: center; margin-top: 20px; color: #666666; font-size: 12px;'>
                    Thank you for choosing Pakistan Airlines
                </div>
            </div>
        </body>
    </html>
    """, ticketDetails);
    }
    private static String formatDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .toString();
    }

    private static void showTicketsWindow() {
        JFrame ticketsFrame = new JFrame("✈ Booked Tickets");
        ticketsFrame.setSize(900, 700);
        ticketsFrame.setLayout(new BorderLayout(10, 10));
        ticketsFrame.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel headerPanel = new GradientPanel(TICKET_HEADER_COLOR, TICKET_HEADER_COLOR.darker());
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        headerPanel.setLayout(new BorderLayout());

        JLabel ticketTitle = new JLabel("All Booked Tickets", SwingConstants.CENTER);
        ticketTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        ticketTitle.setForeground(Color.WHITE);
        headerPanel.add(ticketTitle, BorderLayout.CENTER);

        JTextArea ticketsDisplay = new JTextArea();
        ticketsDisplay.setFont(TICKET_FONT);
        ticketsDisplay.setEditable(false);
        ticketsDisplay.setMargin(new Insets(15, 15, 15, 15));
        ticketsDisplay.setBackground(TICKET_BG_COLOR);
        ticketsDisplay.setForeground(TICKET_TEXT_COLOR);

        StringBuilder allTickets = new StringBuilder();
        for (String ticket : ticketList) {
            allTickets.append(ticket);
        }
        ticketsDisplay.setText(allTickets.toString());
        ticketsDisplay.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(ticketsDisplay);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        scrollPane.getViewport().setBackground(TICKET_BG_COLOR);

        // Customize scrollbar
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = TICKET_HEADER_COLOR;
                this.trackColor = LIGHT_COLOR;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });

        JButton closeButton = new JButton("Close Window");
        closeButton.setFont(BUTTON_FONT);
        closeButton.setForeground(Color.RED);
        closeButton.setBackground(ACCENT_COLOR);
        closeButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> ticketsFrame.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        buttonPanel.add(closeButton);

        ticketsFrame.add(headerPanel, BorderLayout.NORTH);
        ticketsFrame.add(scrollPane, BorderLayout.CENTER);
        ticketsFrame.add(buttonPanel, BorderLayout.SOUTH);
        ticketsFrame.setLocationRelativeTo(null);
        ticketsFrame.setVisible(true);
    }

    private static int calculatePrice(int age, String travelClass, String airline) {
        int basePrice;
        switch (airline) {
            case "AirBlue (PA-406)" -> basePrice = 33500;
            case "AirBlue (PA-402)" -> basePrice = 36045;
            case "Fly Jinnah (9P-846)" -> basePrice = 25943;
            default -> basePrice = 30000;
        }

        // Apply class multiplier
        switch (travelClass) {
            case "First Class" -> basePrice *= 2.5;
            case "Business" -> basePrice *= 1.5;
            default -> basePrice *= 1.0;
        }

        // Apply age discounts
        if (age <= 2) {
            return (int)(basePrice * 0.1); // 90% discount for infants
        } else if (age <= 12) {
            return (int)(basePrice * 0.5); // 50% discount for children
        } else if (age >= 65) {
            return (int)(basePrice * 0.7); // 30% discount for seniors
        }

        return basePrice;
    }

    private static void updatePrice() {
        String airline = (String)airlineSelection.getSelectedItem();
        String travelClass = (String)classSelection.getSelectedItem();

        int basePrice;
        switch (airline) {
            case "AirBlue (PA-406)" -> basePrice = 33500;
            case "AirBlue (PA-402)" -> basePrice = 36045;
            case "Fly Jinnah (9P-846)" -> basePrice = 25943;
            default -> basePrice = 30000;
        }

        // Apply class multiplier
        switch (travelClass) {
            case "First Class" -> basePrice *= 2.5;
            case "Business" -> basePrice *= 1.5;
            default -> basePrice *= 1.0;
        }

        // Double the price for return tickets
        if (returnBtn.isSelected()) {
            basePrice *= 2;
        }

        // Update the price label with formatted price
        priceLabel.setText(String.format("Price: PKR %,d", basePrice));
    }
    private static String generateTicketNumber() {
        ticketCount++;
        return String.format("PK-%04d", ticketCount);
    }

    private static void sendEmail(String toEmail, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, EMAIL_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");

            Transport.send(message);
            JOptionPane.showMessageDialog(null,
                    "✈ Ticket confirmation sent to " + toEmail,
                    "Email Sent Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (MessagingException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Failed to send email confirmation!\n" + e.getMessage(),
                    "Email Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    private static String formatTicketDetails(String ticketNo, String departure,
                                              String destination, String travelClass,
                                              String name, String dob, String phone,
                                              String departureDate, String returnDate,
                                              String airline, String flightTime,
                                              String returnFlightTime, int price) {
        return String.format("""
    ║ Ticket Number: TICKET-%04d
    ║ From: %s
    ║ To: %s
    ║ Class: %s
    ║ Passenger: %s
    ║ Date of Birth: %s
    ║ Phone: %s
    ║ Departure Date: %s
    ║ Departure Time: %s
    ║ Return Date: %s
    ║ Return Time: %s
    ║ Airline: %s
    ║ Price: Rs. %d
    """, ticketCount, departure, destination, travelClass,
                name, dob, phone, departureDate, flightTime,
                returnDate, returnFlightTime, airline, price);
    }
    // Custom UI Components
    private static class GradientPanel extends JPanel {
        private final Color gradientStart;
        private final Color gradientEnd;

        public GradientPanel(Color start, Color end) {
            this.gradientStart = start;
            this.gradientEnd = end;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, gradientStart, w, h, gradientEnd);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
            g2d.dispose();
        }
    }

    private static class ShadowBorder extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillRect(x + 2, y + 2, width - 4, height - 4);
            g2d.dispose();
        }
    }
}