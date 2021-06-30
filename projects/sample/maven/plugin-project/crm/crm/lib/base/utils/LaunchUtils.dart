import 'package:permission_handler/permission_handler.dart';
import 'package:url_launcher/url_launcher.dart';

import 'PermissionUtils.dart';
import 'dart:io';
class LaunchUtils {

  static open(String url,{String phone}) async {
      await launch(url);
    // if (await canLaunch(url)) {
    //   await launch(url);
    // } else {
    //   Common.telcall(phone);
    //   throw 'Could not launch $url';
    // }
  }

  static call(String phone){
    // open("tel:${phone}",phone: phone);
    PermissionUtils.request(Permission.phone,(isExit){
      if(isExit){
        open("tel:${phone}");
      }
    });
  }


}
