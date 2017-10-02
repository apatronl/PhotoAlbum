/**
 * PhotoAlbum Application
 * CS 4470 - User Interface Software
 * apl7@gatech.edu
 *
 * @author Alejandrina Patron Lopez
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class PhotoAlbum {

    private JFrame mainFrame;
    private JPanel mainPanel;
    private JMenuBar menuBar;
    private JMenuItem menuItem;
    private JRadioButtonMenuItem rbMenuItem;
    private JLabel statusLabel;
    private ActionListener actionPrinter;
    private PhotoComponent photoComponent;
    private AnnotationMode annotationMode;
    private Color inkColor = Color.black;
    private ViewMode viewMode;
    private LightTable lightTable;
    private List<JRadioButtonMenuItem> rbMenuItems;
    private List<JRadioButtonMenuItem> magnetMenuItems;
    private List<JCheckBox> tagCheckboxes;
    private final int MIN_ZOOM_FACTOR = 5;
    private final int MAX_ZOOM_FACTOR = 15;
    private final int START_ZOOM_FACTOR = 10;
    private boolean magnetMode = false;

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    /**
     * Creates and shows Photo Album application.
     */
    private static void createAndShowGUI(){
        PhotoAlbum photoAlbum = new PhotoAlbum();
        photoAlbum.createMainFrame();
    }

    /**
     * Creates and shows main frame of the Photo Album application.
     */
    private JFrame createMainFrame() {

        actionPrinter = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("<")) {
                    statusLabel.setText(" Back");
                } else if (e.getActionCommand().equals(">")) {
                    statusLabel.setText(" Forward");
                } else {
                    statusLabel.setText(" " + e.getActionCommand());
                }
            }
        };

        rbMenuItems = new ArrayList<JRadioButtonMenuItem>();
        magnetMenuItems = new ArrayList<JRadioButtonMenuItem>();
        tagCheckboxes = new ArrayList<JCheckBox>();

        mainFrame = new JFrame("Photo Album");
        mainFrame.setSize(1000, 800);
        mainFrame.setMinimumSize(new Dimension(500, 400));

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });
        mainFrame.setJMenuBar(createJMenuBar());
        mainFrame.getContentPane().setLayout(new BorderLayout());
        mainFrame.getContentPane().add(createLeftPanel(), BorderLayout.WEST);
        lightTable = new LightTable(ViewMode.PHOTO, this);
        lightTable.setAnnotationMode(annotationMode);
        lightTable.setInkColor(inkColor);
        mainFrame.getContentPane().add(lightTable, BorderLayout.CENTER);
        mainFrame.getContentPane().add(createStatusBar(), BorderLayout.SOUTH);
        mainFrame.setVisible(true);

        return mainFrame;
    }

    /**
     * Creates the application's menu bar.
     */
    private JMenuBar createJMenuBar() {
        menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createViewMenu());
        menuBar.add(createFormatMenu());
        menuBar.add(createMagnetMenu());
        return menuBar;
    }

    private void showImage(File file) {
        try {
            BufferedImage img = ImageIO.read(file);
            lightTable.addImage(img);
        } catch (IOException ex) {
            statusLabel.setText(ex.getMessage()); // Put error message on status bar
        }
    }

    /**
     * Delete currently selected image, if any.
     */
    private void deleteImage() {
        lightTable.deleteCurrentPhoto();
    }

    /**
     * Creates "File" menu for the application's menu bar.
     */
    private JMenu createFileMenu() {
        JMenu menu;
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Image Files", "jpg", "png", "gif", "jpeg");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(filter);

        menu = new JMenu("File");
        menu.getAccessibleContext().setAccessibleDescription("File");
        menu.addActionListener(actionPrinter);

        menuItem = new JMenuItem("Import");
        menuItem.getAccessibleContext().setAccessibleDescription("Import");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(" " + e.getActionCommand());
                int result = fileChooser.showOpenDialog(mainFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            showImage(file);
                        }
                    });
                    mainFrame.revalidate();
                    mainFrame.repaint();
                }
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Delete");
        menuItem.getAccessibleContext().setAccessibleDescription("Delete");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(" " + e.getActionCommand());
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        deleteImage();
                    }
                });
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Exit");
        menuItem.getAccessibleContext().setAccessibleDescription("Exit");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menu.add(menuItem);
        return menu;
    }

    /**
     * Creates "View" menu for the application's menu bar.
     */
    private JMenu createViewMenu() {
        JMenu viewMenu = new JMenu("View");
        ButtonGroup viewButtonGroup = new ButtonGroup();

        rbMenuItem = new JRadioButtonMenuItem(ViewMode.PHOTO.getModeName());
        rbMenuItem.setSelected(true);
        rbMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lightTable.setViewMode(ViewMode.PHOTO);
                statusLabel.setText(" " + e.getActionCommand());
            }
        });
        rbMenuItems.add(rbMenuItem);
        viewButtonGroup.add(rbMenuItem);
        viewMenu.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem(ViewMode.GRID.getModeName());
        rbMenuItem.setSelected(false);
        rbMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lightTable.setViewMode(ViewMode.GRID);
                statusLabel.setText(" " + e.getActionCommand());
            }
        });
        rbMenuItems.add(rbMenuItem);
        viewButtonGroup.add(rbMenuItem);
        viewMenu.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem(ViewMode.SPLIT.getModeName());
        rbMenuItem.setSelected(false);
        rbMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lightTable.setViewMode(ViewMode.SPLIT);
                statusLabel.setText(" " + e.getActionCommand());
            }
        });
        rbMenuItems.add(rbMenuItem);
        viewButtonGroup.add(rbMenuItem);
        viewMenu.add(rbMenuItem);

        return viewMenu;
    }

    private JMenu createFormatMenu() {
        JMenu menu = new JMenu("Format");
        menu.getAccessibleContext().setAccessibleDescription("Format");
        menu.addActionListener(actionPrinter);

        menuItem = new JMenuItem("Ink Color");
        menuItem.getAccessibleContext().setAccessibleDescription("Ink Color");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(" " + e.getActionCommand());
                inkColor = JColorChooser.showDialog(null, "Choose a color", Color.BLACK);
                lightTable.setInkColor(inkColor);
            }
        });
        menu.add(menuItem);
        return menu;
    }

    /**
     * Creates "View" menu for the application's menu bar.
     */
    private JMenu createMagnetMenu() {
        JMenu magnetMenu = new JMenu("Magnets");

        rbMenuItem = new JRadioButtonMenuItem(Tag.TRAVEL.getTagName());
        rbMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: draw magnet
                if (!isGridViewSelected() || !magnetMode) {
                    // Error, not in grid & magnet mode
                    deselectMagnet(Tag.TRAVEL);
                    statusLabel.setText(" Error, application not in grid & magnet mode.");
                } else {
                    lightTable.updateMagnets(Tag.TRAVEL);
                    statusLabel.setText(" " + e.getActionCommand() + " Magnet");
                }
            }
        });
        magnetMenu.add(rbMenuItem);
        magnetMenuItems.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem(Tag.FAMILY.getTagName());
        rbMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: draw magnet
                if (!isGridViewSelected() || !magnetMode) {
                    // Error, not in grid & magnet mode
                    deselectMagnet(Tag.FAMILY);
                    statusLabel.setText(" Error, application not in grid & magnet mode.");
                } else {
                    lightTable.updateMagnets(Tag.FAMILY);
                    statusLabel.setText(" " + e.getActionCommand() + " Magnet");
                }
            }
        });
        magnetMenu.add(rbMenuItem);
        magnetMenuItems.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem(Tag.SCHOOL.getTagName());
        rbMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: draw magnet
                if (!isGridViewSelected() || !magnetMode) {
                    // Error, not in grid mode
                    deselectMagnet(Tag.SCHOOL);
                    statusLabel.setText(" Error, application not in grid & magnet mode.");
                } else {
                    lightTable.updateMagnets(Tag.SCHOOL);
                    statusLabel.setText(" " + e.getActionCommand() + " Magnet");
                }
            }
        });
        magnetMenu.add(rbMenuItem);
        magnetMenuItems.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem(Tag.WORK.getTagName());
        rbMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: draw magnet
                if (!isGridViewSelected() || !magnetMode) {
                    // Error, not in grid mode
                    deselectMagnet(Tag.WORK);
                    statusLabel.setText(" Error, application not in grid & magnet mode.");
                } else {
                    // Add Magnet
                    lightTable.updateMagnets(Tag.WORK);
                    statusLabel.setText(" " + e.getActionCommand() + " Magnet");
                }
            }
        });
        magnetMenu.add(rbMenuItem);
        magnetMenuItems.add(rbMenuItem);

        return magnetMenu;
    }

    /**
     * Creates the left panel of the application, including tag checkboxes,
     * annotation buttons, and navigation controls.
     */
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        Box topTools = new Box(BoxLayout.Y_AXIS);
        topTools.add(topTools.createVerticalStrut(10));
        topTools.add(createTagCheckboxes());
        topTools.add(topTools.createVerticalStrut(50));
        topTools.add(createAnnotationButtons());
        topTools.add(topTools.createVerticalStrut(50));
        topTools.add(createMagnetModeCheckbox());
        panel.add(topTools, BorderLayout.NORTH);
        Box bottomTools = new Box(BoxLayout.Y_AXIS);
        bottomTools.add(createZoomSlider());
        bottomTools.add(topTools.createVerticalStrut(10));
        bottomTools.add(createNavigationControls());
        panel.add(bottomTools, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates tag checkboxes for the tags "Travel", "Family", "School", and
     * "Work"
     */
    private Box createTagCheckboxes() {
        Box checkboxes = new Box(BoxLayout.Y_AXIS);
        checkboxes.setAlignmentX(Component.CENTER_ALIGNMENT);
        JCheckBox checkbox = new JCheckBox(Tag.TRAVEL.getTagName());
        checkbox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                statusLabel.setText(" " + Tag.TRAVEL.getTagName());
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    lightTable.addTag(Tag.TRAVEL);
                } else {
                    lightTable.removeTag(Tag.TRAVEL);
                };
            }
        });
        checkboxes.add(checkbox);
        tagCheckboxes.add(checkbox);
        checkbox = new JCheckBox(Tag.FAMILY.getTagName());
        checkbox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                statusLabel.setText(" " + Tag.FAMILY.getTagName());
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    lightTable.addTag(Tag.FAMILY);
                } else {
                    lightTable.removeTag(Tag.FAMILY);
                };
            }
        });
        checkboxes.add(checkbox);
        tagCheckboxes.add(checkbox);
        checkbox = new JCheckBox(Tag.SCHOOL.getTagName());
        checkbox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                statusLabel.setText(" " + Tag.SCHOOL.getTagName());
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    lightTable.addTag(Tag.SCHOOL);
                } else {
                    lightTable.removeTag(Tag.SCHOOL);
                };
            }
        });
        checkboxes.add(checkbox);
        tagCheckboxes.add(checkbox);
        checkbox = new JCheckBox(Tag.WORK.getTagName());
        checkbox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                statusLabel.setText(" " + Tag.WORK.getTagName());
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    lightTable.addTag(Tag.WORK);
                } else {
                    lightTable.removeTag(Tag.WORK);
                };
            }
        });
        checkboxes.add(checkbox);
        tagCheckboxes.add(checkbox);

        return checkboxes;
    }

    /**
     * Creates annotation mode buttons, drawing and text.
     */
    private Box createAnnotationButtons() {
        Box buttons = new Box(BoxLayout.X_AXIS);
        ButtonGroup group = new ButtonGroup();
        JRadioButton button = new JRadioButton(AnnotationMode.DRAWING.getModeName());
        button.setSelected(true);
        annotationMode = AnnotationMode.DRAWING;
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(" " + e.getActionCommand());
                annotationMode = AnnotationMode.DRAWING;
                lightTable.setAnnotationMode(annotationMode);
                statusLabel.setText(" " + e.getActionCommand());
            }
        });
        group.add(button);
        buttons.add(button);

        button = new JRadioButton(AnnotationMode.TEXT.getModeName());
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(" " + e.getActionCommand());
                annotationMode = AnnotationMode.TEXT;
                lightTable.setAnnotationMode(annotationMode);
                statusLabel.setText(" " + e.getActionCommand());
            }
        });
        group.add(button);
        buttons.add(button);

        return buttons;
    }

    /**
     * Creates checkbox for switching between normal LightTable browser mode
     * and magnet mode.
     */
    private Box createMagnetModeCheckbox() {
        JCheckBox checkbox = new JCheckBox("Magnet Mode");
        checkbox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    magnetMode = true;
                    lightTable.setMagnetMode(magnetMode);
                } else {
                    magnetMode = false;
                    lightTable.setMagnetMode(magnetMode);
                }
            }
        });
        Box box = new Box(BoxLayout.Y_AXIS);
        box.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(checkbox);
        return box;
    }

    /**
     * Creates a slider used in left panel to modify thumbnails' sizes.
     */
    private JSlider createZoomSlider() {
        JSlider zoomSlider = new JSlider(JSlider.HORIZONTAL, MIN_ZOOM_FACTOR, MAX_ZOOM_FACTOR, START_ZOOM_FACTOR);
        zoomSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                int value = zoomSlider.getValue();
                double newZoomFactor = (double)value / 10.0;
                lightTable.updateScaleFactor(newZoomFactor);
            }
        });
        return zoomSlider;
    }

    /**
     * Creates navigation control buttons (back, forward) used in left panel.
     */
    private Box createNavigationControls() {
        Box buttons = new Box(BoxLayout.X_AXIS);
        JButton button = new JButton("<");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lightTable.previous();
                statusLabel.setText(" " + e.getActionCommand());
            }
        });
        buttons.add(button);
        button = new JButton(">");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lightTable.next();
                statusLabel.setText(" " + e.getActionCommand());
            }
        });
        buttons.add(button);

        return buttons;
    }

    /**
     * Creates the main panel of the application (content area).
     */
    private JPanel createMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setBackground(Color.white);
        mainPanel.setLayout(new BorderLayout());
        return mainPanel;
    }

    /**
     * Creates the application's bottom status bar.
     */
    private JPanel createStatusBar() {
        JPanel statusPanel = new JPanel();
        statusPanel.setPreferredSize(new Dimension(mainFrame.getWidth(), 16));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusLabel = new JLabel(" Status");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);
        statusPanel.setBackground(new Color(155, 161, 163));
        return statusPanel;
    }

    /**
     * Checks if current view mode is GRID.
     *
     * @return true if current view mode is GRID, false otherwise.
     */
    private boolean isGridViewSelected() {
        for (JRadioButtonMenuItem item : rbMenuItems) {
            if (item.getText().equals(ViewMode.GRID.getModeName()) && item.isSelected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Updates View menu in accordance with actions that happen in the
     * light table (e.g. double clicking a thumbnail).
     */
    public void updateViewMenu(ViewMode viewMode) {
        for (JRadioButtonMenuItem item : rbMenuItems) {
            if (!viewMode.getModeName().equals(item.getText())) {
                item.setSelected(false);
            } else {
                item.setSelected(true);
            }
        }
    }

    /**
     * Updates the state of the tag checkboxes in accordance with the tags
     * of the currently selected photo. If tags == null, all tag checkboxes are
     * deselected.
     *
     * @param tags
     */
    public void updateSelectedTags(Set<String> tags) {
        if (tags == null || tags.isEmpty()) {
            deselectAllTags();
        } else {
            for (JCheckBox checkbox : tagCheckboxes) {
                if (tags.contains(checkbox.getText())) checkbox.setSelected(true);
                else checkbox.setSelected(false);
            }
        }
    }

    /**
     * Deselect all tags. Method is called when a new photo is imported.
     */
    private void deselectAllTags() {
        for (JCheckBox checkbox : tagCheckboxes) {
            checkbox.setSelected(false);
        }
    }

    /**
     * Deselects given magnet item.
     */
    private void deselectMagnet(Tag tag) {
        for (JRadioButtonMenuItem item : magnetMenuItems) {
            if (item.getText().equals(tag.getTagName())) {
                if (item.isSelected()) {
                    item.setSelected(false);
                } else {
                    item.setSelected(true);
                }
                break;
            }
        }
    }

    /**
     * Updates the message displayed in the status bar.
     */
    public void updateStatusBar(String status) {
        statusLabel.setText(" " + status);
    }
}
