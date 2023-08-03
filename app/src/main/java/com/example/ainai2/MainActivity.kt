package com.example.ainai2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.Row


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ainai2.ui.theme.Ainai2Theme
import com.example.ainai2.ui.theme.PurpleGrey40
import com.example.ainai2.ui.theme.PurpleGrey80
import com.example.ainai2.ui.theme.baby_blue
import com.example.ainai2.ui.theme.baby_blue_light
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {


                        // Launch CameraActivity using Intent
                        val intent = Intent(this@MainActivity, Camera::class.java)
                        startActivity(intent)
                    },

                color = MaterialTheme.colorScheme.background
            ) {

                navigation()

            }

        }


    }
}

@Preview
@Composable
fun navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") {

            splash_screen(navController = navController)

        }

    }

}


@Composable
fun splash_screen(navController: NavController) {


    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color.White, baby_blue), // Gradient colors
        startY = 500f, // Starting Y position of the gradient
        endY = 5000f // Ending Y position of the gradient
    )
    var logoOffsetY by remember { mutableStateOf(0f) }
    val scale = remember { Animatable(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)

    )

    LaunchedEffect(true) {
        scale.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 500, easing = {
            OvershootInterpolator(3f).getInterpolation(it)

        }))


    }


    Box(
        contentAlignment = Alignment.TopCenter, modifier = Modifier
            .fillMaxSize()
            .padding(90.dp)

    ) {
        Image(
            painter = painterResource(id = R.drawable.vine), contentDescription = "logo",
            // contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(170.dp)
                .graphicsLayer(scaleX = scale.value, scaleY = scale.value)
        )


    }



    Box(
        contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()

    ) {
        val fonts = FontFamily(
            Font(R.font.gulzar_regular)


        )
        Text(

            text = "هذا الذي بين يديك سيكون عيناك، وستكون \n         أذن له،نتمنى لك قضاء وقت ممتع",
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold,/// Increase the font size to 24.sp or any other desired value
            color = Color.DarkGray,
            fontFamily = fonts,

            modifier = Modifier

                .graphicsLayer(scaleX = scale.value, scaleY = scale.value),


            )
    }


}






