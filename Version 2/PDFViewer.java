import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.ImageType;


public class PDFViewer extends JDialog {
    private Content content;
    private ContentManager manager;
    private PDDocument document;
    private PDFRenderer renderer;


    private JLabel imageLabel;
    private JLabel statusLabel;
    private JButton btnFirst, btnPrev, btnNext, btnLast;
    private JButton btnZoomIn, btnZoomOut, btnFit;


    private int currentPage = 0;
    private int totalPages = 0;
    private float zoomLevel = 1.0f;
    private boolean isLoaded = false;
    private float systemDpiScale = 1.0f;


    // Modern colors matching GUI
    private final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private final Color BORDER_COLOR = new Color(222, 226, 230);
    private final Color ACCENT_COLOR = new Color(13, 110, 253);


    public PDFViewer(JFrame parent, Content content, ContentManager manager) {
        super(parent, content.getTitle(), false);
        this.content = content;
        this.manager = manager;


        // Detect system DPI scaling for Windows
        detectSystemDPI();


        setSize(1050, 850);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);


        setupUI();


        SwingUtilities.invokeLater(() -> {
            loadAndDisplayPDF();
        });
    }


    /**
     * Detect Windows DPI scaling (100%, 125%, 150%, 200%)
     * This is critical for sharp PDF rendering on high-DPI displays
     */
    private void detectSystemDPI() {
        try {
            // Get system DPI
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            int screenDPI = toolkit.getScreenResolution();
            systemDpiScale = screenDPI / 96.0f; // 96 DPI is baseline (100% scaling)


            System.out.println("System DPI: " + screenDPI + " (Scale: " + systemDpiScale + "x)");


            // Clamp to reasonable values
            if (systemDpiScale < 1.0f) systemDpiScale = 1.0f;
            if (systemDpiScale > 3.0f) systemDpiScale = 3.0f;
        } catch (Exception e) {
            systemDpiScale = 1.0f;
            System.out.println("Could not detect DPI, using default 1.0x");
        }
    }


    private void setupUI() {
        setLayout(new BorderLayout());


        // Top toolbar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setOpaque(true);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));


        JLabel titleLabel = new JLabel(content.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setOpaque(false);


        topBar.add(titleLabel, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);


        // Center - PDF display with anti-aliasing
        imageLabel = new JLabel("Loading PDF...", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                // Enable anti-aliasing for smoother rendering
                Graphics2D g2d = (Graphics2D) g;


                // Get desktop rendering hints for best quality on Windows
                Toolkit tk = Toolkit.getDefaultToolkit();
                @SuppressWarnings("unchecked")
                java.util.Map<Object, Object> desktopHints =
                        (java.util.Map<Object, Object>) tk.getDesktopProperty("awt.font.desktophints");


                if (desktopHints != null) {
                    g2d.addRenderingHints(desktopHints);
                }


                // Additional rendering hints for quality
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);


                super.paintComponent(g2d);
            }
        };
        imageLabel.setBackground(Color.WHITE);
        imageLabel.setOpaque(true);
        imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        imageLabel.setForeground(TEXT_SECONDARY);


        JScrollPane scrollPane = new JScrollPane(imageLabel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);


        add(scrollPane, BorderLayout.CENTER);


        // Bottom toolbar
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setOpaque(true);
        bottomBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));


        // Navigation panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        navPanel.setBackground(Color.WHITE);
        navPanel.setOpaque(false);


        btnFirst = createButton("First");
        btnPrev = createButton("Prev");


        statusLabel = new JLabel("Page 0 / 0");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        statusLabel.setForeground(TEXT_PRIMARY);
        statusLabel.setOpaque(false);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
        statusLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        statusLabel.setToolTipText("Click to jump to page");


        statusLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isLoaded) {
                    showGoToPageDialog();
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isLoaded) {
                    statusLabel.setForeground(ACCENT_COLOR);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setForeground(TEXT_PRIMARY);
            }
        });


        btnNext = createButton("Next");
        btnLast = createButton("Last");


        navPanel.add(btnFirst);
        navPanel.add(btnPrev);
        navPanel.add(Box.createHorizontalStrut(10));
        navPanel.add(statusLabel);
        navPanel.add(Box.createHorizontalStrut(10));
        navPanel.add(btnNext);
        navPanel.add(btnLast);


        // Zoom panel
        JPanel zoomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        zoomPanel.setBackground(Color.WHITE);
        zoomPanel.setOpaque(false);


        btnZoomOut = createButton("Zoom -");
        btnFit = createButton("Fit");
        btnZoomIn = createButton("Zoom +");


        zoomPanel.add(btnZoomOut);
        zoomPanel.add(btnFit);
        zoomPanel.add(btnZoomIn);


        bottomBar.add(navPanel, BorderLayout.CENTER);
        bottomBar.add(zoomPanel, BorderLayout.EAST);


        add(bottomBar, BorderLayout.SOUTH);


        // Button actions
        btnFirst.addActionListener(e -> goToPage(0));
        btnPrev.addActionListener(e -> goToPage(currentPage - 1));
        btnNext.addActionListener(e -> goToPage(currentPage + 1));
        btnLast.addActionListener(e -> goToPage(totalPages - 1));


        btnZoomIn.addActionListener(e -> changeZoom(0.25f));
        btnZoomOut.addActionListener(e -> changeZoom(-0.25f));
        btnFit.addActionListener(e -> {
            zoomLevel = 1.0f;
            renderPage();
        });


        // Keyboard shortcuts
        addKeyboardShortcuts();


        // Initially disable buttons
        setButtonsEnabled(false);
    }


    private void addKeyboardShortcuts() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() != KeyEvent.KEY_PRESSED || !isLoaded) return false;
                if (!PDFViewer.this.isActive()) return false;


                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_PAGE_UP:
                        if (currentPage > 0) {
                            goToPage(currentPage - 1);
                        }
                        return true;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_PAGE_DOWN:
                        if (currentPage < totalPages - 1) {
                            goToPage(currentPage + 1);
                        }
                        return true;
                    case KeyEvent.VK_HOME:
                        goToPage(0);
                        return true;
                    case KeyEvent.VK_END:
                        goToPage(totalPages - 1);
                        return true;
                    case KeyEvent.VK_G:
                        if (e.isControlDown()) {
                            showGoToPageDialog();
                            return true;
                        }
                        break;
                }
                return false;
            }
        });
    }


    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(ACCENT_COLOR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(85, 36));
        btn.setOpaque(true);


        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(new Color(10, 88, 202));
                }
            }
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(ACCENT_COLOR);
                } else {
                    btn.setBackground(TEXT_SECONDARY);
                }
            }
        });


        return btn;
    }


    private void showGoToPageDialog() {
        if (!isLoaded || totalPages == 0) {
            return;
        }


        JDialog dialog = new JDialog(this, "Jump to Page", true);
        dialog.setSize(380, 180);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(0, 0));
        dialog.getContentPane().setBackground(Color.WHITE);


        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 25));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setOpaque(true);


        JLabel label = new JLabel("Enter page number (1-" + totalPages + "):");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_PRIMARY);
        label.setOpaque(false);


        JTextField pageField = new JTextField(10);
        pageField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        pageField.setForeground(TEXT_PRIMARY);
        pageField.setCaretColor(TEXT_PRIMARY);
        pageField.setOpaque(true);
        pageField.setText(String.valueOf(currentPage + 1));
        pageField.selectAll();
        pageField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));


        inputPanel.add(label);
        inputPanel.add(pageField);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setOpaque(true);


        JButton okButton = new JButton("Go");
        okButton.setBackground(ACCENT_COLOR);
        okButton.setForeground(Color.WHITE);
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        okButton.setFocusPainted(false);
        okButton.setBorderPainted(false);
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.setOpaque(true);
        okButton.setPreferredSize(new Dimension(90, 36));


        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.WHITE);
        cancelButton.setForeground(TEXT_PRIMARY);
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setOpaque(true);
        cancelButton.setPreferredSize(new Dimension(90, 36));


        okButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                okButton.setBackground(new Color(10, 88, 202));
            }
            public void mouseExited(MouseEvent e) {
                okButton.setBackground(ACCENT_COLOR);
            }
        });


        cancelButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                cancelButton.setBackground(new Color(248, 249, 250));
            }
            public void mouseExited(MouseEvent e) {
                cancelButton.setBackground(Color.WHITE);
            }
        });


        okButton.addActionListener(e -> {
            try {
                int pageNum = Integer.parseInt(pageField.getText().trim());
                if (pageNum >= 1 && pageNum <= totalPages) {
                    goToPage(pageNum - 1);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Please enter a number between 1 and " + totalPages,
                            "Invalid Page",
                            JOptionPane.WARNING_MESSAGE);
                    pageField.selectAll();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter a valid number",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                pageField.selectAll();
            }
        });


        cancelButton.addActionListener(e -> dialog.dispose());
        pageField.addActionListener(e -> okButton.doClick());


        dialog.getRootPane().registerKeyboardAction(
                e -> dialog.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );


        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);


        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);


        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                pageField.requestFocusInWindow();
            }
        });


        dialog.setVisible(true);
    }


    private void loadAndDisplayPDF() {
        new Thread(() -> {
            try {
                File pdfFile = new File(content.getFilePath());


                if (!pdfFile.exists()) {
                    SwingUtilities.invokeLater(() -> {
                        imageLabel.setText("<html><center>" +
                                "<h2 style='color:#dc3545;'>File not found</h2>" +
                                "<p style='color:#6c757d;'>" + content.getFilePath() + "</p>" +
                                "</center></html>");
                        isLoaded = false;
                    });
                    return;
                }


                document = Loader.loadPDF(pdfFile);
                renderer = new PDFRenderer(document);
                totalPages = document.getNumberOfPages();


                SwingUtilities.invokeLater(() -> {
                    loadSavedProgress();
                    isLoaded = true;
                    System.out.println("PDF loaded - Pages: " + totalPages +
                            ", Current: " + (currentPage + 1) +
                            ", DPI Scale: " + systemDpiScale);


                    updateStatus();
                    setButtonsEnabled(true);
                    renderPage();
                });


            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    imageLabel.setText("<html><center>" +
                            "<h2 style='color:#dc3545;'>Error loading PDF</h2>" +
                            "<p style='color:#6c757d;'>" + e.getMessage() + "</p>" +
                            "</center></html>");
                    isLoaded = false;
                });
            }
        }).start();
    }


    private void renderPage() {
        if (renderer == null || currentPage < 0 || currentPage >= totalPages || !isLoaded) {
            return;
        }


        final int pageToRender = currentPage;


        imageLabel.setIcon(null);
        imageLabel.setText("Rendering page " + (pageToRender + 1) + "...");


        new Thread(() -> {
            try {
                // CRITICAL FIX FOR WINDOWS DPI SCALING
                // Calculate effective DPI: base 96 DPI * system scale * user zoom
                // This ensures sharp rendering on high-DPI displays (125%, 150%, 200%)
                float baseDPI = 96f;
                float effectiveDPI = baseDPI * systemDpiScale * zoomLevel;


                // Clamp DPI to reasonable range
                // Too low: blurry, Too high: memory issues
                effectiveDPI = Math.max(72f, Math.min(effectiveDPI, 400f));


                System.out.println("Rendering page " + (pageToRender + 1) +
                        " at " + effectiveDPI + " DPI" +
                        " (System: " + systemDpiScale + "x, Zoom: " + zoomLevel + "x)");


                // Use RGB image type for better quality on Windows
                // ARGB can cause blurry rendering in some cases
                BufferedImage image = renderer.renderImageWithDPI(
                        pageToRender,
                        effectiveDPI,
                        ImageType.RGB
                );


                // Apply additional anti-aliasing to the rendered image
                BufferedImage smoothImage = applyAntiAliasing(image);
                ImageIcon icon = new ImageIcon(smoothImage);


                SwingUtilities.invokeLater(() -> {
                    imageLabel.setText("");
                    imageLabel.setIcon(icon);
                });


            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    imageLabel.setText("<html><center>" +
                            "<h2 style='color:#dc3545;'>Error rendering page</h2>" +
                            "<p style='color:#6c757d;'>" + e.getMessage() + "</p>" +
                            "</center></html>");
                });
            }
        }).start();
    }


    /**
     * Apply additional anti-aliasing to improve rendering quality on Windows
     * This helps reduce jagged edges and improve text clarity
     */
    private BufferedImage applyAntiAliasing(BufferedImage source) {
        // Create a new image with the same dimensions
        BufferedImage smoothed = new BufferedImage(
                source.getWidth(),
                source.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );


        Graphics2D g2d = smoothed.createGraphics();


        // Apply high-quality rendering hints
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);


        // Draw the source image with anti-aliasing
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();


        return smoothed;
    }


    private void goToPage(int page) {
        if (page >= 0 && page < totalPages && isLoaded) {
            currentPage = page;
            updateStatus();
            updateNavigationButtons();
            renderPage();
            saveProgress();
        }
    }


    private void changeZoom(float delta) {
        if (!isLoaded) return;
        zoomLevel += delta;
        zoomLevel = Math.max(0.5f, Math.min(3.0f, zoomLevel));
        renderPage();
    }


    private void updateStatus() {
        statusLabel.setText("Page " + (currentPage + 1) + " / " + totalPages);
        updateNavigationButtons();
    }


    private void updateNavigationButtons() {
        if (!isLoaded) return;


        boolean hasPrev = currentPage > 0;
        boolean hasNext = currentPage < totalPages - 1;


        btnFirst.setEnabled(hasPrev);
        btnPrev.setEnabled(hasPrev);
        btnNext.setEnabled(hasNext);
        btnLast.setEnabled(hasNext);


        // Update button colors based on enabled state
        btnFirst.setBackground(hasPrev ? ACCENT_COLOR : TEXT_SECONDARY);
        btnPrev.setBackground(hasPrev ? ACCENT_COLOR : TEXT_SECONDARY);
        btnNext.setBackground(hasNext ? ACCENT_COLOR : TEXT_SECONDARY);
        btnLast.setBackground(hasNext ? ACCENT_COLOR : TEXT_SECONDARY);
    }


    private void setButtonsEnabled(boolean enabled) {
        btnFirst.setEnabled(enabled);
        btnPrev.setEnabled(enabled);
        btnNext.setEnabled(enabled);
        btnLast.setEnabled(enabled);
        btnZoomIn.setEnabled(enabled);
        btnZoomOut.setEnabled(enabled);
        btnFit.setEnabled(enabled);


        if (!enabled) {
            btnFirst.setBackground(TEXT_SECONDARY);
            btnPrev.setBackground(TEXT_SECONDARY);
            btnNext.setBackground(TEXT_SECONDARY);
            btnLast.setBackground(TEXT_SECONDARY);
        }
    }


    @Override
    public void dispose() {
        if (isLoaded) {
            saveProgress();
        }


        if (document != null) {
            try {
                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.dispose();
    }


    private void loadSavedProgress() {
        ReadingHistory history = manager.getReadingHistory(content.getContentId());
        if (history != null && totalPages > 0) {
            float progress = history.getProgress();
            int savedPage = Math.round((progress / 100.0f) * (totalPages - 1));
            currentPage = Math.max(0, Math.min(savedPage, totalPages - 1));


            System.out.println("Loaded progress: " + progress + "% -> Page " + (currentPage + 1));
        } else {
            currentPage = 0;
        }
    }


    private void saveProgress() {
        if (!isLoaded || totalPages == 0) return;


        float progress = ((float) currentPage / (totalPages - 1)) * 100.0f;


        if (totalPages == 1) {
            progress = 100.0f;
        }


        manager.updateProgress(content.getContentId(), progress);
        System.out.println("Saved: Page " + (currentPage + 1) + "/" + totalPages +
                " = " + String.format("%.1f%%", progress));
    }
}


