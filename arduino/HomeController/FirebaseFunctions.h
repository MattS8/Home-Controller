#ifndef FIREBASE_FUNCTIONS
#define FIREBASE_FUNCTIONS

#include "FirebaseESP8266.h"
#include "WiFiCreds.h"
#include <ESP8266WiFi.h>
#include <FS.h>

#ifndef LOCAL_FILE
#define LOCAL_FILE
const char* HomeControllerFilePath = "HomeController.dat";
#endif

#endif