package net.sourceforge.jaad.mp4.od;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

/**
 * The <code>DecoderConfigDescriptor</code> provides information about the decoder type and the
 * required decoder resources needed for the associates elementary stream. This is needed at the
 * receiver to determine whether it is able to decode the elementary stream. A stream type
 * identifies the category of the stream while the optional decoder specific information contains
 * stream specific information for the set up of the decoder in a stream specific format that is
 * opaque to this layer.
 *
 * @author in-somnia
 */
public class DecoderConfigDescriptor extends Descriptor {

  private int objectProfile, streamType, decodingBufferSize;
  private boolean upstream;
  private long maxBitRate, averageBitRate;

  void decode(MP4InputStream in) throws IOException {
    objectProfile = in.read();
    // 6 bits stream type, 1 bit upstream flag, 1 bit reserved
    final int x = in.read();
    streamType = (x >> 2) & 0x3F;
    upstream = ((x >> 1) & 1) == 1;

    decodingBufferSize = (int) in.readBytes(3);
    maxBitRate = in.readBytes(4);
    averageBitRate = in.readBytes(4);

    readChildren(in);
  }

  /**
   * An indication for the object profile (or scene description profile if streamType==scene
   * description stream), that needs to be supported by the decoder for this elementary stream as
   * per the following table. For streamType==object descriptor stream the value 0xFF ('no profile
   * specified') shall be used.
   *
   * <table>
   * <caption></caption>
   * <tr><th>Value</th><th>Description</th></tr>
   * <tr><td>0x00</td><td>Reserved for ISO use</td></tr>
   * <tr><td>0x01</td><td>ISO 14496-1:Systems Simple Scene Description</td></tr>
   * <tr><td>0x02</td><td>ISO 14496-1:Systems 2D Scene Description</td></tr>
   * <tr><td>0x03</td><td>ISO 14496-1:Systems VRML Scene Description</td></tr>
   * <tr><td>0x04</td><td>ISO 14496-1:Systems Audio Scene Description</td></tr>
   * <tr><td>0x05</td><td>ISO 14496-1:Systems Complete Scene Description</td></tr>
   * <tr><td>0x06-0x1F</td><td>reserved for ISO use</td></tr>
   * <tr><td>0x20</td><td>ISO 14496-2:Visual Simple Profile</td></tr>
   * <tr><td>0x21</td><td>ISO 14496-2:Visual Core Profile</td></tr>
   * <tr><td>0x22</td><td>ISO 14496-2:Visual Main Profile</td></tr>
   * <tr><td>0x23</td><td>ISO 14496-2:Visual Simple Scalable Profile</td></tr>
   * <tr><td>0x24</td><td>ISO 14496-2:Visual 12 Bit</td></tr>
   * <tr><td>0x25</td><td>ISO 14496-2:Visual Basic Anim. 2D Texture</td></tr>
   * <tr><td>0x26</td><td>ISO 14496-2:Visual Anim. 2D Mesh</td></tr>
   * <tr><td>0x27</td><td>ISO 14496-2:Visual Simple Face</td></tr>
   * <tr><td>0x28</td><td>ISO 14496-2:Visual Simple Scalable Texture</td></tr>
   * <tr><td>0x29</td><td>ISO 14496-2:Visual Core Scalable Texture</td></tr>
   * <tr><td>0x2A-0x3F</td><td>reserved for ISO use</td></tr>
   * <tr><td>0x40</td><td>ISO 14496-3:Audio AAC Main</td></tr>
   * <tr><td>0x41</td><td>ISO 14496-3:Audio AAC LC</td></tr>
   * <tr><td>0x42</td><td>ISO 14496-3:Audio T/F</td></tr>
   * <tr><td>0x43</td><td>ISO 14496-3:Audio T/F Main Scalable</td></tr>
   * <tr><td>0x44</td><td>ISO 14496-3:Audio T/F LC Scalable</td></tr>
   * <tr><td>0x45</td><td>ISO 14496-3:Audio TwinVQ Core</td></tr>
   * <tr><td>0x46</td><td>ISO 14496-3:Audio CELP</td></tr>
   * <tr><td>0x47</td><td>ISO 14496-3:Audio HVXC</td></tr>
   * <tr><td>0x48</td><td>ISO 14496-3:Audio HILN</td></tr>
   * <tr><td>0x49</td><td>ISO 14496-3:Audio TTSI</td></tr>
   * <tr><td>0x4A</td><td>ISO 14496-3:Audio Main Synthetic</td></tr>
   * <tr><td>0x4B</td><td>ISO 14496-3:Audio Wavetable Synthetic</td></tr>
   * <tr><td>0x4C-0x5F</td><td>reserved for ISO use</td></tr>
   * <tr><td>0x60</td><td>ISO 13818-2:Visual Simple Profile</td></tr>
   * <tr><td>0x61</td><td>ISO 13818-2:Visual Main Profile</td></tr>
   * <tr><td>0x62</td><td>ISO 13818-2:Visual SNR Profile</td></tr>
   * <tr><td>0x63</td><td>ISO 13818-2:Visual Spatial Profile</td></tr>
   * <tr><td>0x64</td><td>ISO 13818-2:Visual High Profile</td></tr>
   * <tr><td>0x65</td><td>ISO 13818-2:Visual 442 Profile</td></tr>
   * <tr><td>0x66</td><td>ISO 13818-7:Audio (MPEG-2 AAC)</td></tr>
   * <tr><td>0x67</td><td>ISO 13818-3:Audio (MPEG-2 layer 1/2/3)</td></tr>
   * <tr><td>0x68</td><td>ISO 11172-2:Visual (MPEG-1)</td></tr>
   * <tr><td>0x69</td><td>ISO 11172-3:Audio (MPEG-1)</td></tr>
   * <tr><td>0xC0-0xFE</td><td>user private</td></tr>
   * <tr><td>0xFF</td><td>no profile specified</td></tr>
   * </table>
   *
   * @return the object profile
   */
  public int getObjectProfile() {
    return objectProfile;
  }

  /**
   * The stream type conveys the type of this elementary stream as per the following table:
   *
   * <table>
   * <caption></caption>
   * <tr><th>Value</th><th>Description</th></tr>
   * <tr><td>0x00</td><td>reserved for ISO use</td></tr>
   * <tr><td>0x01</td><td>ObjectDescriptorStream</td></tr>
   * <tr><td>0x02</td><td>ClockReferenceStream</td></tr>
   * <tr><td>0x03</td><td>SceneDescriptionStream</td></tr>
   * <tr><td>0x04</td><td>VisualStream</td></tr>
   * <tr><td>0x05</td><td>AudioStream</td></tr>
   * <tr><td>0x06</td><td>MPEG-7 Stream</td></tr>
   * <tr><td>0x07-0x09</td><td>reserved for ISO use</td></tr>
   * <tr><td>0x0A</td><td>ObjectContentInfoStream</td></tr>
   * <tr><td>0x0B-0x1F</td><td>reserved for ISO use</td></tr>
   * <tr><td>0x20-0x3F</td><td>user private</td></tr>
   * </table>
   *
   * @return the stream type
   */
  public int getStreamType() {
    return streamType;
  }

  /**
   * Indicates if this stream is used for upstream information.
   *
   * @return true if this stream is used for upstream information
   */
  public boolean isUpstream() {
    return upstream;
  }

  /**
   * The size of the decoding buffer for this elementary stream in byte.
   *
   * @return the decoding buffer size
   */
  public int getDecodingBufferSize() {
    return decodingBufferSize;
  }

  /**
   * The maximum bitrate of this elementary stream in any time window of one second duration.
   *
   * @return the maximum bitrate
   */
  public long getMaxBitRate() {
    return maxBitRate;
  }

  /**
   * The average bitrate of the elementary stream. For streams with variable bitrates this value
   * shall be set to zero.
   *
   * @return the average bitrate or 0 if the stream has a variable bitrate
   */
  public long getAverageBitRate() {
    return averageBitRate;
  }
}
