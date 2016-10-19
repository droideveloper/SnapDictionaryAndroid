package org.fs.dictionary.core;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.Log;

import org.fs.dictionary.utils.LogHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Fatih on 27/11/14.
 */
public final class FileManager {

    private String  mExtention   = ".traineddata";
    private String  mPath        = "/tessdata";
    private File    mRootDir     = null;
    private LogHelper mLogHelper = null;
    private int mAllocation      = 4096;//8192;

    public FileManager(Context mContext) {
        //create logger
        mLogHelper = new LogHelper(this);
        //throws exception
        if(mContext == null) {
            throw new NullPointerException("mContext can not be null.");
        }
        //create actual root
        mRootDir = new File(mContext.getFilesDir().getAbsolutePath() + mPath);
        if(!mRootDir.exists()) {
            mRootDir.mkdirs();
        }

//        //create temp root
//        mTempDir = new File(mContext.getFilesDir().getAbsolutePath() + mTemp);
//        if(!mRootDir.exists()) {
//            mTempDir.mkdirs();
//        }
    }

    public void setAssetManager(AssetManager mAssetManager) throws IOException {
        if(mAssetManager == null) {
            return;
        }
        List<String> mFileList = Arrays.asList(mAssetManager.list("traineddata"));
        if(mFileList != null && mFileList.size() > 0) {
            for(String mFileName : mFileList) {
                InputStream mInputStream = mAssetManager.open("traineddata/" + mFileName);
                ByteArrayOutputStream mArrayStream = new ByteArrayOutputStream();
                byte[] block = new byte[mAllocation];
                int  mPointer;
                while ((mPointer = mInputStream.read(block)) != -1) {
                    mArrayStream.write(block, 0, block.length);
                }
                byte[] bytes = mArrayStream.toByteArray();
                mArrayStream.close();
                mInputStream.close();
                writeZipBufferToFile(bytes);
            }
        }
    }

    public void writeZipBufferToFile(byte[] bytes) throws IOException {
        ZipInputStream mZipInputStream = new ZipInputStream(new ByteArrayInputStream(bytes));
        ZipEntry mZipEntry;
        //loop entries
        while ((mZipEntry = mZipInputStream.getNextEntry()) != null) {
            if(mZipEntry.isDirectory()) {
                String mFolderName = mZipEntry.getName();
                File mDirectory = new File(mRootDir.getAbsolutePath() + "/" + mFolderName);
                if(!mDirectory.exists()) {
                    mDirectory.mkdirs();
                }
            } else {
                String mFileName = mZipEntry.getName();
                if(mFileName.contains(mExtention)) {
                    File mFile = new File(mRootDir.getAbsolutePath() + "/" + mFileName);
                    FileOutputStream mFileOutputStream = new FileOutputStream(mFile);
                    byte[] mAllocated = new byte[mAllocation];
                    int mPointer;
                    while((mPointer = mZipInputStream.read(mAllocated)) != -1) {
                        mFileOutputStream.write(mAllocated, 0, mPointer);
                    }
                    mFileOutputStream.close();

                    if(mLogHelper != null) {
                        mLogHelper.log(Log.ERROR, mFile.getAbsoluteFile().toString());
                    }
                }
            }
            mZipInputStream.closeEntry();
        }
        mZipInputStream.close();
    }

    /**
     * writes stream into file only one file to download it and grab it back.
     * @param in
     * @return
     * @throws IOException
     */
    public String writeMediaFile(InputStream in) throws IOException {
        final String fileName = "/x.mp3";
        FileOutputStream mArrayStream = new FileOutputStream(mRootDir.getAbsoluteFile() + fileName);
        byte[] block = new byte[mAllocation];
        int  mPointer;
        while ((mPointer = in.read(block)) != -1) {
            mArrayStream.write(block, 0, block.length);
        }
        mArrayStream.close();
        return new File(mRootDir.getAbsolutePath() + fileName).toURI().toString();
    }

    public boolean isLogEnabled() {
        return true;
    }

    public String getClassTag() {
        return FileManager.class.getSimpleName();
    }
}
