
import '../widget/ptr/PtrWidget.dart';

import 'BaseViewModel.dart';


abstract class ListViewModel<T> extends BaseViewModel{


  static const int pageNumFirst = 1;

  int get pageSize => 10;


  ListViewModel(){
    refreshController = PtrController(initialRefresh: false);
  }

  /// 当前页码
  int _currentPageNum = pageNumFirst;

  int get page => _currentPageNum;

  //数据源
  List<T> list = [];

  List<T> data;

  Future refresh(Future Function() future) async {
    _currentPageNum = pageNumFirst;
    data = null;
    var code = await future.call();
    if(code == 1) {
      list.clear();
      if (null == data || data.isEmpty) {
        refreshController?.refreshCompleted(resetFooterState: true);
      } else {
        list.addAll(data);
        refreshController?.refreshCompleted();
        // 小于分页的数量,禁止上拉加载更多
        if (data.length < pageSize) {
          refreshController?.loadNoData();
        } else {
          //防止上次上拉加载更多失败,需要重置状态
          refreshController?.loadComplete();
        }
      }
    }
    else if(code == -1){
      if (refreshController.init) list.clear();
      refreshController?.refreshFailed();
    }
    else{
      refreshController?.refreshCompleted(resetFooterState: true);
    }
    return code;
  }

  /// 上拉加载更多
  Future loadMore(Future Function() future) async {
    data = null;
    ++_currentPageNum;
    var code = await future.call();
    if(code == 1) {
      if (null == data || data.isEmpty) {
        _currentPageNum--;
        refreshController?.loadNoData();
      } else {
        list.addAll(data);
        if (data.length < pageSize) {
          refreshController?.loadNoData();
        } else {
          refreshController?.loadComplete();
        }
      }
    }
    else if(code == -1){
      _currentPageNum--;
      refreshController?.loadFailed();
    }
    else{
      _currentPageNum--;
      refreshController?.loadFailed();
    }
    return code;
  }


  // @override
  // void dispose() {
  //   _refreshController?.dispose();
  //   super.dispose();
  // }

}