package net.sourceforge.jaad.mp4.api;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.sourceforge.jaad.mp4.boxes.impl.meta.ITunesMetadataBox.DataType;

public class Artwork {

  // TODO: need this enum? it just copies the DataType
  public enum Type {
    GIF,
    JPEG,
    PNG,
    BMP;

    static Type forDataType(DataType dataType) {
      Type type;
      switch (dataType) {
        case GIF:
          type = GIF;
          break;
        case JPEG:
          type = JPEG;
          break;
        case PNG:
          type = PNG;
          break;
        case BMP:
          type = BMP;
          break;
        default:
          type = null;
      }
      return type;
    }
  }

  private Type type;
  private byte[] data;
  private Image image;

  Artwork(Type type, byte[] data) {
    this.type = type;
    this.data = data;
  }

  /**
   * Returns the type of data in this artwork.
   *
   * @see Type
   * @return the data's type
   */
  public Type getType() {
    return type;
  }

  /**
   * Returns the encoded data of this artwork.
   *
   * @return the encoded data
   */
  public byte[] getData() {
    return data;
  }

  /**
   * Returns the decoded image, that can be painted.
   *
   * @return the decoded image
   * @throws IOException if decoding fails
   */
  public Image getImage() throws IOException {
    try {
      if (image == null) image = ImageIO.read(new ByteArrayInputStream(data));
      return image;
    } catch (IOException e) {
      Logger.getLogger("MP4 API").log(Level.SEVERE, "Artwork.getImage failed: {0}", e.toString());
      throw e;
    }
  }
}
