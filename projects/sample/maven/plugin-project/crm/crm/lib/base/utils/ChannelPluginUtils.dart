
import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

class ChannelPluginUtils {


  static closedApp(){
    //js.context.callMethod("closedCrmWebPage");
    ChannelPluginUtils(channel: "crm", method: "close").invokeMethod();
  }


  final String channel;
  final String method;
  final dynamic arguments;

  const ChannelPluginUtils({
    @required this.channel,
    @required this.method,
    this.arguments}
    );

  Future invokeMethod() async {
    var result;
    try {
      result = await MethodChannel(this.channel).invokeMethod(this.method,this.arguments);
      return Future.value(result);
    } on PlatformException catch (e) {
      return Future.error(e.toString());
    }
  }
}