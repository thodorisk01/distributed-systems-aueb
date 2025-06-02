package com.example.foodapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BuyScreen(
    onSubmit: (String, String, Int) -> Unit,
    onBack: () -> Unit
) {
    var storeName by remember { mutableStateOf("") }
    var productName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Αγορά Προϊόντος")
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = storeName,
            onValueChange = { storeName = it },
            label = { Text("Κατάστημα") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Προϊόν") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it },
            label = { Text("Ποσότητα") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val amount = quantity.toIntOrNull()
            if (storeName.isNotBlank() && productName.isNotBlank() && amount != null) {
                onSubmit(storeName, productName, amount)
            }
        }) {
            Text("Αποστολή Αγοράς")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack) {
            Text("⬅ Επιστροφή")
        }
    }
}