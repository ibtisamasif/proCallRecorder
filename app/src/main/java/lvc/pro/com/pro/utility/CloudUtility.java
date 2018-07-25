package lvc.pro.com.pro.utility;

import java.util.ArrayList;

/**
 * Created by chicmic on 3/8/18.
 */

public class CloudUtility {
    private static ArrayList<String> mFileNames = new ArrayList<>();

    public static void setCloudUploadList(String pFileName) {
        mFileNames.add(pFileName);
    }

    public static ArrayList<String> getCloudUploadList() {
        return mFileNames;
    }

    public static void clearCloudUploadList() {
        mFileNames.clear();
    }
}
