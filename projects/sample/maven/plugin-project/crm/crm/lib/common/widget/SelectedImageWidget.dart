
import '../../base/utils/ImageHelper.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import '../../base/view/BaseWidget.dart';
import '../../base/extension/WidgetExt.dart';
import '../../base/extension/ListExt.dart';

class SelectedImageWidget extends StatefulWidget {

  final String icon;
  final String selectedIcon;
  final SelectedImageController controller;
  final bool selected;

  final double height;
  final double width;
  final EdgeInsetsGeometry padding;
  final EdgeInsetsGeometry margin;
  final Function(SelectedImageController controller) onSelectedCallback;


  const SelectedImageWidget({Key key,
    @required this.icon,
    @required this.selectedIcon,
    this.controller,
    this.selected:false,
    this.height:28,
    this.width:28,
    this.padding,
    this.margin,
    this.onSelectedCallback
  }) : super(key: key);


  @override
  _SelectedImageWidgetState createState() => _SelectedImageWidgetState();
}

class _SelectedImageWidgetState extends BaseWidgetState<SelectedImageWidget,SelectedImageController> {

  @override
  SelectedImageController getController() =>  widget.controller??SelectedImageController();

  @override
  void initState() {
    super.initState();
    controller.selected = widget.selected;
  }

  @override
  Widget build(BuildContext context) {

    var name = controller.selected ? widget.selectedIcon : widget.icon;
    return Container(
      alignment: Alignment.centerLeft,
      padding: widget.padding,
      margin: widget.margin,
      child: ImageHelper.buildImage(name,height: widget.height,width: widget.width)
    ).buildInkWell((){
      widget.onSelectedCallback?.call(controller);
    });
  }
}

class SelectedImageController extends BaseWidgetController{
   bool selected;

   setSelected(bool selected){
     this.selected = selected;
     notifyUI();
   }

}

