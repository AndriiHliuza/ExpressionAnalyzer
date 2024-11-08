package org.pzks.utils;

import java.util.List;

public class ObjectListPrinter {

    private static final StringBuilder spaceBuilder = new StringBuilder();

    public static void printTreeWithHeadline(List<?> objects, String headline) {
        HeadlinePrinter.print(headline, Color.GREEN);
        printTree(objects);
    }

    private static void printTree(List<?> objects) {
        for (Object object : objects) {
            if (object instanceof List<?> list && !list.isEmpty()) {
                System.out.println(spaceBuilder + "- [...]");
                spaceBuilder.append("     ");
                printTree(list);
                spaceBuilder.setLength(spaceBuilder.length() - 5);
            } else {
                System.out.println(spaceBuilder + "- " + object);
            }
        }
    }
}
