/*
 * $RCSfile: CollisionDetector.java,v $
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
 * $Date: 2007/02/09 17:21:33 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.collision;

import java.util.Enumeration;
import javax.media.j3d.*;
import javax.vecmath.*;

public class CollisionDetector extends Behavior {
    private static final Color3f highlightColor =
	new Color3f(0.0f, 1.0f, 0.0f);
    private static final ColoringAttributes highlight =
	new ColoringAttributes(highlightColor,
			       ColoringAttributes.SHADE_GOURAUD);

    private boolean inCollision = false;
    private Shape3D shape;
    private ColoringAttributes shapeColoring;
    private Appearance shapeAppearance;

    private WakeupOnCollisionEntry wEnter;
    private WakeupOnCollisionExit wExit;


    public CollisionDetector(Shape3D s) {
	shape = s;
	shapeAppearance = shape.getAppearance();
	shapeColoring = shapeAppearance.getColoringAttributes();
	inCollision = false;
    }

    public void initialize() {
	wEnter = new WakeupOnCollisionEntry(shape);
	wExit = new WakeupOnCollisionExit(shape);
	wakeupOn(wEnter);
    }

    public void processStimulus(Enumeration criteria) {
	inCollision = !inCollision;

	if (inCollision) {
	    shapeAppearance.setColoringAttributes(highlight);
	    wakeupOn(wExit);
	}
	else {
	    shapeAppearance.setColoringAttributes(shapeColoring);
	    wakeupOn(wEnter);
	}
    }
}
