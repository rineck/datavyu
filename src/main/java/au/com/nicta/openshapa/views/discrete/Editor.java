package au.com.nicta.openshapa.views.discrete;

import java.util.Vector;
import javax.swing.JTextField;

/**
 * Abstract editor - used to edit the value of OpenSHAPA data types (i.e. ints,
 * floats, nominals, timestamps, etc).
 *
 * @author cfreeman
 */
public abstract class Editor extends JTextField {

    /** The last caret position. */
    private int oldCaretPosition;

    /** Should the oldCaretPosition be advanced by a single position? */
    private boolean advanceCaret;

    /** A list of characters that can not be removed from this view. */
    private Vector<Character> preservedChars;

    /** Are we deleting characters, or replacing them with a substitute? */
    private boolean isDeletingChar;

    /** The character to use as a substitute if we are doing replacement. */
    private char replaceChar;

    /**
     * Constructor.
     */
    protected Editor() {
        oldCaretPosition = 0;
        advanceCaret = false;
        isDeletingChar = true;
        preservedChars = new Vector<Character>();

        // Set visual appearance.
        setBorder(null);
        setOpaque(false);
    }

    /**
     * Set a flag to advanceCaret, when updateDatabase is called the
     * oldCaretPosition will be advanced by one position. When set value is
     * called back by the listeners it will reset this flag.
     */
    public final void advanceCaret() {
        advanceCaret = true;
    }

    /**
     * Stores the currentCaretPosition, a call to restoreCaretPosition() can be
     * used to restore the caret position to the save point generated by this
     * method.
     */
    public final void storeCaretPosition() {
        // Character inserted - advance the caret position.
        oldCaretPosition = getCaretPosition();
        if (advanceCaret) {
            oldCaretPosition++;
        }
    }

    /**
     * Rather than delete characters.
     *
     * @param c The character to use when deleting (rather than deleting - the
     * supplied character is used to replace).
     */
    public final void setDeleteChar(final char c) {
        isDeletingChar = false;
        replaceChar = c;
    }

    /**
     * @return The list of preserved characters.
     */
    public final Vector<Character> getPreservedChars() {
        return preservedChars;
    }

    /**
     * Adds a character to the list that must be preserved by the editor
     * (characters that can not be deleted).
     *
     * @param c The character to be preserved.
     */
    public final void addPreservedChar(final Character c) {
        preservedChars.add(c);
    }

    /**
     * Restores the caret position to the last stored position. Use
     * storeCaretPosition() before calling this method.
     */
    public final void restoreCaretPosition() {
        oldCaretPosition = Math.min(oldCaretPosition, getText().length());
        setCaretPosition(oldCaretPosition);
        advanceCaret = false;   // reset the advance caret flag - only applies
                                // once per database update. Database update
                                // triggers this method via a listener.
    }

    /**
     * Removes characters from ahead of the caret if they are not in the
     * preservedChars parameter. If the character is to be preserved, this
     * method will simple shift the caret forward one spot.
     */
    public final void removeAheadOfCaret() {
        // Underlying text field has selection no caret, remove everything that
        // is selected.
        if ((getSelectionEnd() - getSelectionStart()) > 0) {
            removeSelectedText();

        // Underlying Text field has no selection, just a caret. Go ahead and
        // manipulate it as such.
        } else if (getText() != null && getText().length() > 0) {
            // Check ahead of caret to see if it is a preserved character. If
            // the character is preserved - simply move the caret ahead one spot
            // and leave the preserved character untouched.
            for (int i = 0; i < preservedChars.size(); i++) {
                if (getText().charAt(getCaretPosition())
                    == preservedChars.get(i)) {
                    setCaretPosition(getCaretPosition() + 1);
                    break;
                }
            }

            // Delete next character.
            StringBuffer currentValue = new StringBuffer(getText());
            currentValue.deleteCharAt(getCaretPosition());

            if (!isDeletingChar) {
                currentValue.insert(getCaretPosition(), replaceChar);
            }

            int cPosition = getCaretPosition();
            this.setText(currentValue.toString());
            setCaretPosition(cPosition);
        }
    }

    /**
     * Removes characters from behind the caret if they are not in the
     * preservedChars parameter. If the character is to be preserved, this
     * method will simply shift the caret back one spot.
     */
    public final void removeBehindCaret() {
        // Underlying text field has selection and no carret, simply remove
        // everything that is selected.
        if ((getSelectionEnd() - getSelectionStart()) > 0) {
            removeSelectedText();

        // Underlying text field has no selection, just a caret. Go ahead and
        // manipulate it as such.
        } else if (getText() != null && getText().length() > 0) {
            // Check behind the caret to see if it is a preserved character. If
            // the character is preserved - simply move the caret back one spot
            // and leave the preserved character untouched.
            for (int i = 0; i < preservedChars.size(); i++) {
                if (getText().charAt(getCaretPosition() - 1)
                    == preservedChars.get(i)) {
                    setCaretPosition(getCaretPosition() - 1);
                    break;
                }
            }

            // Delete previous character.
            StringBuffer currentValue = new StringBuffer(getText());
            currentValue.deleteCharAt(getCaretPosition() - 1);
            if (!isDeletingChar) {
                currentValue.insert(getCaretPosition() - 1, replaceChar);
            }

            int cPosition = getCaretPosition() - 1;
            this.setText(currentValue.toString());
            setCaretPosition(cPosition);
        }
    }

    /**
     * This method will remove any characters that have been selected in the
     * underlying text field and that don't exist in the preservedChars
     * parameter. If no characters have been selected, the underlying text field
     * is unchanged.
     */
    public final void removeSelectedText() {
        // Get the current value of the visual representation of this DataValue.
        StringBuffer cValue = new StringBuffer(getText());

        // Obtain the start and finish of the selected text.
        int start = this.getSelectionStart();
        int end = this.getSelectionEnd();
        int pos = start;

        for (int i = start; i < end; i++) {
            boolean found = false;

            // See if the character at the current position is reserved.
            for (int j = 0; j < preservedChars.size(); j++) {
                if (preservedChars.get(j) == cValue.charAt(pos)) {
                    found = true;
                    break;
                }
            }

            // Current character is not reserved - either delete or replace it.
            if (!found) {
                cValue.deleteCharAt(pos);

                // Replace the character rather than remove it, we then need to
                // skip to the next position to delete a character.
                if (!isDeletingChar) {
                    cValue.insert(pos, replaceChar);
                    pos++;
                }

            // Current character is reserved, skip over current position.
            } else {
                pos++;
            }
        }

        // Set the text for this data value to the new string.
        this.setText(cValue.toString());
        this.setCaretPosition(start);
    }
}
