package org.pzks.utils.trees;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class TreeSerializer {
    public static boolean saveBinaryTreeToFile(BinaryTreeNode treeNode, String fileName) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(fileName), treeNode);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean safeBinaryTreeToCurrentDirectory(BinaryTreeNode treeNode, String fileName) {
        return saveBinaryTreeToFile(treeNode, fileName);
    }

    public static boolean saveNaryTreeToFile(NaryTreeNode treeNode, String fileName) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(fileName), treeNode);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean safeNaryTreeToCurrentDirectory(NaryTreeNode treeNode, String fileName) {
        return saveNaryTreeToFile(treeNode, fileName);
    }
}
