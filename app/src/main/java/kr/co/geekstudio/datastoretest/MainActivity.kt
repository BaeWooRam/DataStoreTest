package kr.co.geekstudio.datastoretest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.migrations.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var dataStore: DataStore<Preferences>
    private val preferencesKey = preferencesKey<Int>("testKey")
    private var count = 0
    private val countFlow: Flow<Int?>
        get() = dataStore.data.map { preferences ->
            println("map execute")
            preferences[preferencesKey]
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataStore = createDataStore("test")

        btAdd.setOnClickListener(this)
        btRemove.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btAdd -> {
                clickAdd()
                showCount()
            }
            R.id.btRemove -> {
                clickRemove()
                showCount()
            }
        }
    }

    private fun clickAdd() {
        CoroutineScope(Dispatchers.Default).launch {
            dataStore.edit { settings ->
                settings[preferencesKey] = add(1, 2)
            }
        }
    }

    private fun clickRemove() {
        CoroutineScope(Dispatchers.Default).launch {
            dataStore.edit { settings ->
                settings[preferencesKey] = count
            }
        }
    }

    private fun showCount() {
        CoroutineScope(Dispatchers.Default).launch {
            countFlow.collect {
                Log.i("clickAdd", "dataStore testKey = $it")
            }
        }
    }


    fun add(num1: Int, num2:Int): Int {
        return operation { return@operation num1 + num2 }
    }

    fun remove(num1: Int, num2:Int): Int {
        return operation { return@operation num1 - num2 }
    }

    inline fun operation(func:() -> Int):Int{
        return func()
    }


    infix fun String.meet(other:String):String{
        return this + other
    }
}