package org.openshapa.views.continuous.quicktime;

import java.awt.Toolkit;
import java.io.File;
import javax.swing.JFrame;
import org.apache.log4j.Logger;
import org.openshapa.util.Constants;
import org.openshapa.views.continuous.DataController;
import org.openshapa.views.continuous.DataViewer;
import quicktime.QTException;
import quicktime.QTSession;
import quicktime.app.view.QTFactory;
import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;
import quicktime.qd.QDDimension;
import quicktime.std.StdQTConstants;
import quicktime.std.clocks.TimeRecord;
import quicktime.std.movies.Movie;
import quicktime.std.movies.Track;
import quicktime.std.movies.media.Media;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * The viewer for a quicktime video file.
 */
public final class QTDataViewer extends JFrame implements DataViewer {

    //--------------------------------------------------------------------------
    //
    //

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(QTDataViewer.class);

    //--------------------------------------------------------------------------
    //
    //

    /** The quicktime movie this viewer is displaying. */
    private Movie movie;

    /** The visual track for the above quicktime movie. */
    private Track visualTrack;

    /** The visual media for the above visual track. */
    private Media visualMedia;

    /** Rate for playback. */
    private float playRate;

    /** Frames per second. */
    private float fps;

    /** parent controller. */
    private DataController parent;

    /** The fixed window width. */
    private static final int WIN_X = 400;
    /** The fixed window height. */
    private static final int WIN_Y = 400;

    //--------------------------------------------------------------------------
    // [initialization]
    //

    /**
     * Constructor - creates new video viewer.
     */
    public QTDataViewer() {
        try {
            movie = null;

            // Initalise QTJava.
            QTSession.open();
        } catch (Throwable e) {
            logger.error("Unable to create QTVideoViewer", e);
        }
        initComponents();
    }

    //--------------------------------------------------------------------------
    // [interface] org.openshapa.views.continuous.DataViewer
    //

    public long getDuration() { throw new NotImplementedException(); }
    public long getOffset() { throw new NotImplementedException(); }
    public void setOffset(final long offset) { 
        throw new NotImplementedException(); 
    }

    /**
     * @return The parent JFrame that this data viewer resides within.
     */
    public JFrame getParentJFrame() {
        return this;
    }

    /**
     * Method to open a video file for playback.
     *
     * @param videoFile The video file that this viewer is going to display to
     * the user.
     */
    public void setDataFeed(final File videoFile) {

        try {
            this.setTitle(videoFile.getName());
            OpenMovieFile omf = OpenMovieFile.asRead(new QTFile(videoFile));
            movie = Movie.fromFile(omf);

            // Set the time scale for our movie to milliseconds (i.e. 1000 ticks
            // per second.
            movie.setTimeScale(Constants.TICKS_PER_SECOND);

            visualTrack = movie.getIndTrackType(1,
                                       StdQTConstants.visualMediaCharacteristic,
                                       StdQTConstants.movieTrackCharacteristic);

            // Initialise the video to be no bigger than a quarter of the screen
            int hScrnWidth = Toolkit.getDefaultToolkit()
                                    .getScreenSize().width / 2;
            if (movie.getBounds().getWidth() > hScrnWidth) {
                float aspectRatio = movie.getBounds().getWidthF()
                                    / movie.getBounds().getHeightF();
                visualTrack.setSize(new QDDimension(hScrnWidth,
                                                    hScrnWidth / aspectRatio));
            }

            visualMedia = visualTrack.getMedia();

            fps = (float) visualMedia.getSampleCount()
                  / visualMedia.getDuration() * visualMedia.getTimeScale();

            this.add(QTFactory.makeQTComponent(movie).asComponent());

            setName(getClass().getSimpleName() + "-" + videoFile.getName());
            this.pack();
            this.setVisible(true);

            // Set the size of the window to be the same as the incoming video.
            this.setBounds(this.getX(), this.getY(),
                           movie.getBox().getWidth(),
                           movie.getBox().getHeight());
        } catch (QTException e) {
            logger.error("Unable to setVideoFile", e);
        }
    }

    /**
     * Sets parent data controller.
     * @param dataController The data controller to be set as parent.
     */
    public void setParentController(final DataController dataController) {
        parent = dataController;
    }

    /**
     * @return The frames per second.
     */
    public float getFrameRate() {
        return fps;
    }

    /**
     * @param rate The playback rate.
     */
    public void setPlaybackSpeed(final float rate) {
        this.playRate = rate;
    }

    /**
     * Plays the continous data stream at the current playback rate..
     */
    public void play() {
        try {
            if (movie != null) {
                movie.setRate(playRate);
            }
        } catch (QTException e) {
            logger.error("Unable to play", e);
        }
    }

    /**
     * Stops the playback of the continous data stream.
     */
    public void stop() {
        try {
            if (movie != null) {
                movie.stop();
            }
        } catch (QTException e) {
            logger.error("Unable to stop", e);
        }
    }

    /**
     * @param position Millisecond absolute position for track.
     */
    public void seekTo(final long position) {
        try {
            if (movie != null) {
                TimeRecord time = new TimeRecord(Constants.TICKS_PER_SECOND,
                                                 position);
                movie.setTime(time);
            }
        } catch (QTException e) {
            logger.error("Unable to find", e);
        }
    }

    /**
     * @return Current time in milliseconds.
     *
     * @throws QTException If error occurs accessing underlying implementation.
     */
    public long getCurrentTime() throws QTException {
        return movie.getTime();
    }


    //--------------------------------------------------------------------------
    // [generated]
    //

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Action to invoke when the QTDataViewer window is closing (clean itself
     * up)
     *
     * @param evt The event that triggered this action.
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            //removeAll();
            movie.stop();
            //movie.disposeQTObject();
        } catch (QTException e) {
            logger.error("Couldn't kill", e);
        }
        this.parent.shutdown(this);
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
