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

package net.annedawson.chill.ui.item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import net.annedawson.chill.data.Item
import java.text.NumberFormat
import net.annedawson.chill.data.ItemsRepository
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel to validate and insert items in the Room database.
 */
class ItemEntryViewModel(private val itemsRepository: ItemsRepository) : ViewModel() {

    /**
     * Holds current item ui state
     */
    var itemUiState by mutableStateOf(ItemUiState())
        private set

    /*

    The private set modifier is used to define properties
    that have a public getter but a private setter.
    This allows the property to be read from outside the class
    while restricting modification to within the class itself.
    This encapsulation technique is useful for maintaining control
    over how a property's value is modified,
    ensuring that it can only be changed
    through specific methods within the class.

     */

    /**
     * Updates the [itemUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(itemDetails: ItemDetails) {
        itemUiState =
            ItemUiState(itemDetails = itemDetails, isEntryValid = validateInput(itemDetails))
    }

    //  Check if the name, price, quantity and date are empty.
    //  You use this function to verify user input
    //  before adding or updating the entity in the database.
    private fun validateInput(uiState: ItemDetails = itemUiState.itemDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && price.isNotBlank() && quantity.isNotBlank() && date.isNotBlank()
        }
    }

    suspend fun saveItem() {
        if (validateInput()) {
            itemsRepository.insertItem(itemUiState.itemDetails.toItem())
        }
    }

}

/**
 * Represents Ui State for an Item.
 */
data class ItemUiState(
    val itemDetails: ItemDetails = ItemDetails(),
    val isEntryValid: Boolean = false
)

data class ItemDetails(
    val id: Int = 0,
    val name: String = "",
    val price: String = "",
    val quantity: String = "",
    val date: String = ""
)

/**
 * Extension function to convert [ItemDetails] to [Item]. If the value of [ItemDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
 * [ItemDetails.quantity] is not a valid [Int], then the quantity will be set to 0,
 *  [ItemDetails.date] is not a valid [Long], then the quantity will be set to 0L,
 */
fun ItemDetails.toItem(): Item = Item(
    id = id,
    name = name,
    price = price.toDoubleOrNull() ?: 0.0,
    quantity = quantity.toIntOrNull() ?: 0,
    date = convertDateToMillis(date) ?: 0L
)
// everything entered by the user into an outlined text field is a string

/**
 * Extension function to convert [Item] to [ItemDetails]
 */
fun Item.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    name = name,
    price = price.toString(),
    quantity = quantity.toString(),
    date = convertMillisToDate(date)
)

fun Item.formatedPrice(): String {
    return NumberFormat.getCurrencyInstance().format(price)
}

/**
 * Extension function to convert [Item] to [ItemUiState]
 */
fun Item.toItemUiState(isEntryValid: Boolean = false): ItemUiState = ItemUiState(
    itemDetails = this.toItemDetails(),
    isEntryValid = isEntryValid
)



