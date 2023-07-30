package com.example.ainai2

import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ainai2.ui.theme.Ainai2Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {

                navigation()
            }

        }

//        lifecycleScope.launch {
//            delay(50)
//            window.setBackgroundDrawableResource(android.R.color.transparent)
//        }



  }
}
@Preview
@Composable
fun navigation(){
    val navController= rememberNavController()
    NavHost(navController = navController, startDestination = "splash_screen" ){
        composable("splash_screen"){

            splash_screen(navController = navController)
        }

        composable("main_screen"){

            Text(text = "camera", color= Color.Black)

        }
    }

}


@Composable
fun splash_screen(navController: NavController) {
    var logoOffsetY by remember { mutableStateOf(0f) }
    val scale = remember { Animatable(0f) }

    LaunchedEffect(true) {
        scale.animateTo(
            targetValue = 0.3f,
            animationSpec = tween(
                durationMillis = 500,
                easing = {
                    OvershootInterpolator(2f).getInterpolation(it)
                }
            )
        )

        delay(3000L)
        navController.navigate("main_screen")

    }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .offset(y = logoOffsetY.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.vine),
            contentDescription = "logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(370.dp)
                .graphicsLayer(scaleX = scale.value, scaleY = scale.value)
        )
    }
}

