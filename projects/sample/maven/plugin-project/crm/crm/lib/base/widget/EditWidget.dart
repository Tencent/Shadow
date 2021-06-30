
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import '../utils/BaseUtils.dart';
import '../widget/EditLayout.dart';



class EditWidget extends StatelessWidget {

  final EdgeInsetsGeometry margin;
  final Axis direction;
  final Decoration decoration;
  final Color bgColor;
  final MainAxisSize mainAxisSize;

  final double size;
  final String text;
  final Color color;
  final Color hintColor;
  final TextAlign textAlign;
  final bool readOnly;
  final String hint;
  final ValueChanged<String> onChanged;
  final TextEditingController controller;
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
  final InputBorder focusedBorder;
  final InputBorder border;
  final bool isCollapsed;
  final bool filled;
  final Color fillColor;
  final int maxLines;
  final int minLines;


  const EditWidget({Key key,
    this.margin,
    this.direction,
    this.decoration,
    this.bgColor, this.mainAxisSize,
    this.size:14,
    this.color: Colors.black,
    this.hintColor: Colors.grey,
    this.textAlign: TextAlign.start,
    this.readOnly: false,
    this.hint,
    this.isCollapsed:false,
    this.onChanged,
    this.controller,
    this.stroke: Colors.transparent,
    this.keyboardType,
    this.maxLength,
    this.focusNode,
    this.text,
    this.radius: 0.0,
    this.padding: const EdgeInsets.symmetric(vertical: 0.0),
    this.inputType: InputType.text, this.onSubmitted, this.onEditingComplete, this.onTap,
    this.focusedBorder, this.border,
    this.maxLines,
    this.minLines :1, this.filled:false, this.fillColor

  }) : super(key: key);



  @override
  Widget build(BuildContext context) {
    var _controller = controller;
    if(null == controller){
      _controller = TextEditingController();
    }
    if(!BaseUtils.isEmpty(text)) {
      _controller.text = BaseUtils.isEmpty(text) ? "" : text;
    }


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

    int NewMaxLength = maxLength;
    if(inputType == InputType.number){
      formatters.add(WhitelistingTextInputFormatter.digitsOnly);
    }
    else if(inputType == InputType.phone){
      formatters.add(WhitelistingTextInputFormatter.digitsOnly);
      NewMaxLength = 11;
    }

    var cborder = OutlineInputBorder(borderSide: BorderSide(color: stroke),
        borderRadius:  BorderRadius.all(Radius.circular(radius)));

    var newFocusedBorder = focusedBorder??cborder;
    var newBorder = border??cborder;

    return TextField(
        focusNode: focusNode,
        scrollPhysics: const NeverScrollableScrollPhysics(),
        onSubmitted: onSubmitted,
        onChanged: onChanged,
        onEditingComplete: onEditingComplete,
        onTap: onTap,
        obscureText: obscureText,
        inputFormatters: formatters,
        controller: _controller,
        minLines: minLines,
        maxLines: maxLines,
        style: TextStyle(
            height: 1.2,
            fontSize: size.sp,
            color: color
        ),
        textAlign: textAlign,
        readOnly: readOnly,
        maxLength: NewMaxLength,
        toolbarOptions: ToolbarOptions(
            copy: true,
            cut: true,
            paste: true,
            selectAll: true
        ),
        keyboardType: keyboardType,
        decoration: InputDecoration(
            filled: filled,
            fillColor: fillColor,
            isCollapsed: isCollapsed,
            counterText:"",
            contentPadding: padding,
            focusedBorder: newFocusedBorder,
            border: newBorder,
            hintText: hint,
            enabledBorder: newBorder,
            hintStyle: TextStyle(
                fontSize: size.sp,
                color: hintColor

            )
        )
    );
  }
}

