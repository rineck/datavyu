/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openshapa.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.openshapa.graphics.NeedlePainter;
import org.openshapa.graphics.TimescalePainter;
import org.openshapa.graphics.TrackPainter;

/**
 * This class manages the tracks information interface
 */
public class TracksControllerV {

    // Root interface panel
    private JPanel tracksPanel;
    // Panel that holds individual tracks
    private JPanel tracksInfoPanel;
    // Component that is responsible for rendering the time scale
    private TimescalePainter scale;
    // Scroll pane that holds track information
    private JScrollPane tracksScrollPane;
    // Component responsible for painting the timing needle
    private NeedlePainter needle;

    private JLayeredPane layeredPane;

    /* Zoomed into the display by how much.
     * Values should only be 1, 2, 4, 8, 16, 32
     */
    private int zoomSetting = 1;
    /**
     * The value of the longest video's time length in milliseconds
     */
    private long maxEnd;
    /**
     * The value of the earliest video's start time in milliseconds
     */
    private long minStart;

    private List<TrackPainter> trackPainterList;

    public TracksControllerV() {
        // Set default scale values
        maxEnd = 60000;
        minStart = 0;

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 295));
        layeredPane.setSize(layeredPane.getPreferredSize());

        // Set up the root panel
        tracksPanel = new JPanel();
        tracksPanel.setLayout(new GridBagLayout());
        tracksPanel.setBackground(Color.WHITE);

        // Menu buttons
        JButton lockButton = new JButton("Lock");
        JButton bookmarkButton = new JButton("Add Bookmark");
        JButton snapButton = new JButton("Snap");

        lockButton.setEnabled(false);
        bookmarkButton.setEnabled(false);
        snapButton.setEnabled(false);

        JButton zoomInButton = new JButton("( + )");
        zoomInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomInScale(e);
                zoomTracks(e);
            }
        });

        JButton zoomOutButton = new JButton("( - )");
        zoomOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomOutScale(e);
                zoomTracks(e);
            }
        });

        
        final int pad = 3;
        int xOffset = pad * 3;

        lockButton.setSize(lockButton.getPreferredSize());
        lockButton.setLocation(xOffset, 0);
        layeredPane.add(lockButton, new Integer(0));
        xOffset += lockButton.getSize().width + pad;

        bookmarkButton.setSize(bookmarkButton.getPreferredSize());
        bookmarkButton.setLocation(xOffset, 0);
        layeredPane.add(bookmarkButton, new Integer(0));
        xOffset += bookmarkButton.getSize().width + pad;

        snapButton.setSize(snapButton.getPreferredSize());
        snapButton.setLocation(xOffset, 0);
        layeredPane.add(snapButton, new Integer(0));

        zoomOutButton.setSize(zoomOutButton.getPreferredSize());
        xOffset = layeredPane.getSize().width - (pad + pad + zoomOutButton.getSize().width);
        zoomOutButton.setLocation(xOffset, 0);
        layeredPane.add(zoomOutButton, new Integer(0));

        zoomInButton.setSize(zoomInButton.getPreferredSize());
        xOffset -= (pad + zoomInButton.getSize().width);
        zoomInButton.setLocation(xOffset, 0);
        layeredPane.add(zoomInButton, new Integer(0));

        int yOffset = lockButton.getSize().height + pad;

        // Add the timescale
        scale = new TimescalePainter();
        {
            Dimension size = new Dimension();
            size.setSize(785, 35);
            scale.setSize(size);
            scale.setPreferredSize(size);
            scale.setConstraints(minStart, maxEnd, zoomIntervals(1));
            scale.setLocation(10, yOffset);
        }
        layeredPane.add(scale, new Integer(0));

        yOffset += pad + scale.getSize().height;

        // Add the scroll pane

        tracksInfoPanel = new JPanel();
        tracksInfoPanel.setLayout(new GridBagLayout());
        tracksScrollPane = new JScrollPane(tracksInfoPanel);
        tracksScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tracksScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        tracksScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Set an explicit size of the scroll pane
        {
            Dimension size = new Dimension();
            size.setSize(785, 227);
            tracksScrollPane.setSize(size);
            tracksScrollPane.setPreferredSize(size);
            tracksScrollPane.setLocation(10, yOffset);
        }
        layeredPane.add(tracksScrollPane, new Integer(0));

        needle = new NeedlePainter();
        {
            Dimension size = new Dimension();
            size.setSize(765, 274);
            needle.setSize(size);
            needle.setPreferredSize(size);
            needle.setLocation(10, 0);
            needle.setPadding(lockButton.getSize().height + pad, 101);
            needle.setWindowStart(0);
            needle.setWindowEnd(60000);
            needle.setIntervalTime(scale.getIntervalTime());
            needle.setIntervalWidth(scale.getIntervalWidth());
        }
        layeredPane.add(needle, new Integer(10));

        {
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 1;
            tracksPanel.add(layeredPane, c);
        }
        
        tracksPanel.validate();

        trackPainterList = new ArrayList<TrackPainter>();
    }

    /**
     * @return the panel containing the tracks interface.
     */
    public JPanel getTracksPanel() {
        return tracksPanel;
    }

    /**
     * Add a new track to the interface.
     * @param trackName name of the track
     */
    public void addNewTrack(String trackName) {
        JLabel trackLabel = new JLabel(trackName);

        int newRow = tracksInfoPanel.getComponentCount();

        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
        {
            Dimension size = new Dimension();
            size.height = 70;
            size.width = 100;
            infoPanel.setPreferredSize(size);
            infoPanel.add(trackLabel);
            
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = newRow;
            tracksInfoPanel.add(infoPanel, c);
        }

        JPanel carriagePanel = new JPanel();
        carriagePanel.setLayout(new BorderLayout());
        carriagePanel.setBorder(BorderFactory.createMatteBorder(2, 0, 2, 2, Color.BLACK));

        TrackPainter trackPainter = new TrackPainter();
        {
            Dimension size = new Dimension();
            size.height = 66;
            size.width = 665;
            trackPainter.setPreferredSize(size);
        }
        trackPainter.setStart(0);
        trackPainter.setEnd(30000);
        trackPainter.setOffset(6000);
        trackPainter.setIntervalTime(scale.getIntervalTime());
        trackPainter.setIntervalWidth(scale.getIntervalWidth());
        trackPainter.setZoomWindowStart(scale.getStart());
        trackPainter.setZoomWindowEnd(scale.getEnd());

        trackPainterList.add(trackPainter);

        carriagePanel.add(trackPainter, BorderLayout.PAGE_START);
        {
            Dimension size = new Dimension();
            size.height = 70;
            size.width = 665;
            carriagePanel.setPreferredSize(size);
            
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = newRow;
            c.insets = new Insets(0,0,1,0);
            tracksInfoPanel.add(carriagePanel, c);
        }

        tracksPanel.validate();
    }

    public void setCurrentTime(long time) {
        needle.setCurrentTime(time);
        needle.repaint();
    }

    /**
     * Zooms into the displayed scale and re-adjusts the timing needle
     * accordingly.
     * @param evt
     */
    public void zoomInScale(ActionEvent evt) {
        zoomSetting = zoomSetting * 2;
        if (zoomSetting > 32) {
            zoomSetting = 32;
        }

        long range = maxEnd - minStart;
        long mid = range / 2;
        long newStart = mid - (range / zoomSetting / 2);
        long newEnd = mid + (range / zoomSetting / 2);
        
        scale.setConstraints(newStart, newEnd, zoomIntervals(zoomSetting));

        needle.setWindowStart(newStart);
        needle.setWindowEnd(newEnd);
        needle.setIntervalTime(scale.getIntervalTime());
        needle.setIntervalWidth(scale.getIntervalWidth());

        scale.repaint();
        needle.repaint();
    }

    /**
     * Zooms out of the displayed scale and re-adjusts the timing needle 
     * accordingly.
     * @param evt
     */
    public void zoomOutScale(ActionEvent evt) {
        zoomSetting = zoomSetting / 2;
        if (zoomSetting < 1) {
            zoomSetting = 1;
        }

        long range = maxEnd - minStart;
        long mid = range / 2;
        long newStart = mid - (range / zoomSetting / 2);
        long newEnd = mid + (range / zoomSetting / 2);
        
        if (zoomSetting == 1) {
            newStart = minStart;
            newEnd = maxEnd;
        }

        scale.setConstraints(newStart, newEnd, zoomIntervals(zoomSetting));

        needle.setWindowStart(newStart);
        needle.setWindowEnd(newEnd);
        needle.setIntervalTime(scale.getIntervalTime());
        needle.setIntervalWidth(scale.getIntervalWidth());
        
        scale.repaint();
        needle.repaint();
    }

    /**
     * Update the track display after a zoom.
     * @param evt
     */
    public void zoomTracks(ActionEvent evt) {
        for (TrackPainter tp : trackPainterList) {
            tp.setIntervalTime(scale.getIntervalTime());
            tp.setIntervalWidth(scale.getIntervalWidth());
            tp.setZoomWindowStart(scale.getStart());
            tp.setZoomWindowEnd(scale.getEnd());
            tp.repaint();
        }

        tracksInfoPanel.validate();
    }

    /**
     * @param zoomValue supports 1x, 2x, 4x, 8x, 16x, 32x
     * @return the amount of intervals to show given a zoom value
     */
    private int zoomIntervals(final int zoomValue) {
        assert(zoomValue >= 1);
        assert(zoomValue <= 32);
        if (zoomValue <= 2) {
            return 20;
        }
        if (zoomValue <= 8) {
            return 10;
        }
        if (zoomValue <= 32) {
            return 5;
        }
        // Default amount of zoom intervals
        return 20;
    }

}
