package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.TruckMateRepository
import com.example.model.AppLanguage
import com.example.model.TripStatus
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app name from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("TruckMate", appName)
  }

  @Test
  fun `verify promo code calculation`() {
    val ok = TruckMateRepository.applyPromoCode("FIRST50")
    assertTrue(ok)
    assertEquals(50, TruckMateRepository.currentBooking.value.discount)
  }

  @Test
  fun `verify language toggle`() {
    TruckMateRepository.setLanguage(AppLanguage.BANGLA)
    assertEquals(AppLanguage.BANGLA, TruckMateRepository.language.value)
    TruckMateRepository.setLanguage(AppLanguage.ENGLISH)
    assertEquals(AppLanguage.ENGLISH, TruckMateRepository.language.value)
  }

  @Test
  fun `verify wallet top up`() {
    val initial = TruckMateRepository.walletBalance.value
    TruckMateRepository.addMoneyToWallet(500)
    assertEquals(initial + 500, TruckMateRepository.walletBalance.value)
  }
}

