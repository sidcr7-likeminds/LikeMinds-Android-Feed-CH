package com.likeminds.feedsample.utils.mediauploader.di

import android.content.Context
import android.util.Base64
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.likeminds.feedsample.utils.mediauploader.utils.AWSKeys
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AWSModule {

    @Provides
    @Singleton
    fun provideFinalTransferUtility(
        @ApplicationContext context: Context,
        s3Client: AmazonS3Client
    ): TransferUtility {
        val bucketName = String(Base64.decode(AWSKeys.getBucketName(), Base64.DEFAULT))
        return TransferUtility.builder()
            .context(context)
            .defaultBucket(bucketName)
            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .s3Client(s3Client)
            .build()
    }

    @Provides
    @Singleton
    fun provideCredProvider(@ApplicationContext context: Context): CognitoCachingCredentialsProvider {
        return CognitoCachingCredentialsProvider(
            context.applicationContext,
            String(Base64.decode(AWSKeys.getIdentityPoolId(), Base64.DEFAULT)),
            Regions.AP_SOUTH_1
        )
    }

    @Provides
    @Singleton
    fun provideS3Client(credProvider: CognitoCachingCredentialsProvider): AmazonS3Client {
        val sS3Client = AmazonS3Client(credProvider, Region.getRegion(Regions.AP_SOUTH_1))
        sS3Client.setRegion(Region.getRegion(Regions.AP_SOUTH_1))
        return sS3Client
    }
}