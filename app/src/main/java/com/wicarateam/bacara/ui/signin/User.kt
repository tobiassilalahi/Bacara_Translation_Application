package com.wicarateam.bacara.ui.signin

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(
    var email: String? = "",
    var name: String? = "",
    var password: String? = "",
    var joinedDate: String? = "",
    var username: String? = "",
    var subscription: String? = ""
) : Parcelable