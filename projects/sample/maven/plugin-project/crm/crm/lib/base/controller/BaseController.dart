
import '../utils/BaseUtils.dart';

mixin BaseController{


  int index = 0;

  void setIndex(int index,{bool isNotify: true}){
    this.index = index;
    if(isNotify)
    notifyWidget?.call();
  }

  Function() notifyWidget;

  void setNotifyWidget(Function() notifyWidget){
    this.notifyWidget = notifyWidget;
  }

  void notifyUI(){
    notifyWidget?.call();
  }


  String _value;

  String get value => _value;

  void setValue(String value) {
    _value = value;
  }

  void reset(){
    _value = null;
  }

  bool isEmpty(){
    return BaseUtils.isEmpty(value);
  }

  void dispose(){
    notifyWidget = null;
  }

}
