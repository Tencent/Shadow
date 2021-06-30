import 'package:fluro/fluro.dart';
import 'package:flutter/material.dart';

import '../BaseApp.dart';
import '../utils/JsonUtils.dart';
import 'routes.dart';

class RouterHelper{

  //获取传值路径
  static String getRoutePath(String path , Map map){
    if(null == map)
      return path;
    String route = path + "?";
    List arguments = [];
    for(String key in map.keys){
      String encodeStr = Uri.encodeComponent("${map[key]}");
      arguments.add("${key}=${encodeStr}");
    }

    route += arguments.join("&");
    return route;

  }

  static Future _navigateTo(BuildContext context, String path,
      {
        bool replace = false,
        bool clearStack = false,
        Map map,
        RouteSettings  routeSettings,
        Duration transitionDuration = const Duration(milliseconds: 250),
        TransitionType transition:TransitionType.inFromRight,
        RouteTransitionsBuilder transitionBuilder}) {


    return BaseApp.router.navigateTo(context, getRoutePath(path,map),
        replace: replace,
        clearStack: clearStack,
        routeSettings: routeSettings,
        transitionDuration: transitionDuration,
        transitionBuilder: transitionBuilder,
        transition: transition);
  }


  static void pop(BuildContext context,{dynamic backValue}){
    BaseApp.router.pop(context,backValue);
  }


  static Future build(BuildContext context,RouteSettings  routeSettings,{bool replace: false}){
   return _navigateTo(context,routeSettings.name,routeSettings: routeSettings,replace: replace);
  }



  static void buildImage(BuildContext context,List<String> data,{int position:0}){
    _navigateTo(context,Routes.imagePre,map:{"data": data.join(","),"position": position});
  }


  static void buildMain(BuildContext context,String id){
    _navigateTo(context,Routes.main,map:{"id": id,"platform": "ios"},replace: true);
  }


  static void buildLogin(BuildContext context){
    _navigateTo(context,Routes.login,clearStack: true,replace: true);
  }




}