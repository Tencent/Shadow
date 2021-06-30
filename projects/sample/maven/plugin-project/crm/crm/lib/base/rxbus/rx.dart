
import '../extension/ListExt.dart';
import 'package:rxdart/rxdart.dart';

//通信


class Bus<T>{
  PublishSubject<Message<T>> _subject;
  int _tag;
  int _curPageTag;


  PublishSubject<Message<T>> get subject => _subject;

  int get tag => _tag;
  int get curPageTag => _curPageTag;

  Bus(int tag,int curPageTag) {
    this._tag = tag;
    this._curPageTag = curPageTag;
    _subject = PublishSubject<Message<T>>();
  }

}

class RxBusUtils{

  Map<int,int> map;

  void register<T>(int tag,Function(T data) dataCallback){
    if(null == map)
      map = {};
    var systemTime = DateTime.now().millisecondsSinceEpoch;
    map[tag] = systemTime;
    RxBus.singleton.register<T>(tag,map[tag]).listen((value) {
      dataCallback?.call(value.data);
    });
  }

  void post<T>(int tag,T data){
    RxBus.singleton.post<T>(tag, data);
  }

  void dispose() {
    RxBus.singleton.dispose(map);
    map?.clear();
    map = null;
  }
}

class RxBus {

  static const int Code_1 = 1;

  List<Bus> _busList;

  RxBus._internal(){
    _busList = [];
  }

  static final RxBus _singleton =  RxBus._internal();

  factory RxBus() {
    return _singleton;
  }

  static RxBus get singleton => _singleton;



  Stream<Message<T>> register<T>(int tag,int curPageTag) {

    Bus _eventBus;
    //已经注册过的tag不需要重新注册
    if (_busList.isNotEmpty) {
      _busList.forEach((bus) {
        if (bus.tag == tag) {
          _eventBus = bus;
          return;
        }
      });
    }
    if (_eventBus == null) {
      _eventBus = Bus(tag,curPageTag);
      _busList.add(_eventBus);
    }

    var stream =  _eventBus.subject.stream.where((event) {
      var isc = event.tag == tag;
      return isc;
    }).cast<Message<T>>();
    return stream;
  }

  ///发送事件
  void post<T>(int tag,T data) {
    var rxBus = _busList.find<Bus>((element) => element.tag == tag);

    if(null != rxBus) {
      var msg = Message<T>();
      msg.tag = tag;
      msg.data = data;
      rxBus.subject.sink.add(msg);
    }
  }

  void dispose(Map<int,int> map){
    if(null == map)
      return;
    var toRemove = [];
    _busList.forEach((rxBus) {
      var value = map[rxBus.tag];
      if(null != value && map[rxBus.tag] == rxBus.curPageTag){
        rxBus.subject.close();
        toRemove.add(rxBus);
      }
    });

    toRemove.forEach((rxBus) {
      _busList.remove(rxBus);
    });

  }

}


class Message<T>{

  int _tag;
  T _data;



  int get tag => _tag;

  set tag(int value) {
    _tag = value;
  }

  T get data => _data;

  set data(T value) {
    _data = value;
  }


}