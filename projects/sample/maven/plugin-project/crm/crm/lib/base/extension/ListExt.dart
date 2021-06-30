extension ListExt on List{

  bool isNotEmptyOrNull(){
    if(this == null)
      return false;
    return this.isNotEmpty;
  }



  T find<T>(bool test(T element)){
    if(null == this)
      return null;
    for (T element in this){
      if(test(element))
        return element;
    }
    return null;
  }

  List<T> filter<T>(bool test(T element)){
    if(null == this)
      return null;
    List<T> newList = [];
    for (T element in this){
      if(test(element))
        newList.add(element);
    }
    return newList;
  }

  String listToString() {
    if (this == null) {
      return "";
    }
    String result = "";
    this.forEach((element) {
      if(result.isEmpty)
        result = '$element';
      else
        result = '$result,$element';
    });
    return result.toString();
  }

}
