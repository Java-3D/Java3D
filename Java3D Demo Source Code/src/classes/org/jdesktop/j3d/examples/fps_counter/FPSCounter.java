/*
 * $RCSfile: FPSCounter.java,v $
 *
 * Copyright (c) 2007 Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 *
 * $Revision: 1.2 $
 * $Date: 2007/02/09 17:21:38 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.fps_counter;

import javax.media.j3d.*;
import javax.vecmath.*;
import java.text.*;

/** This behavior calculates the frame rate and average frame rate of a
 * Java3D application.
 * The behavior sets itself up to wakeup every time a new frame is rendered.
 *
 * <p> The HotSpot(tm) compiler performs some initial optimizations before
 * running at optimal speed. Frame rates measured during this warmup period
 * will be inaccurate and not indicative of the true performance of the the
 * application. Therefore, before beginning the frame rate computation,
 * the frame counter waits for a fixed time period to allow the HotSpot(tm)
 * compiler to stablilize.
 *
 * <p> To avoid computing the frame rate too frequently (which would also
 * hamper rendering performance), the frame counter only computes the frame
 * rate at fixed time intervals. The default sampling duration is 10 seconds.
 * After waiting for the warmup period, the frame counter needs to calibrate
 * itself. It computes the number of frames rendered during the sampling
 * period. After doing this calibration, the frame counter reports the frame
 * rate after these many frames are rendered. It also reports the average
 * frame rate after a fixed number of sampling intervals (the default is 5).
 *
 * <p>The frame counter can be set up to run for a fixed number of sampling
 * intervals or to run indefinitely. The defaultis to run indefinitely.
 */

public class FPSCounter extends Behavior {
    // Wakeup condition - framecount = 0 -> wakeup on every frame
    WakeupOnElapsedFrames FPSwakeup = new WakeupOnElapsedFrames(0);

    // Do calibration for these many millisec
    private static final long testduration = 1000;

    // Report frame rate after every sampleduration milliseconds
    private static final long sampleduration = 10000;

    // Flag to indicate that it is time to (re)calibrate
    private boolean doCalibration = true;

    // Flag to indicate the counter has started
    private boolean startup = true;

    // Wait for HotSpot compiler to perform optimizations
    private boolean warmup = true;

    // Time to wait for HotSpot compiler to stabilize (in milliseconds)
    private long warmupTime = 20000;

    // Counter for number of frames rendered
    private int numframes = 0;

    // Report frame rate after maxframe number of frames have been rendered
    private int maxframes = 1;

    // Variables to keep track of elapsed time
    private long startuptime = 0;
    private long currtime = 0;
    private long lasttime = 0;
    private long deltatime;

    // Run indefinitely or for a fixed duration
    private boolean finiteLoop = false;

    // No. of sampling intervals to run for if not running indefinitely
    private long maxLoops;

    // No. of sampling intervals run for so far
    private long numLoops = 0;

    // Total number of frames rendered so far
    private int sumFrames = 0;

    // Total time since last reporting of average frame rate
    private long sumTimes = 0;

    // Counts no. of sampling intervals
    private int loop = 0;

    // Average frame rate is reported after loopCount number of
    // sampling intervals
    private int loopCount = 5;
    private double sumFps = 0.0;

    private String symbol[] = {"\\", "|", "|", "/", "-", "|", "-"};
    int index = 0;
    private NumberFormat nf = null;

    public FPSCounter() {
	setEnable(true);
	nf = NumberFormat.getNumberInstance();
    }

    /**
     * Called to init the behavior
     */
    public void initialize() {
	// Set the trigger for the behavior to wakeup on every frame rendered
	wakeupOn(FPSwakeup);
    }

    /**
     * Called every time the behavior is activated
     */
    public void processStimulus(java.util.Enumeration critera) {
	// Apply calibration algorithm to determine number of frames to
	// wait before computing frames per second.
	// sampleduration = 10000 -> to run test, pass for 10 seconds.

	if (doCalibration) { // start calibration
    	    if (startup) {
		// Record time at which the behavior was first invoked
		startuptime = System.currentTimeMillis();
		startup = false;
	    }
	    else if(warmup) { // Wait for the system to stabilize.
		System.out.print("\rFPSCounter warming up..." +
			symbol[(index++)%symbol.length]);
		currtime = System.currentTimeMillis();
		deltatime = currtime - startuptime;
		if(deltatime > warmupTime) {
		    // Done waiting for warmup
		    warmup = false;
		    lasttime = System.currentTimeMillis();
		    System.out.println("\rFPSCounter warming up...Done");
		}
	    }
	    else {
		numframes += 1;
		// Wait till at least maxframe no. of frames have been rendered
		if (numframes >= maxframes) {
		    currtime = System.currentTimeMillis();
		    deltatime = currtime - lasttime;
		    // Do the calibration for testduration no. of millisecs
		    if (deltatime > testduration) {
			// Compute total no. of frames rendered so far in the
			// current sampling duration
			maxframes = (int)Math.ceil((double)numframes *
				((double)sampleduration /
				 (double)deltatime));

			// Done with calibration
			doCalibration = false;
			// reset the value for the measurement
			numframes = 0;
			lasttime = System.currentTimeMillis();
		    }
		    else {
			// Need to run the calibration routine for some more
			// time. Increase the no. of frames to be rendered
			maxframes *= 2;
		    }
		}
	    }
	}
	else { // do the measurement
	    numframes += 1;
	    if (numframes >= maxframes) {
		currtime = System.currentTimeMillis();
		deltatime = currtime - lasttime;
		// time is in millisec, so multiply by 1000 to get frames/sec
		double fps = (double)numframes / ((double)deltatime / 1000.0);

		System.out.println("Frame Rate : \n\tNo. of frames : " +
			numframes + "\n\tTime : " +
			((double)deltatime / 1000.0) +
			" sec." + "\n\tFrames/sec : " + nf.format(fps));

		// Calculate average frame rate
		sumFrames += numframes;
		sumTimes += deltatime;
		sumFps += fps;
		loop++;
		if (loop >= loopCount) {
		    double avgFps = (double)sumFrames*1000.0/(double)sumTimes;
		    double ravgFps = sumFps/(double)loopCount;
		    System.out.println("Aggregate frame rate " +
			    nf.format(avgFps) + " frames/sec");
		    System.out.println("Average frame rate " +
			    nf.format(ravgFps) + " frames/sec");
		    numLoops++;
		    if (finiteLoop && numLoops >= maxLoops) {
			System.out.println("************** The End **************\n");
			setEnable(false);
		    }
		    loop = 0;
		    sumFps = 0;
		}
		numframes = 0;
		lasttime = System.currentTimeMillis();;
	    }
	}
	// Set the trigger for the behavior
	wakeupOn(FPSwakeup);
    }

    /**
     * The frame counter waits for some time before computing the
     * frame rate.  This allows the HotSpot compiler to perform
     * initial optimizations.  The amount of time to wait for is set
     * by this method. The default is 20000 (20 sec)
     *
     * @param amount of time to wait for before computing frame rate
     * (specified in milliseconds)
     */
    public void setWarmupTime(long wt) {
	warmupTime = wt;
    }

    /**
     * Sets the number of sampling intervals to wait for before computing
     * the average frame rate.
     * The default is 5.
     *
     * @param number of sampling intervals over which to compute frame rate.
     * A value of 0 implies the average frame rate is computed over one
     * sampling interval
     */
    public void setLoopCount(int lc) {
	loopCount = lc;
    }

    /**
     * This method sets the number of sampling intervals for which
     * the frame counter should run.
     *
     * @param number of sampling intervals to run for
     */
    public void setMaxLoops(int ml) {
	maxLoops = ml;
	finiteLoop = true;
    }

}
