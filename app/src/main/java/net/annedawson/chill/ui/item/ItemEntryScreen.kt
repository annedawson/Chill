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

@file:OptIn(ExperimentalMaterialApi::class)

package net.annedawson.chill.ui.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.ExposedDropdownMenu
//import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import net.annedawson.chill.InventoryTopAppBar
import net.annedawson.chill.R
import net.annedawson.chill.data.FreezerLocation
import net.annedawson.chill.ui.AppViewModelProvider
import net.annedawson.chill.ui.navigation.NavigationDestination
import net.annedawson.chill.ui.theme.InventoryTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone



object ItemEntryDestination : NavigationDestination {
    override val route = "item_entry"
    override val titleRes = R.string.item_entry_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: ItemEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(ItemEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }  // the following is a trailing lambda of the Scaffold Composable
        // with innerPadding as its input parameter
    ) { innerPadding ->
        ItemEntryBody(
            itemUiState = viewModel.itemUiState,
            onItemValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveItem()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun ItemEntryBody(
    itemUiState: ItemUiState,
    onItemValueChange: (ItemDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        ItemInputForm(
            itemDetails = itemUiState.itemDetails,
            onValueChange = onItemValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = itemUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ItemInputForm(
    itemDetails: ItemDetails,
    modifier: Modifier = Modifier,
    onValueChange: (ItemDetails) -> Unit = {},
    enabled: Boolean = true
) {
    var selectedDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var dateText by remember { mutableStateOf(convertMillisToDate(selectedDateMillis)) }
    var isError by remember { mutableStateOf(false) }


    // *** THIS IS THE NEW DEFAULT TEXT ***
    if (itemDetails.date == "0") {
        dateText = "Click to select date"
    } else {
        dateText = itemDetails.date
    }

    // The line below fixed the error where the date picker shows the correct current date,
    // but also highlighted the day before the current date (in some timezones).
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = null)
    // Added this line below to get the list of string location options
    val locationOptions = FreezerLocation.values().map { it.locationName }
    var selectedLocationText by remember {
        mutableStateOf(FreezerLocation.fromId(itemDetails.location.toIntOrNull() ?: 0)?.locationName ?: 0)
    }


    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        /////////////////////////////
        // name section
        OutlinedTextField(
            value = itemDetails.name,
            onValueChange = { onValueChange(itemDetails.copy(name = it)) },
            label = { Text(stringResource(R.string.item_name_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )

        /////////////////////////////
        // Location string input section
        /*OutlinedTextField(
            value = itemDetails.location,
            onValueChange = { onValueChange(itemDetails.copy(location = it)) },
            label = { Text(stringResource(R.string.location_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )*/

        ////////////////////////////////////////////////////////////////////////////////////////////


        // Location Dropdown section


        //DropdownTextField(options = listOf("Option 1", "Option 2", "Option 3"))
        DropdownTextField(options = locationOptions)

        ////////////////////////////////////////////////////////////////////////////////////////////

        // quantity section
        OutlinedTextField(
            value = itemDetails.quantity,
            onValueChange = { onValueChange(itemDetails.copy(quantity = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(R.string.quantity_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )

        /////////////////////////////////////////////////////
        // Date picker section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
                .padding(bottom = dimensionResource(id = R.dimen.padding_small))
        ) {
            //This is the key to the change
            OutlinedTextField(
                value = dateText, // this is the change
                onValueChange = { },
                label = { Text(stringResource(R.string.date_req)) },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                // This is the change needed to match the container colour
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer, //this is the change.
                    // This is the important new line below
                    disabledTextColor = MaterialTheme.colorScheme.onSurface
                ),
                textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface)
            )

        }


        // DatePickerDialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDateMillis = it
                            selectedDateMillis = epochToLocalTimeZoneConvertor(selectedDateMillis)
                            dateText = convertMillisToDate(selectedDateMillis)
                            onValueChange(itemDetails.copy(date = dateText)) // Update itemDetails!
                        }
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (enabled) {
            Text(
                text = stringResource(R.string.required_fields),
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}

////////////////////////////////////////////////



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DropdownTextField(options: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options.firstOrNull() ?: "") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedOptionText,
            onValueChange = { },
            //onValueChange = { itemDetails.copy(location = it) },
            label = { Text("Location*") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
           // modifier = androidx.compose.ui.Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewDropdownTextField() {
    DropdownTextField(options = listOf("Option 1", "Option 2", "Option 3"))
}




/////////////////////////////////////////////////
// Added three date conversion functions
fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

fun convertDateToMillis(dateString: String, dateFormat: String = "MM/dd/yyyy"): Long {
    println("convertDateToMillis called with: '$dateString'")
    if (dateString.isBlank()) { // Check if it's empty or whitespace-only
        println("Error: dateString is empty or blank") //You can log the error.
        return 0L // Or handle it in another way
    }
    val formatter = SimpleDateFormat(dateFormat, Locale.US)
    return try {
        val date: Date = formatter.parse(dateString) ?: return 0L // Handle parsing failure
        date.time
    } catch (e: Exception) {
        // Handle parsing exception, possibly log the error
        println("Error parsing date: $dateString. Error: ${e.message}")
        0L
    }
}


fun epochToLocalTimeZoneConvertor(epoch: Long): Long {
    val epochCalendar = Calendar.getInstance()
    epochCalendar.timeZone = TimeZone.getTimeZone("UTC")
    epochCalendar.timeInMillis = epoch
    val converterCalendar = Calendar.getInstance()
    converterCalendar.set(
        epochCalendar.get(Calendar.YEAR),
        epochCalendar.get(Calendar.MONTH),
        epochCalendar.get(Calendar.DATE),
        epochCalendar.get(Calendar.HOUR_OF_DAY),
        epochCalendar.get(Calendar.MINUTE),
    )
    converterCalendar.timeZone = TimeZone.getDefault()
    return converterCalendar.timeInMillis
}


@Preview(showBackground = true)
@Composable
private fun ItemEntryScreenPreview() {
    InventoryTheme {
        ItemEntryBody(itemUiState = ItemUiState(
            ItemDetails(
                name = "Item name", location = "Top Left", quantity = "5", date = "0"
            )
        ), onItemValueChange = {}, onSaveClick = {})
    }
}