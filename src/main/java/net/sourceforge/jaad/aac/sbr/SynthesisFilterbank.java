package net.sourceforge.jaad.aac.sbr;

import java.util.Arrays;

class SynthesisFilterbank implements FilterbankTable {

  private static final float[][] qmf32_pre_twiddle = {
    {0.999924701839145f, -0.012271538285720f},
    {0.999322384588350f, -0.036807222941359f},
    {0.998118112900149f, -0.061320736302209f},
    {0.996312612182778f, -0.085797312344440f},
    {0.993906970002356f, -0.110222207293883f},
    {0.990902635427780f, -0.134580708507126f},
    {0.987301418157858f, -0.158858143333861f},
    {0.983105487431216f, -0.183039887955141f},
    {0.978317370719628f, -0.207111376192219f},
    {0.972939952205560f, -0.231058108280671f},
    {0.966976471044852f, -0.254865659604515f},
    {0.960430519415566f, -0.278519689385053f},
    {0.953306040354194f, -0.302005949319228f},
    {0.945607325380521f, -0.325310292162263f},
    {0.937339011912575f, -0.348418680249435f},
    {0.928506080473216f, -0.371317193951838f},
    {0.919113851690058f, -0.393992040061048f},
    {0.909167983090522f, -0.416429560097637f},
    {0.898674465693954f, -0.438616238538528f},
    {0.887639620402854f, -0.460538710958240f},
    {0.876070094195407f, -0.482183772079123f},
    {0.863972856121587f, -0.503538383725718f},
    {0.851355193105265f, -0.524589682678469f},
    {0.838224705554838f, -0.545324988422046f},
    {0.824589302785025f, -0.565731810783613f},
    {0.810457198252595f, -0.585797857456439f},
    {0.795836904608884f, -0.605511041404326f},
    {0.780737228572094f, -0.624859488142386f},
    {0.765167265622459f, -0.643831542889791f},
    {0.749136394523459f, -0.662415777590172f},
    {0.732654271672413f, -0.680600997795453f},
    {0.715730825283819f, -0.698376249408973f}
  };

  private float[] v; // double ringbuffer
  private int v_index; // ringbuffer index
  private final int channels;

  public SynthesisFilterbank(int channels) {
    this.channels = channels;
    v = new float[2 * channels * 20];
    v_index = 0;
  }

  public void reset() {
    Arrays.fill(v, 0);
  }

  void sbr_qmf_synthesis_32(SBR sbr, float[][][] X, float[] output) {
    float[] x1 = new float[32], x2 = new float[32];
    float scale = 1.f / 64.f;
    int n, k, out = 0;
    int l;

    /* qmf subsample l */
    for (l = 0; l < sbr.numTimeSlotsRate; l++) {
      /* shift buffer v */
      /* buffer is not shifted, we are using a ringbuffer */
      // memmove(qmfs.v + 64, qmfs.v, (640-64)*sizeof(real_t));

      /* calculate 64 samples */
      /* complex pre-twiddle */
      for (k = 0; k < 32; k++) {
        x1[k] = (X[l][k][0] * qmf32_pre_twiddle[k][0]) - (X[l][k][1] * qmf32_pre_twiddle[k][1]);
        x2[k] = (X[l][k][1] * qmf32_pre_twiddle[k][0]) + (X[l][k][0] * qmf32_pre_twiddle[k][1]);

        x1[k] *= scale;
        x2[k] *= scale;
      }

      /* transform */
      DCT4_32(x1, x1);
      DST4_32(x2, x2);

      for (n = 0; n < 32; n++) {
        this.v[this.v_index + n] = this.v[this.v_index + 640 + n] = -x1[n] + x2[n];
        this.v[this.v_index + 63 - n] = this.v[this.v_index + 640 + 63 - n] = x1[n] + x2[n];
      }

      /* calculate 32 output samples and window */
      for (k = 0; k < 32; k++) {
        output[out++] =
            (this.v[this.v_index + k] * qmf_c[2 * k])
                + (this.v[this.v_index + 96 + k] * qmf_c[64 + 2 * k])
                + (this.v[this.v_index + 128 + k] * qmf_c[128 + 2 * k])
                + (this.v[this.v_index + 224 + k] * qmf_c[192 + 2 * k])
                + (this.v[this.v_index + 256 + k] * qmf_c[256 + 2 * k])
                + (this.v[this.v_index + 352 + k] * qmf_c[320 + 2 * k])
                + (this.v[this.v_index + 384 + k] * qmf_c[384 + 2 * k])
                + (this.v[this.v_index + 480 + k] * qmf_c[448 + 2 * k])
                + (this.v[this.v_index + 512 + k] * qmf_c[512 + 2 * k])
                + (this.v[this.v_index + 608 + k] * qmf_c[576 + 2 * k]);
      }

      /* update ringbuffer index */
      this.v_index -= 64;
      if (this.v_index < 0) this.v_index = (640 - 64);
    }
  }

  void sbr_qmf_synthesis_64(SBR sbr, float[][][] X, float[] output) {
    float[] in_real1 = new float[32],
        in_imag1 = new float[32],
        out_real1 = new float[32],
        out_imag1 = new float[32];
    float[] in_real2 = new float[32],
        in_imag2 = new float[32],
        out_real2 = new float[32],
        out_imag2 = new float[32];
    float[][] pX;
    float scale = 1.f / 64.f;
    int n, k, out = 0;
    int l;

    /* qmf subsample l */
    for (l = 0; l < sbr.numTimeSlotsRate; l++) {
      /* shift buffer v */
      /* buffer is not shifted, we use double ringbuffer */
      // memmove(qmfs.v + 128, qmfs.v, (1280-128)*sizeof(real_t));

      /* calculate 128 samples */
      pX = X[l];

      in_imag1[31] = scale * pX[1][0];
      in_real1[0] = scale * pX[0][0];
      in_imag2[31] = scale * pX[63 - 1][1];
      in_real2[0] = scale * pX[63 - 0][1];
      for (k = 1; k < 31; k++) {
        in_imag1[31 - k] = scale * pX[2 * k + 1][0];
        in_real1[k] = scale * pX[2 * k][0];
        in_imag2[31 - k] = scale * pX[63 - (2 * k + 1)][1];
        in_real2[k] = scale * pX[63 - (2 * k)][1];
      }
      in_imag1[0] = scale * pX[63][0];
      in_real1[31] = scale * pX[62][0];
      in_imag2[0] = scale * pX[63 - 63][1];
      in_real2[31] = scale * pX[63 - 62][1];

      // dct4_kernel is DCT_IV without reordering which is done before and after FFT
      DCT.dct4_kernel(in_real1, in_imag1, out_real1, out_imag1);
      DCT.dct4_kernel(in_real2, in_imag2, out_real2, out_imag2);

      int pring_buffer_1 = v_index; // *v
      int pring_buffer_3 = pring_buffer_1 + 1280;
      //        ptemp_1 = x1;
      //        ptemp_2 = x2;

      for (n = 0; n < 32; n++) {
        // pring_buffer_3 and pring_buffer_4 are needed only for double ring buffer
        v[pring_buffer_1 + 2 * n] = v[pring_buffer_3 + 2 * n] = out_real2[n] - out_real1[n];
        v[pring_buffer_1 + 127 - 2 * n] =
            v[pring_buffer_3 + 127 - 2 * n] = out_real2[n] + out_real1[n];
        v[pring_buffer_1 + 2 * n + 1] =
            v[pring_buffer_3 + 2 * n + 1] = out_imag2[31 - n] + out_imag1[31 - n];
        v[pring_buffer_1 + 127 - (2 * n + 1)] =
            v[pring_buffer_3 + 127 - (2 * n + 1)] = out_imag2[31 - n] - out_imag1[31 - n];
      }

      pring_buffer_1 = v_index; // *v

      /* calculate 64 output samples and window */
      for (k = 0; k < 64; k++) {
        output[out++] =
            (v[pring_buffer_1 + k + 0] * qmf_c[k + 0])
                + (v[pring_buffer_1 + k + 192] * qmf_c[k + 64])
                + (v[pring_buffer_1 + k + 256] * qmf_c[k + 128])
                + (v[pring_buffer_1 + k + (256 + 192)] * qmf_c[k + 192])
                + (v[pring_buffer_1 + k + 512] * qmf_c[k + 256])
                + (v[pring_buffer_1 + k + (512 + 192)] * qmf_c[k + 320])
                + (v[pring_buffer_1 + k + 768] * qmf_c[k + 384])
                + (v[pring_buffer_1 + k + (768 + 192)] * qmf_c[k + 448])
                + (v[pring_buffer_1 + k + 1024] * qmf_c[k + 512])
                + (v[pring_buffer_1 + k + (1024 + 192)] * qmf_c[k + 576]);
      }

      /* update ringbuffer index */
      this.v_index -= 128;
      if (this.v_index < 0) this.v_index = (1280 - 128);
    }
  }

  private void DCT4_32(float[] y, float[] x) {
    float f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10;
    float f11, f12, f13, f14, f15, f16, f17, f18, f19, f20;
    float f21, f22, f23, f24, f25, f26, f27, f28, f29, f30;
    float f31, f32, f33, f34, f35, f36, f37, f38, f39, f40;
    float f41, f42, f43, f44, f45, f46, f47, f48, f49, f50;
    float f51, f52, f53, f54, f55, f56, f57, f58, f59, f60;
    float f61, f62, f63, f64, f65, f66, f67, f68, f69, f70;
    float f71, f72, f73, f74, f75, f76, f77, f78, f79, f80;
    float f81, f82, f83, f84, f85, f86, f87, f88, f89, f90;
    float f91, f92, f93, f94, f95, f96, f97, f98, f99, f100;
    float f101, f102, f103, f104, f105, f106, f107, f108, f109, f110;
    float f111, f112, f113, f114, f115, f116, f117, f118, f119, f120;
    float f121, f122, f123, f124, f125, f126, f127, f128, f129, f130;
    float f131, f132, f133, f134, f135, f136, f137, f138, f139, f140;
    float f141, f142, f143, f144, f145, f146, f147, f148, f149, f150;
    float f151, f152, f153, f154, f155, f156, f157, f158, f159, f160;
    float f161, f162, f163, f164, f165, f166, f167, f168, f169, f170;
    float f171, f172, f173, f174, f175, f176, f177, f178, f179, f180;
    float f181, f182, f183, f184, f185, f186, f187, f188, f189, f190;
    float f191, f192, f193, f194, f195, f196, f197, f198, f199, f200;
    float f201, f202, f203, f204, f205, f206, f207, f208, f209, f210;
    float f211, f212, f213, f214, f215, f216, f217, f218, f219, f220;
    float f221, f222, f223, f224, f225, f226, f227, f228, f229, f230;
    float f231, f232, f233, f234, f235, f236, f237, f238, f239, f240;
    float f241, f242, f243, f244, f245, f246, f247, f248, f249, f250;
    float f251, f252, f253, f254, f255, f256, f257, f258, f259, f260;
    float f261, f262, f263, f264, f265, f266, f267, f268, f269, f270;
    float f271, f272, f273, f274, f275, f276, f277, f278, f279, f280;
    float f281, f282, f283, f284, f285, f286, f287, f288, f289, f290;
    float f291, f292, f293, f294, f295, f296, f297, f298, f299, f300;
    float f301, f302, f303, f304, f305, f306, f307, f310, f311, f312;
    float f313, f316, f317, f318, f319, f322, f323, f324, f325, f328;
    float f329, f330, f331, f334, f335, f336, f337, f340, f341, f342;
    float f343, f346, f347, f348, f349, f352, f353, f354, f355, f358;
    float f359, f360, f361, f364, f365, f366, f367, f370, f371, f372;
    float f373, f376, f377, f378, f379, f382, f383, f384, f385, f388;
    float f389, f390, f391, f394, f395, f396, f397;

    f0 = x[15] - x[16];
    f1 = x[15] + x[16];
    f2 = (0.7071067811865476f * f1);
    f3 = (0.7071067811865476f * f0);
    f4 = x[8] - x[23];
    f5 = x[8] + x[23];
    f6 = (0.7071067811865476f * f5);
    f7 = (0.7071067811865476f * f4);
    f8 = x[12] - x[19];
    f9 = x[12] + x[19];
    f10 = (0.7071067811865476f * f9);
    f11 = (0.7071067811865476f * f8);
    f12 = x[11] - x[20];
    f13 = x[11] + x[20];
    f14 = (0.7071067811865476f * f13);
    f15 = (0.7071067811865476f * f12);
    f16 = x[14] - x[17];
    f17 = x[14] + x[17];
    f18 = (0.7071067811865476f * f17);
    f19 = (0.7071067811865476f * f16);
    f20 = x[9] - x[22];
    f21 = x[9] + x[22];
    f22 = (0.7071067811865476f * f21);
    f23 = (0.7071067811865476f * f20);
    f24 = x[13] - x[18];
    f25 = x[13] + x[18];
    f26 = (0.7071067811865476f * f25);
    f27 = (0.7071067811865476f * f24);
    f28 = x[10] - x[21];
    f29 = x[10] + x[21];
    f30 = (0.7071067811865476f * f29);
    f31 = (0.7071067811865476f * f28);
    f32 = x[0] - f2;
    f33 = x[0] + f2;
    f34 = x[31] - f3;
    f35 = x[31] + f3;
    f36 = x[7] - f6;
    f37 = x[7] + f6;
    f38 = x[24] - f7;
    f39 = x[24] + f7;
    f40 = x[3] - f10;
    f41 = x[3] + f10;
    f42 = x[28] - f11;
    f43 = x[28] + f11;
    f44 = x[4] - f14;
    f45 = x[4] + f14;
    f46 = x[27] - f15;
    f47 = x[27] + f15;
    f48 = x[1] - f18;
    f49 = x[1] + f18;
    f50 = x[30] - f19;
    f51 = x[30] + f19;
    f52 = x[6] - f22;
    f53 = x[6] + f22;
    f54 = x[25] - f23;
    f55 = x[25] + f23;
    f56 = x[2] - f26;
    f57 = x[2] + f26;
    f58 = x[29] - f27;
    f59 = x[29] + f27;
    f60 = x[5] - f30;
    f61 = x[5] + f30;
    f62 = x[26] - f31;
    f63 = x[26] + f31;
    f64 = f39 + f37;
    f65 = (-0.5411961001461969f * f39);
    f66 = (0.9238795325112867f * f64);
    f67 = (1.3065629648763766f * f37);
    f68 = f65 + f66;
    f69 = f67 - f66;
    f70 = f38 + f36;
    f71 = (1.3065629648763770f * f38);
    f72 = (-0.3826834323650904f * f70);
    f73 = (0.5411961001461961f * f36);
    f74 = f71 + f72;
    f75 = f73 - f72;
    f76 = f47 + f45;
    f77 = (-0.5411961001461969f * f47);
    f78 = (0.9238795325112867f * f76);
    f79 = (1.3065629648763766f * f45);
    f80 = f77 + f78;
    f81 = f79 - f78;
    f82 = f46 + f44;
    f83 = (1.3065629648763770f * f46);
    f84 = (-0.3826834323650904f * f82);
    f85 = (0.5411961001461961f * f44);
    f86 = f83 + f84;
    f87 = f85 - f84;
    f88 = f55 + f53;
    f89 = (-0.5411961001461969f * f55);
    f90 = (0.9238795325112867f * f88);
    f91 = (1.3065629648763766f * f53);
    f92 = f89 + f90;
    f93 = f91 - f90;
    f94 = f54 + f52;
    f95 = (1.3065629648763770f * f54);
    f96 = (-0.3826834323650904f * f94);
    f97 = (0.5411961001461961f * f52);
    f98 = f95 + f96;
    f99 = f97 - f96;
    f100 = f63 + f61;
    f101 = (-0.5411961001461969f * f63);
    f102 = (0.9238795325112867f * f100);
    f103 = (1.3065629648763766f * f61);
    f104 = f101 + f102;
    f105 = f103 - f102;
    f106 = f62 + f60;
    f107 = (1.3065629648763770f * f62);
    f108 = (-0.3826834323650904f * f106);
    f109 = (0.5411961001461961f * f60);
    f110 = f107 + f108;
    f111 = f109 - f108;
    f112 = f33 - f68;
    f113 = f33 + f68;
    f114 = f35 - f69;
    f115 = f35 + f69;
    f116 = f32 - f74;
    f117 = f32 + f74;
    f118 = f34 - f75;
    f119 = f34 + f75;
    f120 = f41 - f80;
    f121 = f41 + f80;
    f122 = f43 - f81;
    f123 = f43 + f81;
    f124 = f40 - f86;
    f125 = f40 + f86;
    f126 = f42 - f87;
    f127 = f42 + f87;
    f128 = f49 - f92;
    f129 = f49 + f92;
    f130 = f51 - f93;
    f131 = f51 + f93;
    f132 = f48 - f98;
    f133 = f48 + f98;
    f134 = f50 - f99;
    f135 = f50 + f99;
    f136 = f57 - f104;
    f137 = f57 + f104;
    f138 = f59 - f105;
    f139 = f59 + f105;
    f140 = f56 - f110;
    f141 = f56 + f110;
    f142 = f58 - f111;
    f143 = f58 + f111;
    f144 = f123 + f121;
    f145 = (-0.7856949583871021f * f123);
    f146 = (0.9807852804032304f * f144);
    f147 = (1.1758756024193588f * f121);
    f148 = f145 + f146;
    f149 = f147 - f146;
    f150 = f127 + f125;
    f151 = (0.2758993792829431f * f127);
    f152 = (0.5555702330196022f * f150);
    f153 = (1.3870398453221475f * f125);
    f154 = f151 + f152;
    f155 = f153 - f152;
    f156 = f122 + f120;
    f157 = (1.1758756024193591f * f122);
    f158 = (-0.1950903220161287f * f156);
    f159 = (0.7856949583871016f * f120);
    f160 = f157 + f158;
    f161 = f159 - f158;
    f162 = f126 + f124;
    f163 = (1.3870398453221473f * f126);
    f164 = (-0.8314696123025455f * f162);
    f165 = (-0.2758993792829436f * f124);
    f166 = f163 + f164;
    f167 = f165 - f164;
    f168 = f139 + f137;
    f169 = (-0.7856949583871021f * f139);
    f170 = (0.9807852804032304f * f168);
    f171 = (1.1758756024193588f * f137);
    f172 = f169 + f170;
    f173 = f171 - f170;
    f174 = f143 + f141;
    f175 = (0.2758993792829431f * f143);
    f176 = (0.5555702330196022f * f174);
    f177 = (1.3870398453221475f * f141);
    f178 = f175 + f176;
    f179 = f177 - f176;
    f180 = f138 + f136;
    f181 = (1.1758756024193591f * f138);
    f182 = (-0.1950903220161287f * f180);
    f183 = (0.7856949583871016f * f136);
    f184 = f181 + f182;
    f185 = f183 - f182;
    f186 = f142 + f140;
    f187 = (1.3870398453221473f * f142);
    f188 = (-0.8314696123025455f * f186);
    f189 = (-0.2758993792829436f * f140);
    f190 = f187 + f188;
    f191 = f189 - f188;
    f192 = f113 - f148;
    f193 = f113 + f148;
    f194 = f115 - f149;
    f195 = f115 + f149;
    f196 = f117 - f154;
    f197 = f117 + f154;
    f198 = f119 - f155;
    f199 = f119 + f155;
    f200 = f112 - f160;
    f201 = f112 + f160;
    f202 = f114 - f161;
    f203 = f114 + f161;
    f204 = f116 - f166;
    f205 = f116 + f166;
    f206 = f118 - f167;
    f207 = f118 + f167;
    f208 = f129 - f172;
    f209 = f129 + f172;
    f210 = f131 - f173;
    f211 = f131 + f173;
    f212 = f133 - f178;
    f213 = f133 + f178;
    f214 = f135 - f179;
    f215 = f135 + f179;
    f216 = f128 - f184;
    f217 = f128 + f184;
    f218 = f130 - f185;
    f219 = f130 + f185;
    f220 = f132 - f190;
    f221 = f132 + f190;
    f222 = f134 - f191;
    f223 = f134 + f191;
    f224 = f211 + f209;
    f225 = (-0.8971675863426361f * f211);
    f226 = (0.9951847266721968f * f224);
    f227 = (1.0932018670017576f * f209);
    f228 = f225 + f226;
    f229 = f227 - f226;
    f230 = f215 + f213;
    f231 = (-0.4105245275223571f * f215);
    f232 = (0.8819212643483549f * f230);
    f233 = (1.3533180011743529f * f213);
    f234 = f231 + f232;
    f235 = f233 - f232;
    f236 = f219 + f217;
    f237 = (0.1386171691990915f * f219);
    f238 = (0.6343932841636455f * f236);
    f239 = (1.4074037375263826f * f217);
    f240 = f237 + f238;
    f241 = f239 - f238;
    f242 = f223 + f221;
    f243 = (0.6666556584777466f * f223);
    f244 = (0.2902846772544623f * f242);
    f245 = (1.2472250129866711f * f221);
    f246 = f243 + f244;
    f247 = f245 - f244;
    f248 = f210 + f208;
    f249 = (1.0932018670017574f * f210);
    f250 = (-0.0980171403295605f * f248);
    f251 = (0.8971675863426364f * f208);
    f252 = f249 + f250;
    f253 = f251 - f250;
    f254 = f214 + f212;
    f255 = (1.3533180011743529f * f214);
    f256 = (-0.4713967368259979f * f254);
    f257 = (0.4105245275223569f * f212);
    f258 = f255 + f256;
    f259 = f257 - f256;
    f260 = f218 + f216;
    f261 = (1.4074037375263826f * f218);
    f262 = (-0.7730104533627369f * f260);
    f263 = (-0.1386171691990913f * f216);
    f264 = f261 + f262;
    f265 = f263 - f262;
    f266 = f222 + f220;
    f267 = (1.2472250129866711f * f222);
    f268 = (-0.9569403357322089f * f266);
    f269 = (-0.6666556584777469f * f220);
    f270 = f267 + f268;
    f271 = f269 - f268;
    f272 = f193 - f228;
    f273 = f193 + f228;
    f274 = f195 - f229;
    f275 = f195 + f229;
    f276 = f197 - f234;
    f277 = f197 + f234;
    f278 = f199 - f235;
    f279 = f199 + f235;
    f280 = f201 - f240;
    f281 = f201 + f240;
    f282 = f203 - f241;
    f283 = f203 + f241;
    f284 = f205 - f246;
    f285 = f205 + f246;
    f286 = f207 - f247;
    f287 = f207 + f247;
    f288 = f192 - f252;
    f289 = f192 + f252;
    f290 = f194 - f253;
    f291 = f194 + f253;
    f292 = f196 - f258;
    f293 = f196 + f258;
    f294 = f198 - f259;
    f295 = f198 + f259;
    f296 = f200 - f264;
    f297 = f200 + f264;
    f298 = f202 - f265;
    f299 = f202 + f265;
    f300 = f204 - f270;
    f301 = f204 + f270;
    f302 = f206 - f271;
    f303 = f206 + f271;
    f304 = f275 + f273;
    f305 = (-0.9751575901732920f * f275);
    f306 = (0.9996988186962043f * f304);
    f307 = (1.0242400472191164f * f273);
    y[0] = f305 + f306;
    y[31] = f307 - f306;
    f310 = f279 + f277;
    f311 = (-0.8700688593994936f * f279);
    f312 = (0.9924795345987100f * f310);
    f313 = (1.1148902097979263f * f277);
    y[2] = f311 + f312;
    y[29] = f313 - f312;
    f316 = f283 + f281;
    f317 = (-0.7566008898816587f * f283);
    f318 = (0.9757021300385286f * f316);
    f319 = (1.1948033701953984f * f281);
    y[4] = f317 + f318;
    y[27] = f319 - f318;
    f322 = f287 + f285;
    f323 = (-0.6358464401941451f * f287);
    f324 = (0.9495281805930367f * f322);
    f325 = (1.2632099209919283f * f285);
    y[6] = f323 + f324;
    y[25] = f325 - f324;
    f328 = f291 + f289;
    f329 = (-0.5089684416985408f * f291);
    f330 = (0.9142097557035307f * f328);
    f331 = (1.3194510697085207f * f289);
    y[8] = f329 + f330;
    y[23] = f331 - f330;
    f334 = f295 + f293;
    f335 = (-0.3771887988789273f * f295);
    f336 = (0.8700869911087114f * f334);
    f337 = (1.3629851833384954f * f293);
    y[10] = f335 + f336;
    y[21] = f337 - f336;
    f340 = f299 + f297;
    f341 = (-0.2417766217337384f * f299);
    f342 = (0.8175848131515837f * f340);
    f343 = (1.3933930045694289f * f297);
    y[12] = f341 + f342;
    y[19] = f343 - f342;
    f346 = f303 + f301;
    f347 = (-0.1040360035527077f * f303);
    f348 = (0.7572088465064845f * f346);
    f349 = (1.4103816894602612f * f301);
    y[14] = f347 + f348;
    y[17] = f349 - f348;
    f352 = f274 + f272;
    f353 = (0.0347065382144002f * f274);
    f354 = (0.6895405447370668f * f352);
    f355 = (1.4137876276885337f * f272);
    y[16] = f353 + f354;
    y[15] = f355 - f354;
    f358 = f278 + f276;
    f359 = (0.1731148370459795f * f278);
    f360 = (0.6152315905806268f * f358);
    f361 = (1.4035780182072330f * f276);
    y[18] = f359 + f360;
    y[13] = f361 - f360;
    f364 = f282 + f280;
    f365 = (0.3098559453626100f * f282);
    f366 = (0.5349976198870972f * f364);
    f367 = (1.3798511851368043f * f280);
    y[20] = f365 + f366;
    y[11] = f367 - f366;
    f370 = f286 + f284;
    f371 = (0.4436129715409088f * f286);
    f372 = (0.4496113296546065f * f370);
    f373 = (1.3428356308501219f * f284);
    y[22] = f371 + f372;
    y[9] = f373 - f372;
    f376 = f290 + f288;
    f377 = (0.5730977622997509f * f290);
    f378 = (0.3598950365349881f * f376);
    f379 = (1.2928878353697271f * f288);
    y[24] = f377 + f378;
    y[7] = f379 - f378;
    f382 = f294 + f292;
    f383 = (0.6970633083205415f * f294);
    f384 = (0.2667127574748984f * f382);
    f385 = (1.2304888232703382f * f292);
    y[26] = f383 + f384;
    y[5] = f385 - f384;
    f388 = f298 + f296;
    f389 = (0.8143157536286401f * f298);
    f390 = (0.1709618887603012f * f388);
    f391 = (1.1562395311492424f * f296);
    y[28] = f389 + f390;
    y[3] = f391 - f390;
    f394 = f302 + f300;
    f395 = (0.9237258930790228f * f302);
    f396 = (0.0735645635996674f * f394);
    f397 = (1.0708550202783576f * f300);
    y[30] = f395 + f396;
    y[1] = f397 - f396;
  }

  private void DST4_32(float[] y, float[] x) {
    float f0, f1, f2, f3, f4, f5, f6, f7, f8, f9;
    float f10, f11, f12, f13, f14, f15, f16, f17, f18, f19;
    float f20, f21, f22, f23, f24, f25, f26, f27, f28, f29;
    float f30, f31, f32, f33, f34, f35, f36, f37, f38, f39;
    float f40, f41, f42, f43, f44, f45, f46, f47, f48, f49;
    float f50, f51, f52, f53, f54, f55, f56, f57, f58, f59;
    float f60, f61, f62, f63, f64, f65, f66, f67, f68, f69;
    float f70, f71, f72, f73, f74, f75, f76, f77, f78, f79;
    float f80, f81, f82, f83, f84, f85, f86, f87, f88, f89;
    float f90, f91, f92, f93, f94, f95, f96, f97, f98, f99;
    float f100, f101, f102, f103, f104, f105, f106, f107, f108, f109;
    float f110, f111, f112, f113, f114, f115, f116, f117, f118, f119;
    float f120, f121, f122, f123, f124, f125, f126, f127, f128, f129;
    float f130, f131, f132, f133, f134, f135, f136, f137, f138, f139;
    float f140, f141, f142, f143, f144, f145, f146, f147, f148, f149;
    float f150, f151, f152, f153, f154, f155, f156, f157, f158, f159;
    float f160, f161, f162, f163, f164, f165, f166, f167, f168, f169;
    float f170, f171, f172, f173, f174, f175, f176, f177, f178, f179;
    float f180, f181, f182, f183, f184, f185, f186, f187, f188, f189;
    float f190, f191, f192, f193, f194, f195, f196, f197, f198, f199;
    float f200, f201, f202, f203, f204, f205, f206, f207, f208, f209;
    float f210, f211, f212, f213, f214, f215, f216, f217, f218, f219;
    float f220, f221, f222, f223, f224, f225, f226, f227, f228, f229;
    float f230, f231, f232, f233, f234, f235, f236, f237, f238, f239;
    float f240, f241, f242, f243, f244, f245, f246, f247, f248, f249;
    float f250, f251, f252, f253, f254, f255, f256, f257, f258, f259;
    float f260, f261, f262, f263, f264, f265, f266, f267, f268, f269;
    float f270, f271, f272, f273, f274, f275, f276, f277, f278, f279;
    float f280, f281, f282, f283, f284, f285, f286, f287, f288, f289;
    float f290, f291, f292, f293, f294, f295, f296, f297, f298, f299;
    float f300, f301, f302, f303, f304, f305, f306, f307, f308, f309;
    float f310, f311, f312, f313, f314, f315, f316, f317, f318, f319;
    float f320, f321, f322, f323, f324, f325, f326, f327, f328, f329;
    float f330, f331, f332, f333, f334, f335;

    f0 = x[0] - x[1];
    f1 = x[2] - x[1];
    f2 = x[2] - x[3];
    f3 = x[4] - x[3];
    f4 = x[4] - x[5];
    f5 = x[6] - x[5];
    f6 = x[6] - x[7];
    f7 = x[8] - x[7];
    f8 = x[8] - x[9];
    f9 = x[10] - x[9];
    f10 = x[10] - x[11];
    f11 = x[12] - x[11];
    f12 = x[12] - x[13];
    f13 = x[14] - x[13];
    f14 = x[14] - x[15];
    f15 = x[16] - x[15];
    f16 = x[16] - x[17];
    f17 = x[18] - x[17];
    f18 = x[18] - x[19];
    f19 = x[20] - x[19];
    f20 = x[20] - x[21];
    f21 = x[22] - x[21];
    f22 = x[22] - x[23];
    f23 = x[24] - x[23];
    f24 = x[24] - x[25];
    f25 = x[26] - x[25];
    f26 = x[26] - x[27];
    f27 = x[28] - x[27];
    f28 = x[28] - x[29];
    f29 = x[30] - x[29];
    f30 = x[30] - x[31];
    f31 = (0.7071067811865476f * f15);
    f32 = x[0] - f31;
    f33 = x[0] + f31;
    f34 = f7 + f23;
    f35 = (1.3065629648763766f * f7);
    f36 = (-0.9238795325112866f * f34);
    f37 = (-0.5411961001461967f * f23);
    f38 = f35 + f36;
    f39 = f37 - f36;
    f40 = f33 - f39;
    f41 = f33 + f39;
    f42 = f32 - f38;
    f43 = f32 + f38;
    f44 = f11 - f19;
    f45 = f11 + f19;
    f46 = (0.7071067811865476f * f45);
    f47 = f3 - f46;
    f48 = f3 + f46;
    f49 = (0.7071067811865476f * f44);
    f50 = f49 - f27;
    f51 = f49 + f27;
    f52 = f51 + f48;
    f53 = (-0.7856949583871021f * f51);
    f54 = (0.9807852804032304f * f52);
    f55 = (1.1758756024193588f * f48);
    f56 = f53 + f54;
    f57 = f55 - f54;
    f58 = f50 + f47;
    f59 = (-0.2758993792829430f * f50);
    f60 = (0.8314696123025452f * f58);
    f61 = (1.3870398453221475f * f47);
    f62 = f59 + f60;
    f63 = f61 - f60;
    f64 = f41 - f56;
    f65 = f41 + f56;
    f66 = f43 - f62;
    f67 = f43 + f62;
    f68 = f42 - f63;
    f69 = f42 + f63;
    f70 = f40 - f57;
    f71 = f40 + f57;
    f72 = f5 - f9;
    f73 = f5 + f9;
    f74 = f13 - f17;
    f75 = f13 + f17;
    f76 = f21 - f25;
    f77 = f21 + f25;
    f78 = (0.7071067811865476f * f75);
    f79 = f1 - f78;
    f80 = f1 + f78;
    f81 = f73 + f77;
    f82 = (1.3065629648763766f * f73);
    f83 = (-0.9238795325112866f * f81);
    f84 = (-0.5411961001461967f * f77);
    f85 = f82 + f83;
    f86 = f84 - f83;
    f87 = f80 - f86;
    f88 = f80 + f86;
    f89 = f79 - f85;
    f90 = f79 + f85;
    f91 = (0.7071067811865476f * f74);
    f92 = f29 - f91;
    f93 = f29 + f91;
    f94 = f76 + f72;
    f95 = (1.3065629648763766f * f76);
    f96 = (-0.9238795325112866f * f94);
    f97 = (-0.5411961001461967f * f72);
    f98 = f95 + f96;
    f99 = f97 - f96;
    f100 = f93 - f99;
    f101 = f93 + f99;
    f102 = f92 - f98;
    f103 = f92 + f98;
    f104 = f101 + f88;
    f105 = (-0.8971675863426361f * f101);
    f106 = (0.9951847266721968f * f104);
    f107 = (1.0932018670017576f * f88);
    f108 = f105 + f106;
    f109 = f107 - f106;
    f110 = f90 - f103;
    f111 = (-0.6666556584777466f * f103);
    f112 = (0.9569403357322089f * f110);
    f113 = (1.2472250129866713f * f90);
    f114 = f112 - f111;
    f115 = f113 - f112;
    f116 = f102 + f89;
    f117 = (-0.4105245275223571f * f102);
    f118 = (0.8819212643483549f * f116);
    f119 = (1.3533180011743529f * f89);
    f120 = f117 + f118;
    f121 = f119 - f118;
    f122 = f87 - f100;
    f123 = (-0.1386171691990915f * f100);
    f124 = (0.7730104533627370f * f122);
    f125 = (1.4074037375263826f * f87);
    f126 = f124 - f123;
    f127 = f125 - f124;
    f128 = f65 - f108;
    f129 = f65 + f108;
    f130 = f67 - f114;
    f131 = f67 + f114;
    f132 = f69 - f120;
    f133 = f69 + f120;
    f134 = f71 - f126;
    f135 = f71 + f126;
    f136 = f70 - f127;
    f137 = f70 + f127;
    f138 = f68 - f121;
    f139 = f68 + f121;
    f140 = f66 - f115;
    f141 = f66 + f115;
    f142 = f64 - f109;
    f143 = f64 + f109;
    f144 = f0 + f30;
    f145 = (1.0478631305325901f * f0);
    f146 = (-0.9987954562051724f * f144);
    f147 = (-0.9497277818777548f * f30);
    f148 = f145 + f146;
    f149 = f147 - f146;
    f150 = f4 + f26;
    f151 = (1.2130114330978077f * f4);
    f152 = (-0.9700312531945440f * f150);
    f153 = (-0.7270510732912803f * f26);
    f154 = f151 + f152;
    f155 = f153 - f152;
    f156 = f8 + f22;
    f157 = (1.3315443865537255f * f8);
    f158 = (-0.9039892931234433f * f156);
    f159 = (-0.4764341996931612f * f22);
    f160 = f157 + f158;
    f161 = f159 - f158;
    f162 = f12 + f18;
    f163 = (1.3989068359730781f * f12);
    f164 = (-0.8032075314806453f * f162);
    f165 = (-0.2075082269882124f * f18);
    f166 = f163 + f164;
    f167 = f165 - f164;
    f168 = f16 + f14;
    f169 = (1.4125100802019777f * f16);
    f170 = (-0.6715589548470187f * f168);
    f171 = (0.0693921705079402f * f14);
    f172 = f169 + f170;
    f173 = f171 - f170;
    f174 = f20 + f10;
    f175 = (1.3718313541934939f * f20);
    f176 = (-0.5141027441932219f * f174);
    f177 = (0.3436258658070501f * f10);
    f178 = f175 + f176;
    f179 = f177 - f176;
    f180 = f24 + f6;
    f181 = (1.2784339185752409f * f24);
    f182 = (-0.3368898533922200f * f180);
    f183 = (0.6046542117908008f * f6);
    f184 = f181 + f182;
    f185 = f183 - f182;
    f186 = f28 + f2;
    f187 = (1.1359069844201433f * f28);
    f188 = (-0.1467304744553624f * f186);
    f189 = (0.8424460355094185f * f2);
    f190 = f187 + f188;
    f191 = f189 - f188;
    f192 = f149 - f173;
    f193 = f149 + f173;
    f194 = f148 - f172;
    f195 = f148 + f172;
    f196 = f155 - f179;
    f197 = f155 + f179;
    f198 = f154 - f178;
    f199 = f154 + f178;
    f200 = f161 - f185;
    f201 = f161 + f185;
    f202 = f160 - f184;
    f203 = f160 + f184;
    f204 = f167 - f191;
    f205 = f167 + f191;
    f206 = f166 - f190;
    f207 = f166 + f190;
    f208 = f192 + f194;
    f209 = (1.1758756024193588f * f192);
    f210 = (-0.9807852804032304f * f208);
    f211 = (-0.7856949583871021f * f194);
    f212 = f209 + f210;
    f213 = f211 - f210;
    f214 = f196 + f198;
    f215 = (1.3870398453221475f * f196);
    f216 = (-0.5555702330196022f * f214);
    f217 = (0.2758993792829431f * f198);
    f218 = f215 + f216;
    f219 = f217 - f216;
    f220 = f200 + f202;
    f221 = (0.7856949583871022f * f200);
    f222 = (0.1950903220161283f * f220);
    f223 = (1.1758756024193586f * f202);
    f224 = f221 + f222;
    f225 = f223 - f222;
    f226 = f204 + f206;
    f227 = (-0.2758993792829430f * f204);
    f228 = (0.8314696123025452f * f226);
    f229 = (1.3870398453221475f * f206);
    f230 = f227 + f228;
    f231 = f229 - f228;
    f232 = f193 - f201;
    f233 = f193 + f201;
    f234 = f195 - f203;
    f235 = f195 + f203;
    f236 = f197 - f205;
    f237 = f197 + f205;
    f238 = f199 - f207;
    f239 = f199 + f207;
    f240 = f213 - f225;
    f241 = f213 + f225;
    f242 = f212 - f224;
    f243 = f212 + f224;
    f244 = f219 - f231;
    f245 = f219 + f231;
    f246 = f218 - f230;
    f247 = f218 + f230;
    f248 = f232 + f234;
    f249 = (1.3065629648763766f * f232);
    f250 = (-0.9238795325112866f * f248);
    f251 = (-0.5411961001461967f * f234);
    f252 = f249 + f250;
    f253 = f251 - f250;
    f254 = f236 + f238;
    f255 = (0.5411961001461969f * f236);
    f256 = (0.3826834323650898f * f254);
    f257 = (1.3065629648763766f * f238);
    f258 = f255 + f256;
    f259 = f257 - f256;
    f260 = f240 + f242;
    f261 = (1.3065629648763766f * f240);
    f262 = (-0.9238795325112866f * f260);
    f263 = (-0.5411961001461967f * f242);
    f264 = f261 + f262;
    f265 = f263 - f262;
    f266 = f244 + f246;
    f267 = (0.5411961001461969f * f244);
    f268 = (0.3826834323650898f * f266);
    f269 = (1.3065629648763766f * f246);
    f270 = f267 + f268;
    f271 = f269 - f268;
    f272 = f233 - f237;
    f273 = f233 + f237;
    f274 = f235 - f239;
    f275 = f235 + f239;
    f276 = f253 - f259;
    f277 = f253 + f259;
    f278 = f252 - f258;
    f279 = f252 + f258;
    f280 = f241 - f245;
    f281 = f241 + f245;
    f282 = f243 - f247;
    f283 = f243 + f247;
    f284 = f265 - f271;
    f285 = f265 + f271;
    f286 = f264 - f270;
    f287 = f264 + f270;
    f288 = f272 - f274;
    f289 = f272 + f274;
    f290 = (0.7071067811865474f * f288);
    f291 = (0.7071067811865474f * f289);
    f292 = f276 - f278;
    f293 = f276 + f278;
    f294 = (0.7071067811865474f * f292);
    f295 = (0.7071067811865474f * f293);
    f296 = f280 - f282;
    f297 = f280 + f282;
    f298 = (0.7071067811865474f * f296);
    f299 = (0.7071067811865474f * f297);
    f300 = f284 - f286;
    f301 = f284 + f286;
    f302 = (0.7071067811865474f * f300);
    f303 = (0.7071067811865474f * f301);
    f304 = f129 - f273;
    f305 = f129 + f273;
    f306 = f131 - f281;
    f307 = f131 + f281;
    f308 = f133 - f285;
    f309 = f133 + f285;
    f310 = f135 - f277;
    f311 = f135 + f277;
    f312 = f137 - f295;
    f313 = f137 + f295;
    f314 = f139 - f303;
    f315 = f139 + f303;
    f316 = f141 - f299;
    f317 = f141 + f299;
    f318 = f143 - f291;
    f319 = f143 + f291;
    f320 = f142 - f290;
    f321 = f142 + f290;
    f322 = f140 - f298;
    f323 = f140 + f298;
    f324 = f138 - f302;
    f325 = f138 + f302;
    f326 = f136 - f294;
    f327 = f136 + f294;
    f328 = f134 - f279;
    f329 = f134 + f279;
    f330 = f132 - f287;
    f331 = f132 + f287;
    f332 = f130 - f283;
    f333 = f130 + f283;
    f334 = f128 - f275;
    f335 = f128 + f275;
    y[31] = (0.5001506360206510f * f305);
    y[30] = (0.5013584524464084f * f307);
    y[29] = (0.5037887256810443f * f309);
    y[28] = (0.5074711720725553f * f311);
    y[27] = (0.5124514794082247f * f313);
    y[26] = (0.5187927131053328f * f315);
    y[25] = (0.5265773151542700f * f317);
    y[24] = (0.5359098169079920f * f319);
    y[23] = (0.5469204379855088f * f321);
    y[22] = (0.5597698129470802f * f323);
    y[21] = (0.5746551840326600f * f325);
    y[20] = (0.5918185358574165f * f327);
    y[19] = (0.6115573478825099f * f329);
    y[18] = (0.6342389366884031f * f331);
    y[17] = (0.6603198078137061f * f333);
    y[16] = (0.6903721282002123f * f335);
    y[15] = (0.7251205223771985f * f334);
    y[14] = (0.7654941649730891f * f332);
    y[13] = (0.8127020908144905f * f330);
    y[12] = (0.8683447152233481f * f328);
    y[11] = (0.9345835970364075f * f326);
    y[10] = (1.0144082649970547f * f324);
    y[9] = (1.1120716205797176f * f322);
    y[8] = (1.2338327379765710f * f320);
    y[7] = (1.3892939586328277f * f318);
    y[6] = (1.5939722833856311f * f316);
    y[5] = (1.8746759800084078f * f314);
    y[4] = (2.2820500680051619f * f312);
    y[3] = (2.9246284281582162f * f310);
    y[2] = (4.0846110781292477f * f308);
    y[1] = (6.7967507116736332f * f306);
    y[0] = (20.3738781672314530f * f304);
  }
}
