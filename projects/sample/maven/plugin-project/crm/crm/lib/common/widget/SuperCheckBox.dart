
import 'package:crm/base/utils/ImageHelper.dart';
import 'package:flutter/material.dart';


class SuperCheckBox extends StatelessWidget {


  final String icon;
  final String checkIcon;
  final double height;
  final double width;
  final bool isCheck;

  const SuperCheckBox({Key key, this.icon, this.checkIcon, this.height, this.width,
    this.isCheck:false
  }) : super(key: key);


  @override
  Widget build(BuildContext context) {
      var name = isCheck? checkIcon : icon;

    var widgets = ImageHelper.buildImage(name,height: height,width: width);
    return Container(
        height: height,
        width: width,
        child: widgets
    );
  }
}

