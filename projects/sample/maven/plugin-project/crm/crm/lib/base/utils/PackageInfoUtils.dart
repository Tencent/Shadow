import 'package:package_info/package_info.dart';

class PackageInfoUtils {

  void t() async{
    PackageInfo packageInfo = await PackageInfo.fromPlatform();
    String appName = packageInfo.appName;
    String packageName = packageInfo.packageName;
  }
}
