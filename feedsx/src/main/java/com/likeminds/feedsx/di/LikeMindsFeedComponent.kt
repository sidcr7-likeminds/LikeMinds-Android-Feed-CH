package com.likeminds.feedsx.di

import android.app.Application
import com.likeminds.feedsx.SDKApplication
import com.likeminds.feedsx.di.feed.FeedComponent
import com.likeminds.feedsx.di.feed.FeedComponentModule
import com.likeminds.feedsx.di.likes.LikesComponent
import com.likeminds.feedsx.di.likes.LikesComponentModule
import com.likeminds.feedsx.di.media.MediaComponent
import com.likeminds.feedsx.di.media.MediaComponentModule
import com.likeminds.feedsx.di.moderation.ModerationComponentModule
import com.likeminds.feedsx.di.moderation.reasonchoose.ReasonChooseComponent
import com.likeminds.feedsx.di.moderation.report.ReportComponent
import com.likeminds.feedsx.di.notificationfeed.NotificationFeedComponent
import com.likeminds.feedsx.di.notificationfeed.NotificationFeedComponentModule
import com.likeminds.feedsx.di.post.PostComponentModule
import com.likeminds.feedsx.di.post.create.CreatePostComponent
import com.likeminds.feedsx.di.post.detail.PostDetailComponent
import com.likeminds.feedsx.di.post.edit.EditPostComponent
import com.likeminds.feedsx.di.topic.LMFeedTopicComponent
import com.likeminds.feedsx.di.topic.LMFeedTopicComponentModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        RoomModule::class,
        FeedComponentModule::class,
        LikesComponentModule::class,
        MediaComponentModule::class,
        NotificationFeedComponentModule::class,
        PostComponentModule::class,
        ModerationComponentModule::class,
        LMFeedTopicComponentModule::class
    ]
)
interface LikeMindsFeedComponent {
    fun inject(sdkApplication: SDKApplication)
    fun feedComponent(): FeedComponent.Factory
    fun likesComponent(): LikesComponent.Factory
    fun mediaComponent(): MediaComponent.Factory
    fun notificationFeedComponent(): NotificationFeedComponent.Factory
    fun createPostComponent(): CreatePostComponent.Factory
    fun postDetailComponent(): PostDetailComponent.Factory
    fun editPostComponent(): EditPostComponent.Factory
    fun reportComponent(): ReportComponent.Factory
    fun reasonChooseComponent(): ReasonChooseComponent.Factory
    fun topicComponent(): LMFeedTopicComponent.Factory

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): LikeMindsFeedComponent
    }
}