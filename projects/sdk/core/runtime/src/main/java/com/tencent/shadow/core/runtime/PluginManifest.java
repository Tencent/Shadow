package com.tencent.shadow.core.runtime;

import android.os.Parcel;
import android.os.Parcelable;

public interface PluginManifest {
    /**
     * same as android.content.pm.PackageItemInfo#packageName
     */
    String getApplicationPackageName();

    /**
     * same as android.content.pm.ApplicationInfo#className
     */
    String getApplicationClassName();

    /**
     * same as android.content.pm.ApplicationInfo#appComponentFactory
     */
    String getAppComponentFactory();

    /**
     * same as android.content.pm.ApplicationInfo#theme
     */
    int getApplicationTheme();

    ActivityInfo[] getActivities();

    ServiceInfo[] getServices();

    ReceiverInfo[] getReceivers();

    ProviderInfo[] getProviders();

    abstract class ComponentInfo implements Parcelable {
        public final String className;

        public ComponentInfo(String className) {
            this.className = className;
        }

        protected ComponentInfo(Parcel in) {
            className = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(className);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<ComponentInfo> CREATOR = new Creator<ComponentInfo>() {
            @Override
            public ComponentInfo createFromParcel(Parcel in) {
                throw new UnsupportedOperationException();
            }

            @Override
            public ComponentInfo[] newArray(int size) {
                return new ComponentInfo[size];
            }
        };
    }

    final class ActivityInfo extends ComponentInfo implements Parcelable {
        public final int theme;
        public final int configChanges;
        public final int softInputMode;

        public ActivityInfo(String className,
                            int theme,
                            int configChanges,
                            int softInputMode) {
            super(className);
            this.theme = theme;
            this.configChanges = configChanges;
            this.softInputMode = softInputMode;
        }

        protected ActivityInfo(Parcel in) {
            super(in);
            theme = in.readInt();
            configChanges = in.readInt();
            softInputMode = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(theme);
            dest.writeInt(configChanges);
            dest.writeInt(softInputMode);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<ActivityInfo> CREATOR = new Creator<ActivityInfo>() {
            @Override
            public ActivityInfo createFromParcel(Parcel in) {
                return new ActivityInfo(in);
            }

            @Override
            public ActivityInfo[] newArray(int size) {
                return new ActivityInfo[size];
            }
        };
    }

    final class ServiceInfo extends ComponentInfo {

        public ServiceInfo(String className) {
            super(className);
        }
    }

    final class ReceiverInfo extends ComponentInfo {
        public final String[] actions;

        public ReceiverInfo(String className, String[] actions) {
            super(className);
            this.actions = actions;
        }
    }

    final class ProviderInfo extends ComponentInfo {
        public final String authorities;
        public final boolean grantUriPermissions;

        public ProviderInfo(String className, String authorities, boolean grantUriPermissions) {
            super(className);
            this.authorities = authorities;
            this.grantUriPermissions = grantUriPermissions;
        }
    }

}
