package com.example.foodapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.foodapp.model.SearchRequest

@Composable
fun SearchScreen(
    onSubmit: (SearchRequest) -> Unit,
    onBack: () -> Unit
) {
    var category by remember { mutableStateOf("") }
    var minStars by remember { mutableStateOf("") }
    var maxStars by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var maxDistance by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        Text("Αναζήτηση Καταστημάτων")
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Κατηγορία φαγητού") })
        OutlinedTextField(value = minStars, onValueChange = { minStars = it }, label = { Text("Ελάχιστα Αστέρια") })
        OutlinedTextField(value = maxStars, onValueChange = { maxStars = it }, label = { Text("Μέγιστα Αστέρια") })
        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Τιμή ($/$$/$$$)") })
        OutlinedTextField(value = maxDistance, onValueChange = { maxDistance = it }, label = { Text("Μέγιστη απόσταση (km)") })
        OutlinedTextField(value = latitude, onValueChange = { latitude = it }, label = { Text("Latitude") })
        OutlinedTextField(value = longitude, onValueChange = { longitude = it }, label = { Text("Longitude") })

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val request = SearchRequest(
                category = category,
                minStars = minStars.toIntOrNull() ?: 0,
                maxStars = maxStars.toIntOrNull() ?: 5,
                price = price,
                maxDistance = maxDistance.toDoubleOrNull() ?: 5.0,
                latitude = latitude.toDoubleOrNull() ?: 0.0,
                longitude = longitude.toDoubleOrNull() ?: 0.0
            )
            onSubmit(request)
        }) {
            Text("Αναζήτηση")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onBack) {
            Text("⬅ Επιστροφή")
        }
    }
}