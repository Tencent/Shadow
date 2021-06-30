
import 'package:crm/base/provider/BaseViewModel.dart';
import 'package:flutter/material.dart';
import '../provider/StateWidget.dart';


class MyFutureProvider<T> extends StatefulWidget {


  final Future<T> Function() future;

  final BaseViewModel model;

  final AsyncWidgetBuilder<T> builder;

  final Widget Function(BuildContext context) errorBuilder;
  final Widget Function(BuildContext context) selfBuilder;

  const MyFutureProvider({Key key, this.future, this.builder, this.model, this.errorBuilder, this.selfBuilder}) : super(key: key);

  @override
  _MyFutureProviderState createState() => _MyFutureProviderState<T>();
}

class _MyFutureProviderState<T> extends State<MyFutureProvider<T>> {


  Object _activeCallbackIdentity;
  AsyncSnapshot<T> _snapshot;

  bool init = true;

  @override
  void initState() {
    super.initState();
    _snapshot = AsyncSnapshot<T>.nothing();
    _subscribe();
  }


  @override
  void didUpdateWidget(covariant MyFutureProvider<T> oldWidget) {
    super.didUpdateWidget(oldWidget);
    if(widget.model.requestTrue){
      if (_activeCallbackIdentity != null) {
        _unsubscribe();
        _snapshot = _snapshot.inState(ConnectionState.none);
      }
      _subscribe();
    }
  }


  @override
  Widget build(BuildContext context){
    if(!init){
      return widget.builder(context, _snapshot);
    }

    switch(_snapshot.connectionState) {
      case ConnectionState.none:
        return ViewStateEmptyWidget();
      case ConnectionState.waiting:
        return ViewStateBusyWidget();
      case ConnectionState.done:{
        if(_snapshot.hasError){
          return widget.errorBuilder?.call(context)??ViewStateErrorWidget(
            onPressed: (){
              widget.model?.notifyUI();
            },
          );
        }
        else{
          if(_snapshot.data == -1){
            return widget.errorBuilder?.call(context)??ViewStateErrorWidget(
              onPressed: (){
                widget.model?.notifyUI();
              },
            );
          }
          else if(_snapshot.data == 1) {
            init = false;
            return widget.builder(context, _snapshot);
          }
          else
            return widget.selfBuilder?.call(context)??ViewStateFailedWidget(
              onPressed: (){
                widget.model?.notifyUI();
              },
            );
        }
      }
    }

    return widget.builder(context, _snapshot);
  }


  @override
  void dispose() {
    _unsubscribe();
    super.dispose();
  }

  void _subscribe() {
    if (widget.future != null) {
      widget.model?.requestTrue = false;
      final Object callbackIdentity = Object();
      _activeCallbackIdentity = callbackIdentity;
      widget.future?.call()?.then<void>((T data) {
        if (_activeCallbackIdentity == callbackIdentity) {
          setState(() {
            _snapshot = AsyncSnapshot<T>.withData(ConnectionState.done, data);
          });
        }
      }, onError: (Object error, StackTrace stackTrace) {
        if (_activeCallbackIdentity == callbackIdentity) {
          setState(() {
            _snapshot = AsyncSnapshot<T>.withError(ConnectionState.done, error);
          });
        }
      });
      _snapshot = _snapshot.inState(ConnectionState.waiting);
    }
  }

  void _unsubscribe() {
    _activeCallbackIdentity = null;
  }
}
