package com.tencent.shadow.dynamic.host;

import org.json.JSONException;
import org.json.JSONObject;

public class RuntimeInfo {

    public final String apkPath;

    public final String oDexPath;

    public final String libraryPath;

    public RuntimeInfo(String apkPath, String oDexPath, String libraryPath) {
        this.apkPath = apkPath;
        this.oDexPath = oDexPath;
        this.libraryPath = libraryPath;
    }

    public RuntimeInfo(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            apkPath = jsonObject.getString("apkPath");
            oDexPath = jsonObject.optString("oDexPath");
            libraryPath = jsonObject.optString("libraryPath");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("apkPath", apkPath);
            jsonObject.putOpt("oDexPath", oDexPath);
            jsonObject.putOpt("libraryPath", libraryPath);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }


}
