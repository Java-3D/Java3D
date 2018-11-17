/*
 * $RCSfile: README.txt,v $
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
 * $Revision: 1.3 $
 * $Date: 2007/02/09 17:21:34 $
 * $State: Exp $
 */

This directory contains a simple example application that demonstrates the
ConfiguredUniverse utility class available in the com.sun.j3d.utils.universe
package.  J3DFly, available separately, is a fully featured application that
also uses ConfiguredUniverse.

ConfiguredUniverse is an extension of SimpleUniverse that can set up an
interactive viewing environment based upon the contents of a site-specific
configuration file.  This is useful when an application needs to run without
change across a broad range of viewing configurations and locally available
input and audio devices.  InputDevice implementations can be instantiated by
ConfiguredUniverse and their Sensors can be retrieved by applications along
with the names bound to them in the configuration file.

Supported viewing configurations include windows on conventional desktops,
stereo-enabled views, fullscreen immersive displays on single or multiple
screens, and virtual reality installations such as cave and head-mounted
displays incorporating 6-degree-of-freedom sensor devices.

The ConfigObjLoad application is a modified version of the ObjLoad example
program which uses the ConfiguredUniverse utility instead of SimpleUniverse.
It also differs in the following other respects:

    It is an application and cannot be run in a browser.  ConfiguredUniverse
    creates a JFrame, JPanel, and Canvas3D itself for each screen and is
    oriented towards multiple fullscreen viewing environments, although
    conventional windowed displays are also supported.  The components
    created are easily accessable so applications can still incorporate them
    into their own user interfaces.

    The configuration file to load is specified by the j3d.configURL
    property.  If one is not specified, it will load the file j3d1x1-window.cfg
    in this directory.

    Alternative custom view platform behaviors other than OrbitBehavior can
    be used by specifying the behavior in the configuration file.

    It can retrieve a 6DOF Sensor specified in the configuration file and
    use it to demonstrate the Mouse6DPointerBehavior class.

    Typing a "q" or the Escape key will terminate the example program.  This
    is useful for fullscreen configurations.

To load a specific configuration file, set the j3d.configURL property on the
command line:

    java -Dj3d.configURL=<URL string> ConfigObjLoad <args> <obj file>

For example, to load j3d1x2-rot30.cfg in the current directory, run

    java -Dj3d.configURL=file:j3d1x2-rot30.cfg ConfigObjLoad <args> <obj file>

This directory includes the following sample configuration files.  Normally
a configuration file is site-specific but many of these can used as-is.
Others may need customization for screen sizes, available input devices, and
PhysicalBody parameters.

    j3d1x1.cfg: single fullscreen desktop configuration.
    
    j3d1x1-behavior.cfg: single fullscreen desktop configuration with a
    configurable view platform behavior.

    j3d1x1-stereo.cfg: single fullscreen desktop configuration with stereo
    viewing.     

    j3d1x1-vr.cfg: single fullscreen desktop configuration with stereo viewing,
    head tracker, and 6DOF mouse.

    j3d1x1-window.cfg: single screen windowed desktop configuration.

    j3d1x2-flat.cfg: dual-screen flat desktop configuration.

    j3d1x2-rot30.cfg: dual-screen desktop configuration with each screen rotated
    toward the other by 30 degrees about Y.

    j3d1x3-cave.cfg: 3-projector configuration with screens to the left, front,
    and right of the user.

    j3d1x3-cave-vr.cfg: 3-projector configuration with screens to the left,
    front, and right of the user.  Includes head tracking and stereo
    viewing.

    j3d1x3-rot45.cfg: 3-screen desktop configuration with left and right screens
    angled by 45 degrees from the center screen.

    j3d2x2-flat.cfg: 4-screen projector configuration arranged in a 2x2 power
    wall. 

Note: JDK 1.4 or newer is required when configuring multiple screens if the
X11 Xinerama extension is being used to create a single virtual screen.
This is due to a limitation of the getScreenDevices() method in the JDK 1.3
version of GraphicsConfiguration which returns only a single GraphicsDevice
from a virtual screen.  ConfiguredUniverse will report this condition as an
error in specifying more screens than are available.

Also: Graphics performance may be degraded in some environments when using a
virtual screen device.  See the description of the j3d.disableXinerama
property for possible performance improvements when using Xinerama.

