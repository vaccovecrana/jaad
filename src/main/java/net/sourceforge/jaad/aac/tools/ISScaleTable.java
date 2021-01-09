package net.sourceforge.jaad.aac.tools;

/**
 * Intensity stereo lookup table for all possible values for 0.5<sup>0.25*scaleFactor</sup>.
 *
 * @author in-somnia
 */
interface ISScaleTable {

  float[] SCALE_TABLE = {
    1.0f,
    0.8408964152537146f,
    0.7071067811865476f,
    0.5946035575013605f,
    0.5f,
    0.4204482076268573f,
    0.35355339059327373f,
    0.29730177875068026f,
    0.25f,
    0.21022410381342865f,
    0.17677669529663687f,
    0.14865088937534013f,
    0.125f,
    0.10511205190671433f,
    0.08838834764831843f,
    0.07432544468767006f,
    0.0625f,
    0.05255602595335716f,
    0.044194173824159216f,
    0.03716272234383503f,
    0.03125f,
    0.02627801297667858f,
    0.022097086912079608f,
    0.018581361171917516f,
    0.015625f,
    0.01313900648833929f,
    0.011048543456039804f,
    0.009290680585958758f,
    0.0078125f,
    0.006569503244169645f,
    0.005524271728019902f,
    0.004645340292979379f,
    0.00390625f,
    0.0032847516220848227f,
    0.002762135864009951f,
    0.0023226701464896895f,
    0.001953125f,
    0.0016423758110424114f,
    0.0013810679320049755f,
    0.0011613350732448448f,
    9.765625E-4f,
    8.211879055212057E-4f,
    6.905339660024878E-4f,
    5.806675366224224E-4f,
    4.8828125E-4f,
    4.1059395276060284E-4f,
    3.452669830012439E-4f,
    2.903337683112112E-4f,
    2.44140625E-4f,
    2.0529697638030142E-4f,
    1.7263349150062194E-4f,
    1.451668841556056E-4f,
    1.220703125E-4f,
    1.0264848819015071E-4f,
    8.631674575031097E-5f,
    7.25834420778028E-5f,
    6.103515625E-5f,
    5.1324244095075355E-5f,
    4.3158372875155485E-5f,
    3.62917210389014E-5f,
    3.0517578125E-5f,
    2.5662122047537677E-5f,
    2.1579186437577742E-5f,
    1.81458605194507E-5f,
    1.52587890625E-5f,
    1.2831061023768839E-5f,
    1.0789593218788871E-5f,
    9.07293025972535E-6f,
    7.62939453125E-6f,
    6.415530511884419E-6f,
    5.394796609394436E-6f,
    4.536465129862675E-6f,
    3.814697265625E-6f,
    3.2077652559422097E-6f,
    2.697398304697218E-6f,
    2.2682325649313374E-6f,
    1.9073486328125E-6f,
    1.6038826279711048E-6f,
    1.348699152348609E-6f,
    1.1341162824656687E-6f,
    9.5367431640625E-7f,
    8.019413139855524E-7f,
    6.743495761743044E-7f,
    5.670581412328344E-7f,
    4.76837158203125E-7f,
    4.009706569927762E-7f,
    3.371747880871522E-7f,
    2.835290706164172E-7f,
    2.384185791015625E-7f,
    2.004853284963881E-7f,
    1.685873940435761E-7f,
    1.417645353082086E-7f,
    1.1920928955078125E-7f,
    1.0024266424819405E-7f,
    8.429369702178806E-8f,
    7.08822676541043E-8f,
    5.9604644775390625E-8f,
    5.0121332124097026E-8f,
    4.214684851089403E-8f,
    3.544113382705215E-8f,
    2.9802322387695312E-8f,
    2.5060666062048513E-8f,
    2.1073424255447014E-8f,
    1.7720566913526073E-8f,
    1.4901161193847656E-8f,
    1.2530333031024257E-8f,
    1.0536712127723507E-8f,
    8.860283456763037E-9f,
    7.450580596923828E-9f,
    6.265166515512128E-9f,
    5.2683560638617535E-9f,
    4.430141728381518E-9f,
    3.725290298461914E-9f,
    3.132583257756064E-9f,
    2.6341780319308768E-9f,
    2.215070864190759E-9f,
    1.862645149230957E-9f,
    1.566291628878032E-9f,
    1.3170890159654384E-9f,
    1.1075354320953796E-9f,
    9.313225746154785E-10f,
    7.83145814439016E-10f,
    6.585445079827192E-10f,
    5.537677160476898E-10f,
    4.6566128730773926E-10f,
    3.91572907219508E-10f,
    3.292722539913596E-10f,
    2.768838580238449E-10f,
    2.3283064365386963E-10f,
    1.95786453609754E-10f,
    1.646361269956798E-10f,
    1.3844192901192245E-10f,
    1.1641532182693481E-10f,
    9.7893226804877E-11f,
    8.23180634978399E-11f,
    6.922096450596122E-11f,
    5.820766091346741E-11f,
    4.89466134024385E-11f,
    4.115903174891995E-11f,
    3.461048225298061E-11f,
    2.9103830456733704E-11f,
    2.447330670121925E-11f,
    2.0579515874459975E-11f,
    1.7305241126490306E-11f,
    1.4551915228366852E-11f,
    1.2236653350609626E-11f,
    1.0289757937229987E-11f,
    8.652620563245153E-12f,
    7.275957614183426E-12f,
    6.118326675304813E-12f,
    5.144878968614994E-12f,
    4.3263102816225765E-12f,
    3.637978807091713E-12f,
    3.0591633376524064E-12f,
    2.572439484307497E-12f,
    2.1631551408112883E-12f,
    1.8189894035458565E-12f,
    1.5295816688262032E-12f,
    1.2862197421537484E-12f,
    1.0815775704056441E-12f,
    9.094947017729282E-13f,
    7.647908344131016E-13f,
    6.431098710768742E-13f,
    5.407887852028221E-13f,
    4.547473508864641E-13f,
    3.823954172065508E-13f,
    3.215549355384371E-13f,
    2.7039439260141103E-13f,
    2.2737367544323206E-13f,
    1.911977086032754E-13f,
    1.6077746776921855E-13f,
    1.3519719630070552E-13f,
    1.1368683772161603E-13f,
    9.55988543016377E-14f,
    8.038873388460928E-14f,
    6.759859815035276E-14f,
    5.6843418860808015E-14f,
    4.779942715081885E-14f,
    4.019436694230464E-14f,
    3.379929907517638E-14f,
    2.8421709430404007E-14f,
    2.3899713575409425E-14f,
    2.009718347115232E-14f,
    1.689964953758819E-14f,
    1.4210854715202004E-14f,
    1.1949856787704712E-14f,
    1.004859173557616E-14f,
    8.449824768794095E-15f,
    7.105427357601002E-15f,
    5.974928393852356E-15f,
    5.02429586778808E-15f,
    4.2249123843970474E-15f,
    3.552713678800501E-15f,
    2.987464196926178E-15f,
    2.51214793389404E-15f,
    2.1124561921985237E-15f,
    1.7763568394002505E-15f,
    1.493732098463089E-15f,
    1.25607396694702E-15f,
    1.0562280960992619E-15f,
    8.881784197001252E-16f,
    7.468660492315445E-16f,
    6.2803698347351E-16f,
    5.281140480496309E-16f,
    4.440892098500626E-16f,
    3.7343302461577226E-16f,
    3.14018491736755E-16f,
    2.6405702402481546E-16f,
    2.220446049250313E-16f,
    1.8671651230788613E-16f,
    1.570092458683775E-16f,
    1.3202851201240773E-16f,
    1.1102230246251565E-16f,
    9.335825615394307E-17f,
    7.850462293418875E-17f,
    6.601425600620387E-17f,
    5.551115123125783E-17f,
    4.667912807697153E-17f,
    3.925231146709437E-17f,
    3.300712800310193E-17f,
    2.7755575615628914E-17f,
    2.3339564038485766E-17f,
    1.9626155733547187E-17f,
    1.6503564001550966E-17f,
    1.3877787807814457E-17f,
    1.1669782019242883E-17f,
    9.813077866773593E-18f,
    8.251782000775483E-18f,
    6.938893903907228E-18f,
    5.834891009621442E-18f,
    4.906538933386797E-18f,
    4.1258910003877416E-18f,
    3.469446951953614E-18f,
    2.917445504810721E-18f,
    2.4532694666933983E-18f,
    2.0629455001938708E-18f,
    1.734723475976807E-18f,
    1.4587227524053604E-18f,
    1.2266347333466992E-18f,
    1.0314727500969354E-18f,
    8.673617379884035E-19f,
    7.293613762026802E-19f,
    6.133173666733496E-19f,
    5.157363750484677E-19f,
    4.3368086899420177E-19f,
    3.646806881013401E-19f,
    3.066586833366748E-19f,
    2.5786818752423385E-19f,
    2.1684043449710089E-19f,
    1.8234034405067005E-19f,
    1.533293416683374E-19f,
    1.2893409376211693E-19f,
    1.0842021724855044E-19f,
    9.117017202533503E-20f,
    7.66646708341687E-20f
  };
}
