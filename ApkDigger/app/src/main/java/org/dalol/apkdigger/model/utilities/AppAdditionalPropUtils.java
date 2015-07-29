package org.dalol.apkdigger.model.utilities;

import android.os.Handler;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * @author Filippo Engidashet
 * @version 1.0
 * @date 7/29/2015
 *
 * {@link Runnable, @link Handler}
 * AppSizeUtils.java: This is a helper class that takes an instance of filesize and modifiedDate
 * and after doing some process it reflect the result using a handler
 */
public class AppAdditionalPropUtils implements Runnable {

    private static final double BLOCK_SIZE = 1024.0;
    private static final double UNIT_LIMIT = 969.0;
    private TextView mFileSize;
    private TextView mModifiedDate;
    private String mFileName;

    public AppAdditionalPropUtils(TextView fileSize, TextView modifiedDate, String fileName) {
        mFileSize = fileSize;
        mModifiedDate = modifiedDate;
        mFileName = fileName;
        new Handler().post(this);
    }

    @Override
    public synchronized void run() {

        long installed = new File(mFileName).lastModified();
        setModifiedDate(installed);

        double fileSizeInBytes = new File(mFileName).length();

        if (fileSizeInBytes > UNIT_LIMIT) {

            double fileSizeInKiloB = (fileSizeInBytes / BLOCK_SIZE);

            if (fileSizeInKiloB > UNIT_LIMIT) {

                double fileSizeInMegaB = (fileSizeInKiloB / BLOCK_SIZE);

                if (fileSizeInMegaB > UNIT_LIMIT) {

                    double fileSizeInGigaB = (fileSizeInMegaB / BLOCK_SIZE);

                    if (fileSizeInGigaB > UNIT_LIMIT) {

                        double fileSizeInTeraB = (fileSizeInGigaB / BLOCK_SIZE);

                        if (fileSizeInTeraB > UNIT_LIMIT) {

                            double fileSizeInPetaB = (fileSizeInTeraB / BLOCK_SIZE);

                            if (fileSizeInPetaB > UNIT_LIMIT) {

                                double fileSizeInExaB = (fileSizeInPetaB / BLOCK_SIZE);

                                if (fileSizeInExaB > UNIT_LIMIT) {

                                    double fileSizeInZetaB = (fileSizeInExaB / BLOCK_SIZE);

                                    if (fileSizeInZetaB > UNIT_LIMIT) {

                                        double fileSizeInYotaB = (fileSizeInZetaB / BLOCK_SIZE);
                                        setSize(fileSizeInYotaB, "YB");

                                    } else {
                                        setSize(fileSizeInZetaB, "ZB");
                                    }

                                } else {
                                    setSize(fileSizeInExaB, "EB");
                                }

                            } else {
                                setSize(fileSizeInPetaB, "PB");
                            }

                        } else {
                            setSize(fileSizeInTeraB, "TB");
                        }
                    } else {
                        setSize(fileSizeInGigaB, "GB");
                    }

                } else {
                    setSize(fileSizeInMegaB, "MB");
                }

            } else {
                setSize(fileSizeInKiloB, "KB");
            }
        } else {
            setSize(fileSizeInBytes, "B");
        }
    }

    private void setSize(double size, String format) {
        DecimalFormat dateFormat = new DecimalFormat("#.##");
        mFileSize.setText(dateFormat.format(size) + format);
    }

    private void setModifiedDate(long modifiedDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        mModifiedDate.setText(dateFormat.format(new Date(modifiedDate)));
    }
}
