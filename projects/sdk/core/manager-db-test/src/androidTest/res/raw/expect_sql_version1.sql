PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;
CREATE TABLE shadowPluginManager ( id INTEGER PRIMARY KEY AUTOINCREMENT,hash VARCHAR , filePath VARCHAR, type INTEGER, partKey VARCHAR, dependsOn VARCHAR, uuid VARCHAR, version VARCHAR, installedTime INTEGER ,odexPath VARCHAR ,libPath VARCHAR , hostWhiteList VARCHAR, businessName VARCHAR);
INSERT INTO shadowPluginManager VALUES(1,'B0358A1919582A0FD467A42EEC40A5B7','/data/user/0/com.tencent.shadow.core.manager.test/cache/loader-apk-debug.apk',3,NULL,NULL,'0087DACB-3373-4E11-B50B-1B3076BD4F16','plugin_config_version1.json',1556258429000,NULL,NULL,NULL,NULL);
INSERT INTO shadowPluginManager VALUES(2,'51FDE1246F62D17D881493B037795D63','/data/user/0/com.tencent.shadow.core.manager.test/cache/runtime-apk-debug.apk',4,NULL,NULL,'0087DACB-3373-4E11-B50B-1B3076BD4F16','plugin_config_version1.json',1556258429000,NULL,NULL,NULL,NULL);
INSERT INTO shadowPluginManager VALUES(3,'DAC0234D1BE7F363A66263A01595DA64','/data/user/0/com.tencent.shadow.core.manager.test/cache/test-plugin-debug.apk',1,'test_main','[]','0087DACB-3373-4E11-B50B-1B3076BD4F16','plugin_config_version1.json',1556258429000,NULL,NULL,NULL,NULL);
INSERT INTO shadowPluginManager VALUES(4,NULL,'0087DACB-3373-4E11-B50B-1B3076BD4F16',5,NULL,NULL,'0087DACB-3373-4E11-B50B-1B3076BD4F16','plugin_config_version1.json',1556258429000,NULL,NULL,NULL,NULL);
COMMIT;
