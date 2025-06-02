package com.example.foodapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.R

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onBuyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.food_logo2),
            contentDescription = "FoodApp Logo",
            modifier = Modifier
                .height(195.dp)
                .padding(bottom = 24.dp)
        )

        // Title
        Text(
            text = "Καλωσήρθες στην πλατφόρμα μας!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Search Button
        Button(
            onClick = onSearchClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("🔍 Αναζήτηση Καταστημάτων", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Buy Button
        Button(
            onClick = onBuyClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCB8011))
        ) {
            Text("🛒 Αγορά Προϊόντος", fontSize = 16.sp, color = Color.White)
        }
    }
}