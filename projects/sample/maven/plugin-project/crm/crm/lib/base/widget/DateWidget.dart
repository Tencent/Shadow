

import 'package:flutter/material.dart';
import '../controller/BaseController.dart';
import '../painting/MyDecoration.dart';
import '../utils/BaseUtils.dart';
import '../utils/ImageHelper.dart';
import '../utils/CstColors.dart';
import '../widget/TextView.dart';






class DateWidget extends StatefulWidget {

  final String hint;
  final String text;
  final bool isMin;
  final void Function(String value) onDateSelected;
  final DateController controller ;

  const DateWidget({Key key, this.hint, this.text,this.controller,this.onDateSelected,this.isMin : true}) : super(key: key);

  @override
  _DateWidgetState createState() => _DateWidgetState();
}

class _DateWidgetState extends State<DateWidget> {


  DateController viewModel;

  @override
  void initState() {
    viewModel = widget.controller??DateController();
    viewModel?.setNotifyWidget(() {
      setState(() {

      });
    });
    viewModel?.setValue(widget.text);
    super.initState();
  }

  @override
  Widget build(BuildContext context) {

    var color = viewModel.isSelected? CstColors.cl_00020D : CstColors.cl_9BA0AA;
    var text =  viewModel.value ??widget.hint;

    var myWidget = widget.isMin ? SizedBox.shrink() : Spacer();
    var inkWell =  InkWell(
      child: Container(
        alignment: Alignment.centerLeft,
        padding: EdgeInsets.symmetric(horizontal: 10),
        constraints:BoxConstraints(
          minHeight: 34,
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            ConstrainedBox(
              constraints:BoxConstraints(
                minWidth: 122,
              ),
              child: TextView(text,color: color,size: 14,),
            ),
            SizedBox(width: 10,),
            myWidget,
            IndexedStack(
              index: !viewModel.isSelected? 0: 1,
              children: [
                ImageHelper.buildImage("ic_date.png",width: 16,height: 16),
                InkWell(
                  child: ImageHelper.buildImage("ic_close.png",width: 16,height: 16,),
                  onTap: () async {
                    viewModel?.setDate(null);
                  },
                )
              ],
            )
          ],
        ),
      ),
      onTap: () async {

        var selected = await showDatePicker(
            context: context,
            initialDate: DateTime.now(),
            firstDate: DateTime(2020),
            lastDate: DateTime(2030));

        if(null == selected)
          return;


        viewModel?.setDate(selected);

        widget.onDateSelected?.call(viewModel?.date);
      },
    );
    return Ink(
      child: inkWell,
      decoration: MyDecoration.buildShape(
        stroke: CstColors.cl_E6EAEE,
      ),
    );
  }

  @override
  void dispose() {
    viewModel?.dispose();
    super.dispose();
  }
}

class DateController  with BaseController{


  String _date;


  String get date => _date;

  void setDate(DateTime start){
    if(null == start){
      setValue(null);
    }
    else{
      setValue("${start.year}-${getZStr(start.month)}-${getZStr(start.day)}");
    }
  }


  String getZStr(int value){
    return value < 10 ? "0${value}" : "${value}";
  }

  @override
  void setValue(String value) {
    this._date = value;
    super.setValue(value);
    isSelected = !BaseUtils.isEmpty(value);
    notifyWidget();
  }


  bool _isSelected = false;

  bool get isSelected => _isSelected;

  set isSelected(bool value) {
    _isSelected = value;
  }



}

