class ItemModel {


  String _title;
  String _content;
  String _icon;
  String _iconSelected;
  String _url;
  int _isSelected;
  int _type;


  ItemModel(
  {
    String title,
    String content,
    String icon,
    String iconSelected,
    String url,
    int isSelected,
    int type,
}
      ){
    this._title = title;
    this._content = content;
    this._icon = icon;
    this._url = url;
    this._isSelected = isSelected;
    this._type = type;
    this._iconSelected = iconSelected;
  }


  String get iconSelected => _iconSelected;

  set iconSelected(String value) {
    _iconSelected = value;
  }

  String get title => _title;

  set title(String value) {
    _title = value;
  }


  int get type => _type;

  set type(int value) {
    _type = value;
  }

  String get content => _content;

  int get isSelected => _isSelected;

  set isSelected(int value) {
    _isSelected = value;
  }

  set content(String value) {
    _content = value;
  }

  String get url => _url;

  set url(String value) {
    _url = value;
  }

  String get icon => _icon;

  set icon(String value) {
    _icon = value;
  }


}
