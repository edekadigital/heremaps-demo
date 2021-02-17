/*
 * Copyright (c) 2021 EDEKA AG
 *
 *  ███████╗    ███████╗██████╗ ██████╗ ██╗       `        `
 *  ██╔════╝    ██╔════╝██╔══██╗██╔══██╗██║    ```````  ```````
 *  █████╗      █████╗  ██║  ██║██║  ██║██║    ``````◆◆◆◆``````
 *  ██╔══╝      ██╔══╝  ██║  ██║██║  ██║██║      ``◆◆◆◆◆◆◆◆``
 *  ███████╗    ███████╗██████╔╝██████╔╝██║        `◆◆◆◆◆◆`
 *  ╚══════╝    ╚══════╝╚═════╝ ╚═════╝ ╚═╝          `◆◆`
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package edeka.digital.app.marketsearch

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.here.sdk.core.Point2D
import com.here.sdk.gestures.GestureType
import com.here.sdk.gestures.TapListener
import com.here.sdk.mapview.MapScheme
import com.here.sdk.mapview.WatermarkPlacement
import edeka.digital.app.marketsearch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { _ ->
            val bottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet)
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        setupBottomSheet()
        initMap(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupBottomSheet() {
        // Opening/closing the bottom sheet and all the animations included
        val bottomBehavior = BottomSheetBehavior.from(binding.menuBottomSheet)
        bottomBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                updateMapView(bottomSheet.top)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                updateMapView(bottomSheet.top)
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        updateMapView(bottomSheet.top)

                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        updateMapView(bottomSheet.top)
                    }
                    else -> { /* void */
                    }
                }
            }
        })
    }

    private fun initMap(savedInstance: Bundle?) {


        binding.map.apply {
            onCreate(savedInstance)
            mapScene.loadScene(MapScheme.NORMAL_DAY) {}
            gestures.disableDefaultAction(GestureType.TWO_FINGER_PAN)
            gestures.tapListener = TapListener { }
            setWatermarkPosition(WatermarkPlacement.BOTTOM_LEFT, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.map.onDestroy()
    }

    private fun updateMapView(bottomSheetTop: Int) {
        val mapView = binding.map

        val principalY = Math.min(bottomSheetTop / 2.0, mapView.height / 2.0)
        mapView.camera.principalPoint = Point2D(mapView.width / 2.0, principalY)

        val logoMargin = Math.max(0, mapView.bottom - bottomSheetTop)
        mapView.setWatermarkPosition(WatermarkPlacement.BOTTOM_CENTER, logoMargin.toLong())
    }
}