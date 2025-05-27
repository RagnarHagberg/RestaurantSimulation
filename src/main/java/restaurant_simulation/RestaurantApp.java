package restaurant_simulation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.metal.MetalSliderUI;
import java.awt.*;
import java.io.*;
import java.util.HashMap;

/**
 * Main application class responsible for starting the restaurant simulation.
 * Shows settings GUI first, then starts the simulation.
 */
public class RestaurantApp {
    static final String FILEPATH = "settings.txt";

    static final int delta = 10;
    private static RestaurantMain panel;
    private static final java.util.Map<String, JSlider> sliderMap = new java.util.LinkedHashMap<>();

    /**
     * Create and show the restaurant simulation GUI
     */
    private static void createAndShowSimulationGUI() {
        JFrame frame = new JFrame("Restaurant Simulation");
        frame.setSize(RestaurantMain.WIDTH, RestaurantMain.HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        panel = new RestaurantMain();
        frame.add(panel);

        frame.setVisible(true);
    }

    /**
     * Start the game loop using Swing Timer
     */
    private static void startGameLoop() {
        Timer gameTimer = new Timer(delta, e -> {
            RestaurantMain.update(delta);
            if (panel != null) {
                panel.repaint();
            }
        });
        gameTimer.start();
    }

    /**
     * Create and show the settings GUI
     */
    private static void createAndShowSettingsGUI() {
        JFrame frame = new JFrame("Restaurant Settings");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 850);
        frame.setLocationRelativeTo(null);

        Color primaryBlue = new Color(0x1E88E5);
        Color secondaryBlue = new Color(0x90CAF9);
        Color white = Color.WHITE;

        JPanel panel = new JPanel();
        panel.setBackground(primaryBlue);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel titleLabel = new JLabel("Settings", SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setForeground(white);

        JLabel descriptionLabel = new JLabel("Fine tune the workings of your restaurant", SwingConstants.CENTER);
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        descriptionLabel.setForeground(white);
        descriptionLabel.setBorder(new EmptyBorder(10, 0, 30, 0));

        panel.add(titleLabel);
        panel.add(descriptionLabel);

        // Add sliders with different ranges
        JPanel sliderContainerPanel = new RoundedPanel(25);
        sliderContainerPanel.setLayout(new BoxLayout(sliderContainerPanel, BoxLayout.Y_AXIS));
        sliderContainerPanel.setBackground(secondaryBlue);
        sliderContainerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        sliderContainerPanel.add(createSliderPanel("Dish price", 1, 5, 3, secondaryBlue, primaryBlue, white));
        sliderContainerPanel.add(createSliderPanel("Amount of waiters", 1, 5, 1, secondaryBlue, primaryBlue, white));
        sliderContainerPanel.add(createSliderPanel("Amount of tables", 1, 8, 2, secondaryBlue, primaryBlue, white));
        sliderContainerPanel.add(createSliderPanel("Rows of tables", 1, 4, 1, secondaryBlue, primaryBlue, white));
        sliderContainerPanel.add(createSliderPanel("Chef speed", 1, 5, 3, secondaryBlue, primaryBlue, white));
        sliderContainerPanel.add(createSliderPanel("Customers per table", 1, 10, 5, secondaryBlue, primaryBlue, white));

        panel.add(sliderContainerPanel);

        JButton startButton = new JButton("Start simulation");
        startButton.addActionListener(e -> {
            // Save settings to file
            saveSettings(FILEPATH);
            applySettingsToSimulationData(FILEPATH);

            // Close settings window
            frame.dispose();

            // Start the restaurant simulation
            SwingUtilities.invokeLater(() -> {
                createAndShowSimulationGUI();
                RestaurantMain.setupRestaurant();
                startGameLoop();
            });
        });

        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        startButton.setBackground(secondaryBlue);
        startButton.setForeground(white);
        startButton.setFocusPainted(false);
        startButton.setMaximumSize(new Dimension(200, 50));
        startButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.add(Box.createVerticalStrut(30));
        panel.add(startButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    /**
     * Create a slider panel with the specified parameters
     */
    private static JPanel createSliderPanel(String label, int min, int max, int initial,
                                            Color bgColor, Color trackColor, Color textColor) {
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
        sliderPanel.setBackground(bgColor);
        sliderPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel sliderLabel = new JLabel(label);
        sliderLabel.setForeground(textColor);
        sliderLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSlider slider = new JSlider(min, max, initial);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing((max - min) / (max/2));
        slider.setBackground(bgColor);
        slider.setForeground(textColor);
        slider.setAlignmentX(Component.CENTER_ALIGNMENT);
        slider.setMaximumSize(new Dimension(500, 60));

        slider.setUI(new MetalSliderUI() {
            @Override
            public void paintTrack(Graphics g) {
                g.setColor(trackColor);
                g.fillRect(trackRect.x, trackRect.y + trackRect.height / 2 - 2, trackRect.width, 4);
            }
        });

        slider.addChangeListener(e -> System.out.println(label + " = " + slider.getValue()));

        sliderMap.put(label, slider);

        sliderPanel.add(sliderLabel);
        sliderPanel.add(slider);
        return sliderPanel;
    }

    /**
     * Apply saved settings to the simulationData singleton before the simulation starts.
     */

    public static void applySettingsToSimulationData(String filepath){
        HashMap<String, Integer> settings = loadSettings(filepath);

        // print out keys in data for debugging purposes
        //for(String key : sliderMap.keySet()){
        //    System.out.println(key);
        //}

        SimulationData.getInstance().setAMOUNT_OF_TABLES(settings.get("Amount of tables"));
        SimulationData.getInstance().setAMOUNT_OF_WAITERS(settings.get("Amount of waiters"));
        SimulationData.getInstance().setGUESTS_PER_TABLE(settings.get("Customers per table"));
        SimulationData.getInstance().setROWS_OF_TABLES(settings.get("Rows of tables"));
        SimulationData.getInstance().setCHEF_SPEED_MULTIPLIER(settings.get("Chef speed"));
        SimulationData.getInstance().setDISH_PRICE_MULTIPLIER(settings.get("Dish price"));

    }

    /**
     * Save settings to a file
     */
    public static void saveSettings(String filename){
            // Save settings to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                for (String key : sliderMap.keySet()) {
                    int value = sliderMap.get(key).getValue();
                    writer.write(key.replaceAll(" ", "_") + "=" + value);
                    writer.newLine();
                }
                System.out.println("Settings saved to " + filename);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
    }


    /**
     * Load settings from a file
     */
    public static HashMap<String, Integer> loadSettings(String filename) {
        HashMap<String, Integer> settings = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || !line.contains("=")) continue;
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0].replaceAll("_", " ").trim();
                    int value = Integer.parseInt(parts[1].trim());
                    settings.put(key, value);
                }
            }
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
        return settings;
    }

    /**
     * Main entry point for the restaurant simulation application
     */
    public static void main(String[] args) {
        // Show settings GUI first
        SwingUtilities.invokeLater(() -> {
            createAndShowSettingsGUI();
        });
    }
}