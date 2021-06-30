import 'package:flutter/material.dart';
import 'package:provider/provider.dart';



class ProviderWidget<T extends ChangeNotifier> extends StatefulWidget {
  final ValueWidgetBuilder<T> builder;
  final T model;
  final Widget child;
  final Function(T model) onModelReady;
  final bool autoDispose;
  final bool wantKeepAlive;

  ProviderWidget({
    Key key,
    @required this.builder,
    @required this.model,
    this.child,
    this.onModelReady,
    this.autoDispose: true,
    this.wantKeepAlive: false,
  }) : super(key: key);

  _ProviderWidgetState<T> createState() => _ProviderWidgetState<T>();
}

class _ProviderWidgetState<T extends ChangeNotifier>
    extends State<ProviderWidget<T>> with AutomaticKeepAliveClientMixin{
  T model;

  @override
  void initState() {
    model = widget.model;
    widget.onModelReady?.call(model);
    super.initState();
  }

  @override
  void dispose() {
    if (widget.autoDispose) model.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider<T>.value(
      value: model,
      child: Consumer<T>(
        builder: widget.builder,
        child: widget.child,
      ),
    );
  }



  @override
  bool get wantKeepAlive => widget.wantKeepAlive;

}

typedef ValueWidgetBuilder2<A,B> = Widget Function(BuildContext context, A model1, B model2, Widget child);

class ProviderWidget2<A extends ChangeNotifier, B extends ChangeNotifier>
    extends StatefulWidget{


  final ValueWidgetBuilder2<A,B>
  builder;
  final A model1;
  final B model2;
  final Widget child;
  final Function(A model1, B model2) onModelReady;
  final bool autoDispose;
  final bool isInit;
  final bool wantKeepAlive;

  ProviderWidget2({
    Key key,
    @required this.builder,
    @required this.model1,
    @required this.model2,
    this.child,
    this.onModelReady,
    this.autoDispose,
    this.isInit: false,
    this.wantKeepAlive: false,
  }) : super(key: key);

  _ProviderWidgetState2<A, B> createState() => _ProviderWidgetState2<A, B>();
}

class _ProviderWidgetState2<A extends ChangeNotifier, B extends ChangeNotifier>
    extends State<ProviderWidget2<A, B>> with AutomaticKeepAliveClientMixin {
  A model1;
  B model2;

  @override
  void initState() {
    model1 = widget.model1;
    model2 = widget.model2;
    widget.onModelReady?.call(model1, model2);
    super.initState();
  }

  @override
  void dispose() {
    if (widget.autoDispose) {
      model1.dispose();
      model2.dispose();
    }
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
        providers: [
          ChangeNotifierProvider<A>.value(value: model1),
          ChangeNotifierProvider<B>.value(value: model2),
        ],
        child: Consumer2<A, B>(
          builder: widget.builder,
          child: widget.child,
        ));
  }



  @override
  bool get wantKeepAlive => widget.wantKeepAlive;
}
