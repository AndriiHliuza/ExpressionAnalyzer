package org.pzks.utils.trees;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class TreeSerializer {
    public static boolean saveToFile(TreeNode treeNode, String fileName) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(fileName), treeNode);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean safeToCurrentDirectory(TreeNode treeNode, String fileName) {
        return saveToFile(treeNode, fileName);
    }
}
