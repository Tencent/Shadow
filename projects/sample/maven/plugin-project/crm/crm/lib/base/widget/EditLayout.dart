

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../provider/ProviderWidget.dart';
import '../provider/BaseViewModel.dart';
import '../utils/BaseUtils.dart';
import '../utils/ImageHelper.dart';
import '../widget/LinearWidget.dart';

import '../controller/BaseController.dart';

enum InputType {
  /// Align the text on the left edge of the container.
  number,
  numberDecimal,
  phone,
  abc,
  text,
  password,
  password_number,
}

class EditLayout extends StatelessWidget {

  final EdgeInsetsGeometry margin;
  final Axis direction;
  final MainAxisAlignment mainAxisAlignment;
  final CrossAxisAlignment crossAxisAlignment;
  final Decoration decoration;
  final Color bgColor;
  final MainAxisSize mainAxisSize;
  final double height;
  final double width;

  final double size;
  final String text;
  final Color color;
  final Color hintColor;
  final TextAlign textAlign;
  final bool readOnly;
  final String hint;
  final ValueChanged<String> onChanged;
  final EditController controller;
  final TextInputType keyboardType;
  final int maxLength;
  final Color stroke;
  final double radius;
  final EdgeInsetsGeometry padding;
  final InputType inputType;
  final ValueChanged<String> onSubmitted;
  final VoidCallback onEditingComplete;
  final VoidCallback onTap;
  final FocusNode focusNode;
  final List<Widget> leftChildren;
  final List<Widget> rightChildren;
  final BoxConstraints constraints;
  final InputBorder focusedBorder;
  final InputBorder border;
  final bool close;


  const EditLayout({Key key, this.margin,
    this.direction,
    this.mainAxisAlignment: MainAxisAlignment.center,
    this.crossAxisAlignment: CrossAxisAlignment.center
    , this.decoration,
    this.bgColor, this.mainAxisSize,
    this.height, this.width, this.size:12,
    this.color: Colors.black,
    this.hintColor: Colors.grey,
    this.textAlign: TextAlign.start,
    this.readOnly: false,
    this.hint,
    this.onChanged,
    this.controller,
    this.stroke: Colors.transparent,
    this.keyboardType,
    this.maxLength,
    this.focusNode,
    this.leftChildren,
    this.rightChildren,
    this.constraints,
    this.text,
    this.close: true,
    this.radius: 0.0,
    this.padding: const EdgeInsets.symmetric(vertical: 0.0),
    this.inputType: InputType.text,
    this.onSubmitted,
    this.onEditingComplete,
    this.onTap,
    this.focusedBorder,
    this.border

  }) : super(key: key);




  @override
  Widget build(BuildContext context) {
    var _controller = controller;
    if(null == controller){
      _controller = EditController();
    }
    if(!BaseUtils.isEmpty(text)) {
      _controller.text = BaseUtils.isEmpty(text) ? "" : text;
    }

    var hideClose = readOnly ? false : close;

    var obscureText =  inputType == InputType.password || inputType == InputType.password_number;

    List<TextInputFormatter> formatters = [];

    if(inputType == InputType.abc){
      var f = FilteringTextInputFormatter(RegExp("[a-zA-Z]"));
      formatters.add(f);
    }

    if(inputType == InputType.numberDecimal){
      var f = FilteringTextInputFormatter.allow(RegExp("[0-9.]"));
      formatters.add(f);
    }

    if(inputType == InputType.number || inputType == InputType.password_number){
      formatters.add(WhitelistingTextInputFormatter.digitsOnly);
    }

    var cborder = OutlineInputBorder(borderSide: BorderSide(color: stroke),
        borderRadius:  BorderRadius.all(Radius.circular(radius)));

    var newFocusedBorder = focusedBorder??cborder;
    var newBorder = border??cborder;


    var _textField =  TextField(
        focusNode: focusNode,
//        scrollPhysics: const NeverScrollableScrollPhysics(),
        onSubmitted: onSubmitted,
        onChanged: (value){
          _controller.closeController.offstage = value.isEmpty;
          onChanged(value);
        },
        onEditingComplete: onEditingComplete,
        onTap: onTap,
        obscureText: obscureText,
        inputFormatters: formatters,
        controller: _controller,
        style: TextStyle(
            fontSize: size,
            color: color
        ),
        textAlign: textAlign,
        readOnly: readOnly,
        maxLength: maxLength,
        toolbarOptions: ToolbarOptions(
            copy: true,
            cut: true,
            paste: true,
            selectAll: true
        ),
        keyboardType: keyboardType,
        decoration: InputDecoration(
            counterText:"",
            contentPadding: padding,
            focusedBorder: newFocusedBorder,
            border: newBorder,
            hintText: hint,
            enabledBorder: newBorder,
            hintStyle: TextStyle(
                fontSize: size,
                color: hintColor

            )
        )
    );

    List<Widget> children = [];

    if(null != leftChildren)
      children.addAll(leftChildren);
    children.add(Expanded(child: Stack(
      alignment: Alignment.centerLeft,
      children: [
        _textField,
        Positioned(
            right: 10,
            child: ProviderWidget<CloseController>(
              autoDispose: false,
              model: _controller.closeController,
              builder: (context,model,child){
                return  Visibility(
                    visible: !(model.offstage) && hideClose,
                    child: InkWell(
                      child: ImageHelper.buildImage("ic_close.png",width: 16,height: 16,),
                      onTap: (){
                        _controller.reset();
                      },
                    ));
              },
            )
        )
      ],
    )));
    if(null != rightChildren)
      children.addAll(rightChildren);
    return LinearWidget(
        constraints:constraints,
        direction: Axis.horizontal,
//        mainAxisAlignment: mainAxisAlignment,
        crossAxisAlignment: crossAxisAlignment,
        alignment: Alignment.centerLeft,
        height: height,
        width: width,
        margin: margin,
        decoration:decoration,
        bgColor: bgColor,
        children:children
    );
  }
}


class EditController extends TextEditingController  {


  CloseController _closeController;


  CloseController get closeController => _closeController;

  set closeController(CloseController value) {
    _closeController = value;
  }

  EditController({String text}):super(text: text){
    _closeController = CloseController();
     _closeController.offstage = BaseUtils.isEmpty(text);
  }

  void reset(){
    text = "";
    _closeController?.offstage = true;
  }

  bool isEmpty(){
    return BaseUtils.isEmpty(text);
  }

  @override
  void dispose() {
    // TODO: implement dispose
    _closeController?.dispose();
    super.dispose();
  }

}

class CloseController extends BaseViewModel{


  bool _offstage = true;

  bool get offstage => _offstage;

  set offstage(bool value) {
    if(_offstage == value)
      return;
    _offstage = value;
    notifyListeners();
  }


}

