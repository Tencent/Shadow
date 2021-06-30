
import 'package:crm/base/router/RouterHelper.dart';
import 'package:crm/base/utils/LaunchUtils.dart';
import 'package:flutter/material.dart';
import '../extension/WidgetExt.dart';
import '../model/FileModel.dart';
import '../utils/BaseUtils.dart';
import '../utils/ImageHelper.dart';
import '../utils/CstColors.dart';
import '../widget/LinearWidget.dart';
import '../widget/TextView.dart';


class FileWidget extends StatefulWidget {

  final FileModel fileInfo;
  final String url;
  final String title;
  final double width;
  final double height;
  final String placeholder;

  const FileWidget({Key key,this.fileInfo, this.url, this.title,
    this.width: 80,
    this.height:80,
    this.placeholder:"ic_placeholder.png",
  }) : super(key: key);

  @override
  _FileWidgetState createState() => _FileWidgetState();
}

class _FileWidgetState extends State<FileWidget> {

  String url;

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    url = widget.url??widget.fileInfo?.bigPath;
    var curUrl = BaseUtils.isImageByEnd(url) ? url : "";
    return Container(
      padding: EdgeInsets.only(right: 8,top: 10),
      child:   ImageHelper.load(
        curUrl,
        placeholder: widget.placeholder,
        height: widget.height,
        width: widget.width,
      ).buildInkWell(() {
        onClickItem(widget.fileInfo);
      }),
    );
  }

  void onClickItem(FileModel model){
    if(BaseUtils.isImageByEnd(model.bigPath)){
      RouterHelper.buildImage(context,[model.bigPath]);
    }
    else{
      LaunchUtils.open(model.bigPath);
    }
  }
}

