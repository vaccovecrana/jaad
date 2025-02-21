package net.sourceforge.jaad.spi.javasound;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import net.sourceforge.jaad.aac.Decoder;
import net.sourceforge.jaad.aac.SampleBuffer;
import net.sourceforge.jaad.mp4.MP4Container;
import net.sourceforge.jaad.mp4.api.AudioTrack;
import net.sourceforge.jaad.mp4.api.Frame;
import net.sourceforge.jaad.mp4.api.Movie;
import net.sourceforge.jaad.mp4.api.Track;

class MP4AudioInputStream extends AsynchronousAudioInputStream {

  private final AudioTrack track;
  private final Decoder decoder;
  private final SampleBuffer sampleBuffer;
  private AudioFormat audioFormat;
  private byte[] saved;

  MP4AudioInputStream(InputStream in, AudioFormat format, long length) throws IOException {
    super(in, format, length);
    final MP4Container cont = new MP4Container(in);
    final Movie movie = cont.getMovie();
    final List<Track> tracks = movie.getTracks(AudioTrack.AudioCodec.AAC);
    if (tracks.isEmpty()) throw new IOException("movie does not contain any AAC track");
    track = (AudioTrack) tracks.get(0);

    decoder = new Decoder(track.getDecoderSpecificInfo());
    sampleBuffer = new SampleBuffer();
  }

  @Override
  public AudioFormat getFormat() {
    if (audioFormat == null) {
      // read first frame
      decodeFrame();
      audioFormat =
          new AudioFormat(
              sampleBuffer.getSampleRate(),
              sampleBuffer.getBitsPerSample(),
              sampleBuffer.getChannels(),
              true,
              true);
      saved = sampleBuffer.getData();
    }
    return audioFormat;
  }

  public void execute() {
    if (saved == null) {
      decodeFrame();
      if (buffer.isOpen()) buffer.write(sampleBuffer.getData());
    } else {
      buffer.write(saved);
      saved = null;
    }
  }

  private void decodeFrame() {
    if (!track.hasMoreFrames()) {
      buffer.close();
      return;
    }
    try {
      final Frame frame = track.readNextFrame();
      if (frame == null) {
        buffer.close();
        return;
      }
      decoder.decodeFrame(frame.getData(), sampleBuffer);
    } catch (IOException e) {
      buffer.close();
      return;
    }
  }
}
