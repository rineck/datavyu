package org.uispec4j;

import au.com.nicta.openshapa.views.discrete.SpreadsheetCell;
import au.com.nicta.openshapa.views.discrete.datavalues.MatrixV;
import java.awt.Component;
import junit.framework.Assert;



/**
 *
 * @author mmuthukrishna
 */
public class Cell extends AbstractUIComponent {
    /**
     * UISpec4J convention to declare type.
     */
    public static final String TYPE_NAME = "SpreadsheetCell";
    /**
     * UISpec4J convention to declare associated class.
     */
    public static final Class[] SWING_CLASSES = {SpreadsheetCell.class};

    /**
     * Since this is an Adaptor class, this is the class being adapted.
     */
    private SpreadsheetCell ssCell;

    /**
     * Spreadsheet constructor.
     * @param SpreadsheetCell actual SpreadsheetCell class being adapted
     */
    public Cell(final SpreadsheetCell spreadsheetCell) {
        Assert.assertNotNull(spreadsheetCell);
        this.ssCell = spreadsheetCell;
    }

    public Component getAwtComponent() {
        return ssCell;
    }

    public String getDescriptionTypeName() {
        return TYPE_NAME;
    }

    /**
     * returns the ordinal column identifier.
     * @return long ordinal column identifier
     */
    public final long getOrd() {
        return ssCell.getOrdinal().getItsValue();
    }

    /**
     * Returns Timestamp of Onset so that components are easily accessible.
     * @return Timestamp onset timestamp
     */
    public final Timestamp getOnsetTime() {
        return new Timestamp(ssCell.getOnsetDisplay());
    }

    /**
     * Returns Timestamp of Offset so that components are easily accessible.
     * @return Timestamp offset timestamp
     */
    public final Timestamp getOffsetTime() {
        return new Timestamp(ssCell.getOffsetDisplay());
    }

    /**
     * @return The data value view (matrixV) for the cell.
     */
    public final MatrixV getDataValueV() {
        return ssCell.getDataValueV();
    }
}
