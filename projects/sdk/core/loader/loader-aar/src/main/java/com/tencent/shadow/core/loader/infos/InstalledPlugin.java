package com.tencent.shadow.core.loader.infos;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Loader加载插件的输入参数结构体
 * <p>
 * 这个类不能用Kotlin写是因为这个类可能会由非Kotlin写的代码new出来，
 * 而Loader打包的kotlin运行时可能连同Loader一起在一个独立的ClassLoader中。
 * 如果这个类用Kotlin写，就要求构造这个类对象的代码具有Kotlin运行时。
 *
 * @author cubershi
 */
public class InstalledPlugin implements Parcelable {
    final public File pluginFile;
    final public int pluginFileType;
    final public String partKey;
    final public String pluginVersionForPluginLoaderManage;
    final public String[] dependsOn;

    public InstalledPlugin(File pluginFile, int pluginFileType, String partKey, String pluginVersionForPluginLoaderManage, String[] dependsOn) {
        this.pluginFile = pluginFile;
        this.pluginFileType = pluginFileType;
        this.partKey = partKey;
        this.pluginVersionForPluginLoaderManage = pluginVersionForPluginLoaderManage;
        this.dependsOn = dependsOn;
    }

    protected InstalledPlugin(Parcel in) {
        pluginFile = new File(in.readString());
        pluginFileType = in.readInt();
        partKey = in.readString();
        pluginVersionForPluginLoaderManage = in.readString();
        dependsOn = in.createStringArray();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pluginFile.getAbsolutePath());
        dest.writeInt(pluginFileType);
        dest.writeString(partKey);
        dest.writeString(pluginVersionForPluginLoaderManage);
        dest.writeStringArray(dependsOn);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InstalledPlugin> CREATOR = new Creator<InstalledPlugin>() {
        @Override
        public InstalledPlugin createFromParcel(Parcel in) {
            return new InstalledPlugin(in);
        }

        @Override
        public InstalledPlugin[] newArray(int size) {
            return new InstalledPlugin[size];
        }
    };
}
