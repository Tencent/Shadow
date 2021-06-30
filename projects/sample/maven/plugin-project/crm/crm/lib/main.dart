
import 'dart:io';

import 'package:bot_toast/bot_toast.dart';
import 'package:crm/base/BaseApp.dart';
import 'package:crm/base/router/routes.dart';
import 'package:crm/ui/main/page/LoginPage.dart';
import 'package:flutter/material.dart';
import 'package:flutter/physics.dart';
import 'package:flutter/services.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:lifecycle/lifecycle.dart';
// import 'package:lifecycle/lifecycle.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';
import 'package:crm/base/utils/CstColors.dart';


void main() async{

  WidgetsFlutterBinding.ensureInitialized();
  BaseApp.init();



  runApp(ScreenUtilInit(
    designSize: Size(390, 754),
    builder:()=> MyApp(),
    // child: MyApp(),
  ));

  if (Platform.isAndroid) {
    SystemChrome.setSystemUIOverlayStyle(SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
        statusBarIconBrightness: Brightness.dark,
        statusBarBrightness: Brightness.dark));
  }

}






class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return RefreshConfiguration(
        headerBuilder: () => WaterDropHeader(),        // Configure the default header indicator. If you have the same header indicator for each page, you need to set this
        footerBuilder:  () => ClassicFooter(),        // Configure default bottom indicator
        headerTriggerDistance: 80.0,        // header trigger refresh trigger distance
        springDescription:SpringDescription(stiffness: 170, damping: 16, mass: 1.9),         // custom spring back animate,the props meaning see the flutter api
        maxOverScrollExtent :100, //The maximum dragging range of the head. Set this property if a rush out of the view area occurs
        maxUnderScrollExtent:0, // Maximum dragging range at the bottom
        enableScrollWhenRefreshCompleted: false, //This property is incompatible with PageView and TabBarView. If you need TabBarView to slide left and right, you need to set it to true.
        enableLoadingWhenFailed : true, //In the case of load failure, users can still trigger more loads by gesture pull-up.
        hideFooterWhenNotFull: false, // Disable pull-up to load more functionality when Viewport is less than one screen
        enableBallisticLoad: true, // trigger load more by BallisticScrollActivity
        child: MaterialApp(
          debugShowCheckedModeBanner: false,
          theme: ThemeData(
            //primarySwatch: Colors.green,//主题色
              primaryColor: Colors.white,//导航栏颜色
              canvasColor: CstColors.bgColor,//画布颜色
              // textTheme: TextTheme(bodyText1:TextStyle(height: 1.2)),
              buttonTheme: ButtonThemeData(minWidth: 40,
                  layoutBehavior:ButtonBarLayoutBehavior.padded)

          ),

          localeListResolutionCallback: (List<Locale> locales, Iterable<Locale> supportedLocales) {
            return Locale('zh');
          },
          //下拉刷新
          localeResolutionCallback:
              (Locale locale, Iterable<Locale> supportedLocales) {
            return locale;
          },

          supportedLocales: [
            const Locale('zh', 'CH'),
            const Locale('en', 'US'),
          ],
          builder: BotToastInit(),
          // builder: (context, child)
          //     GestureDetector(
          //       onTap: () {
          //         FocusScopeNode currentFocus = FocusScope.of(context);
          //         if (!currentFocus.hasPrimaryFocus && currentFocus.focusedChild != null) {
          //           var l = currentFocus.focusedChild;
          //           var list = l.children;
          //           if(null != list && list.isNotEmpty){
          //             FocusScopeNode trueNode = list.last;
          //             var focusedChild= trueNode.focusedChild;
          //             if(focusedChild != null){
          //               FocusManager.instance.primaryFocus.unfocus();
          //             }
          //           }
          //
          //         }
          //       },
          //       child: BotToastInit().call(context,child),
          //     ),

          //       builder: (context, widget) {
          // return MediaQuery(
          // ///Setting font does not change with system font size
          // data: MediaQuery.of(context).copyWith(textScaleFactor: 1.0),
          // child: widget,
          // );
          navigatorObservers: [BotToastNavigatorObserver(),defaultLifecycleObserver],
          localizationsDelegates: [
            RefreshLocalizations.delegate,
            GlobalMaterialLocalizations.delegate,
            GlobalWidgetsLocalizations.delegate,
            GlobalCupertinoLocalizations.delegate,
          ],

          // initialRoute: MyRoutes.homePage,
          // onGenerateRoute: (setting) => CRMBaseApp.fluroRouter.generator(setting),
          initialRoute: Routes.initRouter,
          onGenerateRoute: (settings)=> Routes.onGenerateRoute(context,settings),
          onUnknownRoute: (settings)=> Routes.onGenerateRoute(context, settings),


        )
    );

  }

}
