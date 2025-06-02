package com.example.foodapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.foodapp.model.SearchRequest
import com.example.foodapp.network.TcpClient
import com.example.foodapp.network.buildSearchString
import com.example.foodapp.ui.*
import com.example.foodapp.ui.theme.FoodAppTheme

class MainActivity : ComponentActivity() {

    private lateinit var client: TcpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        client = TcpClient("10.0.2.2", 5000)
        client.connect()

        setContent {
            FoodAppTheme {
                var screenState by remember { mutableStateOf("home") }
                var storeList by remember { mutableStateOf(listOf<String>()) }
                var resultMessage by remember { mutableStateOf("") }

                when (screenState) {
                    "home" -> HomeScreen(
                        onSearchClick = { screenState = "search" },
                        onBuyClick = { screenState = "buy" }
                    )
                    "search" -> SearchScreen(
                        onSubmit = { searchRequest ->
                            val query = buildSearchString(searchRequest)
                            client.sendRequest(query) { results ->
                                storeList = results
                                screenState = "results"
                            }
                        },
                        onBack = { screenState = "home" }
                    )
                    "results" -> ResultsScreenText(
                        stores = storeList,
                        onBack = { screenState = "home" }
                    )
                    "buy" -> BuyScreen(
                        onSubmit = { store, product, amount ->
                            val command = "BUY:$store:$product:$amount"
                            client.sendRequest(command) { response ->
                                resultMessage = response.firstOrNull() ?: "⚠️ No response"
                                screenState = "buyResult"
                            }
                        },
                        onBack = { screenState = "home" }
                    )
                    "buyResult" -> ResultsScreenText(
                        stores = listOf(resultMessage),
                        onBack = { screenState = "home" }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        client.close()
    }
}