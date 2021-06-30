

import 'dart:convert';

import 'package:url_launcher/url_launcher.dart';


class BaseUtils{



  static String toJson(Map<String,dynamic> map){
    if(null == map)
      return "";
    return json.encode(map);
//    json.encode(map,quoteMapKeys: true);
  }

  static Map<String,dynamic> fromJson(String value){
    return json.decode(value);
  }

  static bool isEmpty(String value){
    if(null == value)
      return true;
    return value.isEmpty;
  }

  static void launchURL(String url) async{
      if (await canLaunch(url)) {
        await launch(url);
      } else {
        throw 'Could not launch $url';
      }
  }



  static bool isEmptyList<T>(List<T> value){
    if(null == value)
      return true;
    return value.isEmpty;
  }

  static T find<T>(List<T> value,bool test(T element)){
    if(null == value)
      return null;
    for (T element in value){
      if(test(element))
        return element;
    }

    return null;
  }
  static bool compareDate(String start,String end){
    var starts = start.split("-");
    var ends = end.split("-");
    if(starts.length == 3 &&  ends.length == 3){
      var startDate = DateTime(int.parse(starts[0]), int.parse(starts[1]), int.parse(starts[2]));
      var endDate = DateTime(int.parse(ends[0]), int.parse(ends[1]), int.parse(ends[2]));
      //比较相差的天数
      final difference = startDate.difference(endDate).inDays;
      if(difference > 0)
        return true;
    }


    return false;
  }


  static String findItemByIndex(List<String> value,dynamic index){
    if(null == value)
      return "";
    if(null == index)
      return "";

    int newIndex;

    if(index is int){
      newIndex = index - 1;
    }
    else{
      newIndex = int.parse(index) - 1;
    }
    if(newIndex < value.length)
    return value[newIndex];
    return "";
  }

  static int parseInt(dynamic index){
    if(null == index)
      return 0;


    if(index is int){
      return index;
    }
    return  int.parse(index);
  }

   static bool isImageByEnd(String name){
    if(isEmpty(name))
      return false;
    String pathName = name.toLowerCase();
    if(pathName.endsWith("png") || pathName.endsWith("jpg")  || pathName.endsWith("jpeg")
        || pathName.endsWith("bmp") || pathName.endsWith("gif") )
      return true;
    return false;
  }



}