package edu.vanderbilt.crawler.ui.screens.settings

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jakewharton.rxbinding2.widget.RxAdapterView
import com.jakewharton.rxbinding2.widget.RxCompoundButton
import com.jakewharton.rxbinding2.widget.RxRadioGroup
import com.jakewharton.rxbinding2.widget.RxSeekBar
import edu.vanderbilt.crawler.R
import edu.vanderbilt.crawler.databinding.SettingsDialogFragmentBinding
import edu.vanderbilt.crawler.extensions.grabTouchEvents
import edu.vanderbilt.crawler.preferences.CompositeUnsubscriber
import edu.vanderbilt.crawler.preferences.ObservablePreference
import edu.vanderbilt.crawler.preferences.Subscriber
import edu.vanderbilt.crawler.ui.screens.settings.Settings.GRID_SCALE_RANGE
import edu.vanderbilt.crawler.ui.screens.settings.Settings.TRANSPARENCY_RANGE
import edu.vanderbilt.crawler.ui.screens.settings.adapters.CrawlDepthAdapter
import edu.vanderbilt.crawler.ui.screens.settings.adapters.CrawlerTypeSpinnerAdapter
import edu.vanderbilt.crawler.ui.screens.settings.adapters.TransformsAdapter
import edu.vanderbilt.imagecrawler.crawlers.CrawlerType
import io.reactivex.disposables.CompositeDisposable

/**
 * Application developer options fragment that shows a list of tunable
 * options in a modal bottom sheet.
 *
 * Note that any SeekBars added to this settings panel should call
 * [SeekBar.grabTouchEvents] so that sliding the thumb button to
 * right will not be interpreted by the panel as a slide closed
 * action.
 *
 * To show this bottom sheet:
 * <pre>
 * SettingsDialogFragment.newInstance().show(getSupportFragmentManager(), "dialog");
 * </pre>
 * You activity (or fragment) needs to implement [SettingsDialogFragment.Listener].
 */
class SettingsDialogFragment : BottomSheetDialogFragment() {
    companion object {
        fun newInstance(): SettingsDialogFragment {
            return SettingsDialogFragment()
        }
    }

    private lateinit var binding: SettingsDialogFragmentBinding

    /** RxJava subscriptions */
    private val disposables = CompositeDisposable()

    /** Observe all crawl speed preference changes. */
    private val compositeUnsubscriber = CompositeUnsubscriber()

    private var crawlSpeed: Int by ObservablePreference(
            default = 100,
            name = "CrawlSpeedPreference",
            subscriber = object : Subscriber<Int> {
                override val subscriber: (Int) -> Unit
                    get() = { binding.speedSeekBar.progress = it }

                override fun unsubscribe(callback: () -> Unit) {
                    compositeUnsubscriber.add(callback)
                }
            })

    private var speedBarState: Int by ObservablePreference(
            default = STATE_COLLAPSED,
            name = "speedBarStatePreference",
            subscriber = object : Subscriber<Int> {
                override val subscriber: (Int) -> Unit
                    get() = { binding.settingsShowSpeedBar.isChecked = it != STATE_HIDDEN }

                override fun unsubscribe(callback: () -> Unit) {
                    compositeUnsubscriber.add(callback)
                }
            })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        val contextThemeWrapper = ContextThemeWrapper(activity, R.style.SettingsThemeDark)

        // clone the inflater using the ContextThemeWrapper
        val localInflater = inflater.cloneInContext(contextThemeWrapper)
        binding = SettingsDialogFragmentBinding.inflate(localInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        compositeUnsubscriber.invoke()
        disposables.clear()
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureTransforms()
        configureCrawlerStrategy()
        configureCrawlerMaxDepth()
        configureCrawlerLocation()
        configureDebugLogging()
        configureThreadSpeed()
        configureShowSpeedBar()
        configureViewScale()
        configureViewTransparency()
        configureShowProgress()
        configureShowState()
        configureShowSize()
        configureShowThread()
    }

    private fun configureTransforms() {
        // Let the adapter handle adding child views.
        TransformsAdapter.buildAdapter(binding.transformsLayoutView)
    }

    private fun configureCrawlerMaxDepth() {
        // Setup adapter and current selection.
        val adapter = CrawlDepthAdapter(
                ContextThemeWrapper(activity, R.style.SettingsThemeDark))
        with(binding) {
            settingsCrawlDepth.adapter = adapter
            settingsCrawlDepth.setSelection(
                    CrawlDepthAdapter.getPositionForValue(Settings.crawlDepth))

            // Use Rx to filter item selections and save changed value to shared preference.
            RxAdapterView.itemSelections(settingsCrawlDepth)
                    .skipInitialValue()
                    .map<Int> { adapter.getItem(it) }
                    .filter { it != Settings.crawlDepth }
                    .subscribe { Settings.crawlDepth = it }
                    .also { disposables.add(it) }
        }
    }

    private fun configureCrawlerStrategy() {
        // Setup adapter and current selection.
        val adapter = CrawlerTypeSpinnerAdapter(
                ContextThemeWrapper(activity, R.style.SettingsThemeDark),
                CrawlerType::class.java)
        with(binding) {
            settingsCrawlStrategy.adapter = adapter
            settingsCrawlStrategy.setSelection(adapter.getPositionForValue(Settings.crawlStrategy) ?: 0)

            // Use Rx to filter item selections and save changed value to shared preference.
            RxAdapterView.itemSelections(settingsCrawlStrategy)
                    .skipInitialValue()
                    .map<CrawlerType> { adapter.getItem(it) }
                    .filter { it != Settings.crawlStrategy }
                    .subscribe { Settings.crawlStrategy = it }
                    .also { disposables.add(it) }
        }
    }

    private fun configureCrawlerLocation() {
        with(binding) {
            val checkedId = if (Settings.localCrawl) {
                settingsLocalCrawl.id
            } else {
                settingsRemoteCrawl.id
            }
            settingsImageSourceRadioGroup.check(checkedId)

            RxRadioGroup.checkedChanges(settingsImageSourceRadioGroup)
                    .skipInitialValue()
                    .subscribe { Settings.localCrawl = (it == settingsLocalCrawl.id) }
                    .also { disposables.add(it) }
        }
    }

    private fun configureDebugLogging() {
        with(binding) {
            settingsDebugOutput.isChecked = Settings.debugLogging
            RxCompoundButton.checkedChanges(settingsDebugOutput)
                    .skipInitialValue()
                    .subscribe { Settings.debugLogging = it }
                    .also { disposables.add(it) }
        }
    }

    private fun configureThreadSpeed() {
        with(binding) {
            speedSeekBar.grabTouchEvents()
            speedSeekBar.progress = Settings.crawlSpeed
            RxSeekBar.userChanges(speedSeekBar)
                    // Don't set shared pref if configuring.
                    .skipInitialValue()
                    .subscribe { Settings.crawlSpeed = it }
                    .also { disposables.add(it) }
        }
    }

    private fun configureShowSpeedBar() {
        with(binding) {
            settingsShowSpeedBar.isChecked =
                    Settings.speedBarState != STATE_COLLAPSED
            RxCompoundButton.checkedChanges(settingsShowSpeedBar)
                    // Don't set shared pref if configuring.
                    .skipInitialValue()
                    .subscribe {
                        val currentState = Settings.speedBarState
                        if (it) {
                            if (currentState == STATE_HIDDEN) {
                                Settings.speedBarState = STATE_EXPANDED
                            }
                        } else {
                            if (currentState != STATE_HIDDEN) {
                                Settings.speedBarState = STATE_HIDDEN
                            }
                        }
                    }
                    .also { disposables.add(it) }
        }
    }

    private fun configureViewScale() {
        with(binding) {
            viewScaleSeekBar.grabTouchEvents()
            viewScaleSeekBar.max = GRID_SCALE_RANGE.progressMax
            viewScaleSeekBar.progress =
                    GRID_SCALE_RANGE.toProgress(Settings.viewScale)
            RxSeekBar.userChanges(viewScaleSeekBar)
                    .skipInitialValue()
                    .subscribe {
                        val value = GRID_SCALE_RANGE.fromProgress(it)
                        Settings.viewScale = value
                    }
                    .also { disposables.add(it) }
        }
    }

    private fun configureViewTransparency() {
        with(binding) {
            viewTransparencySeekBar.grabTouchEvents()
            viewTransparencySeekBar.max = TRANSPARENCY_RANGE.progressMax
            viewTransparencySeekBar.progress =
                    TRANSPARENCY_RANGE.toProgress(Settings.viewTransparency)
            RxSeekBar.userChanges(viewTransparencySeekBar)
                    .subscribe {
                        val value = TRANSPARENCY_RANGE.fromProgress(it)
                        Settings.viewTransparency = value
                        view?.alpha = 1 - (value * (1 / 100f))
                    }
                    .also { disposables.add(it) }
        }
    }

    private fun configureShowProgress() {
        with(binding) {
            settingsShowProgress.isChecked = Settings.showProgress
            RxCompoundButton.checkedChanges(settingsShowProgress)
                    .skipInitialValue()
                    .subscribe { Settings.showProgress = it }
                    .also { disposables.add(it) }
        }
    }

    private fun configureShowState() {
        with(binding) {
            settingsShowState.isChecked = Settings.showState
            RxCompoundButton.checkedChanges(settingsShowState)
                    .skipInitialValue()
                    .subscribe { Settings.showState = it }
                    .also { disposables.add(it) }
        }
    }

    private fun configureShowThread() {
        with(binding) {
            settingsShowThread.isChecked = Settings.showThread
            RxCompoundButton.checkedChanges(settingsShowThread)
                    .skipInitialValue()
                    .subscribe { Settings.showThread = it }
                    .also { disposables.add(it) }
        }
    }

    private fun configureShowSize() {
        with(binding) {
            settingsShowSize.isChecked = Settings.showSize
            RxCompoundButton.checkedChanges(settingsShowSize)
                    .skipInitialValue()
                    .subscribe { Settings.showSize = it }
                    .also { disposables.add(it) }
        }
    }
}
