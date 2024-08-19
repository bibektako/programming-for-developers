import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class DeliveryRouteOptimizer extends JFrame {
    private JTextArea deliveryList;
    private JComboBox<String> algorithmSelector;
    private JTextField vehicleCapacity;
    private JTextField maxDistance;
    private JButton optimizeButton;
    private JButton importDeliveriesButton;
    private RouteVisualization routeVisualization;
    private OptimizationManager optimizationManager;

    public DeliveryRouteOptimizer() {
        super("Delivery Route Optimizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);  // Maximize to full screen width
        setLayout(new BorderLayout());

        optimizationManager = new OptimizationManager();

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        inputPanel.setBackground(Color.LIGHT_GRAY);
        inputPanel.setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Import Deliveries Button
        importDeliveriesButton = new JButton("Import Deliveries");
        styleButton(importDeliveriesButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        inputPanel.add(importDeliveriesButton, gbc);

        // Delivery List
        deliveryList = new JTextArea(15, 30);
        deliveryList.setLineWrap(true);
        deliveryList.setFont(new Font("Arial", Font.PLAIN, 16));
        deliveryList.setBorder(new RoundedBorder(10));
        JScrollPane scrollPane = new JScrollPane(deliveryList);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        inputPanel.add(new JLabel("Delivery Points:"), gbc);

        gbc.gridy++;
        inputPanel.add(scrollPane, gbc);

        // Algorithm Selector
        algorithmSelector = new JComboBox<>(new String[]{"Nearest Neighbor", "Genetic Algorithm", "Simulated Annealing"});
        algorithmSelector.setFont(new Font("Arial", Font.PLAIN, 16));
        algorithmSelector.setBorder(new RoundedBorder(10));

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(new JLabel("Select Algorithm:"), gbc);

        gbc.gridy++;
        inputPanel.add(algorithmSelector, gbc);

        // Vehicle Capacity
        vehicleCapacity = new JTextField(20);
        vehicleCapacity.setFont(new Font("Arial", Font.PLAIN, 16));
        vehicleCapacity.setBorder(new RoundedBorder(10));

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(new JLabel("Vehicle Capacity:"), gbc);

        gbc.gridy++;
        inputPanel.add(vehicleCapacity, gbc);

        // Max Distance
        maxDistance = new JTextField(20);
        maxDistance.setFont(new Font("Arial", Font.PLAIN, 16));
        maxDistance.setBorder(new RoundedBorder(10));

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(new JLabel("Max Distance:"), gbc);

        gbc.gridy++;
        inputPanel.add(maxDistance, gbc);

        // Optimize Route Button
        optimizeButton = new JButton("Optimize Route");
        styleButton(optimizeButton);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(optimizeButton, gbc);

        add(inputPanel, BorderLayout.CENTER);

        // Visualization Panel
        routeVisualization = new RouteVisualization();
        add(routeVisualization, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(0, 102, 204));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBorder(new RoundedBorder(10));
        button.setFocusPainted(false);
    }

    private void optimizeRoute() {
        String selectedAlgorithm = (String) algorithmSelector.getSelectedItem();
        RouteOptimizer optimizer;

        switch (selectedAlgorithm) {
            case "Nearest Neighbor":
                optimizer = new NearestNeighborOptimizer();
                break;
            case "Genetic Algorithm":
                optimizer = new GeneticAlgorithmOptimizer();
                break;
            case "Simulated Annealing":
                optimizer = new SimulatedAnnealingOptimizer();
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid algorithm selection");
                return;
        }

        List<DeliveryPoint> deliveryPoints = parseDeliveryPoints(deliveryList.getText());
        double capacity = Double.parseDouble(vehicleCapacity.getText());
        double maxDist = Double.parseDouble(maxDistance.getText());

        SwingWorker<List<DeliveryPoint>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<DeliveryPoint> doInBackground() throws Exception {
                return optimizationManager.optimizeRoute(optimizer, deliveryPoints, capacity, maxDist);
            }

            @Override
            protected void done() {
                try {
                    List<DeliveryPoint> optimizedRoute = get();
                    routeVisualization.setRoute(optimizedRoute);
                    routeVisualization.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(DeliveryRouteOptimizer.this,
                            "Error during optimization: " + ex.getMessage());
                }
            }
        };

        worker.execute();
    }

    private List<DeliveryPoint> parseDeliveryPoints(String text) {
        // Parse the text input into a list of DeliveryPoint objects
        List<DeliveryPoint> points = new ArrayList<>();
        String[] lines = text.split("\n");
        for (String line : lines) {
            points.add(new DeliveryPoint(line, 0.1, 0.1, 1));  // Example values
        }
        return points;
    }

    private void importDeliveries() {
        // Example implementation to add test delivery points to the text area
        deliveryList.setText("Point 1\nPoint 2\nPoint 3\nPoint 4\nPoint 5");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DeliveryRouteOptimizer().setVisible(true);
        });
    }

    // DeliveryPoint class
    static class DeliveryPoint {
        private String address;
        private double latitude;
        private double longitude;
        private int priority;

        public DeliveryPoint(String address, double latitude, double longitude, int priority) {
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
            this.priority = priority;
        }

        // Getters
        public String getAddress() { return address; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public int getPriority() { return priority; }
    }

    // RouteOptimizer interface
    interface RouteOptimizer {
        List<DeliveryPoint> optimizeRoute(List<DeliveryPoint> deliveryPoints, double vehicleCapacity, double maxDistance);
    }

    // NearestNeighborOptimizer class
    static class NearestNeighborOptimizer implements RouteOptimizer {
        @Override
        public List<DeliveryPoint> optimizeRoute(List<DeliveryPoint> deliveryPoints, double vehicleCapacity, double maxDistance) {
            // Implement Nearest Neighbor algorithm
            List<DeliveryPoint> optimizedRoute = new ArrayList<>(deliveryPoints);
            Collections.shuffle(optimizedRoute);  // Just for demonstration
            return optimizedRoute;
        }
    }

    // GeneticAlgorithmOptimizer class
    static class GeneticAlgorithmOptimizer implements RouteOptimizer {
        @Override
        public List<DeliveryPoint> optimizeRoute(List<DeliveryPoint> deliveryPoints, double vehicleCapacity, double maxDistance) {
            // Implement Genetic Algorithm optimization
            return new ArrayList<>(deliveryPoints);
        }
    }

    // SimulatedAnnealingOptimizer class
    static class SimulatedAnnealingOptimizer implements RouteOptimizer {
        @Override
        public List<DeliveryPoint> optimizeRoute(List<DeliveryPoint> deliveryPoints, double vehicleCapacity, double maxDistance) {
            // Implement Simulated Annealing optimization
            return new ArrayList<>(deliveryPoints);
        }
    }

    // OptimizationManager class
    static class OptimizationManager {
        public List<DeliveryPoint> optimizeRoute(RouteOptimizer optimizer, List<DeliveryPoint> deliveryPoints,
                                                 double vehicleCapacity, double maxDistance) {
            return optimizer.optimizeRoute(deliveryPoints, vehicleCapacity, maxDistance);
        }
    }

    // RouteVisualization class
    static class RouteVisualization extends JPanel {
        private List<DeliveryPoint> route;

        public void setRoute(List<DeliveryPoint> route) {
            this.route = route;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (route == null || route.isEmpty()) return;

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(2));
            for (int i = 1; i < route.size(); i++) {
                DeliveryPoint prev = route.get(i - 1);
                DeliveryPoint curr = route.get(i);
                g2d.drawLine((int) prev.getLongitude(), (int) prev.getLatitude(),
                        (int) curr.getLongitude(), (int) curr.getLatitude());
            }
        }
    }

    // RoundedBorder class
    static class RoundedBorder implements Border {
        private int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius + 2, radius + 2, radius + 2, radius + 2);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(Color.GRAY);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }
    }
}

