import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;

public class ImageConverterApp extends JFrame {

    private JButton selectFilesButton;
    private JButton startButton;
    private JButton cancelButton;
    private JProgressBar progressBar;
    private JLabel statusBar;
    private JFileChooser fileChooser;
    private File[] selectedFiles;
    private ExecutorService executorService;
    private SwingWorker<Void, String> worker;

    public ImageConverterApp() {
        initUI();
        executorService = Executors.newFixedThreadPool(4); // Thread pool for concurrent conversions
    }

    private void initUI() {
        setTitle("Image Converter");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        selectFilesButton = new JButton("Select Files");
        startButton = new JButton("Start Conversion");
        cancelButton = new JButton("Cancel");

        selectFilesButton.addActionListener(new FileSelectionListener());
        startButton.addActionListener(new StartButtonListener());
        cancelButton.addActionListener(new CancelButtonListener());

        panel.add(selectFilesButton);
        panel.add(startButton);
        panel.add(cancelButton);

        add(panel, BorderLayout.NORTH);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        add(progressBar, BorderLayout.CENTER);

        statusBar = new JLabel("Status: Ready");
        add(statusBar, BorderLayout.SOUTH);

        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png");
        fileChooser.setFileFilter(filter);
    }

    private void resizeAndSaveImage(File inputFile, String outputDir) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputFile);
        int targetWidth = originalImage.getWidth() / 2;
        int targetHeight = originalImage.getHeight() / 2;
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, originalImage.getType());

        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();

        String outputFileName = outputDir + File.separator + "resized_" + inputFile.getName();
        ImageIO.write(resizedImage, "jpg", new File(outputFileName));
    }

    private class FileSelectionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFiles = fileChooser.getSelectedFiles();
                statusBar.setText("Status: " + selectedFiles.length + " files selected");
            }
        }
    }

    private class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedFiles == null || selectedFiles.length == 0) {
                JOptionPane.showMessageDialog(null, "Please select files to convert.");
                return;
            }

            worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    progressBar.setMaximum(selectedFiles.length);
                    String outputDir = "resized_images";
                    try {
                        Files.createDirectories(Path.of(outputDir));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    for (int i = 0; i < selectedFiles.length; i++) {
                        if (isCancelled()) {
                            break;
                        }
                        File file = selectedFiles[i];
                        try {
                            resizeAndSaveImage(file, outputDir);
                            publish("Converted: " + file.getName());
                            progressBar.setValue(i + 1);
                        } catch (IOException ex) {
                            publish("Error converting: " + file.getName());
                        }
                    }
                    return null;
                }

                @Override
                protected void process(List<String> chunks) {
                    statusBar.setText(chunks.get(chunks.size() - 1));
                }

                @Override
                protected void done() {
                    if (!isCancelled()) {
                        JOptionPane.showMessageDialog(null, "Conversion completed!");
                        statusBar.setText("Status: Conversion completed");
                    } else {
                        statusBar.setText("Status: Conversion cancelled");
                    }
                }
            };

            worker.execute();
        }
    }

    private class CancelButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (worker != null && !worker.isDone()) {
                worker.cancel(true);
            }
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ImageConverterApp app = new ImageConverterApp();
            app.setVisible(true);
        });
    }
}
