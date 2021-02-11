/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileexplorer;

import java.io.File;
import java.io.FilenameFilter;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;


//implements the treemodel to work with files
public class FolderModel implements TreeModel{
    private File root;
    public FolderModel(String path){
        root = new File(path);
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        File directory = (File)parent;
        String[] children = GetFolders(directory);
        return new File( directory, children[index] );
    }


    @Override
    public int getChildCount( Object parent ) {
        File fileSysEntity = (File)parent;
        if ( fileSysEntity.isDirectory() ) {
            String[] children = GetFolders(fileSysEntity);
            return children == null? 0:children.length;
        }
        else {
            return 0;
        }
    }
    
    public boolean isLeaf( Object node ) {
        return ((File)node).isFile();
    }
    
    @Override
    public int getIndexOfChild( Object parent, Object child ) {
        File directory = (File)parent;
        File fileSysEntity = (File)child;
        String[] children = directory.list();
        int result = -1;

        for ( int i = 0; i < children.length; ++i ) {
            if ( fileSysEntity.getName().equals( children[i] ) ) {
                result = i;
                break;
            }
        }

        return result;
    }
    
    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
    }
    private String[] GetFolders(File file){
        return file.list(new FilenameFilter(){
            public boolean accept(File file, String name){
                return new File(file,name).isDirectory();
            }
        });
    }
}
