package net.annedawson.chill.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import net.annedawson.chill.data.Item
import net.annedawson.chill.data.ItemsRepository

enum class SortOrder {
    BY_NAME,
    BY_QUANTITY,
    BY_LOCATION,
    BY_DATE
}

class HomeViewModel(private val itemsRepository: ItemsRepository) : ViewModel() {

    private val _sortOrder = MutableStateFlow(SortOrder.BY_NAME) // Initial sort order
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    val homeUiState: StateFlow<HomeUiState> = _sortOrder.flatMapLatest { sortOrder ->
        itemsRepository.getAllItemsStream().map { items ->
            HomeUiState(
                itemList = when (sortOrder) {
                    SortOrder.BY_NAME -> items.sortedBy { it.name }
                    SortOrder.BY_QUANTITY -> items.sortedBy { it.quantity }
                    SortOrder.BY_LOCATION -> items.sortedBy { it.location }
                    SortOrder.BY_DATE -> items.sortedBy { it.date }
                }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = HomeUiState()
    )

    fun setSortOrder(sortOrder: SortOrder) {
        _sortOrder.value = sortOrder
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    data class HomeUiState(val itemList: List<Item> = listOf())
}
