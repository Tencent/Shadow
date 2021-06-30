class DateUtil {

  ///将时间日期格式转化为时间戳
  ///2018年12月11日
  ///2019-12-11
  ///2018年11月15 11:14分89
  ///结果是毫秒
  static int getTimeStap({formartData: String}) {
    var result = formartData.substring(0, 4) + "-" + formartData.substring(5, 7) + "-" + formartData.substring(8, 10);
    if (formartData.toString().length>=13&&formartData.substring(10, 13) != null) {
      result += "" + formartData.substring(10, 13);
    }
    if (formartData.toString().length>=17&&formartData.toString().substring(14, 16) != null) {
      result += ":" + formartData.substring(14, 16);
    }
    if (formartData.toString().length>=19&&formartData.substring(17, 19) != null) {
      result += ":" + formartData.substring(17, 19);
    }
    var dataTime = DateTime.parse(result);
    print(dataTime.millisecondsSinceEpoch);
    return dataTime.millisecondsSinceEpoch;
  }


  static String getChangeTime(String dateStr){
    var dates = dateStr.split(" ");
    if(dates.length != 2)
      return dateStr;
    DateTime dateTime = DateTime.tryParse(dateStr);


    if(isToday(dateTime)){
      return "今天 ${dates[1]}";
    }
    if(isYesterday(dateTime)){
      return "昨天 ${dates[1]}";
    }
    return dateStr;
  }

  static String getYearMonthDay(String dateStr){
    var dates = dateStr.split(" ");
    if(dates.length != 2)
      return dateStr;
    return dates[0];
  }

  static DateTime lastDayOfMonth(DateTime month) {
    final date = month.month < 12
        ? DateTime.utc(month.year, month.month + 1, 1, 12)
        : DateTime.utc(month.year + 1, 1, 1, 12);
    return date.subtract(const Duration(days: 1));
  }
  /// 是否是今天.
  static bool isToday(DateTime date, {bool isUtc = false}) {
    if (date == null) return false;
    DateTime old = date;
    DateTime now = isUtc ? DateTime.now().toUtc() : DateTime.now().toLocal();
    return old.year == now.year && old.month == now.month && old.day == now.day;
  }

  /// 是否是昨天.
  static bool isYesterday(DateTime date, {bool isUtc = false}) {
    if (date == null) return false;
    DateTime old = date.add(Duration(days: 1));
    DateTime now = isUtc ? DateTime.now().toUtc() : DateTime.now().toLocal();
    return old.year == now.year && old.month == now.month && old.day == now.day;
  }

  /// year is today.
  /// 是否是是同一天.
  static bool isCommonDay(DateTime oldDate, DateTime newDate,{bool isUtc = false}) {
    if (oldDate == null || newDate == null) return false;
    DateTime old = oldDate;
    DateTime now = isUtc ? newDate.toUtc() : newDate.toLocal();
    return old.year == now.year && old.month == now.month && old.day == now.day;
  }


  // 获取星期
  static String getWeek(DateTime date){
    var week = date.weekday;
    String w = '';
    switch (week.toString()) {
      case '1':
        w = '一';
        break;
      case '2':
        w = '二';
        break;
      case '3':
        w = '三';
        break;
      case '4':
        w = '四';
        break;
      case '5':
        w = '五';
        break;
      case '6':
        w = '六';
        break;
      case '7':
        w = '日';
        break;
    }
    return '周' + w.toString();
  }
}