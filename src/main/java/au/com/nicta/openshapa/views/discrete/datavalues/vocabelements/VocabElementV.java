package au.com.nicta.openshapa.views.discrete.datavalues.vocabelements;

import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.ExternalVocabElementListener;
import au.com.nicta.openshapa.db.FormalArgument;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.db.VocabElement;
import au.com.nicta.openshapa.views.VocabEditorV;
import au.com.nicta.openshapa.views.discrete.Editor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.log4j.Logger;

/**
 *
 * @author cfreeman
 */
public abstract class VocabElementV extends JPanel
implements KeyListener, ExternalVocabElementListener {

    //private static final int MARGIN = 5;

    private static final int VE_WIDTH = 16;

    private static final int VE_HEIGHT = 16;

    private static final Dimension ICON_SIZE = new Dimension(VE_WIDTH,
                                                             VE_HEIGHT);

    private JLabel typeIcon;

    private JLabel deltaIcon;

    private Editor veNameField;

    private ImageIcon deltaImageIcon;

    boolean hasVEChanged;

    private VocabElement veModel;

    private Vector<FormalArgumentV> argViews;

    private VocabEditorV parentEditor;

    private static Logger logger = Logger.getLogger(VocabElementV.class);

    protected VocabElementV(VocabElement vocabElement, VocabEditorV vev) {
        URL iconURL = getClass().getResource("/icons/d_16.png");
        deltaImageIcon = new ImageIcon(iconURL);
        hasVEChanged = false;
        veModel = vocabElement;
        parentEditor = vev;
        argViews = new Vector<FormalArgumentV>();

        deltaIcon = new JLabel();
        deltaIcon.setMaximumSize(ICON_SIZE);
        deltaIcon.setMinimumSize(ICON_SIZE);
        deltaIcon.setPreferredSize(ICON_SIZE);

        typeIcon = new JLabel();
        typeIcon.setMaximumSize(ICON_SIZE);
        typeIcon.setMinimumSize(ICON_SIZE);
        typeIcon.setPreferredSize(ICON_SIZE);

        veNameField = new VocabElementNameV(vev);
        veNameField.setBorder(null);
        veNameField.addKeyListener(this);
        //veNameField.setMargin(new Insets(MARGIN, MARGIN, MARGIN, MARGIN));

        this.setBackground(Color.WHITE);
        ((FlowLayout) this.getLayout()).setAlignment(FlowLayout.LEFT);
        this.setMaximumSize(new Dimension(50000, VE_HEIGHT));

        this.rebuildContents();
    }

    final protected void setTypeIcon(final ImageIcon newIcon) {
        this.typeIcon.setIcon(newIcon);
    }

    final public void rebuildContents() {
        this.removeAll();
        this.add(deltaIcon);
        this.add(typeIcon);
        this.parentEditor.updateDialogState();

        boolean hasFocus = veNameField.hasFocus();
        veNameField.storeCaretPosition();
        veNameField.setText(veModel.getName());

        FormalArgument focusedArg = null;

        this.add(veNameField);
        this.add(new JLabel("("));
        try {
            for (int i = 0; i < argViews.size(); i++) {
                FormalArgumentV view = argViews.get(0);
                if (view.hasFocus()) {
                    focusedArg = view.getModel();
                }

                argViews.remove(0);
            }

            for (int i = 0; i < veModel.getNumFormalArgs(); i++) {

                if (i > 0) {
                    this.add(new JLabel(","));
                }

                this.add(new JLabel("<"));
                FormalArgumentV fargV = new FormalArgumentV(veModel.getFormalArg(i), this, parentEditor);
                this.argViews.add(fargV);
                this.add(fargV);
                this.add(new JLabel(">"));
            }

            if (veModel.getVarLen()) {
                if (veModel.getNumFormalArgs() > 0) {
                    this.add(new JLabel(","));
                }

                this.add(new JLabel(" ..."));
            }
        } catch (SystemErrorException e) {
            logger.error("unable to rebuild contents.", e);
        }
        this.add(new JLabel(")"));

        // Redraw the component (to clear anything underlying).
        repaint();
        validate();

        // Maintain focus after draw.
        if (hasFocus && focusedArg != null) {
            veNameField.requestFocus();
        } else if (focusedArg != null) {
            getArgumentView(focusedArg).requestFocus();
        }

    }

    final public void setHasChanged(boolean hasChanged) {
        if (hasChanged) {
            deltaIcon.setIcon(deltaImageIcon);
        } else {
            deltaIcon.setIcon(null);
        }

        hasVEChanged = hasChanged;
    }

    final public boolean hasChanged() {
        return hasVEChanged;
    }

    final public FormalArgumentV getArgumentView(FormalArgument fa) {
        for (FormalArgumentV view : argViews) {
            if (view.getModel().equals(fa)) {
                return view;
            }
        }

        return null;
    }

    final public VocabElement getVocabElement() {
        return veModel;
    }

    final public Editor getNameComponent() {
        return this.veNameField;
    }

    final public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
            case KeyEvent.VK_BACK_SPACE:
            case KeyEvent.VK_DELETE:
                // Ignore - handled when the key is typed.
                e.consume();
                break;
            default:
                break;
        }
    }

    final public void keyReleased(KeyEvent e) {
        // Ignore key release
    }

    @Override
    final public boolean hasFocus() {
        if (this.veNameField.hasFocus()) {
            return true;
        } else {
            for (FormalArgumentV view : argViews) {
                if (view.hasFocus()) {
                    return true;
                }
            }
        }

        return false;
    }

    final public FormalArgumentV getArgWithFocus() {
        for (FormalArgumentV view : argViews) {
            if (view.hasFocus()) {
                return view;
            }
        }

        return null;
    }

    public void VEChanged(Database db,
                          long VEID,
                          boolean nameChanged,
                          String oldName,
                          String newName,
                          boolean varLenChanged,
                          boolean oldVarLen,
                          boolean newVarLen,
                          boolean fargListChanged,
                          Vector<FormalArgument> oldFargList,
                          Vector<FormalArgument> newFargList) {
    }

    public void VEDeleted(Database db, long VEID) {
        // Ignored for the moment.
    }
}
