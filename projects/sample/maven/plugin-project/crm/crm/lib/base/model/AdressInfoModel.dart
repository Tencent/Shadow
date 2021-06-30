/// id : "100000"
/// pid : "0"
/// name : "黑龙江省"
/// level : "country"

class AdressInfoModel {
  String _id;
  String _pid;
  String _name;
  String _level;

  String get id => _id;
  String get pid => _pid;
  String get name => _name;
  String get level => _level;

  AdressInfoModel({
      String id, 
      String pid, 
      String name, 
      String level}){
    _id = id;
    _pid = pid;
    _name = name;
    _level = level;
}

  AdressInfoModel.fromJson(dynamic json) {
    _id = json["id"];
    _pid = json["pid"];
    _name = json["name"];
    _level = json["level"];
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    map["id"] = _id;
    map["pid"] = _pid;
    map["name"] = _name;
    map["level"] = _level;
    return map;
  }

}