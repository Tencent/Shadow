

import 'dart:typed_data';

import 'package:crm/base/adapter/BaseAdapter.dart';
import 'package:crm/base/router/RouterHelper.dart';
import 'package:crm/base/utils/LaunchUtils.dart';
import 'package:crm/base/utils/PermissionUtils.dart';
import 'package:crm/base/utils/ToastUtil.dart';
import 'package:crm/base/utils/WidgetUtils.dart';
import 'package:crm/base/view/BaseWidget.dart';
import 'package:crm/base/widget/LinearWidget.dart';
import 'package:crm/base/widget/dialog/BottomDialog.dart';
import 'package:crm/common/MyColors.dart';
import 'package:crm/common/widget/ItemAddWidget.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:permission_handler/permission_handler.dart';
import '../controller/BaseController.dart';
import '../extension/WidgetExt.dart';
import '../http/HttpsProvider.dart';
import '../http/Params.dart';
import '../http/ReqCallBack.dart';
import '../model/FileModel.dart';
import '../utils/BaseUtils.dart';
import '../utils/ImageHelper.dart';
import '../utils/CstColors.dart';
import '../widget/TextView.dart';




class UploadWidget extends StatefulWidget {

  final String title;
  final String placeholder;
  final List<FileModel> fileList;
  final UploadController controller;
  final bool isNotNull;
  final int type;

  const UploadWidget({Key key, this.title, this.placeholder,
    this.fileList,
    this.isNotNull,
    this.controller,
    this.type:0,
  }) : super(key: key);

  @override
  _UploadWidgetState createState() => _UploadWidgetState();
}

class _UploadWidgetState extends BaseWidgetState<UploadWidget,UploadController> {

  @override
  UploadController getController() =>  widget.controller??UploadController();

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    controller.fileList = widget.fileList;
  }

  void onClickItem(FileModel model){
    if(BaseUtils.isImageByEnd(model.path)){
      RouterHelper.buildImage(context,[model.bigPath]);
    }
    else{
      LaunchUtils.open(model.path);
    }
  }

  @override
  Widget build(BuildContext context) {
    if(widget.type == 1){

      return Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          ItemAddWidget(title:widget.title,isNotNull: widget.isNotNull,callback: (){
            open();
          },),
          WidgetUtils.buildGrid(
              crossAxisCount:1,
              // padding: EdgeInsets.only(left: 16,right: 16),
              childAspectRatio: MediaQuery.of(context).size.width/78,
              adapter: BaseAdapter<FileModel>(
                  data: controller.fileList,
                  onItemClick: (context,index,model){
                    onClickItem(model);
                  },
                  builder: (context,index,model){
                    return LinearWidget(
                      height: 78,
                      bgColor: Colors.white,
                      direction: Axis.horizontal,
                      padding: EdgeInsets.symmetric(horizontal: 16),
                      // margin: EdgeInsets.symmetric(horizontal: 4),
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: [
                        ImageHelper.buildImage("ic_file_unknow.png",width: 22,height: 26),
                        SizedBox(width: 12,),
                        Column(
                          mainAxisSize: MainAxisSize.min,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            TextView(model.name,size: 16,color: MyColors.cl_1F2736,softWrap: false,),
                            SizedBox(height: 10,),
                            TextView(model.fsize,size: 12,color: MyColors.cl_7B8290,),
                          ],
                        ).buildExpanded(),
                        SizedBox(width: 10,),
                        ImageHelper.buildImage("ic_delete_file.png",width: 18,height: 18).buildInkWell(() {
                          controller.notifyRemove(model);
                        }),
                      ],
                    );
                  }
              )
          )
        ],
      );
    }



    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        ItemAddWidget(title:widget.title,isNotNull: widget.isNotNull,callback: (){
          open();
        },),
        Container(
          color: Colors.white,
          child: WidgetUtils.buildGrid(
              crossAxisCount:4,
              padding: EdgeInsets.only(left: 16,right: 16,top: 11),
              crossAxisSpacing: 8,
              mainAxisSpacing: 8,
              childAspectRatio:1.5,
              adapter: BaseAdapter<FileModel>(
                  data: controller.fileList,
                  onItemClick: (context,index,model){
                    onClickItem(model);
                  },
                  builder: (context,index,model){
                    return Stack(
                      alignment: Alignment.topRight,
                      children: [
                        Container(
                          margin: EdgeInsets.only(top: 9,right: 9),
                          child: null != model.data ? Image.memory(model.data, fit: BoxFit.contain,) :
                          ImageHelper.load(model.bigPath,fit: BoxFit.contain,),
                          width: double.infinity,
                        ),
                        ImageHelper.buildImage("ic_delete_file.png",width: 18,height: 18).buildInkWell(() {
                          controller.notifyRemove(model);
                        }),
                      ],
                    );
                  }
              )
          ),
        )
      ],
    );
  }

  // open() async {
  //   FilePickerResult result = await FilePicker.platform.pickFiles(
  //     allowCompression: true,
  //     type: FileType.any,
  //     // allowedExtensions: ['jpg', 'png'],
  //   );
  //   if (!BaseUtils.isEmptyList(result.files)) {
  //     var model = result.files.first;
  //     controller.upImage(model);
  //   }
  // }

  final _picker = ImagePicker();

  void open()  {
    BottomDialog.show(context,data: ["相机","相片"],title: "请选择",callback: (value,index) async {
      PickedFile response;
      if (index == 0)
        response = await _picker.getImage(source: ImageSource.camera);
      else{
        response = await _picker.getImage(source: ImageSource.gallery);
      }
      if (response != null) {
        controller.upImage(response);
      }
    }
    );


  }
}



class UploadController extends BaseWidgetController{



  void upImage(PickedFile model) async{
    ToastUtil.showDialog();
    var param = Params(type: 1);
    var bytes = await model.readAsBytes();
    var path = model.path;
    var name = path.substring(path.lastIndexOf("/") +1,path.length);
    param.putMultipartFile(
        MultipartFile.fromBytes(bytes, filename: name));

    var reqCallBack = ReqCallBack(
        isPaging: true,
        key: "detail",
        onSuccess: (result) {
          List<FileModel> list = result.map<FileModel>((item) =>
              FileModel.fromJson(item)
          ).toList();

          if (!BaseUtils.isEmptyList(list)) {
            list.first?.data = bytes;
            notifyAdd(list.first);
          }
        }
    );
    await postFile("Attach/upload", param, reqCallBack);
  }

  List<FileModel> _fileList;


  List<FileModel> get fileList => _fileList;

  set fileList(List<FileModel> value) {
    _fileList = value;
  }

  void notifyAdd(FileModel model){
    if(null == _fileList)
      _fileList = [];
    _fileList.add(model);
    notifyUI();
  }

  void notifyRemove(FileModel model){
    if(null == _fileList)
      _fileList = [];
    _fileList.remove(model);
    notifyUI();
  }

  @override
  String get value {
    return _fileList?.map((e) => e.id)?.join(",");
  }
}



//   //实例化选择图片
//   final ImagePicker picker = new ImagePicker();
// //用户本地图片
//   FileModel _userImage;//存放获取到的本地路径
// //异步吊起相机拍摄新照片方法
//   Future _getCameraImage() async {
//     final cameraImages = await picker.getImage(source: ImageSource.camera);
//     if (mounted) {
//       setState(() {
//         //拍摄照片不为空
//         if (cameraImages != null) {
//           // _userImage = File(cameraImages.path);
//           print('你选择的路径是：${_userImage.toString()}');
//           //图片为空
//         } else {
//           print('没有照片可以选择');
//         }
//       });
//     }
//   }
//   Future _getImage() async {
//     //选择相册
//     final pickerImages = await picker.getImage(source: ImageSource.gallery);
//     if(mounted){
//       setState(() {
//         if(pickerImages != null){
//           print('你选择的本地路径是：${_userImage.toString()}');
//         }else{
//           print('没有照片可以选择');
//         }
//       });
//     }
//   }
