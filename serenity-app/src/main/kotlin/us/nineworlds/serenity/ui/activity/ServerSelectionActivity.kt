package us.nineworlds.serenity.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.ButterKnife.*
import us.nineworlds.serenity.AndroidTV
import us.nineworlds.serenity.R
import us.nineworlds.serenity.common.Server
import us.nineworlds.serenity.core.util.StringPreference
import us.nineworlds.serenity.injection.ForMediaServers
import us.nineworlds.serenity.injection.InjectingActivity
import us.nineworlds.serenity.injection.ServerClientPreference
import javax.inject.Inject

class ServerSelectionActivity : InjectingActivity() {

  companion object {
    const val SERVER_DISPLAY_DELAY = 4000L
  }

  @field:[Inject ForMediaServers]
  lateinit var servers: MutableMap<String, Server>

  @BindView(R.id.server_container)
  internal lateinit var serverContainer: LinearLayout

  @BindView(R.id.server_loading_progress)
  internal lateinit var serverLoadingProgressBar: ProgressBar

  internal lateinit var serverDisplayHandler: Handler

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_server_selection)
    bind(this)

    serverDisplayHandler = Handler()
    serverDisplayHandler.postDelayed({
      createServerList()
    }, SERVER_DISPLAY_DELAY)
  }

  private fun createServerList() {
    serverLoadingProgressBar.visibility = GONE
    servers.forEach { key, serverInfo -> displayServers(key, serverInfo) }
    if (serverContainer.childCount > 0) {
      serverContainer.getChildAt(0).requestFocus()
    }
    serverContainer.visibility = VISIBLE
  }

  private fun displayServers(key: String, serverInfo: Server) {
    if (serverInfo.discoveryProtocol() == "Emby") {
      createServerView(R.layout.item_server_select_emby, serverInfo)
    } else {
      createServerView(R.layout.item_server_select_plex, serverInfo)
    }
  }

  private fun createServerView(layoutId: Int, serverInfo: Server) {
    val serverView = LayoutInflater.from(this).inflate(layoutId, serverContainer, false) as FrameLayout?
    val serverNameText = serverView!!.findViewById<TextView>(R.id.server_name)
    serverNameText?.text = serverInfo.serverName
    serverContainer.addView(serverView)
    serverView.setOnClickListener { view -> startNextActivity(serverInfo) }
  }

  private fun startNextActivity(serverInfo: Server) {
  //  serverPreference.set(serverInfo.discoveryProtocol())

    val intent = Intent(this, AndroidTV::class.java)
    startActivity(intent)
    finish()
  }
}