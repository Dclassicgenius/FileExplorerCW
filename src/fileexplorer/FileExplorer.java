/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileexplorer;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;


public class FileExplorer {


    public static void main(String[] args) {
        // TODO code application logic here
        JFrame frame = new JFrame( "File Explorer" );
        JSplitPane fileSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        File[] root = File.listRoots();        
        
        Map<String,List<Color>> colorStorage = ReadStorage();        
        FileModel model = new FileModel(root[0].getPath(),colorStorage);
        DefaultListModel<JCheckBox> checkBoxes = new DefaultListModel<JCheckBox>();
        JScrollPane fileView = new JScrollPane();
        
        JPanel fileOpened = new JPanel();        
        JList list = new JList(model);
        list.addListSelectionListener(new ListListener(colorStorage,fileOpened));        
        fileView.setViewportView(fileOpened);
        
        JSplitPane fileSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JScrollPane listScroller = new JScrollPane( list );
        JPanel treePanel = new JPanel();
        
        
        
       // RadioButtons radios = new RadioButtons(root[0].listFiles(),folderModel,tree);
        JComboBox cb = new JComboBox(root);
        JPanel cards = new JPanel(new CardLayout());
        for(int i =0;i<root.length;i++){
            FolderModel folderModel = new FolderModel(root[i].getPath());
            JTree tree = new JTree(folderModel);      
            tree.addTreeSelectionListener(new TreeListener(model));      
            JScrollPane treepane = new JScrollPane(tree);
            cards.add(treepane,root[i].getPath());
        }
        
        
        cb.addItemListener(new ComboBoxListener(cards));
        treePanel.add(cb);                
        treePanel.add(Box.createVerticalStrut(5));
        treePanel.add(cards);        
        
        JScrollPane treeScroller = new JScrollPane( treePanel );
        treePanel.setLayout(new BoxLayout(treePanel,BoxLayout.PAGE_AXIS));
        treePanel.setPreferredSize(treePanel.getPreferredSize());
        treePanel.setBackground(Color.WHITE);
       // treeScroller.setMinimumSize( new Dimension( 0, 0 ) );
        //fileView.add(displayFile);        
       
        //tree.setAlignmentX(Component.LEFT_ALIGNMENT);        
        
        JPanel filePanel = new JPanel();
        filePanel.setLayout(new BoxLayout(filePanel,BoxLayout.X_AXIS));
        filePanel.add(listScroller);
        filePanel.setBackground(Color.white);
        
        JCheckBox redFilter = new JCheckBox("Red");
        JCheckBox blueFilter = new JCheckBox("Blue");
        JCheckBox greenFilter = new JCheckBox("Green");
        
        checkBoxes.addElement(redFilter);
        checkBoxes.addElement(blueFilter);
        checkBoxes.addElement(greenFilter);
        
        CheckboxListRenderer filterFile = new CheckboxListRenderer(checkBoxes);
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel,BoxLayout.PAGE_AXIS));
        filterPanel.setBackground(Color.white);        
        JLabel filterLabel = new JLabel("Filter");
        filterLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterFile.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.add(filterLabel);
        filterPanel.add(Box.createVerticalStrut(5));
        filterPanel.add(filterFile);        
        filterPanel.add(Box.createVerticalStrut(5));
        JButton button = new JButton("Filter");
        button.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    List<Color> color = new ArrayList<Color>();
                    for(int i =0;i<checkBoxes.size();i++){
                        JCheckBox temp = checkBoxes.get(i);
                        if(temp.isSelected()){
                            if(temp.getText()=="Red"){
                                color.add(Color.red);
                            }
                            else if(temp.getText() == "Blue"){
                                color.add(Color.BLUE);
                            }else if(temp.getText() == "Green"){
                                color.add(Color.GREEN);
                            }
                        }                       
                    }
                    model.SetFilter(color);
                }                
            });        
        filterPanel.setBorder(new EmptyBorder(0,5,0,5));
        filterPanel.add(button);
        filePanel.add(filterPanel);
                
        fileSplit.add(filePanel);
        fileSplit.add(fileView);
        fileSplitPane.add(treeScroller);
        fileSplitPane.add(fileSplit);
        
        
        fileSplitPane.setContinuousLayout( true );
       
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
        mainPanel.add(fileSplitPane);
       // mainPanel.add(new RadioButtons(root));
       
        frame.getContentPane().add(mainPanel );
        frame.setSize( 800, 800 );
        frame.setVisible(true);
    }
    
    
    private static Map<String,List<Color>> ReadStorage(){
        //Reads the storage file and converts it to a hashmap, in case of errors returns empty hashmaps
        HashMap<String,List<Color>> map;
        try {
            FileInputStream fileIn = new FileInputStream(new File("Store.ser").getCanonicalFile());
            ObjectInputStream in = new ObjectInputStream(fileIn);
            map = (HashMap<String,List<Color>>) in.readObject();
            in.close();
            fileIn.close();
         } catch (IOException i) {
            i.printStackTrace();
            return new HashMap<String,List<Color>> ();
         } catch (ClassNotFoundException c) {
            c.printStackTrace();
            return new HashMap<String,List<Color>> ();
         }   
        return map;
    }
    
    protected static class TreeListener implements TreeSelectionListener {
        FileModel model;
        public TreeListener( FileModel mdl) {
            model = mdl;
        }
        public void valueChanged( TreeSelectionEvent e ) {
            File fileSysEntity = (File)e.getPath().getLastPathComponent();
            if (fileSysEntity == null){
                System.out.println("Can't be opened");
            }
            if ( fileSysEntity.isDirectory() ) {
                model.setDirectory( fileSysEntity );
            }
            else {
                model.setDirectory( null );
            }
        }
    }
    
    protected static class ComboBoxListener implements ItemListener{
        JPanel cards;
        public ComboBoxListener(JPanel cards){
            this.cards = cards;
        }
        @Override
        public void itemStateChanged(ItemEvent e) {
            CardLayout cl = (CardLayout)(cards.getLayout());
            cl.show(cards, ((File)e.getItem()).getPath());
            
        }
        
 
    }
    
    protected static class ListListener implements ListSelectionListener{
        Map<String,List<Color>> colorStorage;        
        JPanel fileOpened;
        DefaultListModel<JCheckBox> checkBoxes;
        CheckboxListRenderer displayFile;           
        public ListListener(Map<String,List<Color>> colorStorage,JPanel fileOpened){
            this.colorStorage = colorStorage;
            this.fileOpened = fileOpened;
            checkBoxes = new DefaultListModel<JCheckBox>();
            displayFile = new CheckboxListRenderer(checkBoxes);
            //Box Layout is used to set the layout of the panel, stacking the next item on top of the previous item
            this.fileOpened.setLayout(new BoxLayout(fileOpened,BoxLayout.PAGE_AXIS));
            this.fileOpened.setBackground(Color.WHITE);
        }
        //Function  triggered whenever a new item in the list is selected
        @Override
        public void valueChanged(ListSelectionEvent e) {        
            JList list = (JList)e.getSource();
            int firstIndex = list.getSelectedIndex();
            FileModel model = (FileModel) list.getModel();
            File file = (File)model.getElementAt(firstIndex);           
            checkBoxes.clear();
            List<Color> colors = new ArrayList<Color>();

            if(colorStorage.containsKey(file.getPath())){
                colors = colorStorage.get(file.getPath());
            }

            if(colors.contains(Color.RED)){                
                checkBoxes.addElement(new JCheckBox("Red",true));               
            }else{
                checkBoxes.addElement(new JCheckBox("Red"));
            }
            
            if(colors.contains(Color.blue)){                
                checkBoxes.addElement(new JCheckBox("Blue",true));
            }else{                
                checkBoxes.addElement(new JCheckBox("Blue"));
            }
            
            if(colors.contains(Color.GREEN)){                
                checkBoxes.addElement(new JCheckBox("Green",true));
            }else{                
                checkBoxes.addElement(new JCheckBox("Green"));
            }
            //The fileOpened JPanel isn't associated with any model that can trigger a redraw
            // so We need to remove everything added before adding new items
            fileOpened.removeAll();
            JLabel fileName = new JLabel("File Name : "+file.getName());
            Font font = new Font("Courier", Font.PLAIN,12);
            JLabel filePath = new JLabel("File Path : "+file.getPath());
            filePath.setFont(font);
            JButton explorer = new JButton("Open in explorer");
            JButton saveMarkers = new JButton("Save Color Markers");
            saveMarkers.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {                    
                    List<Color> color = new ArrayList<Color>();
                    for(int i =0;i<checkBoxes.size();i++){
                        JCheckBox temp = checkBoxes.get(i);
                        if(temp.isSelected()){
                            if(temp.getText()=="Red"){
                                color.add(Color.red);
                            }
                            else if(temp.getText() == "Blue"){
                                color.add(Color.BLUE);
                            }else if(temp.getText() == "Green"){
                                color.add(Color.GREEN);
                            }
                        }                       
                    }
                    colorStorage.put(file.getPath(), color);
                    saveData();
                }
                private void saveData(){
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
            explorer.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        desktop.open(file.getParentFile());
                    } catch (IOException ex) {
                        Logger.getLogger(FileExplorer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }                
            });
            
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
            
            fileOpened.setBorder(new EmptyBorder(5,5,5,5));
            fileName.setBorder(new EmptyBorder(0,0,2,0));
            filePath.setBorder(new EmptyBorder(0,0,2,0));            
            displayFile.setBorder(new EmptyBorder(0,0,2,0));            
            //explorer.setMargin(new Insets(0,0,5,0));            
            //saveMarkers.setMargin(new Insets(0,0,5,0));            
        }        
    }
}
