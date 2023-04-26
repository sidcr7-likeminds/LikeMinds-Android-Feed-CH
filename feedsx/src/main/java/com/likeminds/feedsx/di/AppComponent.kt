package com.likeminds.feedsx.di

import android.app.Application
import com.likeminds.feedsx.di.feed.FeedComponent
import com.likeminds.feedsx.di.feed.FeedComponentModule
import com.likeminds.feedsx.di.likes.LikesComponent
import com.likeminds.feedsx.di.likes.LikesComponentModule
import com.likeminds.feedsx.di.media.MediaComponent
import com.likeminds.feedsx.di.notificationfeed.NotificationFeedComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        FeedComponentModule::class,
        LikesComponentModule::class,
        MediaComponent::class,
        NotificationFeedComponent::class
    ]
)
interface AppComponent {
    fun feedComponent(): FeedComponent.Factory
    fun likesComponent(): LikesComponent.Factory
    fun mediaComponent(): MediaComponent.Factory
    fun notificationFeedComponent(): NotificationFeedComponent.Factory

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}