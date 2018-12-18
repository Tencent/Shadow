package com.tencent.shadow.dynamic.host;

import java.io.File;

import dalvik.system.BaseDexClassLoader;

public class UUIDClassLoader extends BaseDexClassLoader {


    /**
     * 标示runtime版本
     */
    public String UUID;


    public UUIDClassLoader(String UUID, String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory == null ? null : new File(optimizedDirectory), librarySearchPath, parent);
        this.UUID = UUID;
    }
}
