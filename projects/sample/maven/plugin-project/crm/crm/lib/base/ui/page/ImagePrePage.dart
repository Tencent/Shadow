
import 'package:crm/base/adapter/BaseAdapter.dart';
import 'package:crm/base/provider/BaseViewModel.dart';
import 'package:crm/base/utils/ImageHelper.dart';
import 'package:crm/base/utils/WidgetUtils.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:photo_view/photo_view.dart';
import 'package:photo_view/photo_view_gallery.dart';
import '../../../base/view/BasePage.dart';
import '../../../base/extension/WidgetExt.dart';
import '../../../base/extension/ListExt.dart';

class ImagePrePage extends StatefulWidget {

  final List<String> data;
  final int position;

  const ImagePrePage({Key key, this.data,this.position:0}) : super(key: key);

  @override
  _ImagePrePageState createState() => _ImagePrePageState();
}

class _ImagePrePageState extends BasePageState<ImagePrePage,ImagePreVM> {

  @override
  ImagePreVM getViewModel() =>  ImagePreVM();

  @override
  Widget getAppBar(BuildContext context) {
    return createHeaderWidget(backIcon: "ic_close_white.png",backgroundColor: Colors.black);
  }

  @override
  Color get bgColor => Colors.black;

  PageController _pageController;

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    var index = (widget.position < widget.data?.length??0) ? widget.position : 0;
    _pageController = PageController(initialPage: index);
  }

  @override
  void dispose() {
    _pageController?.dispose();
    super.dispose();
  }
  @override
  Widget getView(BuildContext context) {
    return Container(
        child: WidgetUtils.buildPageView(
          controller: _pageController,
          adapter: BaseAdapter<String>(
            isClick: false,
            onItemClick: (context,index,model){

            },
            builder: (context,index,model){

              return PhotoView(
                imageProvider: NetworkImage(model),
              );
            },
            data: widget.data
          )
        )
    );
  }
}

class ImagePreVM extends BaseViewModel{

}

