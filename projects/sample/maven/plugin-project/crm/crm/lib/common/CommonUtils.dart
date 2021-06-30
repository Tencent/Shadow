import 'package:crm/base/utils/BaseUtils.dart';

class CommonUtils{

  static String feedLineByLength(String value,{int length: 4}){
    if(BaseUtils.isEmpty(value))
      return "";

    var trim = value.trim();

    var newValue = StringBuffer();
    int len = 1;
    for(int i = 0;i < trim.length;i ++){
      newValue.write(trim[i]);
      if(i >=length*len){
        newValue.write("\n");
        len ++;
      }
    }
    return newValue.toString();
  }

  static String take(String value,{int length:4}){
    if(BaseUtils.isEmpty(value))
      return "";

    var trim = value.trim();
    return trim.length <= length ? trim : trim.substring(0,length);
  }
}