/*
*  Copyright (C) 2011 in-somnia
*
*  This file is part of JAAD.
*
*  JAAD is free software; you can redistribute it and/or modify it
*  under the terms of the GNU Lesser General Public License as
*  published by the Free Software Foundation; either version 3 of the
*  License, or (at your option) any later version.
*
*  JAAD is distributed in the hope that it will be useful, but WITHOUT

*  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General
*  Public License for more details.
*
*  You should have received a copy of the GNU Lesser General Public
*  License along with this library.
*  If not, see <http://www.gnu.org/licenses/>.
*/
package net.sourceforge.jaad.mp4.boxes.impl.sampleentries;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

/**
 * The MPEG sample entry is used in MP4 streams other than video, audio and hint. It contains only
 * one <code>ESDBox</code>.
 *
 * @author in-somnia
 */
public class MPEGSampleEntry extends SampleEntry {

  public MPEGSampleEntry() {
    super("MPEG Sample Entry");
  }

  @Override
  public void decode(MP4InputStream in) throws IOException {
    super.decode(in);

    readChildren(in);
  }
}
