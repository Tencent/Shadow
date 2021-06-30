

import 'package:crm/base/ui/page/ImagePrePage.dart';
import 'package:crm/ui/main/page/LoginPage.dart';
import 'package:fluro/fluro.dart';
import 'package:flutter/material.dart';

import '../../ui/main/page/MainPage.dart';
import '../BaseApp.dart';
import '../Config.dart';
import '../router/RouteModel.dart';


class Routes {

  static const String initRouter = "/";
  static const String main = "/main";
  static const String imagePre = "/imagePre";
  static const String login = "/login";
  static const String test = "/test";



  static onGenerateRoute(BuildContext context, RouteSettings settings) {

    String name = settings.name;
    if(name == "/"){
      if(Config.isLogin){
        return BaseApp.router.generator(RouteSettings(name: main));
      }
      return BaseApp.router.generator(RouteSettings(name: login));
    }
    return BaseApp.router.generator(settings);
  }

  static const String runningDet = "/main/runningDet";
  static const String createRunning = "/main/runningCreate";
  static const String runningSearch = "/main/runningSearch";
  static const String relatedCustomers = "/relatedCustomers";
  static const String contractDet = "/main/contractDet";
  static const String contractAdd = "/main/contractAdd";
  static const String company = "/company";
  static const String message = "/message";
  static const String remind = "/remind";
  static const String runAnalyseSearch = "/runAnalyseSearch";
  static const String rank = "/rank";
  static const String runMember = "/runMember";
  static const String businessAdd = "/businessAdd";
  static const String businessDet = "/businessDet";
  static const String developmentDet = "/developmentDet";

  static List<RouteModel> tabList = [
  ];



  static Future<void> configureRoutes(FluroRouter router) async {

    router.notFoundHandler = new Handler(
        handlerFunc: (BuildContext context, Map<String, List<String>> params) {
          print("ROUTE WAS NOT FOUND !!!");
          return null;//LoginPage();
        });

    // MyRoutes.configureRoutes(router);

    tabList.forEach((element) {
      router.define(element.path, handler: MyHandler(
          func: (context,map){
            // context.settings.arguments = map;
            return element.child; //LoginPage();
          }
      ));

    });


    router.define(login, handler: MyHandler(
        func: (context,map){
          return LoginPage();
        }
    ));

    router.define(main, handler: MyHandler(
        func: (context,map){
          return MainPage(userId: map["id"],platform:map["platform"]);
        }
    ));

    // router.define(rank, handler: MyHandler(
    //     func: (context,map){
    //       var type = int.parse(map["type"]??1);
    //       return RankPage(type: type);
    //     }
    // ));

    router.define(imagePre, handler: MyHandler(
        func: (context,map){
          String value = map["data"];
          List<String> data = value?.split(",");
          return ImagePrePage(data: data,);
        }
    ));


  }

}




typedef Widget Func(
    BuildContext context, Map<String, dynamic> parameters);

class MyHandler extends Handler{
  final HandlerType type;
  final Func func;

  MyHandler({this.type = HandlerType.route, this.func}):
        super(type: type,handlerFunc:(_,map){
       var newMap =  map?.map((key, value) => MapEntry(key, value.first));

        return func(_,newMap);
      });
}
