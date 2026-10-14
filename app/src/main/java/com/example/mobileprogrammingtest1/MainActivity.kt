package com.example.mobileprogrammingtest1

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fetchButton: Button
    private lateinit var progressBar: ProgressBar

    private val users = mutableListOf<User>()
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        fetchButton = findViewById(R.id.fetchButton)
        progressBar = findViewById(R.id.progressBar)

        adapter = UserAdapter(users)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchButton.setOnClickListener { fetchUsers() }
    }

    private fun fetchUsers() {
        progressBar.visibility = View.VISIBLE
        fetchButton.isEnabled = false

        val client = AsyncHttpClient()
        client.get("https://jsonplaceholder.typicode.com/users", object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                progressBar.visibility = View.GONE
                fetchButton.isEnabled = true
                try {
                    val array = json.jsonArray
                    val parsed = mutableListOf<User>()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        parsed.add(
                            User(
                                name = obj.getString("name"),
                                email = obj.getString("email"),
                                phone = obj.getString("phone")
                            )
                        )
                    }
                    users.clear()
                    users.addAll(parsed)
                    adapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    Log.e("MainActivity", "Parse error", e)
                    Toast.makeText(this@MainActivity, "Failed to fetch data.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                progressBar.visibility = View.GONE
                fetchButton.isEnabled = true
                Log.e("MainActivity", "Request failed: $statusCode $response", throwable)
                Toast.makeText(this@MainActivity, "Failed to fetch data.", Toast.LENGTH_LONG).show()
            }
        })
    }
}