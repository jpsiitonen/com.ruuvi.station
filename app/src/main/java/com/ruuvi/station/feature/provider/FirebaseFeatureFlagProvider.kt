package com.ruuvi.station.feature.provider

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.ruuvi.station.R
import com.ruuvi.station.feature.data.Feature
import com.ruuvi.station.feature.data.FeatureFlag
import timber.log.Timber

class FirebaseFeatureFlagProvider() : FeatureFlagProvider {
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Timber.d("fetch remoteConfig fetch ${task.result}")

                // After config data is successfully fetched, it must be activated before newly fetched values are returned.
                } else {
                task.exception
                Timber.d("fetch remoteConfig failed ${task.exception}")
            }
        }
    }

    override val priority: Int = MAX_PRIORITY

    override fun isFeatureEnabled(feature: Feature): Boolean {
        Timber.d("remoteConfig = ${remoteConfig.getString(feature.key)}")
        return remoteConfig.getBoolean(feature.key)
    }

    override fun hasFeature(feature: Feature): Boolean {
        return when (feature) {
            FeatureFlag.RUUVI_NETWORK -> true
            else -> false
        }
    }
}