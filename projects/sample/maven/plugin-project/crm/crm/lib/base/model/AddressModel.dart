


import '../model/AdressInfoModel.dart';

class AddressModel{
  AdressInfoModel _provice;
  AdressInfoModel _city;
  AdressInfoModel _area;

  AdressInfoModel get provice => _provice;

  set provice(AdressInfoModel value) {
    _provice = value;
  }

  AdressInfoModel get city => _city;

  set city(AdressInfoModel value) {
    _city = value;
  }

  AdressInfoModel get area => _area;

  set area(AdressInfoModel value) {
    _area = value;
  }

  String getAddr(){
    StringBuffer text = StringBuffer();
    if(null != _provice) {
      text.write(_provice?.name);
      text.write(" ");
    }
    if(null != _city) {
      text.write(_city?.name);
      text.write(" ");
    }
    if(null != _area)
      text.write(_area?.name);

    return text.toString();
  }


  String getpId(){

    if(null != _area) {
      return _area?.pid;
    }

    if(null != _city) {
      return _city?.pid;
    }

    if(null != provice) {
      return provice?.pid;
    }

    return "100000";
  }

}
