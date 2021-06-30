
import 'package:crm/base/painting/MyDecoration.dart';
import 'package:crm/base/utils/ImageHelper.dart';
import 'package:crm/base/view/BaseWidget.dart';
import 'package:crm/base/widget/EditWidget.dart';
import 'package:crm/common/MyColors.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import '../../base/extension/WidgetExt.dart';
import '../../base/extension/ListExt.dart';



class SearchInputWidget extends StatefulWidget {



  final TextEditingController controller;
  final String hint;
  final FocusNode focusNode;
  final ValueChanged<String> onChanged;
  final EdgeInsetsGeometry padding;


  const SearchInputWidget({Key key, this.controller, this.hint, this.onChanged,this.focusNode,
    this.padding}) : super(key: key);

  @override
  _SearchInputWidgetState createState() => _SearchInputWidgetState();
}

class _SearchInputWidgetState extends BaseWidgetState<SearchInputWidget,SearchInputController> {

  @override
  SearchInputController getController() =>  SearchInputController();


  @override
  Widget build(BuildContext context) {
    return Container(
        color: Colors.white,
        padding: widget.padding,
        height: 50,
        child: Material(
          color: Colors.transparent,
          child: Stack(
            alignment: Alignment.centerLeft,
            children: [
              Container(
                height: 36,
                alignment: Alignment.centerLeft,
                decoration: MyDecoration.buildShape(radius: 18,solid: MyColors.cl_F4F4F6),
                child: EditWidget(
                  focusNode: widget.focusNode,
                  controller: widget.controller,
                  hintColor: MyColors.cl_7B8290,
                  hint: widget.hint,
                  color: MyColors.cl_161722,
                  size: 14,
                  padding: EdgeInsets.only(left: 34,right: 12),
                  onChanged: (value){
                    widget.onChanged?.call(value);
                    controller?.notifyUI();
                  },
                ),
              ),
              Positioned(child: ImageHelper.buildImage("ic_search.png",width: 14,height: 14),left: 12,),
              Positioned(right: 12,child: Visibility(
                  visible: widget.controller.text.isNotEmpty,
                  child: ImageHelper.buildImage("ic_delete_file.png",width: 14,height: 14).buildInkWell((){
                    widget.controller.text = "";
                    widget.onChanged?.call(widget.controller.text);
                    controller?.notifyUI();
                  },
                  )
              ))
            ],
          ),
        )
    );
  }

}


class SearchInputController extends BaseWidgetController{

}

