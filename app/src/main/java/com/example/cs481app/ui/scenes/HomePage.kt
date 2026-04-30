package com.example.cs481app.ui.scenes

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import com.example.cs481app.R


@Composable
fun HomePage(
    navController: NavController
) {
    val name = "TONY"
    Column (
        modifier = Modifier
            .fillMaxSize()
            .clickable {navController.navigate(Routes.INITIAL_PAGE)},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hey $name"
            )

            Spacer(
                modifier = Modifier.width(275.dp)
            )

            Image(
                modifier = Modifier.size(25.dp),
                painter = painterResource(id = R.drawable.notification_bell),
                contentDescription = "Notification Bell"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Good bye",
            fontSize = 24.em
        )
    }
}