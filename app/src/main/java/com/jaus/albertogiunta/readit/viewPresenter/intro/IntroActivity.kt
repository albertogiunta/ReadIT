@file:Suppress("DEPRECATION")

package com.jaus.albertogiunta.readit.viewPresenter.intro

import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import com.jaus.albertogiunta.readit.R
import com.jaus.albertogiunta.readit.db.Prefs

@Suppress("OverridingDeprecatedMember")
class IntroActivity : AppIntro2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fun newSlider(customTitle: String = "", customDescription: String = "", res: Int = -1): SliderPage = SliderPage().apply {
            title = customTitle
            description = customDescription
            if (res != -1) imageDrawable = res
            bgColor = ResourcesCompat.getColor(resources, R.color.appBG, null)
            titleColor = ResourcesCompat.getColor(resources, R.color.cardTxtPrimary, null)
            descColor = ResourcesCompat.getColor(resources, R.color.cardTxtPrimary, null)
        }

        addSlide(AppIntroFragment.newInstance(newSlider("\"I'll read it whenever\" no more", "Share any link (tweets, articles, videos, cat gifs...) you might want to check out later to ReadIT", R.drawable.img_smirking_face)))
        addSlide(AppIntroFragment.newInstance(newSlider("Either read it or drop it", "A link lasts 24 hours and after that it's gone.\n\nIn fact, science says that if you really care about it you're wanna gonna read it, otherwise not much of a loss uh?", R.drawable.img_hourglass)))


        showStatusBar(true)
        showSkipButton(false)

        // Turn vibration on and set intensity
        // NOTE: you will need to ask VIBRATE permission in Manifest if you haven't already
        setVibrate(true)
        setVibrateIntensity(30)

        // Animations -- use only one of the below. Using both could cause errors.
        setFadeAnimation() // OR
    }

    override fun onDonePressed() {
        super.onDonePressed()
        Prefs.tutorialSeen = true
        finish()
    }

}