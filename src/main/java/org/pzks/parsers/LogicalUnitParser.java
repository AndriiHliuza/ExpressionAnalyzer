package org.pzks.parsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogicalUnitParser {
    private List<String> logicalUnits;

    public LogicalUnitParser(List<String> logicalUnits) {
        this.logicalUnits = logicalUnits;
    }

    public List<String> parse() {
        return combineNumbers().logicalUnits;
    }

    private LogicalUnitParser combineNumbers() {
        List<String> logicalUnitsResult = new ArrayList<>();
        StringBuilder logicalUnitBuilder = new StringBuilder();
        boolean isNumberBuilding = false;
        for (String logicalUnit : logicalUnits) {
            if (isNumberBuilding && logicalUnit.matches("\\d+|\\.") && !logicalUnitBuilder.toString().contains(".")) {
                logicalUnitBuilder.append(logicalUnit);
            } else if (logicalUnit.matches("\\d+")) {
                logicalUnitBuilder.append(logicalUnit);
                isNumberBuilding = true;
            } else {
                if (!logicalUnitBuilder.isEmpty()) {
                    String combinedNumber = logicalUnitBuilder.toString();
                    if (combinedNumber.matches("\\d+\\.")) {
                        logicalUnitsResult.addAll(Arrays.stream(combinedNumber.split("\\b")).toList());
                    } else {
                        logicalUnitsResult.add(combinedNumber);
                    }
                    logicalUnitBuilder.delete(0, logicalUnitBuilder.length());
                }
                logicalUnitsResult.add(logicalUnit);
            }
        }

        if (!logicalUnitBuilder.isEmpty()) {
            logicalUnitsResult.add(logicalUnitBuilder.toString());
        }
        logicalUnits = new ArrayList<>(logicalUnitsResult);
        return this;
    }
}
