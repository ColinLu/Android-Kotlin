//package com.colin.library.android.map.tomtom
//
//import android.content.Context
//import android.os.Bundle
//import android.util.AttributeSet
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.annotation.MainThread
//import com.colin.library.android.base.BaseFragment
//import com.tomtom.sdk.common.Cancellable
//import com.tomtom.sdk.common.android.getParcelableCompat
//import com.tomtom.sdk.map.display.MapOptions
//import com.tomtom.sdk.map.display.TomTomMap
//import com.tomtom.sdk.map.display.annotation.ExperimentalMapStateApi
//import com.tomtom.sdk.map.display.map.MapStateChangedListener
//import com.tomtom.sdk.map.display.marker.BalloonViewAdapter
//import com.tomtom.sdk.map.display.ui.MapFragment
//import com.tomtom.sdk.map.display.ui.MapProvider
//import com.tomtom.sdk.map.display.ui.MapReadyCallback
//import com.tomtom.sdk.map.display.ui.MapView
//import com.tomtom.sdk.map.display.ui.UiComponents
//import com.tomtom.sdk.map.display.ui.compass.CompassButton
//import com.tomtom.sdk.map.display.ui.currentlocation.CurrentLocationButton
//import com.tomtom.sdk.map.display.ui.logo.LogoView
//import com.tomtom.sdk.map.display.ui.scale.ScaleView
//import com.tomtom.sdk.map.display.ui.zoom.ZoomControlsView
//import java.util.concurrent.CopyOnWriteArrayList
//import java.util.logging.Logger
//
///**
// * Author:ColinLu
// * E-mail:945919945@qq.com
// * Date  :2024-08-24
// *
// * Des   :TODO
// */
//class MapFragment :BaseFragment(), UiComponents, MapProvider {
//    private lateinit var mapView: MapView
//    private lateinit var mapOptions: MapOptions
//    private val listenerContainer = CopyOnWriteArrayList<MapReadyCallback>()
//
//    @ExperimentalMapStateApi
//    private val mapStateChangedListeners = CopyOnWriteArrayList<MapStateChangedListener>()
//
//    /**
//     * This represents the compass button on the map. This button provides
//     * a quick way for users to change the map to have a northern orientation.
//     */
//    override val compassButton: CompassButton
//    get() = mapView.compassButton
//
//    /**
//     * This represents the zoom controls on the map. These controls allow
//     * users to zoom in and out of the map view.
//     */
//    override val zoomControlsView: ZoomControlsView
//    get() = mapView.zoomControlsView
//
//    /**
//     * This represents the button that shows the user's current location on the map.
//     */
//    override val currentLocationButton: CurrentLocationButton
//    get() = mapView.currentLocationButton
//
//    /**
//     * This represents the scale view on the map. This shows the scale of the
//     * map in relation to the real world distances.
//     */
//    override val scaleView: ScaleView
//    get() = mapView.scaleView
//
//    /**
//     * This represents the logo of the map provider. This logo ensures the
//     * correct attribution is given to the map data source.
//     */
//    override val logoView: LogoView
//    get() = mapView.logoView
//
//    /**
//     * This represents the container view for any balloon markers on the map.
//     * When clicked, balloon markers typically show more information about
//     * a specific point on the map.
//     */
//    override val balloonsViewContainer: ViewGroup
//    get() = mapView.balloonsViewContainer
//
//    /**
//     * This represents the adapter for the balloon views. The adapter determines
//     * how the content inside each balloon marker is displayed.
//     */
//    override var markerBalloonViewAdapter: BalloonViewAdapter
//    get() = mapView.markerBalloonViewAdapter
//    set(value) {
//        mapView.markerBalloonViewAdapter = value
//    }
//
//    /**
//     * This is called when the fragment's UI is created. This method parses the given attributes
//     * and sets the map options accordingly.
//     *
//     * @param context The context the fragment is currently running in.
//     * @param attrs The attributes of the XML tag that is inflating the fragment.
//     * @param savedInstanceState If the fragment is being recreated from a previous saved state,
//     * this is the state.
//     */
//    override fun onInflate(
//        context: Context,
//        attrs: AttributeSet,
//        savedInstanceState: Bundle?,
//    ) {
//        super.onInflate(context, attrs, savedInstanceState)
//        Logger.v { "${hashCode()} - onInflate()" }
//        val parser = MapOptionsAttrParser(context)
//        mapOptions = parser.parse(attrs, FragmentMapResources())
//    }
//
//    /**
//     * Handles the 'onCreate' lifecycle event. Initializes the MapView internals and UI components.
//     *
//     * @param bundle The saved instance state, if any, from which the view should be restored.
//     */
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Logger.v { "${hashCode()} - onCreate()" }
//        setHasOptionsMenu(true)
//        parseBundle(savedInstanceState)
//    }
//
//    private fun parseBundle(instanceState: Bundle?) {
//        Logger.v { "${hashCode()} - parseBundle()" }
//        if (!::mapOptions.isInitialized) {
//            mapOptions =
//                if (instanceState?.containsKey(KEY_MAP_OPTIONS) == true) {
//                    Logger.v { "Retrieving map options from instance state bundle" }
//                    instanceState.getParcelableCompat(KEY_MAP_OPTIONS)!!
//                } else {
//                    Logger.v { "Retrieving map options from arguments" }
//                    requireArguments().getParcelableCompat(KEY_MAP_OPTIONS)!!
//                }
//        }
//    }
//
//    /**
//     * Called on the fragment to instantiate its [MapView].
//     * If there were some calls for [getMapAsync(callback: OnMapReadyCallback)][getMapAsync],
//     * then the subscribed callbacks will be executed as soon as the [MapView] is created and the
//     * underlying [TomTomMap] is available.
//     */
//    @OptIn(ExperimentalMapStateApi::class)
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        state: Bundle?,
//    ): View {
//        Logger.v { "onCreateView(inflater = $inflater, container = $container, state = $state)" }
//        if (!::mapView.isInitialized) {
//            mapView = MapView(requireContext(), mapOptions)
//            mapView.onCreate(state)
//            mapStateChangedListeners.forEach { mapView.addMapStateChangedListener(it) }
//            mapStateChangedListeners.clear()
//            listenerContainer.forEach { mapView.getMapAsync(it) }
//            listenerContainer.clear()
//        }
//        return mapView
//    }
//
//    /**
//     * This handles the 'onStart' lifecycle event. It is called when the parent component becomes visible to the user.
//     * This ensures the internal map view components are ready to display.
//     */
//    override fun onStart() {
//        Logger.v { "${hashCode()} - onStart()" }
//        super.onStart()
//        mapView.onStart()
//    }
//
//    /**
//     * This handles the 'onResume' lifecycle event. It is called when the parent component is visible to the user and actively running.
//     * This ensures the internal map view components are active and updated.
//     */
//    override fun onResume() {
//        super.onResume()
//        Logger.v { "${hashCode()} - onResume()" }
//        mapView.onResume()
//    }
//
//    /**
//     * This handles the 'onPause' lifecycle event. It is called when the parent component is no longer resumed but still visible.
//     * This ensures the internal map view components pause their active processes to conserve resources.
//     */
//    @OptIn(ExperimentalMapStateApi::class)
//    override fun onPause() {
//        Logger.v { "${hashCode()} - onPause()" }
//        mapView.onPause()
//        listenerContainer.clear()
//        mapStateChangedListeners.clear()
//        super.onPause()
//    }
//
//    /**
//     * This handles the 'onStop' lifecycle event. It is called when the parent component is no longer visible to the user.
//     * This ensures the internal map view components stop all active processes to conserve resources.
//     */
//    override fun onStop() {
//        Logger.v { "${hashCode()} - onStop()" }
//        mapView.onStop()
//        super.onStop()
//    }
//
//    /**
//     * This handles the 'onDestroy' lifecycle event. It is called when the parent component is being destroyed.
//     * This ensures the internal map view components are properly shut down and resources are freed.
//     */
//    override fun onDestroy() {
//        Logger.v { "${hashCode()} - onDestroy()" }
//        mapView.onDestroy()
//        super.onDestroy()
//    }
//
//    override fun initView(savedInstanceState: Bundle?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun initData(bundle: Bundle?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun loadData(refresh: Boolean) {
//        TODO("Not yet implemented")
//    }
//
//    /**
//     * This handles the 'onSaveInstanceState' lifecycle event. It is called when the parent component saves its instance state.
//     * This stores the current state of the MapView component, ensuring that the state can be restored after a configuration change or other events that restart the component.
//     */
//    override fun onSaveInstanceState(outState: Bundle) {
//        Logger.v { "${hashCode()} - onSaveInstanceState($outState)" }
//        super.onSaveInstanceState(outState)
//        mapView.onSaveInstanceState(outState)
//        outState.putParcelable(KEY_MAP_OPTIONS, mapOptions)
//    }
//
//    /**
//     * Sets a callback that will be triggered when the map instance is ready to use.
//     * The callback will be executed on the main thread.
//     * Note: You must call this method from the main thread.
//     * Otherwise an exception will be thrown.
//     *
//     * @param callback The listener that handles the result.
//     * @return [Cancellable] object that can remove the listener.
//     */
//    @MainThread
//    override fun getMapAsync(callback: MapReadyCallback): Cancellable {
//        Logger.v { "${hashCode()} - getMapAsync() map view is initialized $callback" }
//        return if (this::mapView.isInitialized) {
//            mapView.getMapAsync(callback)
//        } else {
//            listenerContainer.add(callback)
//            Cancellable { listenerContainer.remove(callback) }
//        }
//    }
//
//    /**
//     * Adds a listener for map state changed event callbacks.
//     * This allows for actions to be taken in response to specific events
//     * during the map's initialization process.
//     *
//     * @suppress This API is for experimental use only.
//     * It may change without prior notice and should not be used publicly.
//     */
//    @ExperimentalMapStateApi
//    override fun addMapStateChangedListener(listener: MapStateChangedListener) {
//        if (this::mapView.isInitialized) {
//            mapView.addMapStateChangedListener(listener)
//        } else {
//            mapStateChangedListeners.add(listener)
//        }
//    }
//
//    /**
//     * Removes a listener previously added by [addMapStateChangedListener].
//     *
//     * @suppress This API is for experimental use only.
//     * It may change without prior notice and should not be used publicly.
//     */
//    @ExperimentalMapStateApi
//    override fun removeMapStateChangedListener(listener: MapStateChangedListener) {
//        if (this::mapView.isInitialized) {
//            mapView.removeMapStateChangedListener(listener)
//        } else {
//            mapStateChangedListeners.remove(listener)
//        }
//    }
//
//    companion object {
//        private const val KEY_MAP_OPTIONS = "MAP_OPTIONS"
//
//        /**
//         * Creates a new instance of the [MapFragment].
//         *
//         * @param mapOptions [MapOptions] with the initial map configuration.
//         */
//        fun newInstance(mapOptions: MapOptions): MapFragment {
//            val fragment = MapFragment()
//            val bundle = Bundle()
//            bundle.putParcelable(KEY_MAP_OPTIONS, mapOptions)
//            fragment.arguments = bundle
//            return fragment
//        }
//    }
//}