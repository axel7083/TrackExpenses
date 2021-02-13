package com.github.trackexpenses.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.github.trackexpenses.ISlideOperator
import com.github.trackexpenses.R
import com.github.trackexpenses.adapters.SliderPagerAdapter
import com.github.trackexpenses.fragments.AmountFragment
import com.github.trackexpenses.fragments.CurrencyPickerFragment
import com.github.trackexpenses.fragments.DateRangeFragment
import com.github.trackexpenses.models.Settings
import com.github.trackexpenses.views.LockableViewPager
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson

class IntroActivity : AppCompatActivity() {
    private lateinit var viewPager: LockableViewPager
    private lateinit var button: Button
    private lateinit var adapter: SliderPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // making activity full screen
        if (Build.VERSION.SDK_INT >= 21) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        setContentView(R.layout.activity_intro)

        // bind views
        viewPager = findViewById(R.id.pagerIntroSlider)
        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        button = findViewById(R.id.button)

        // init slider pager adapter
        adapter = SliderPagerAdapter(
            supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )

        // set adapter
        viewPager.adapter = adapter
        viewPager.setSwipeable(false)

        // set dot indicators
        tabLayout.setupWithViewPager(viewPager)

        // make status bar transparent
        changeStatusBarColor()
        button.setOnClickListener(View.OnClickListener {
            if (viewPager.currentItem < adapter.count) {
                val slide: ISlideOperator =
                    adapter.getItem(viewPager.currentItem) as ISlideOperator

                if (slide.canNext()) {

                    if(viewPager.currentItem == adapter.count -1) {
                        Toast.makeText(this,"Get started",Toast.LENGTH_SHORT).show()

                        val returnIntent = Intent()

                        val setting: Settings = Settings()
                        for (i in 1..3) {
                            when(i) {
                                1 -> {
                                    setting.startFormatted = (adapter.getItem(1) as DateRangeFragment).startFormatted
                                    setting.endFormatted = (adapter.getItem(1) as DateRangeFragment).endFormatted
                                }
                                2-> setting.currency = (adapter.getItem(2) as CurrencyPickerFragment).currency
                                3 -> setting.amount = (adapter.getItem(3) as AmountFragment).amount
                            }
                        }
                        returnIntent.putExtra("settings", Gson().toJson(setting))
                        setResult(RESULT_OK, returnIntent)
                        finish()
                    }
                    else
                    {
                        viewPager.currentItem = viewPager.currentItem + 1
                    }
                }
                else
                {
                    Toast.makeText(this,
                        when(viewPager.currentItem) {
                            1 -> "Please select a valid period of time"
                            2 -> "Please select a currency"
                            else-> "Please complete form" }
                        ,Toast.LENGTH_SHORT).show()
                }
            }
        })
        /**
         * Add a listener that will be invoked whenever the page changes
         * or is incrementally scrolled
         */
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == adapter.count - 1) {
                    button.setText(R.string.get_started)
                } else {
                    button.setText(R.string.next)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    var backCount: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() / 1000 - backCount < 3) {
            val returnIntent = Intent()
            //TODO: create settings object
            setResult(RESULT_CANCELED, returnIntent)
            finish()
        } else {
            backCount = System.currentTimeMillis() / 1000
            Toast.makeText(this, R.string.back_alert, Toast.LENGTH_LONG).show()
        }
    }
}