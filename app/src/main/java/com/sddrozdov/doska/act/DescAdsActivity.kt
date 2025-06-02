package com.sddrozdov.doska.act

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sddrozdov.doska.R
import com.sddrozdov.doska.databinding.ActivityDescAdsBinding
import com.sddrozdov.doska.models.Ads
import com.sddrozdov.doska.models.Bid
import com.sddrozdov.doska.models.DbManager
import com.sddrozdov.doska.recyclerViewAdapters.ImageAdapterForViewPager
import com.sddrozdov.doska.utilites.ImageManager

class DescAdsActivity : AppCompatActivity() {

    private var _binding: ActivityDescAdsBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding must not be null")
    private lateinit var imageAdapter: ImageAdapterForViewPager
    private var adDetails: Ads? = null
    private var ownerUid: String? = null
    private val auth = Firebase.auth
    private var auctionTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityDescAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.descActToolbar)
        MainActivity.WindowInsetUtil.applyWindowInsets(binding.root)

        initialize()
        setupClickListeners()
        setupAuctionUI()
        openChatActivity()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            Toast.makeText(this, "НАЖАЛИ НАЗАД ", Toast.LENGTH_LONG).show()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initialize() {
        imageAdapter = ImageAdapterForViewPager()
        binding.viewPager.adapter = imageAdapter
        retrieveAdDetailsFromIntent()
        setupViewPagerImageChangeListener()
    }

    private fun setupClickListeners() {
        binding.fbTel.setOnClickListener { initiatePhoneCall() }
        binding.fbEmail.setOnClickListener { initiateEmailSending() }
    }

    private fun initiatePhoneCall() {
        val callUri = "tel:${adDetails?.tel}"
        val intentCall = Intent(Intent.ACTION_DIAL).apply {
            data = callUri.toUri()
        }
        startActivity(intentCall)
    }

    private fun initiateEmailSending() {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(adDetails?.email))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.AD))
            putExtra(Intent.EXTRA_TEXT, "TODO()") // TODO: Complete email body
        }
        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.OPEN_WITH)))
        } catch (exception: ActivityNotFoundException) {
            showToast("Нет приложения для отправки почты")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun retrieveAdDetailsFromIntent() {
        adDetails = intent.getSerializableExtra("AD") as Ads
        ownerUid = adDetails?.uid
        adDetails?.let { updateUI(it) }
    }

    private fun updateUI(ad: Ads) {
        ImageManager.fillImageArray(ad, imageAdapter)
        populateTextViews(ad)
    }

    private fun populateTextViews(ad: Ads) {
        binding.apply {
            tvTitle.text = ad.title
            tvDescription.text = ad.description
            tvPrice.text = ad.price
            tvEmail.text = ad.email
            tvTel.text = ad.tel
            tvCountry.text = ad.country
            tvCity.text = ad.city
            tvIndex.text = ad.index
        }
    }

    private fun setupViewPagerImageChangeListener() {
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val imageCounter = "${position + 1}/${binding.viewPager.adapter?.itemCount ?: 0}"
                binding.tvImageCounter.text = imageCounter
            }
        })
    }

    private fun setupAuctionUI() {
        adDetails?.let { ad ->
            if (ad.isAuction) {
                binding.auctionLayout.visibility = View.VISIBLE
                updateAuctionUI(ad)
                setupBidButton(ad)
                setupShowBidsButton(ad)
                startAuctionTimer(ad.auctionEndTime)

                binding.tvCurrentBid.text = ad.auctionCurrentPrice?.let {
                    "Текущая ставка: $it"
                } ?: "Начальная цена: ${ad.auctionStartPrice}"
            }
        }
    }

    private fun setupShowBidsButton(ad: Ads) {
        binding.btnShowBids.setOnClickListener {
            val intent = Intent(this, AuctionBidsActivity::class.java).apply {
                putExtra("AD_KEY", ad.key)
            }
            startActivity(intent)
        }
    }

    private fun openChatActivity() {
        binding.fbMessage.setOnClickListener {
            val intent = Intent(this@DescAdsActivity, ChatActivity::class.java).apply {
                putExtra("AD_ID", adDetails?.key) // идентификатор объявления
                putExtra("USER_ID", auth.currentUser?.uid) // UID текущего пользователя
                putExtra("OWNER_ID", ownerUid) // UID владельца объявления
            }
            startActivity(intent)
        }
    }

    private fun updateAuctionUI(ad: Ads) {
        val currentPrice = ad.auctionCurrentPrice ?: ad.auctionStartPrice
        binding.tvCurrentBid.text = getString(R.string.current_price, currentPrice)
        startAuctionTimer(ad.auctionEndTime)
    }

    private fun setupBidButton(ad: Ads) {
        binding.btnPlaceBid.setOnClickListener {
            if (ownerUid.isNullOrEmpty()) {
                showError("Ошибка: невозможно определить владельца объявления")
                return@setOnClickListener
            }

            if (ad.key.isNullOrBlank()) {
                showError("Ошибка: отсутствует идентификатор объявления")
                return@setOnClickListener
            }

            val bidInput = binding.etNewBid.text.toString()
            if (bidInput.isBlank()) {
                showError("Введите сумму ставки")
                return@setOnClickListener
            }

            val bidAmount = bidInput.toFloatOrNull()
            if (bidAmount == null || bidAmount <= 0) {
                showError("Некорректный формат суммы или сумма должна быть больше нуля")
                return@setOnClickListener
            }

            val currentPrice = ad.auctionCurrentPrice?.toFloatOrNull() ?: ad.auctionStartPrice?.toFloatOrNull() ?: 0f
            if (bidAmount <= currentPrice) {
                showError("Ставка должна быть выше текущей")
                return@setOnClickListener
            }

            val bid = Bid(
                userId = auth.currentUser !!.uid,
                amount = "%.2f".format(bidAmount),
                timestamp = System.currentTimeMillis()
            )

            DbManager().placeBid(
                adKey = ad.key,
                ownerUid = ownerUid!!,
                bid = bid,
                finishWorkListener = object : DbManager.FinishWorkListener {
                    override fun onFinish(boolean: Boolean?) {
                        onFinishBid(boolean,bid)
                    }
                }
            )
        }
    }

    private fun onFinishBid(boolean: Boolean?,bid: Bid){
        when (boolean) {
            true -> {
                // Успешное размещение ставки
                runOnUiThread {
                    Toast.makeText(
                        this@DescAdsActivity,
                        "Ставка успешно размещена!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Обновляем UI
                    binding.etNewBid.text?.clear()
                    adDetails?.auctionCurrentPrice = bid.amount
                    adDetails?.price = bid.amount
                    updateAuctionUI(adDetails!!)
                }
            }
            false -> {
                // Ошибка при размещении ставки
                runOnUiThread {
                    Toast.makeText(
                        this@DescAdsActivity,
                        "Ошибка при размещении ставки",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else -> {
                // Неизвестная ошибка
                runOnUiThread {
                    Toast.makeText(
                        this@DescAdsActivity,
                        "Неизвестная ошибка при размещении ставки",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        binding.etNewBid.requestFocus()
    }

    private fun startAuctionTimer(endTime: Long) {
        val remainingTime = endTime - System.currentTimeMillis()
        auctionTimer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (!isDestroyed) {
                    binding.tvAuctionTime.text = formatTime(millisUntilFinished)
                }
            }

            override fun onFinish() {
                if (!isDestroyed && adDetails?.key != null) {
                    binding.tvAuctionTime.text = "Аукцион завершен"
                    DbManager().checkAuctionEnd(adDetails!!.key!!)
                }
            }
        }.start()
    }

    private fun formatTime(millis: Long): String {
        val seconds = millis / 1000
        val days = seconds / 86400
        val hours = (seconds % 86400) / 3600
        val minutes = (seconds % 3600) / 60
        return String.format("%dд %02d:%02d", days, hours, minutes)
    }

    override fun onDestroy() {

        auctionTimer?.cancel()
        super.onDestroy()
        _binding = null
    }
}
