package com.example.matchmaker.route

interface Destinations{
 val route: String
}

object Home: Destinations{
    override val route = "Home"
}
object ConfessionScreen: Destinations{
    override val route = "ConfessionScreen"

}