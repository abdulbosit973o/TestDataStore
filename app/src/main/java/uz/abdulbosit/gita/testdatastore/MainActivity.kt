package uz.abdulbosit.gita.testdatastore

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private var counter = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val textView = findViewById<TextView>(R.id.tv_increment)
        runBlocking {
            counter = getUserName() ?: 0
            textView.text = counter.toString()
        }

        findViewById<AppCompatButton>(R.id.btn_increment).setOnClickListener {
            ++counter
            textView.text = counter.toString()
        }

    }

    override fun onPause() {
        super.onPause()
        CoroutineScope(Dispatchers.IO).launch {
            saveUserName(counter.toString())
        }
    }

    // Ma'lumot yozish
    private suspend fun saveUserName(userName: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = userName
        }
    }

    // Ma'lumot o'qish
    private suspend fun getUserName(): Int? {
        val preferences = dataStore.data.first()
        return preferences[USER_NAME_KEY]?.toInt()
    }
}