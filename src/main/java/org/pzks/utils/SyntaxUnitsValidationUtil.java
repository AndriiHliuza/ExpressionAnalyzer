package org.pzks.utils;

import org.pzks.units.Function;
import org.pzks.units.LogicalBlock;
import org.pzks.units.SyntaxUnit;

import java.util.List;

public class SyntaxUnitsValidationUtil {
    public static boolean isFunctionsAbsent(List<SyntaxUnit> syntaxUnits) {
        boolean isFunctionsAbsent = true;
        for (SyntaxUnit syntaxUnit : syntaxUnits) {
            if (syntaxUnit instanceof Function) {
                isFunctionsAbsent = false;
                break;
            } else if (syntaxUnit instanceof LogicalBlock) {
                boolean innerResult = isFunctionsAbsent(syntaxUnit.getSyntaxUnits());
                if (!innerResult) {
                    isFunctionsAbsent = false;
                    break;
                }
            }
        }
        return isFunctionsAbsent;
    }
}
