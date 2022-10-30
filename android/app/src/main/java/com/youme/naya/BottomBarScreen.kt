package com.youme.naya

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Home
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource

sealed class BottomBarScreen(val route: String, val title: String, val icon: Int) {
    object Home : BottomBarScreen(
        route = "home",
        title = "Home",
        icon = R.drawable.nav_home_icon
    )
    object NuyaCardHolder : BottomBarScreen(
        route = "nuya",
        title = "Nuya",
        icon = R.drawable.nav_nuya_icon
    )

    object NayaCard : BottomBarScreen(
        route = "naya",
        title = "Naya",
        icon = R.drawable.nav_naya_icon
    )

    object Calendar : BottomBarScreen(
        route = "schedule",
        title = "Schedule",
        icon = R.drawable.nav_schedule_icon
    )

    object Settings : BottomBarScreen(
        route = "settings",
        title = "Settings",
        icon = R.drawable.home_icon_setting
    )
}