package com.twugteam.admin.chirp.navigation

object IOSExternalUriHandler {
    private var cachedUri: String? = null

    var uriListener: ((uri: String) -> Unit)? = null
        set(value) {
            field = value

            if (value != null) {
                cachedUri?.let { uri ->
                    value.invoke(uri)
                }
                cachedUri = null
            }
        }

    fun onNewUriArrived(uri: String) {
        cachedUri = uri
        uriListener?.let { listener ->
            listener.invoke(uri)
            cachedUri = null
        }
    }

}