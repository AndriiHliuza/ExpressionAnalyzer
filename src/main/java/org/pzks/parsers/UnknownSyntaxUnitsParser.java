package org.pzks.parsers;

import org.pzks.units.SyntaxContainer;
import org.pzks.units.SyntaxUnit;
import org.pzks.units.UnknownSyntaxUnit;
import org.pzks.units.UnknownSyntaxUnitSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UnknownSyntaxUnitsParser implements SyntaxUnitParser {

    private SyntaxUnit syntaxUnit;

    public UnknownSyntaxUnitsParser(SyntaxUnit syntaxUnit) {
        this.syntaxUnit = syntaxUnit;
    }

    @Override
    public SyntaxUnit parse() {
        List<SyntaxUnit> syntaxUnits = syntaxUnit.getSyntaxUnits();
        List<SyntaxUnit> processedSyntaxUnits = processSyntaxUnits(syntaxUnits);

        syntaxUnit.setSyntaxUnits(processedSyntaxUnits);

        return syntaxUnit;
    }

    public List<SyntaxUnit> processSyntaxUnits(List<SyntaxUnit> syntaxUnits) {
        List<SyntaxUnit> processedSyntaxUnits = new ArrayList<>();

        processRecursivelySyntaxUnitsForUnknownSyntaxUnitsIdentificationAndCombination(
                syntaxUnits,
                processedSyntaxUnits
        );
        return processedSyntaxUnits;
    }

    public void processRecursivelySyntaxUnitsForUnknownSyntaxUnitsIdentificationAndCombination(
            List<SyntaxUnit> syntaxUnits,
            List<SyntaxUnit> processedSyntaxUnits
    ) {
        for (int i = 0; i < syntaxUnits.size(); i++) {
            SyntaxUnit syntaxUnit = syntaxUnits.get(i);
            if (syntaxUnit instanceof UnknownSyntaxUnit) {
                List<UnknownSyntaxUnit> unknownSyntaxUnits = new ArrayList<>();

                while (i < syntaxUnits.size() && syntaxUnits.get(i) instanceof UnknownSyntaxUnit unknownSyntaxUnit) {
                    unknownSyntaxUnits.add(unknownSyntaxUnit);
                    i++;
                }
                i--;

                if (unknownSyntaxUnits.size() > 1) {
                    UnknownSyntaxUnitSequence unknownSyntaxUnitSequence = new UnknownSyntaxUnitSequence(
                            unknownSyntaxUnits.getFirst().getIndex(),
                            null
                    );
                    unknownSyntaxUnitSequence.setSyntaxUnits(new ArrayList<>(unknownSyntaxUnits));
                    unknownSyntaxUnitSequence.setValue(unknownSyntaxUnits.stream()
                            .map(SyntaxUnit::getValue)
                            .collect(Collectors.joining())
                    );
                    processedSyntaxUnits.add(unknownSyntaxUnitSequence);
                } else {
                    processedSyntaxUnits.add(syntaxUnit);
                }
                unknownSyntaxUnits.clear();

            } else if (syntaxUnit instanceof SyntaxContainer syntaxContainer) {
                List<SyntaxUnit> containerSyntaxUnits = syntaxContainer.getSyntaxUnits();
                List<SyntaxUnit> processedContainerSyntaxUnits = new ArrayList<>();
                processRecursivelySyntaxUnitsForUnknownSyntaxUnitsIdentificationAndCombination(
                        containerSyntaxUnits,
                        processedContainerSyntaxUnits
                );
                syntaxContainer.setSyntaxUnits(processedContainerSyntaxUnits);
                processedSyntaxUnits.add(syntaxContainer);
            } else {
                processedSyntaxUnits.add(syntaxUnit);
            }
        }
    }
}
