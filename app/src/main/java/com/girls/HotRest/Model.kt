package com.girls.HotRest

import java.io.Serializable

data class Model(val imagePath: String, val modelId: String, val collection: String, val prompt: String, val type: String?):
    Serializable
