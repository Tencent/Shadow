
import 'dart:io';

import 'package:fluro/fluro.dart';
import 'package:flutter/material.dart';
import '../base/utils/ChangeUtils.dart';
import '../base/utils/LoginUtils.dart';

import 'cache/Cache.dart';
import 'router/routes.dart';

import '../base/http/HttpsProvider.dart';

class BaseApp{

  static FluroRouter _router;


  static FluroRouter get router => _router;

  static void init() async{

    HttpUtils.init();
    _router = FluroRouter();
    Routes.configureRoutes(router);


//    GetIt.instance.registerSingleton(NavigateService());
    await Cache.initSp();
    // LoginUtils.instance().init();
    ChangeUtils.init();

    // if (Platform.isIOS || Platform.isAndroid) {
    //
    // }
  }

  // var locator = GetIt();
  // void setupLocator() {
  //   TelAndSmsServic
  //   locator.registerSingleton(TelAndSmsService());
  // }




  }