import '../provider/BaseViewModel.dart';

class TabVController extends BaseViewModel{

  int index = 0;

  int pages = 1;

  int pageCount = 10;

  int get page => index + 1;

  TabVController({this.index: 0});

  bool jump(int index){
    if(index == this.index)
      return false;
    if(index > pages)
      return false;
    this.index = index;
    notifyListeners();
    return true;
  }

  bool next(){
    if(index >= pages -1)
      return false;
    index ++;
    notifyListeners();
    return true;
  }

  bool pre(){
    if(index == 0)
      return false;
    index --;
    notifyListeners();
    return true;
  }

  void clear(){
    index = 0;
    pages = 1;
  }

}
