/*
 * $RCSfile: DistortBehavior.java,v $
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
 * $Date: 2007/02/09 17:21:36 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.distort_glyph;

import java.util.Enumeration;

import javax.media.j3d.Appearance;
import javax.media.j3d.Behavior;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.media.j3d.WakeupOnElapsedTime;

import javax.vecmath.Vector3f;

public class DistortBehavior extends Behavior {
    // the wake up condition for the behavior
    protected WakeupCondition	m_InitialWakeupCondition	= null;
    protected WakeupCondition	m_FrameWakeupCondition		= null;

    // the GeometryArray for the Shape3D that we are modifying
    protected Shape3D		m_Shape3D			= null;
    protected GeometryArray	m_GeometryArray			= null;

    protected float[]		m_CoordinateArray		= null;
    protected float[]		m_OriginalCoordinateArray	= null;
    protected Appearance	m_Appearance			= null;

    protected int		m_nElapsedTime			= 0;
    protected int		m_nNumFrames			= 0;
    protected int		m_nFrameNumber			= 0;

    private int			frame				= 0;
    protected Vector3f		m_Vector			= null;

    public DistortBehavior(Shape3D shape3D, int nElapsedTime, int nNumFrames) {
        // allocate a temporary vector
        m_Vector = new Vector3f();

        m_FrameWakeupCondition = new WakeupOnElapsedFrames(0);

        restart(shape3D, nElapsedTime, nNumFrames);
    }

    public WakeupCondition restart(Shape3D shape3D, int nElapsedTime, int nNumFrames) {
        m_Shape3D = shape3D;
        m_nElapsedTime = nElapsedTime;
        m_nNumFrames = nNumFrames;
        m_nFrameNumber = 0;

        // create the WakeupCriterion for the behavior
        m_InitialWakeupCondition = new WakeupOnElapsedTime(m_nElapsedTime);

        // save the GeometryArray that we are modifying
        m_GeometryArray = (GeometryArray) m_Shape3D.getGeometry();

        if (m_Shape3D.isLive() == false && m_Shape3D.isCompiled() == false) {
            // set the capability bits that the behavior requires
            m_Shape3D.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
            m_Shape3D.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

            m_Shape3D.getAppearance().setCapability(Appearance.ALLOW_POINT_ATTRIBUTES_WRITE);
            m_Shape3D.getAppearance().setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);
            m_Shape3D.getAppearance().setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
            m_Shape3D.getAppearance().setCapability(Appearance.ALLOW_TEXTURE_WRITE);

            m_GeometryArray.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
            m_GeometryArray.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
            m_GeometryArray.setCapability(GeometryArray.ALLOW_COUNT_READ);
        }

        // make a copy of the object's original appearance
        m_Appearance = new Appearance();
        m_Appearance = (Appearance) m_Shape3D.getAppearance().cloneNodeComponent(true);

        // allocate an array for the model coordinates
        m_CoordinateArray = new float[3 * m_GeometryArray.getVertexCount()];

        // make a copy of the models original coordinates
        m_OriginalCoordinateArray = new float[3 * m_GeometryArray.getVertexCount()];
        m_GeometryArray.getCoordinates(0, m_OriginalCoordinateArray);

        // start (or restart) the behavior
        setEnable(true);

        return m_InitialWakeupCondition;
    }

    public void initialize() {
        // apply the initial WakeupCriterion
        wakeupOn(m_InitialWakeupCondition);
    }

    public void processStimulus(Enumeration criteria) {
        while (criteria.hasMoreElements()) {
            WakeupCriterion wakeUp = (WakeupCriterion) criteria.nextElement();

            if (wakeUp instanceof WakeupOnElapsedTime) {
            } else {
                // we are mid explosion, modify the GeometryArray
                m_nFrameNumber++;
                frame++;
                m_GeometryArray.getCoordinates(0, m_CoordinateArray);

                Transform3D t3 = new Transform3D();
                for (int n = 0; n < m_CoordinateArray.length; n += 3) {
                    m_Vector.x = m_OriginalCoordinateArray[n];
                    m_Vector.y = m_OriginalCoordinateArray[n + 1];
                    m_Vector.z = m_OriginalCoordinateArray[n + 2];

                    float spx = (float) (Math.sin(frame *3f / 500));
                    float spy = (float) (Math.cos(frame *5f / 500));
                    Vector3f v = new Vector3f(spx, spy, 0);

                    float px = (m_Vector.x - v.x);
                    float py = (m_Vector.y - v.y);
                    float pz = (m_Vector.z - v.z);
                    float d = (float) Math.sqrt(px * px + py * py + pz * pz);


                    m_Vector.add(new Vector3f(-.25f, -.25f, -.25f));
                    //m_Vector.scale(d);

                    t3.rotZ(d);
                    t3.rotX(d*2);
                    t3.rotY(d);
                    t3.transform(m_Vector);

                    m_CoordinateArray[n] = m_Vector.x;
                    m_CoordinateArray[n + 1] = m_Vector.y;
                    m_CoordinateArray[n + 2] = m_Vector.z;

                }

                // assign the new coordinates
                m_GeometryArray.setCoordinates(0, m_CoordinateArray);
            }
        }

        if (m_nFrameNumber < m_nNumFrames) {
            // assign the next WakeUpCondition, so we are notified again
            wakeupOn(m_FrameWakeupCondition);
        } else {
            // restart
            m_nFrameNumber = 0;
            wakeupOn(m_FrameWakeupCondition);
        }
    }
}
