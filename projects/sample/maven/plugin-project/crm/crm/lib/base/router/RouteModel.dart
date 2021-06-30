import 'package:flutter/material.dart';

class RouteModel {

  String _title;
  String _path;
  Widget _child;
  int _isValue;
  int _isHome;

  RouteModel(String path,String title,Widget child,{int isValue: 0,int isHome: 0}){
    this.path = path;
    this.title = title;
    this.child = child;
    this._isValue = isValue;
    this._isHome = isHome;
  }


  int get isHome => _isHome;

  set isHome(int value) {
    _isHome = value;
  }

  int get isValue => _isValue;

  String get title => _title;

  set title(String value) {
    _title = value;
  }

  String get path => _path;

  set path(String value) {
    _path = value;
  }

  Widget get child => _child;

  set child(Widget value) {
    _child = value;
  }


}
