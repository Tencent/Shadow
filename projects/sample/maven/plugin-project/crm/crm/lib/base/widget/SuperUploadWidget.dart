
import 'package:crm/base/adapter/BaseAdapter.dart';
import 'package:crm/base/model/FileModel.dart';
import 'package:crm/base/utils/ImageHelper.dart';
import 'package:crm/base/utils/WidgetUtils.dart';
import 'package:crm/common/widget/ItemAddWidget.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import '../../base/view/BaseWidget.dart';
import '../../base/extension/WidgetExt.dart';
import '../../base/extension/ListExt.dart';

class SuperUploadWidget extends StatefulWidget {

  final String title;
  final String placeholder;
  final double width;
  final double height;
  final FileModel fileInfo;
  final SuperUploadController controller;
  final bool isNotNull;

  const SuperUploadWidget({Key key, this.title, this.placeholder,
    this.height:129,
    this.width:200,
    this.fileInfo,
    this.isNotNull,
    this.controller
  }) : super(key: key);


  @override
  _SuperUploadWidgetState createState() => _SuperUploadWidgetState();
}

class _SuperUploadWidgetState extends BaseWidgetState<SuperUploadWidget,SuperUploadController> {

  @override
  SuperUploadController getController() =>  SuperUploadController();

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        ItemAddWidget(title:widget.title,isNotNull: widget.isNotNull,callback: (){
          // open();
        },),
        WidgetUtils.buildGrid(
            padding: EdgeInsets.only(left: 16,right: 16),
            adapter: BaseAdapter<FileModel>(
                data: null,
                builder: (context,index,model){
                  return Stack(
                    children: [
                      ImageHelper.load(model.bigPath),

                    ],
                  );
                }
            )
        )
      ],
    );
  }
}

class SuperUploadController extends BaseWidgetController{

}