
package fileexplorer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//The model from the MVC(model-view-controller) architecture
// AbstractListModel is a view model associated with a Jlist
public class FileModel extends AbstractListModel<File> {
    private List<File> data;
    private File root;
    private final Map<String, List<Color>> map;

    public FileModel(String pathName, Map<String, List<Color>> map) {
        root = new File(pathName);
        File[] temp = GetFiles();
        if (temp == null) {
            data = new ArrayList<>();
        } else {
            data = Arrays.asList(temp);
        }
        this.map = map;
    }

    //A filter for showing only files
    private File[] GetFiles() {
        try {
            return root.listFiles((file, name) -> new File(file, name).isFile());
        } catch (Exception ex) {
            return new File[0];
        }
    }

    //A filter that checks the files and see whether their markers are the same with the markers in the filter
    public void SetFilter(List<Color> filters) {
        List<File> temp = new ArrayList<>();
        File[] tempData = GetFiles();
        if (tempData == null) {
            data = new ArrayList<>();
        } else {
            data = Arrays.asList(tempData);
        }

        if (filters.size() > 0) {
            for (File datum : data) {
                String filePath = datum.getPath();
                if (map.containsKey(filePath)) {
                    List<Color> colors = map.get(filePath);
                    if (colors.containsAll(filters)) {
                        temp.add(datum);
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
                data = new ArrayList<>();
            } else {
                data = Arrays.asList(temp);
            }
        } else {
            root = null;
            data = new ArrayList<>();
        }
        fireContentsChanged(this, 0, getSize() - 1);
    }

    @Override
    public int getSize() {
        return data.size();
    }

    @Override
    public File getElementAt(int index) {
        return data.get(index);
    }
}

