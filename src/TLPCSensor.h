/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class TLPCSensor */

#include "sensor.h"
#include "config.h"
#include "report.h"

#ifndef _Included_TLPCSensor
#define _Included_TLPCSensor
#ifdef __cplusplus
extern "C" {
#endif

#define _nb_perf_counter 3

char* _perf_counters_type[_nb_perf_counter] = {
    "INSTRUCTIONS_RETIRED",
    "LLC_MISSES",
    "CYCLES"
};

#define _nb_rapl_counter 1

char* _rapl_counters_type[_nb_rapl_counter] = {
    "RAPL_ENERGY_PKG"
};

/*
 * Class:     TLPCSensor
 * Method:    start
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_fr_davidson_tlpc_sensor_TLPCSensor_start
  (JNIEnv *, jobject, jstring);

/*
 * Class:     TLPCSensor
 * Method:    stop
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_fr_davidson_tlpc_sensor_TLPCSensor_stop
  (JNIEnv *, jobject, jstring);

/*
 * Class:     TLPCSensor
 * Method:    report
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_fr_davidson_tlpc_sensor_TLPCSensor_report
  (JNIEnv *, jobject, jstring);

#ifdef __cplusplus
}
#endif
#endif
