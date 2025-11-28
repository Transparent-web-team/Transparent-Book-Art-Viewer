import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;


public class GUI extends JFrame {
    private ContentManager manager;
    private JPanel contentListPanel;
    private JTextField searchField;
    private JComboBox<String> sortComboBox;
    private List<Content> currentDisplayList;


    // Modern color scheme - optimized for Windows
    private final Color PRIMARY_COLOR = new Color(33, 33, 33);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private final Color BORDER_COLOR = new Color(222, 226, 230);
    private final Color ACCENT_COLOR = new Color(13, 110, 253);
    private final Color FAVORITE_COLOR = new Color(220, 53, 69);


    public GUI() {
        manager = new ContentManager();


        // Enable anti-aliasing for better text rendering on Windows
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");


        setTitle("Transparent PDF Reader");
        setSize(950, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        // Set background to opaque white to prevent transparency issues
        getContentPane().setBackground(BACKGROUND_COLOR);


        // Set application icon
        try {
            java.net.URL iconURL = getClass().getResource("/app-icon.png");
            if (iconURL != null) {
                Image icon = new ImageIcon(iconURL).getImage();
                setIconImage(icon);
            } else {
                Image icon = new ImageIcon("app-icon.png").getImage();
                setIconImage(icon);
            }
        } catch (Exception e) {
            System.out.println("Could not load icon: " + e.getMessage());
        }


        initComponents();
        refreshContentList();


        setVisible(true);
    }


    private void initComponents() {
        setLayout(new BorderLayout());


        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);


        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);


        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }


    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(Color.WHITE);
        panel.setOpaque(true);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));


        // Title with modern styling
        JLabel titleLabel = new JLabel("Transparent");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setOpaque(false);


        // Search and sort panel
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);


        // Search field with modern styling
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(280, 38));
        searchField.setBackground(Color.WHITE);
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setCaretColor(TEXT_PRIMARY);
        searchField.setOpaque(true);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));


        // Sort dropdown
        String[] sortOptions = {"Sort by Title", "Sort by Author", "Sort by Favourite"};
        sortComboBox = new JComboBox<>(sortOptions);
        sortComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sortComboBox.setPreferredSize(new Dimension(180, 38));
        sortComboBox.setBackground(Color.WHITE);
        sortComboBox.setForeground(TEXT_PRIMARY);
        sortComboBox.setOpaque(true);
        ((JComponent) sortComboBox.getRenderer()).setOpaque(true);


        rightPanel.add(searchField);
        rightPanel.add(sortComboBox);


        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);


        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                performSearch();
            }
        });


        sortComboBox.addActionListener(e -> performSearch());


        return panel;
    }


    private JPanel createCenterPanel() {
        contentListPanel = new JPanel();
        contentListPanel.setLayout(new BoxLayout(contentListPanel, BoxLayout.Y_AXIS));
        contentListPanel.setBackground(BACKGROUND_COLOR);
        contentListPanel.setOpaque(true);
        contentListPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));


        JScrollPane scrollPane = new JScrollPane(contentListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setOpaque(true);


        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BACKGROUND_COLOR);
        wrapper.setOpaque(true);
        wrapper.add(scrollPane, BorderLayout.CENTER);


        return wrapper;
    }


    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 18));
        panel.setBackground(Color.WHITE);
        panel.setOpaque(true);
        panel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, BORDER_COLOR));


        JButton addButton = new JButton("+ Add PDF");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        addButton.setBackground(ACCENT_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setPreferredSize(new Dimension(160, 45));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.setOpaque(true);


        addButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                addButton.setBackground(new Color(10, 88, 202));
            }
            public void mouseExited(MouseEvent e) {
                addButton.setBackground(ACCENT_COLOR);
            }
        });


        addButton.addActionListener(e -> showAddDialog());


        panel.add(addButton);


        return panel;
    }


    private void refreshContentList() {
        contentListPanel.removeAll();
        currentDisplayList = manager.getAllContents();


        if (currentDisplayList.isEmpty()) {
            JLabel emptyLabel = new JLabel("No PDFs yet. Click '+ Add PDF' to get started!");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            emptyLabel.setForeground(TEXT_SECONDARY);
            emptyLabel.setOpaque(false);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentListPanel.add(Box.createVerticalStrut(80));
            contentListPanel.add(emptyLabel);
        } else {
            for (Content content : currentDisplayList) {
                contentListPanel.add(createContentCard(content));
                contentListPanel.add(Box.createVerticalStrut(12));
            }
        }


        contentListPanel.revalidate();
        contentListPanel.repaint();
    }


    private JPanel createContentCard(Content content) {
        JPanel card = new JPanel(new BorderLayout(18, 0));
        card.setBackground(CARD_COLOR);
        card.setOpaque(true);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));


        ReadingHistory historyCheck = manager.getReadingHistory(content.getContentId());
        int cardHeight = (historyCheck != null && historyCheck.getProgress() > 0) ? 130 : 105;
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, cardHeight));


        // Favorite heart icon
        boolean isFav = manager.isFavorite(content.getContentId());
        JLabel favoriteIcon = new JLabel(isFav ? "\u2665" : "\u2661");
        favoriteIcon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 30));
        favoriteIcon.setForeground(isFav ? FAVORITE_COLOR : TEXT_SECONDARY);
        favoriteIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        favoriteIcon.setOpaque(false);


        favoriteIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                manager.toggleFavorite(content.getContentId());
                refreshContentList();
            }
            public void mouseEntered(MouseEvent e) {
                favoriteIcon.setForeground(FAVORITE_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                favoriteIcon.setForeground(manager.isFavorite(content.getContentId()) ?
                        FAVORITE_COLOR : TEXT_SECONDARY);
            }
        });


        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);


        JLabel titleLabel = new JLabel(content.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setOpaque(false);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);


        JLabel authorLabel = new JLabel("By " + content.getAuthor());
        authorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        authorLabel.setForeground(TEXT_SECONDARY);
        authorLabel.setOpaque(false);
        authorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);


        JLabel sizeLabel = new JLabel(content.getInfo().getSizeFormat() + " • " +
                content.getInfo().getDateAdded());
        sizeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sizeLabel.setForeground(new Color(134, 142, 150));
        sizeLabel.setOpaque(false);
        sizeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);


        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(authorLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(sizeLabel);


        // Progress indicator if reading
        ReadingHistory history = manager.getReadingHistory(content.getContentId());
        if (history != null && history.getProgress() > 0) {
            JLabel progressLabel = new JLabel(String.format("Progress: %.1f%%",
                    history.getProgress()));
            progressLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            progressLabel.setForeground(new Color(25, 135, 84));
            progressLabel.setOpaque(false);
            progressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(Box.createVerticalStrut(4));
            infoPanel.add(progressLabel);
        }


        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);


        JButton readButton = createModernButton("Read", ACCENT_COLOR);
        readButton.addActionListener(e -> openPDFViewer(content));


        JButton menuButton = createModernButton("...", TEXT_SECONDARY);
        menuButton.setPreferredSize(new Dimension(45, 36));
        menuButton.addActionListener(e -> showContentMenu(content, menuButton));


        buttonPanel.add(readButton);
        buttonPanel.add(menuButton);


        card.add(favoriteIcon, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.EAST);


        return card;
    }


    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(90, 36));
        button.setOpaque(true);


        Color darkerColor = bgColor.darker();
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(darkerColor);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });


        return button;
    }


    private void showContentMenu(Content content, Component invoker) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        menu.setBackground(Color.WHITE);


        JMenuItem infoItem = createMenuItem("View Info");
        infoItem.addActionListener(e -> showInfoDialog(content));


        JMenuItem favoriteItem = createMenuItem(
                manager.isFavorite(content.getContentId()) ?
                        "Remove from Favorites" : "Add to Favorites"
        );
        favoriteItem.addActionListener(e -> {
            manager.toggleFavorite(content.getContentId());
            refreshContentList();
        });


        JMenuItem deleteItem = createMenuItem("Delete");
        deleteItem.setForeground(FAVORITE_COLOR);
        deleteItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this PDF?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                manager.removeContent(content.getContentId());
                refreshContentList();
            }
        });


        menu.add(infoItem);
        menu.add(favoriteItem);
        menu.addSeparator();
        menu.add(deleteItem);


        menu.show(invoker, 0, invoker.getHeight());
    }


    private JMenuItem createMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        item.setBackground(Color.WHITE);
        item.setForeground(TEXT_PRIMARY);
        item.setOpaque(true);
        item.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return item;
    }


    private void showInfoDialog(Content content) {
        JDialog dialog = new JDialog(this, "PDF Information", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(0, 0));
        dialog.getContentPane().setBackground(Color.WHITE);


        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setOpaque(true);


        addInfoRow(infoPanel, "ID:", content.getContentId());
        addInfoRow(infoPanel, "Title:", content.getTitle());
        addInfoRow(infoPanel, "Author:", content.getAuthor());
        addInfoRow(infoPanel, "Size:", content.getInfo().getSizeFormat());
        addInfoRow(infoPanel, "Format:", content.getInfo().getFormat());
        addInfoRow(infoPanel, "Date Added:", content.getInfo().getDateAdded().toString());
        addInfoRow(infoPanel, "File Path:", content.getFilePath());


        ReadingHistory history = manager.getReadingHistory(content.getContentId());
        if (history != null) {
            addInfoRow(infoPanel, "Progress:", String.format("%.1f%%", history.getProgress()));
        }


        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setBackground(TEXT_SECONDARY);
        closeButton.setForeground(Color.WHITE);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setOpaque(true);
        closeButton.setPreferredSize(new Dimension(100, 38));


        closeButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                closeButton.setBackground(TEXT_SECONDARY.darker());
            }
            public void mouseExited(MouseEvent e) {
                closeButton.setBackground(TEXT_SECONDARY);
            }
        });
        closeButton.addActionListener(e -> dialog.dispose());


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setOpaque(true);
        buttonPanel.add(closeButton);


        JScrollPane scrollPane = new JScrollPane(infoPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);


        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);


        dialog.setVisible(true);
    }


    private void addInfoRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 8));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        row.setBackground(Color.WHITE);
        row.setOpaque(true);


        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComp.setForeground(TEXT_PRIMARY);
        labelComp.setOpaque(false);
        labelComp.setPreferredSize(new Dimension(110, 25));


        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueComp.setForeground(TEXT_SECONDARY);
        valueComp.setOpaque(false);


        row.add(labelComp);
        row.add(valueComp);
        panel.add(row);
    }


    private void showAddDialog() {
        JDialog dialog = new JDialog(this, "Add New PDF", true);
        dialog.setSize(550, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(0, 0));
        dialog.getContentPane().setBackground(Color.WHITE);


        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        formPanel.setBackground(Color.WHITE);
        formPanel.setOpaque(true);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);


        // File selection
        JLabel fileLabel = new JLabel("PDF File:");
        fileLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        fileLabel.setForeground(TEXT_PRIMARY);
        fileLabel.setOpaque(false);


        JTextField fileField = new JTextField();
        fileField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fileField.setEditable(false);
        fileField.setBackground(BACKGROUND_COLOR);
        fileField.setForeground(TEXT_PRIMARY);
        fileField.setOpaque(true);
        fileField.setPreferredSize(new Dimension(200, 38));
        fileField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));


        JButton browseButton = new JButton("Browse");
        browseButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        browseButton.setBackground(TEXT_SECONDARY);
        browseButton.setForeground(Color.WHITE);
        browseButton.setFocusPainted(false);
        browseButton.setBorderPainted(false);
        browseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        browseButton.setOpaque(true);
        browseButton.setPreferredSize(new Dimension(90, 38));


        browseButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                browseButton.setBackground(TEXT_SECONDARY.darker());
            }
            public void mouseExited(MouseEvent e) {
                browseButton.setBackground(TEXT_SECONDARY);
            }
        });


        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(fileLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(fileField, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(browseButton, gbc);


        // Title field
        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setOpaque(false);


        JTextField titleField = new JTextField();
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleField.setBackground(Color.WHITE);
        titleField.setForeground(TEXT_PRIMARY);
        titleField.setCaretColor(TEXT_PRIMARY);
        titleField.setOpaque(true);
        titleField.setPreferredSize(new Dimension(200, 38));
        titleField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));


        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(titleLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1;
        formPanel.add(titleField, gbc);


        // Author field
        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        authorLabel.setForeground(TEXT_PRIMARY);
        authorLabel.setOpaque(false);


        JTextField authorField = new JTextField("Unknown");
        authorField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        authorField.setBackground(Color.WHITE);
        authorField.setForeground(TEXT_PRIMARY);
        authorField.setCaretColor(TEXT_PRIMARY);
        authorField.setOpaque(true);
        authorField.setPreferredSize(new Dimension(200, 38));
        authorField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));


        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.weightx = 0;
        formPanel.add(authorLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1;
        formPanel.add(authorField, gbc);


        final String[] selectedFilePath = {null};


        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".pdf");
                }
                public String getDescription() {
                    return "PDF Files (*.pdf)";
                }
            });


            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                selectedFilePath[0] = file.getAbsolutePath();
                fileField.setText(file.getName());


                try {
                    PDDocument document = Loader.loadPDF(file);
                    PDDocumentInformation info = document.getDocumentInformation();


                    String fileName = file.getName();
                    String title = fileName.substring(0, fileName.lastIndexOf('.'));
                    titleField.setText(title);


                    String pdfAuthor = info.getAuthor();
                    if (pdfAuthor != null && !pdfAuthor.trim().isEmpty()) {
                        authorField.setText(pdfAuthor.trim());
                    } else {
                        authorField.setText("Unknown");
                    }


                    document.close();
                } catch (Exception ex) {
                    String fileName = file.getName();
                    titleField.setText(fileName.substring(0, fileName.lastIndexOf('.')));
                    authorField.setText("Unknown");
                }
            }
        });


        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 18));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setOpaque(true);


        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setBackground(Color.WHITE);
        cancelButton.setForeground(TEXT_PRIMARY);
        cancelButton.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setOpaque(true);
        cancelButton.setPreferredSize(new Dimension(100, 38));


        cancelButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                cancelButton.setBackground(BACKGROUND_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                cancelButton.setBackground(Color.WHITE);
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());


        JButton doneButton = new JButton("Add");
        doneButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        doneButton.setBackground(ACCENT_COLOR);
        doneButton.setForeground(Color.WHITE);
        doneButton.setBorderPainted(false);
        doneButton.setFocusPainted(false);
        doneButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        doneButton.setOpaque(true);
        doneButton.setPreferredSize(new Dimension(100, 38));


        doneButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                doneButton.setBackground(new Color(10, 88, 202));
            }
            public void mouseExited(MouseEvent e) {
                doneButton.setBackground(ACCENT_COLOR);
            }
        });


        doneButton.addActionListener(e -> {
            if (selectedFilePath[0] == null) {
                JOptionPane.showMessageDialog(dialog,
                        "Please select a PDF file!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (titleField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter a title!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }


            try {
                String newId = manager.addContent(
                        titleField.getText().trim(),
                        authorField.getText().trim(),
                        selectedFilePath[0]
                );
                JOptionPane.showMessageDialog(dialog,
                        "PDF added successfully! ID: " + newId);
                dialog.dispose();
                refreshContentList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });


        buttonPanel.add(cancelButton);
        buttonPanel.add(doneButton);


        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);


        dialog.setVisible(true);
    }


    private void performSearch() {
        String keyword = searchField.getText();
        currentDisplayList = manager.search(keyword);


        int sortIndex = sortComboBox.getSelectedIndex();
        if (sortIndex == 0) {
            manager.sortByTitle(currentDisplayList);
        } else if (sortIndex == 1) {
            manager.sortByAuthor(currentDisplayList);
        } else if (sortIndex == 2) {
            manager.sortByFavorite(currentDisplayList);
        }


        contentListPanel.removeAll();


        if (currentDisplayList.isEmpty()) {
            JLabel emptyLabel = new JLabel("No results found for: \"" + keyword + "\"");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            emptyLabel.setForeground(TEXT_SECONDARY);
            emptyLabel.setOpaque(false);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentListPanel.add(Box.createVerticalStrut(80));
            contentListPanel.add(emptyLabel);
        } else {
            for (Content content : currentDisplayList) {
                contentListPanel.add(createContentCard(content));
                contentListPanel.add(Box.createVerticalStrut(12));
            }
        }


        contentListPanel.revalidate();
        contentListPanel.repaint();
    }


    private void openPDFViewer(Content content) {
        PDFViewer viewer = new PDFViewer(this, content, manager);


        viewer.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                refreshContentList();
            }
        });


        viewer.setVisible(true);
    }


    public static void main(String[] args) {
        // Enable system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Set system properties for better rendering on Windows
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");


        SwingUtilities.invokeLater(() -> new GUI());
    }
}


