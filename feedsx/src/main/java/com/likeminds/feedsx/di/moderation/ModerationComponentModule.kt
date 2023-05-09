package com.likeminds.feedsx.di.moderation

import com.likeminds.feedsx.di.moderation.reasonchoose.ReasonChooseComponent
import com.likeminds.feedsx.di.moderation.report.ReportComponent
import dagger.Module

@Module(subcomponents = [ReportComponent::class, ReasonChooseComponent::class])
class ModerationComponentModule