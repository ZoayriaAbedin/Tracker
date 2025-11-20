package com.example.tracker.data

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromCategoryType(value: String?): CategoryType? {
        return value?.let { CategoryType.valueOf(it) }
    }

    @TypeConverter
    fun categoryTypeToString(categoryType: CategoryType?): String? {
        return categoryType?.name
    }
}
