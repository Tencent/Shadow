

import 'dart:collection';

import 'package:dio/dio.dart';
import '../utils/LoginUtils.dart';

import '../Config.dart';

class Params{

  Map<String,dynamic> map;

  final int type;

  Params({this.type = 0,Map<String,dynamic> paramMap}){
    if(null == paramMap)
      map = HashMap();
    else
      map = paramMap;
    if(type == 0) {
      put("size", 10);
      if(Config.isLogin){
        put("userId", LoginUtils.instance().getUser()?.userId);
      }
      
    }
  }


  Params put(String key,dynamic value){
    map[key] = value;

    return this;
  }

  void putMultipartFile(MultipartFile file){
    map["file"] = file;
  }

  void putFile(String path,String fileName){
    map["file"] = MultipartFile.fromFile(path,filename: fileName);
  }



}