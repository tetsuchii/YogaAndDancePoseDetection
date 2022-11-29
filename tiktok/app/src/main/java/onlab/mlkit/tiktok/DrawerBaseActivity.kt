package onlab.mlkit.tiktok

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

open class DrawerBaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth : FirebaseAuth
    private lateinit var drawerLayout : DrawerLayout

    override fun setContentView(view: View?) {
        drawerLayout = layoutInflater.inflate(R.layout.activity_drawer_base,null) as DrawerLayout
        val container = drawerLayout.findViewById<FrameLayout>(R.id.activityContainer)
        container.addView(view)
        super.setContentView(drawerLayout)

        auth= Firebase.auth

        val toolbar = drawerLayout.findViewById<Toolbar>(R.id.toolBar)
        setSupportActionBar(toolbar)

        val navigationView = drawerLayout.findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.menu_drawer_open,R.string.menu_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)

        when (item.itemId){
            R.id.nav_list -> {
                startActivity(Intent(this,MainActivity::class.java))
                overridePendingTransition(0,0)
            }
            R.id.nav_settings -> {
                startActivity(Intent(this,SettingsActivity::class.java))
                overridePendingTransition(0,0)
            }
            R.id.nav_logout -> {
                displayLogoutDialog()
                overridePendingTransition(0,0)
            }
        }
        return false
    }


    fun displayLogoutDialog() {
        val logoutDialog = Dialog(this)
        logoutDialog.setCancelable(true)
        logoutDialog.setContentView(R.layout.logout_dialog)
        logoutDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val button=logoutDialog.findViewById<Button>(R.id.logout)
        val cancel=logoutDialog.findViewById<TextView>(R.id.cancel)

        button.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this,LoginActivity::class.java))
        }
        cancel.setOnClickListener {
            logoutDialog.cancel()
        }

        logoutDialog.show()
    }

    fun allocateActivityTitle(titleString: String){
        if (supportActionBar != null){
            supportActionBar!!.title=titleString
        }
    }
}