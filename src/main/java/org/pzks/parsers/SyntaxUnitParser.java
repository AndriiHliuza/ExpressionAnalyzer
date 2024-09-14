package org.pzks.parsers;

import org.pzks.units.SyntaxUnit;

public interface SyntaxUnitParser {
    SyntaxUnit parse() throws Exception;
}
