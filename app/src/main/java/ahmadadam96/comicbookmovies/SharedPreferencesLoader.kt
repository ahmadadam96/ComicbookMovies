package ahmadadam96.comicbookmovies

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.loader.content.AsyncTaskLoader

/**
 * Created by ahmad on 2017-04-04.
 */

class SharedPreferencesLoader(context: Context) : AsyncTaskLoader<SharedPreferences>(context), SharedPreferences.OnSharedPreferenceChangeListener {
    private var prefs: SharedPreferences? = null

    // Load the data asynchronously
    override fun loadInBackground(): SharedPreferences? {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs!!.registerOnSharedPreferenceChangeListener(this)
        return prefs
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences,
                                           key: String) {
        // notify loader that content has changed
        onContentChanged()
    }

    /**
     * starts the loading of the data
     * once result is ready the onLoadFinished method is called
     * in the main thread. It loader was started earlier the result
     * is return directly
     *
     *
     * method must be called from main thread.
     */
    override fun onStartLoading() {
        if (prefs != null) {
            deliverResult(prefs)
        }

        if (takeContentChanged() || prefs == null) {
            forceLoad()
        }
    }

    companion object {

        fun persist(editor: SharedPreferences.Editor) {
            editor.apply()
        }
    }
}