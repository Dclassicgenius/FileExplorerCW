
package fileexplorer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class FileExplorer {

    public static void main(String[] args) {
        // TODO code application logic here
        JFrame frame = new JFrame("File Explorer");
        JSplitPane fileSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        File[] root = File.listRoots();

        Map<String, List<Color>> colorStorage = ReadStorage();
        FileModel model = new FileModel(root[0].getPath(), colorStorage);
        DefaultListModel<JCheckBox> checkBoxes = new DefaultListModel<>();
        JScrollPane fileView = new JScrollPane();

        JPanel fileOpened = new JPanel();
        JList<File> list = new JList<>(model);
        list.addListSelectionListener(new ListListener(colorStorage, fileOpened));
        list.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem item = new JMenuItem("Open in Explorer");
                    item.addActionListener(e1 -> {
                        File file = new File(list.getSelectedValue().toString());
                        openInExplorer(file);
                    });
                    menu.add(item);
                    list.setSelectedIndex(list.locationToIndex(e.getPoint()));
                    menu.show(list, 15, list.getCellBounds(
                            list.getSelectedIndex(),
                            list.getSelectedIndex()).y + 15);
                }
            }
        });
        fileView.setViewportView(fileOpened);

        JSplitPane fileSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JScrollPane listScroller = new JScrollPane(list);
        JPanel treePanel = new JPanel();


        JComboBox<File> cb = new JComboBox<>(root);
        JPanel cards = new JPanel(new CardLayout());
        IntStream.range(0, root.length).forEach(i -> {
            FolderModel folderModel = new FolderModel(root[i].getPath());
            JTree tree = new JTree(folderModel);
            tree.addTreeSelectionListener(new TreeListener(model));
            JScrollPane treePane = new JScrollPane(tree);
            cards.add(treePane, root[i].getPath());
        });


        cb.addItemListener(new ComboBoxListener(cards));
        treePanel.add(cb);
        treePanel.add(Box.createVerticalStrut(5));
        treePanel.add(cards);

        JScrollPane treeScroller = new JScrollPane(treePanel);
        treePanel.setLayout(new BoxLayout(treePanel, BoxLayout.PAGE_AXIS));
        treePanel.setPreferredSize(treePanel.getPreferredSize());
        treePanel.setBackground(Color.WHITE);

        JPanel filePanel = new JPanel();
        filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.X_AXIS));
        filePanel.add(listScroller);
        filePanel.setBackground(Color.white);

        for (Map.Entry<String, Color> entry : Colors.colors.entrySet()) {
            String keyColor = entry.getKey();
            JCheckBox filter = new JCheckBox(keyColor);
            checkBoxes.addElement(filter);
        }

        CheckboxListRenderer filterFile = new CheckboxListRenderer(checkBoxes);
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.PAGE_AXIS));
        filterPanel.setBackground(Color.white);
        JLabel filterLabel = new JLabel("Filter");
        filterLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterFile.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.add(filterLabel);
        filterPanel.add(Box.createVerticalStrut(5));
        filterPanel.add(filterFile);
        filterPanel.add(Box.createVerticalStrut(5));
        JButton button = new JButton("Filter");
        button.addActionListener(e -> {
            List<Color> color = new ArrayList<>();
            for (int i = 0; i < checkBoxes.size(); i++) {
                JCheckBox temp = checkBoxes.get(i);
                if (temp.isSelected()) {
                    for (Map.Entry<String, Color> entry : Colors.colors.entrySet()) {
                        String keyColor = entry.getKey();
                        Color valueColor = entry.getValue();
                        if (temp.getText().equals(keyColor)) {
                            color.add(valueColor);
                            break;
                        }
                    }
                }
            }
            model.SetFilter(color);
        });
        filterPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
        filterPanel.add(button);
        filePanel.add(filterPanel);

        fileSplit.add(filePanel);
        fileSplit.add(fileView);
        fileSplitPane.add(treeScroller);
        fileSplitPane.add(fileSplit);


        fileSplitPane.setContinuousLayout(true);


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(fileSplitPane);
        // mainPanel.add(new RadioButtons(root));

        frame.getContentPane().add(mainPanel);
        frame.setSize(800, 800);
        frame.setVisible(true);
    }


    private static void openInExplorer(File file) {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(file.getParentFile());
        } catch (IOException ex) {
            Logger.getLogger(FileExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Map<String, List<Color>> ReadStorage() {
        //Reads the storage file and converts it to a hashmap, in case of errors returns empty hashmaps
        HashMap<String, List<Color>> map;
        try {
            FileInputStream fileIn = new FileInputStream(new File("Store.ser").getCanonicalFile());
            ObjectInputStream in = new ObjectInputStream(fileIn);

            //The unchecked cast error is due to the fact that the compiler has no way of pre-knowing
            //which object is contained in the file.
            map = (HashMap<String, List<Color>>) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException i) {
            i.printStackTrace();
            return new HashMap<>();
        }
        return map;
    }

    protected static class TreeListener implements TreeSelectionListener {
        FileModel model;

        public TreeListener(FileModel mdl) {
            model = mdl;
        }

        public void valueChanged(TreeSelectionEvent e) {
            File fileSysEntity = (File) e.getPath().getLastPathComponent();
            if (fileSysEntity == null) {
                System.out.println("Can't be opened");
            } else if (fileSysEntity.isDirectory()) {
                model.setDirectory(fileSysEntity);
            } else {
                model.setDirectory(null);
            }
        }
    }

    protected static class ComboBoxListener implements ItemListener {
        JPanel cards;

        public ComboBoxListener(JPanel cards) {
            this.cards = cards;
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            CardLayout cl = (CardLayout) (cards.getLayout());
            cl.show(cards, ((File) e.getItem()).getPath());

        }


    }

    protected static class ListListener implements ListSelectionListener {
        Map<String, List<Color>> colorStorage;
        JPanel fileOpened;
        DefaultListModel<JCheckBox> checkBoxes;
        CheckboxListRenderer displayFile;

        public ListListener(Map<String, List<Color>> colorStorage, JPanel fileOpened) {
            this.colorStorage = colorStorage;
            this.fileOpened = fileOpened;
            checkBoxes = new DefaultListModel<>();
            displayFile = new CheckboxListRenderer(checkBoxes);
            //Box Layout is used to set the layout of the panel, stacking the next item on top of the previous item
            fileOpened.setLayout(new BoxLayout(fileOpened, BoxLayout.PAGE_AXIS));
            fileOpened.setBackground(Color.WHITE);
        }

        //Function  triggered whenever a new item in the list is selected
        @Override
        public void valueChanged(ListSelectionEvent e) {
            //The unchecked cast error is due to the fact that the compiler has no way of pre-knowing
            //which object is contained in the list
            JList<File> list = (JList<File>) e.getSource();
            int firstIndex = list.getSelectedIndex();
            FileModel model = (FileModel) list.getModel();
            File file = model.getElementAt(firstIndex);
            checkBoxes.clear();
            List<Color> colors = new ArrayList<>();

            if (colorStorage.containsKey(file.getPath())) {
                colors = colorStorage.get(file.getPath());
            }
            for (Map.Entry<String, Color> entry : Colors.colors.entrySet()) {
                String keyColor = entry.getKey();
                Color valueColor = entry.getValue();
                if (colors.contains(valueColor)) {
                    checkBoxes.addElement(new JCheckBox(keyColor, true));
                } else {
                    checkBoxes.addElement(new JCheckBox(keyColor));
                }
            }
            //The fileOpened JPanel isn't associated with any model that can trigger a redraw
            // so We need to remove everything added before adding new items
            fileOpened.removeAll();
            JLabel fileName = new JLabel("File Name : " + file.getName());
            Font font = new Font("Courier", Font.PLAIN, 12);
            JLabel filePath = new JLabel("File Path : " + file.getPath());
            filePath.setFont(font);
            JButton explorer = new JButton("Open in explorer");
            JButton saveMarkers = new JButton("Save Color Markers");
            saveMarkers.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    List<Color> color = new ArrayList<>();
                    for (int i = 0; i < checkBoxes.size(); i++) {
                        JCheckBox temp = checkBoxes.get(i);
                        if (temp.isSelected()) {
                            for (Map.Entry<String, Color> entry : Colors.colors.entrySet()) {
                                String keyColor = entry.getKey();
                                Color valueColor = entry.getValue();
                                if (temp.getText().equals(keyColor)) {
                                    color.add(valueColor);
                                    break;
                                }
                            }
                        }
                    }
                    colorStorage.put(file.getPath(), color);
                    saveData();
                }

                private void saveData() {
                    try {
                        FileOutputStream fileOut =
                                new FileOutputStream(new File("Store.ser").getCanonicalFile());
                        ObjectOutputStream out = new ObjectOutputStream(fileOut);
                        out.writeObject(colorStorage);
                        out.close();
                        fileOut.close();
                    } catch (IOException i) {
                        i.printStackTrace();
                    }
                }
            });
            explorer.addActionListener(e1 -> openInExplorer(file));

            fileName.setAlignmentX(Component.LEFT_ALIGNMENT);
            filePath.setAlignmentX(Component.LEFT_ALIGNMENT);
            displayFile.setAlignmentX(Component.LEFT_ALIGNMENT);
            explorer.setAlignmentX(Component.LEFT_ALIGNMENT);
            saveMarkers.setAlignmentX(Component.LEFT_ALIGNMENT);

            fileOpened.add(fileName);
            fileOpened.add(filePath);
            fileOpened.add(displayFile);
            fileOpened.add(explorer);
            fileOpened.add(Box.createVerticalStrut(5));
            fileOpened.add(saveMarkers);

            fileOpened.setBorder(new EmptyBorder(5, 5, 5, 5));
            fileName.setBorder(new EmptyBorder(0, 0, 2, 0));
            filePath.setBorder(new EmptyBorder(0, 0, 2, 0));
            displayFile.setBorder(new EmptyBorder(0, 0, 2, 0));

        }
    }
}
