package com.dnitinverma.amazons3library;

import android.content.Context;
import android.provider.ContactsContract;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import static android.provider.ContactsContract.CommonDataKinds.StructuredPostal.REGION;


/**
 * Created by admin1 on 17/11/16.
 */

public class AmazonUtils {
    private static AmazonS3Client sS3Client;
    private static CognitoCachingCredentialsProvider sCredProvider;
    private static TransferUtility sTransferUtility;

    /**
     * Gets an instance of the TransferUtility which is constructed using the
     * given Context
     *
     * @param context
     * @param AMAZON_POOLID
     * @param END_POINT
     * @param REGION
     * @return a TransferUtility instance
     */
    public static TransferUtility getTransferUtility(Context context, String AMAZON_POOLID, String END_POINT, String REGION) {
        if (sTransferUtility == null) {
            sTransferUtility = new TransferUtility(getS3Client(context.getApplicationContext(),AMAZON_POOLID,END_POINT, REGION),
                    context.getApplicationContext());
        }

        return sTransferUtility;
    }

    /**
     * Gets an instance of a S3 client which is constructed using the given
     * Context.
     *
     * @param context An Context instance.
     * @param AMAZON_POOLID
     * @param END_POINT
     * @param REGION
     * @return A default S3 client.
     */
    public static AmazonS3Client getS3Client(Context context, String AMAZON_POOLID, String END_POINT, String REGION) {
        if (sS3Client == null) {
            sS3Client = new AmazonS3Client(getCredProvider(context.getApplicationContext(),AMAZON_POOLID, REGION));
            sS3Client.setEndpoint(END_POINT);
        }
        return sS3Client;
    }

    /**
     * Gets an instance of CognitoCachingCredentialsProvider which is
     * constructed using the given Context.
     *
     * @param context An Context instance.
     * @param AMAZON_POOLID
     * @param REGION
     * @return A default credential provider.
     */
    public static CognitoCachingCredentialsProvider getCredProvider(Context context, String AMAZON_POOLID, String REGION) {
        if (sCredProvider == null) {
            sCredProvider = new CognitoCachingCredentialsProvider(
                    context, AMAZON_POOLID, // Identity Pool ID
                    REGION.equals("1") ? Regions.US_EAST_1 : Regions.EU_WEST_1 // Region
            );
        }
        return sCredProvider;
    }

    /**
     * Converts number of bytes into proper scale.
     *
     * @param bytes number of bytes to be converted.
     * @return A string that represents the bytes in a proper scale.
     */
    public static String getBytesString(long bytes) {
        String[] quantifiers = new String[]{
                "KB", "MB", "GB", "TB"
        };
        double speedNum = bytes;
        for (int i = 0; ; i++) {
            if (i >= quantifiers.length) {
                return "";
            }
            speedNum /= 1024;
            if (speedNum < 512) {
                return String.format("%.2f", speedNum) + " " + quantifiers[i];
            }
        }
    }


}
