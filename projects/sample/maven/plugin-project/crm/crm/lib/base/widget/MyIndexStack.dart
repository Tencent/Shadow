

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class MyIndexStack extends StatelessWidget {

  final IndexStackController controller;
  final List<Widget> children;


  const MyIndexStack({Key key,
    @required this.controller,
    @required this.children,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {

    List<Widget> widgets = [];

    List.generate(children.length, (index) {
      if(controller.isAdd(index))
        widgets.add(children[index]);
      else
        widgets.add(SizedBox.shrink());
    });

    return ChangeNotifierProvider<IndexStackController>.value(
      value: controller,
      child: Consumer<IndexStackController>(
        builder: (context,model,child){

          if(!controller.isAdd(model.index)){
            widgets.replaceRange(model.index, model.index + 1, [children[model.index]]);
          }
          return IndexedStack(
            index: model.index,
            children: widgets,
          );
        },
      ),
    );
  }


}





class IndexStackController with ChangeNotifier{

  List<int> indexList = [];

  void save(int index){
    if(!indexList.contains(index)){
      indexList.contains(index);
    }
  }

  int index;

  IndexStackController({this.index: 0}){
    save(index);
  }

  bool isAdd(int index){
    return indexList.contains(index);
  }

  void setIndex(int index){
    this.index = index;
    save(index);
    notifyListeners();
  }
}
