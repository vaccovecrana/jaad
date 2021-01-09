package net.sourceforge.jaad.aac.gain;

interface PQFTables {

  float[] PROTO_TABLE = {
    1.2206911E-5f,
    1.7261988E-5f,
    1.2300094E-5f,
    -1.0833943E-5f,
    -5.77725E-5f,
    -1.2764768E-4f,
    -2.0965187E-4f,
    -2.8166673E-4f,
    -3.123486E-4f,
    -2.673852E-4f,
    -1.19494245E-4f,
    1.396514E-4f,
    4.886414E-4f,
    8.7044627E-4f,
    0.001194943f,
    0.0013519708f,
    0.0012346314f,
    7.695321E-4f,
    -5.2242434E-5f,
    -0.0011516092f,
    -0.002353847f,
    -0.0034033123f,
    -0.004002855f,
    -0.0038745415f,
    -0.0028321072f,
    -8.503889E-4f,
    0.0018856751f,
    0.004968874f,
    0.0078056706f,
    0.0097027905f,
    0.009996043f,
    0.008201936f,
    0.0041642073f,
    -0.0018364454f,
    -0.0090384865f,
    -0.016241528f,
    -0.021939551f,
    -0.02453318f,
    -0.022591664f,
    -0.015122066f,
    -0.0017971713f,
    0.016903413f,
    0.039672315f,
    0.064487524f,
    0.08885003f,
    0.11011329f,
    0.12585402f,
    0.13422394f,
    0.13422394f,
    0.12585402f,
    0.11011329f,
    0.08885003f,
    0.064487524f,
    0.039672315f,
    0.016903413f,
    -0.0017971713f,
    -0.015122066f,
    -0.022591664f,
    -0.02453318f,
    -0.021939551f,
    -0.016241528f,
    -0.0090384865f,
    -0.0018364454f,
    0.0041642073f,
    0.008201936f,
    0.009996043f,
    0.0097027905f,
    0.0078056706f,
    0.004968874f,
    0.0018856751f,
    -8.503889E-4f,
    -0.0028321072f,
    -0.0038745415f,
    -0.004002855f,
    -0.0034033123f,
    -0.002353847f,
    -0.0011516092f,
    -5.2242434E-5f,
    7.695321E-4f,
    0.0012346314f,
    0.0013519708f,
    0.001194943f,
    8.7044627E-4f,
    4.886414E-4f,
    1.396514E-4f,
    -1.19494245E-4f,
    -2.673852E-4f,
    -3.123486E-4f,
    -2.8166673E-4f,
    -2.0965187E-4f,
    -1.2764768E-4f,
    -5.77725E-5f,
    -1.0833943E-5f,
    1.2300094E-5f,
    1.7261988E-5f,
    1.2206911E-5f
  };
  float[][] COEFS_Q0 = {
    {1.6629392f, -0.39018065f, -1.9615706f, -1.11114f},
    {1.9615705f, 1.6629392f, 1.1111404f, 0.39018047f},
    {1.9615705f, 1.6629392f, 1.1111404f, 0.39018047f},
    {1.6629392f, -0.39018065f, -1.9615706f, -1.11114f}
  };
  float[][] COEFS_Q1 = {
    {1.1111404f, -1.9615706f, 0.39018083f, 1.6629387f},
    {0.39018047f, -1.11114f, 1.6629387f, -1.9615704f},
    {-0.39018065f, 1.1111408f, -1.6629395f, 1.9615709f},
    {-1.1111407f, 1.9615705f, -0.39018044f, -1.6629392f}
  };
  float[][] COEFS_T0 = {
    {
      4.8827646E-5f,
      0.0012493944f,
      0.0049385256f,
      0.011328429f,
      0.016656829f,
      0.0071886852f,
      0.53689575f,
      0.060488265f,
      0.032807745f,
      0.015498166f,
      0.0054078833f,
      0.0011266669f
    },
    {
      6.904795E-5f,
      0.0010695409f,
      0.0030781284f,
      0.0034015556f,
      -0.0073457817f,
      -0.067613654f,
      0.50341606f,
      0.090366654f,
      0.03998417f,
      0.01601142f,
      0.004779772f,
      8.386075E-4f
    },
    {
      4.9200375E-5f,
      4.7797698E-4f,
      -2.0896974E-4f,
      -0.0075427005f,
      -0.036153946f,
      -0.15868926f,
      0.44045317f,
      0.09813272f,
      0.038811162f,
      0.013613249f,
      0.003481785f,
      5.1059073E-4f
    },
    {
      -4.333577E-5f,
      -5.586056E-4f,
      -0.004606437f,
      -0.019875497f,
      -0.06496611f,
      -0.2579501f,
      0.35540012f,
      0.087758206f,
      0.031222682f,
      0.009415388f,
      0.0019545655f,
      2.3109E-4f
    }
  };
  float[][] COEFS_T1 = {
    {
      -2.3109E-4f,
      -0.0019545655f,
      -0.009415388f,
      -0.031222682f,
      -0.087758206f,
      -0.35540012f,
      0.2579501f,
      0.06496611f,
      0.019875497f,
      0.004606437f,
      5.586056E-4f,
      4.333577E-5f
    },
    {
      -5.1059073E-4f,
      -0.003481785f,
      -0.013613249f,
      -0.038811162f,
      -0.09813272f,
      -0.44045317f,
      0.15868926f,
      0.036153946f,
      0.0075427005f,
      2.0896974E-4f,
      -4.7797698E-4f,
      -4.9200375E-5f
    },
    {
      -8.386075E-4f,
      -0.004779772f,
      -0.01601142f,
      -0.03998417f,
      -0.090366654f,
      -0.50341606f,
      0.067613654f,
      0.0073457817f,
      -0.0034015556f,
      -0.0030781284f,
      -0.0010695409f,
      -6.904795E-5f
    },
    {
      -0.0011266669f,
      -0.0054078833f,
      -0.015498166f,
      -0.032807745f,
      -0.060488265f,
      -0.53689575f,
      -0.0071886852f,
      -0.016656829f,
      -0.011328429f,
      -0.0049385256f,
      -0.0012493944f,
      -4.8827646E-5f
    }
  };
}
