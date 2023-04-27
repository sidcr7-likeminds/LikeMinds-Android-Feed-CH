package com.likeminds.feedsx.di.post

import com.likeminds.feedsx.di.post.create.CreatePostComponent
import com.likeminds.feedsx.di.post.detail.PostDetailComponent
import com.likeminds.feedsx.di.post.edit.EditPostComponent
import dagger.Module

@Module(
    subcomponents = [
        CreatePostComponent::class,
        PostDetailComponent::class,
        EditPostComponent::class
    ]
)
class PostComponentModule