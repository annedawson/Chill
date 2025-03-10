/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.annedawson.chill.data
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity data class defines the table.
 * Each instantiation represents a single row in the database.
 */
@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,  // Assign the id a default value of 0,
    // which is necessary for the id to auto generate id values.
    val name: String,
    val location: String,
    val quantity: Int,
    val date: Long  // Use Long for the date value.
    // It is not possible to store a Date object in a Room database.
)
