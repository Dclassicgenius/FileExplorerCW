/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileexplorer;

import java.awt.Color;
import java.util.List;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import javax.swing.AbstractListModel;
import javax.swing.ListCellRenderer;


//The model from the MVC(model-view-controller) architecture
// AbstractListModel is a view model associated with a Jlist
public class FileModel extends AbstractListModel {
    private List<File> data = null;
    private File root;
    private Map<String, List<Color>> map;

    public FileModel(String pathName, Map<String, List<Color>> map) {
        root = new File(pathName);
        File[] temp = GetFiles();
        if (temp == null) {
            data = new ArrayList<File>();
        } else {
            data = Arrays.asList(temp);
        }
        this.map = map;
    }

    public FileModel(File file) {
        root = file;
        File[] temp = GetFiles();
        if (temp == null) {
            data = new ArrayList<File>();
        } else {
            data = Arrays.asList(temp);
        }
    }

    //A filter for showing only files
    private File[] GetFiles() {
        try {
            return root.listFiles(new FilenameFilter() {
                public boolean accept(File file, String name) {
                    return new File(file, name).isFile();
                }
            });
        } catch (Exception ex) {
            return new File[0];
        }
    }

    //A filter that checks the files and see whether their markers are the same with the markers in the filter
    public void SetFilter(List<Color> filters) {
        List<File> temp = new ArrayList<File>();
        File[] tempData = GetFiles();
        if (tempData == null) {
            data = new ArrayList<File>();
        } else {
            data = Arrays.asList(tempData);
        }

        if (filters.size() > 0) {
            for (int i = 0; i < data.size(); i++) {
                String filePath = data.get(i).getPath();
                if (map.containsKey(filePath)) {
                    List<Color> colors = map.get(filePath);
                    if (colors.containsAll(filters)) {
                        temp.add(data.get(i));
                    }
                }
            }
            data = temp;
        }
        fireContentsChanged(this, 0, getSize() - 1);
    }

    public void setDirectory(File dir) {
        if (dir != null) {
            root = dir;
            File[] temp = GetFiles();
            if (temp == null) {
                data = new ArrayList<File>();
            } else {
                data = Arrays.asList(temp);
            }
        } else {
            root = null;
            data = new ArrayList<File>();
        }
        fireContentsChanged(this, 0, getSize() - 1);
    }

    @Override
    public int getSize() {
        return data.size();
    }

    @Override
    public Object getElementAt(int index) {
        return data.get(index);
    }
}

