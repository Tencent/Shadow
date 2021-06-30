/// data : {"id":null,"delete_tag":null,"createTime":null,"updateTime":null,"idStr":null,"msg":"success","total":null,"returnCode":"0","result":{"id":33,"delete_tag":0,"createTime":"2020-09-15 19:12:31","updateTime":"2021-02-05 10:37:41","idStr":"33","userName":"wyw","sName":"wyw","name":"吴怡文","password":"e10adc3949ba59abbe56e057f20f883e","userId":203,"postName":"科员","orgId":118,"companyId":118,"gjId":81,"gjName":"杭州广角","roleId":3,"perId":0,"orgIds":null,"headImgId":90,"deviceId":null,"clientId":null,"telephone":"17376561620","lastLogin":"2021-02-05 10:37:41","token":"","report":null,"status":0,"sign":"个性签名","headImg":{"id":null,"path":"http://pre-crm.leadertable.com/crm/Public/Uploads/2018-01-24/_word_head_be83ee5bd20d969631ab62b11dad2f9d.jpg","bigPath":"http://pre-crm.leadertable.com/crm/Public/Uploads/2018-01-24/_word_head_be83ee5bd20d969631ab62b11dad2f9d.jpg","middlePath":"http://pre-crm.leadertable.com/crm/Public/Uploads/2018-01-24/_word_head_be83ee5bd20d969631ab62b11dad2f9d.jpg"},"orgName":"浙江中财担保有限公司","companyName":"浙江中财担保有限公司","roleName":"普通用户"},"success":true,"resultSuccess":true}
/// meta : {"code":200,"msg":"请求成功"}

class BaseModel {
  Data _data;
  Meta _meta;

  Data get data => _data;
  Meta get meta => _meta;

  BaseModel({
      Data data, 
      Meta meta}){
    _data = data;
    _meta = meta;
}

  BaseModel.fromJson(dynamic json) {
    _data = json["data"] != null ? Data.fromJson(json["data"]) : null;
    _meta = json["meta"] != null ? Meta.fromJson(json["meta"]) : null;
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    if (_data != null) {
      map["data"] = _data.toJson();
    }
    if (_meta != null) {
      map["meta"] = _meta.toJson();
    }
    return map;
  }

}

/// code : 200
/// msg : "请求成功"

class Meta {
  int _code;
  String _msg;

  int get code => _code;
  String get msg => _msg;

  Meta({
      int code, 
      String msg}){
    _code = code;
    _msg = msg;
}

  Meta.fromJson(dynamic json) {
    _code = json["code"];
    _msg = json["msg"];
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    map["code"] = _code;
    map["msg"] = _msg;
    return map;
  }

}

/// id : null
/// delete_tag : null
/// createTime : null
/// updateTime : null
/// idStr : null
/// msg : "success"
/// total : null
/// returnCode : "0"
/// result : {"id":33,"delete_tag":0,"createTime":"2020-09-15 19:12:31","updateTime":"2021-02-05 10:37:41","idStr":"33","userName":"wyw","sName":"wyw","name":"吴怡文","password":"e10adc3949ba59abbe56e057f20f883e","userId":203,"postName":"科员","orgId":118,"companyId":118,"gjId":81,"gjName":"杭州广角","roleId":3,"perId":0,"orgIds":null,"headImgId":90,"deviceId":null,"clientId":null,"telephone":"17376561620","lastLogin":"2021-02-05 10:37:41","token":"","report":null,"status":0,"sign":"个性签名","headImg":{"id":null,"path":"http://pre-crm.leadertable.com/crm/Public/Uploads/2018-01-24/_word_head_be83ee5bd20d969631ab62b11dad2f9d.jpg","bigPath":"http://pre-crm.leadertable.com/crm/Public/Uploads/2018-01-24/_word_head_be83ee5bd20d969631ab62b11dad2f9d.jpg","middlePath":"http://pre-crm.leadertable.com/crm/Public/Uploads/2018-01-24/_word_head_be83ee5bd20d969631ab62b11dad2f9d.jpg"},"orgName":"浙江中财担保有限公司","companyName":"浙江中财担保有限公司","roleName":"普通用户"}
/// success : true
/// resultSuccess : true

class Data {
  dynamic _id;
  dynamic _createTime;
  dynamic _updateTime;
  dynamic _idStr;
  String _msg;
  dynamic _total;
  String _returnCode;
  dynamic _result;
  bool _success;
  bool _resultSuccess;

  dynamic get id => _id;
  dynamic get createTime => _createTime;
  dynamic get updateTime => _updateTime;
  dynamic get idStr => _idStr;
  String get msg => _msg;
  dynamic get total => _total;
  String get returnCode => _returnCode;
  dynamic get result => _result;
  bool get success => _success;
  bool get resultSuccess => _resultSuccess;


  Data.fromJson(dynamic json) {
    _id = json["id"];
    _createTime = json["createTime"];
    _updateTime = json["updateTime"];
    _idStr = json["idStr"];
    _msg = json["msg"];
    _total = json["total"];
    _returnCode = json["returnCode"]?.toString();
    _result = json["result"];
    _success = json["success"];
    _resultSuccess = json["resultSuccess"];
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    map["id"] = _id;
    map["createTime"] = _createTime;
    map["updateTime"] = _updateTime;
    map["idStr"] = _idStr;
    map["msg"] = _msg;
    map["total"] = _total;
    map["returnCode"] = _returnCode;
    if (_result != null) {
      map["result"] = _result.toJson();
    }
    map["success"] = _success;
    map["resultSuccess"] = _resultSuccess;
    return map;
  }

}
