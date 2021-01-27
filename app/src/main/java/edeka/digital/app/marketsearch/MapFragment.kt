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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.here.sdk.core.Point2D
import com.here.sdk.gestures.GestureType
import com.here.sdk.gestures.TapListener
import com.here.sdk.mapview.MapScheme
import com.here.sdk.mapview.WatermarkPlacement
import edeka.digital.app.marketsearch.databinding.FragmentMapBinding


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap(view, savedInstanceState)
    }

    private fun initMap(view: View, savedInstance: Bundle?) {

        val mapActivity = activity as MainActivity?


        binding.map.apply {
            onCreate(savedInstance)
            mapScene.loadScene(MapScheme.NORMAL_DAY) {}
            gestures.disableDefaultAction(GestureType.TWO_FINGER_PAN)
            gestures.tapListener = TapListener { touchPoint -> pickMarker(touchPoint) }
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.map.onDestroy()
        _binding = null
    }

    private fun pickMarker(touchPoint: Point2D) {
        val radiusInPixel = 2.0
    }
}