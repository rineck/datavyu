package au.com.nicta.openshapa.views.discrete.datavalues;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.db.TimeStampDataValue;
import au.com.nicta.openshapa.views.discrete.Selector;
import org.apache.log4j.Logger;

/**
 * A view representation for an onset timestamp.
 *
 * @author cfreeman
 */
public final class OnsetView extends TimeStampDataValueView {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(OnsetView.class);

    /**
     * Constructor.
     *
     * @param cellSelection The parent cell selection.
     * @param cell The parent cell for this onset timestamp.
     * @param timeStampDataValue A datavalue for the onset to wrap around.
     * @param editable Is the datavalue editable or not - true if it is editable
     * false otherwise.
     */
    public OnsetView(final Selector cellSelection,
                     final DataCell cell,
                     final TimeStampDataValue timeStampDataValue,
                     final boolean editable) {
        super(cellSelection, cell, timeStampDataValue, editable);
    }

    /**
     * Hook for updating the database with the latest onset value from the onset
     * editor.
     */
    @Override
    public void updateDatabase() {
        try {
            this.getEditor().storeCaretPosition();
            TimeStampDataValue tsdv = (TimeStampDataValue) this.getValue();
            getParentCell().setOnset(tsdv.getItsValue());
            getParentCell().getDB().replaceCell(getParentCell());
        } catch (SystemErrorException se) {
            logger.error("Unable to update Database: ", se);
        }
    }
}
