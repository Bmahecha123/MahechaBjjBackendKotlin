package com.mahechabjj.userservice.model

data class Playlist(
        val name: String,
        val description: String,
        val videos: ArrayList<Video>)

data class User(
        val id: String,
        val email: String,
        val name: String,
        val belt: String,
        val secretQuestion: String,
        val secretQuestionAnswer: String,
        var password: String,
        var playlists: ArrayList<Playlist>,
        val packages: Packages)

data class Packages(
        val noGiJiuJitsu: Boolean,
        val giJiuJitsu: Boolean,
        val giAndNoGiJiuJitsu: Boolean)

data class Video(
        val name: String,
        val image: String,
        val link: String,
        val linkHd: String,
        val description: String)