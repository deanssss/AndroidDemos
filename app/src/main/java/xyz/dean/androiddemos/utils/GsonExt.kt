package xyz.dean.androiddemos.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.Reader

inline fun <reified T> Gson.fromJson(json: String): T = fromJson(json, T::class.java)

inline fun <reified T> Gson.fromJson(json: Reader): T = fromJson(json, T::class.java)

inline fun <reified T> T.toJson(): String = GsonBuilder().create().toJson(this)