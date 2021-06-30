
import 'package:crm/base/utils/ImageHelper.dart';
import 'package:flutter/material.dart';


class MyCheckBox extends StatelessWidget {


  final int state;

  final ValueChanged<int> onChanged;

  const MyCheckBox({Key key, this.state, this.onChanged}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    var newState = state??0;
    var name = newState == 0 ? "ic_item.png" : (newState == 1 ? "ic_item_selected.png" : "ic_item_unable.png");

    var widget = ImageHelper.buildImage(name,height: 22,width: 22);
    return Container(
        height: 22,
        width: 22,
        child: InkWell(
          child: widget,
          onTap: (){
            if(newState != 2)
              onChanged?.call(newState == 0? 1: 0);
          },
        )
    );
  }
}
