package com.mahechabjj.userservice.model

data class Playlist(
        val name: String = "",
        var description: String = "",
        var videos: ArrayList<Video> = ArrayList())

data class User(
        var id: String? = null,
        var email: String = "",
        var name: String = "",
        var belt: String = "",
        var secretQuestion: String = "",
        var secretQuestionAnswer: String = "",
        var password: String = "",
        var playlists: ArrayList<Playlist> = ArrayList(),
        var packages: Packages? = null)

data class Packages(
        val noGiJiuJitsu: Boolean = false,
        val giJiuJitsu: Boolean = false,
        val giAndNoGiJiuJitsu: Boolean = false)

data class Video(
        val name: String = "",
        val image: String = "",
        val link: String = "",
        val linkHd: String = "",
        val description: String = "")