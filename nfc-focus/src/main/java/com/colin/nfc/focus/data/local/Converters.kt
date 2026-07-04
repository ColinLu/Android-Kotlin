package com.colin.nfc.focus.data.local

import androidx.room.TypeConverter
import com.colin.nfc.focus.data.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    
    private val gson = Gson()
    
    // List<String> 转换
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
    
    // TimeRule 转换
    @TypeConverter
    fun fromTimeRule(value: TimeRule?): String? {
        return value?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toTimeRule(value: String?): TimeRule? {
        return value?.let { gson.fromJson(it, TimeRule::class.java) }
    }
    
    // ExceptionRule 转换
    @TypeConverter
    fun fromExceptionRule(value: ExceptionRule?): String? {
        return value?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toExceptionRule(value: String?): ExceptionRule? {
        return value?.let { gson.fromJson(it, ExceptionRule::class.java) }
    }
    
    // List<TimeSlot> 转换
    @TypeConverter
    fun fromTimeSlotList(value: List<TimeSlot>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toTimeSlotList(value: String): List<TimeSlot> {
        val listType = object : TypeToken<List<TimeSlot>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
    
    // AllowListType 转换
    @TypeConverter
    fun fromAllowListType(value: AllowListType): String {
        return value.name
    }
    
    @TypeConverter
    fun toAllowListType(value: String): AllowListType {
        return AllowListType.valueOf(value)
    }
    
    // UnlockMethod 转换
    @TypeConverter
    fun fromUnlockMethod(value: UnlockMethod): String {
        return value.name
    }
    
    @TypeConverter
    fun toUnlockMethod(value: String): UnlockMethod {
        return UnlockMethod.valueOf(value)
    }
    
    // TriggerType 转换
    @TypeConverter
    fun fromTriggerType(value: TriggerType): String {
        return value.name
    }
    
    @TypeConverter
    fun toTriggerType(value: String): TriggerType {
        return TriggerType.valueOf(value)
    }
}
