package com.rkbapps.canvas.navigation

import com.rkbapps.canvas.model.SavedDesign
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@SerialName("home")
object Home

@Serializable
@SerialName("draw")
data class Draw(val design: String? = null)