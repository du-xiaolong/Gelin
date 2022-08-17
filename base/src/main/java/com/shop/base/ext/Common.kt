package com.shop.base.ext

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.*
import com.shop.base.R
import com.shop.base.common.BaseActivity
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

inline fun postMain(crossinline block: () -> Unit) {
    Handler(Looper.getMainLooper()).post { block.invoke() }
}

inline fun postMainDelayed(seconds: Double, crossinline block: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        block.invoke()
    }, (seconds * 1000).toLong())
}

//数据不能为空
fun <T> T?.requireNotNull(lazyMessage: (() -> Any)? = null): T {
    if (this == null) {
        val message = lazyMessage?.let { it().toString() } ?: "数据获取失败！错误码542"
        throw IllegalArgumentException(message)
    } else {
        return this
    }
}

//字符串不能为空
fun String?.requireNotEmpty(lazyMessage: (() -> Any)? = null): String {
    if (this.isNullOrEmpty()) {
        val message = lazyMessage?.let { it().toString() } ?: "数据异常！错误码778"
        throw IllegalArgumentException(message)
    } else {
        return this
    }
}

private const val THROTTLE_WINDOW = 600

/**
 * 防抖点击
 * 添加了点击效果
 */
fun View.clickAnim(action: (value: View) -> Unit) {
    setOnClickListener {
        val key = R.id.video_view_throttle_first_id
        val windowStartTime = getTag(key) as? Long ?: 0
        val currentTime = System.currentTimeMillis()
        val delta = currentTime - windowStartTime
        if (delta >= THROTTLE_WINDOW) {
            setTag(key, currentTime)
            val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(this, "alpha", 0.3f, 1f)

            objectAnimator.interpolator = DecelerateInterpolator()
            objectAnimator.duration = 300
            objectAnimator.start()

            action.invoke(it)
        }
    }
}

/**
 * 防抖点击
 * 没有点击效果
 */
fun View.click(action: (value: View) -> Unit) {
    setOnClickListener {
        val key = R.id.video_view_throttle_first_id
        val windowStartTime = getTag(key) as? Long ?: 0
        val currentTime = System.currentTimeMillis()
        val delta = currentTime - windowStartTime
        if (delta >= THROTTLE_WINDOW) {
            setTag(key, currentTime)
            action.invoke(it)
        }
    }
}


var Float.dp2px: Int
    get() {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics
        ).roundToInt()
    }
    set(value) {}


@SuppressLint("ClickableViewAccessibility")
fun View?.setPressState() {
    this ?: return
    isClickable = true
    val onPressTouchListener = View.OnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                v.alpha = 0.3f
            }
            MotionEvent.ACTION_CANCEL -> {
                v.alpha = 1f
            }
            MotionEvent.ACTION_UP -> {
                v.alpha = 1f
            }
        }
        false
    }
    this.setOnTouchListener(onPressTouchListener);
}




//一个全局的ViewModel
@MainThread
public inline fun <reified VM : ViewModel> ComponentActivity.applicationViewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val factoryPromise = factoryProducer ?: {
        defaultViewModelProviderFactory
    }
    return ViewModelLazy(VM::class, { viewModelStore }, factoryPromise)
}

val viewModelStore: ViewModelStore by lazy {
    ViewModelStore()
}

/**
 * 开启倒计时
 * [seconds] 倒计时秒数，如60
 * [onTick] 每秒回调一次，参数为当前倒计时秒数 ，如60,59,58,..,0
 */
inline fun BaseActivity.startCountDown(seconds: Int, crossinline onTick: (Int) -> Unit) {
    var disposable: Disposable? = null

    Observable.interval(0, 1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : Observer<Long> {

            override fun onSubscribe(d: Disposable) {
                disposable = d
            }

            override fun onNext(t: Long) {
                val current = seconds - t
                onTick.invoke(current.toInt())
                if (current <= 0) {
                    disposable?.dispose()
                }
            }

            override fun onError(e: Throwable) {
                disposable?.dispose()
            }

            override fun onComplete() {
                disposable?.dispose()
            }


        })
    disposable.disposeOnDestroy()
}

/**
 * onStart开始时回调
 *
 * @param invoke
 * @receiver
 */
fun LifecycleOwner.repeatOnStart(invoke: suspend () -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            invoke.invoke()
        }
    }
}

/**
 * 显示view，带有渐显动画效果。
 *
 * @param duration 毫秒，动画持续时长，默认500毫秒。
 */
fun View?.visibleAlphaAnimation(duration: Long = 500L) {
    this?.visibility = View.VISIBLE
    this?.startAnimation(AlphaAnimation(0f, 1f).apply {
        this.duration = duration
        fillAfter = true
    })
}


/**
 * 占位隐藏view，带有渐隐动画效果。
 *
 * @param duration 毫秒，动画持续时长，默认500毫秒。
 */
fun View?.invisibleAlphaAnimation(duration: Long = 500L) {
    this?.visibility = View.INVISIBLE
    this?.startAnimation(AlphaAnimation(1f, 0f).apply {
        this.duration = duration
        fillAfter = true
    })
}

/**
 * 隐藏view，带有渐隐动画效果。
 *
 * @param duration 毫秒，动画持续时长，默认500毫秒。
 */
fun View?.goneAlphaAnimation(duration: Long = 500L) {
    this?.visibility = View.GONE
    this?.startAnimation(AlphaAnimation(1f, 0f).apply {
        this.duration = duration
        fillAfter = true
    })
}