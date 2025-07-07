package com.colin.library.android.utils.helper

import android.os.Handler
import android.os.Looper
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-05-08 20:51
 *
 * Des   :ThreadHelper
 * <a href="https://www.jianshu.com/p/a27416b7f01f">...</a>
 * 描述： 阻塞系数 = 阻塞时间 / 总的运行时间，如果任务有 50% 的时间处于阻塞状态，则阻塞系数为0.5
 */

@IntDef(
    PoolType.CUSTOM,
    PoolType.SINGLE,
    PoolType.FIXED,
    PoolType.CACHED,
    PoolType.SCHEDULED,
    PoolType.IO,
    PoolType.CPU
)
@Retention(AnnotationRetention.SOURCE)
annotation class PoolType {
    companion object {
        // 线程池类型  IO流网络请求  CPU复杂运算操作
        const val CUSTOM: Int = 0
        const val SINGLE: Int = 1
        const val FIXED: Int = 2
        const val CACHED: Int = 3
        const val SCHEDULED: Int = 4
        const val IO: Int = 5
        const val CPU: Int = 6
    }
}

///////////////////////////////////////////////////////////////////////////
// 线程池类型  IO流网络请求  CPU复杂运算操作
//
// 计算密集（CPU密集）一般配置CPU处理器个数+/-1个线程，
// 所谓计算密集型就是指系统大部分时间是在做程序正常的计算任务，
// 例如数字运算、赋值、分配内存、内存拷贝、循环、查找、排序等，这些处理都需要CPU来完成。
//
//IO密集
// 是指系统大部分时间在跟I/O交互，而这个时间线程不会占用CPU来处理，即在这个时间范围内，可以由其他线程来使用CPU，因而可以多配置一些线程。
//
//混合型
//混合型的话，是指两者都占有一定的时间。
///////////////////////////////////////////////////////////////////////////
object ThreadHelper {

    const val THREAD_MAX: Int = 128
    const val KEEP_LIVE: Long = 30L
    val TIME_UNIT: TimeUnit = TimeUnit.SECONDS
    val THREAD_CPU_COUNT: Int = Runtime.getRuntime().availableProcessors()

    //主线程
    val HANDLER: Handler = Handler(Looper.getMainLooper())
    private val POOL: ExecutorService by lazy {
        ThreadPoolExecutor(
            getCoreCount(PoolType.IO),
            getMaxCount(PoolType.IO),
            KEEP_LIVE,
            TIME_UNIT,
            LinkedBlockingQueue<Runnable?>(THREAD_MAX),
            CustomThreadFactory(PoolType.IO, Thread.NORM_PRIORITY),
            getDefaultRejected()
        )
    }

    fun shutdown() {
        POOL.shutdownNow()
    }

    fun isMain() = Looper.myLooper() == Looper.getMainLooper()

    fun getCoreCount(@PoolType type: Int): Int {
        return when (type) {
            PoolType.FIXED, PoolType.CPU -> THREAD_CPU_COUNT + 1
            PoolType.CACHED -> 0
            PoolType.IO -> (THREAD_CPU_COUNT shl 1) + 1
            PoolType.SINGLE, PoolType.CUSTOM, PoolType.SCHEDULED -> 1
            else -> 1
        }
    }

    fun getMaxCount(@PoolType type: Int): Int {
        return when (type) {
            PoolType.FIXED -> THREAD_CPU_COUNT + 1
            PoolType.CACHED -> THREAD_MAX
            PoolType.IO, PoolType.CPU -> (THREAD_CPU_COUNT shl 1) + 1
            PoolType.SINGLE, PoolType.CUSTOM, PoolType.SCHEDULED -> 1
            else -> 1
        }
    }

    fun getDefaultRejected(): RejectedExecutionHandler {
        return ThreadPoolExecutor.DiscardOldestPolicy()
    }

    fun post(runnable: Runnable) {
        if (isMain()) runnable.run()
        else HANDLER.post(runnable)
    }

    fun postDelayed(runnable: Runnable, delayMillis: Long) {
        HANDLER.postDelayed(runnable, delayMillis)
    }

    fun doAsync(runnable: Runnable) {
        POOL.execute(runnable)
    }

    fun doAsync(@PoolType type: Int, runnable: Runnable) {
        when (type) {
            PoolType.SINGLE -> single().execute(runnable)
            PoolType.FIXED -> fixed().execute(runnable)
            PoolType.CACHED -> cache().execute(runnable)
            PoolType.IO -> io().execute(runnable)
            PoolType.CPU -> cup().execute(runnable)
            else -> POOL.execute(runnable)
        }
    }

    /**
     * 参数说明：
     * corePoolSize         -> 1
     * maximumPoolSize      -> 1
     * keepAliveTime        -> 0L
     * TimeUnit             -> TimeUnit.SECONDS
     * WorkQueue            -> new LinkedBlockingQueue<Runnable>() 无解阻塞队列
     *
     *
     * 说明：创建只有一个线程的线程池，且线程的存活时间是无限的；当该线程正繁忙时，对于新任务会进入阻塞队列中(无界的阻塞队列)
     *
     *
     * 适用：一个任务一个任务执行的场景
     * 返回：ThreadPoolExecutor
     */
    @JvmOverloads
    @JvmStatic
    fun single(
        factory: ThreadFactory = CustomThreadFactory(PoolType.SINGLE, Thread.NORM_PRIORITY),
        handler: RejectedExecutionHandler = getDefaultRejected()
    ): ExecutorService {
        return threadPool(
            1, 1, 0L, TIME_UNIT, LinkedBlockingQueue<Runnable>(THREAD_MAX), factory, handler
        )
    }

    /**
     * 参数说明：
     * corePoolSize         -> 自定义
     * maximumPoolSize      -> 自定义
     * keepAliveTime        -> 0L
     * TimeUnit             -> TimeUnit.MILLISECONDS
     * WorkQueue            -> new LinkedBlockingQueue<Runnable>() 无解阻塞队列
     *
     *
     * 说明：创建可容纳固定数量线程的池子，每隔线程的存活时间是无限的，当池子满了就不在添加线程了；
     * 如果池中的所有线程均在繁忙状态，对于新任务会进入阻塞队列中(无界的阻塞队列)
     *
     *
     * 适用：执行长期的任务，性能好很多
     * 返回：ThreadPoolExecutor
     */
    @JvmOverloads
    @JvmStatic
    fun fixed(
        @IntRange(from = 1, to = 4) core: Int = getCoreCount(PoolType.FIXED),
        @IntRange(from = 1, to = 128) max: Int = getMaxCount(PoolType.FIXED),
        factory: ThreadFactory = CustomThreadFactory(PoolType.FIXED, Thread.NORM_PRIORITY),
        handler: RejectedExecutionHandler = getDefaultRejected()
    ): ExecutorService {
        require(core >= 1 && core <= 4) { "core must be between 1 and 4" }
        require(max >= 1 && core <= 128) { "max must be between 1 and 128" }
        return threadPool(
            core, max, 0L, TIME_UNIT, LinkedBlockingQueue<Runnable>(THREAD_MAX), factory, handler
        )
    }

    /**
     * 参数说明：
     * nThreads             -> 0
     * maximumPoolSize      -> Integer.MAX_VALUE
     * keepAliveTime        -> 60L
     * TimeUnit             -> TimeUnit.SECONDS
     * WorkQueue            -> new SynchronousQueue<Runnable>()(同步队列)
     *
     *
     * 说明：当有新任务到来，则插入到SynchronousQueue中，由于SynchronousQueue是同步队列，因此会在池中寻找可用线程来执行，
     * 若有可以线程则执行，若没有可用线程则创建一个线程来执行该任务；若池中线程空闲时间超过指定大小，则该线程会被销毁。
     *
     *
     * 适用：执行很多短期异步的小程序或者负载较轻的服务器
     * 返回：ThreadPoolExecutor
     */
    @JvmOverloads
    @JvmStatic
    fun cache(
        @IntRange(from = 1, to = 128) max: Int = getMaxCount(PoolType.CACHED),
        keepLive: Long = KEEP_LIVE * 2,
        unit: TimeUnit = TimeUnit.SECONDS,
        factory: ThreadFactory = CustomThreadFactory(PoolType.CACHED, Thread.NORM_PRIORITY),
        handler: RejectedExecutionHandler = getDefaultRejected()
    ): ExecutorService {
        require(max >= 0 && max <= 128) { "max must be between 0 and 128" }
        return threadPool(0, max, keepLive, unit, SynchronousQueue<Runnable>(), factory, handler)
    }

    @JvmOverloads
    @JvmStatic
    fun io(
        @IntRange(from = 1, to = 12) core: Int = getCoreCount(PoolType.IO),
        @IntRange(from = 1, to = 128) max: Int = getMaxCount(PoolType.IO),
        keepLive: Long = KEEP_LIVE,
        unit: TimeUnit = TimeUnit.SECONDS,
        factory: ThreadFactory = CustomThreadFactory(PoolType.IO, Thread.NORM_PRIORITY),
        handler: RejectedExecutionHandler = getDefaultRejected()
    ): ExecutorService {
        return threadPool(
            core, max, keepLive, unit, LinkedBlockingQueue<Runnable>(THREAD_MAX), factory, handler
        )
    }

    @JvmOverloads
    @JvmStatic
    fun cup(
        @IntRange(from = 1, to = 12) core: Int = getCoreCount(PoolType.CPU),
        @IntRange(from = 1, to = 128) max: Int = getMaxCount(PoolType.CPU),
        keepLive: Long = KEEP_LIVE,
        unit: TimeUnit = TimeUnit.SECONDS,
        factory: ThreadFactory = CustomThreadFactory(PoolType.CPU, Thread.NORM_PRIORITY),
        handler: RejectedExecutionHandler = getDefaultRejected()
    ): ExecutorService {
        return threadPool(
            core, max, keepLive, unit, LinkedBlockingQueue<Runnable>(THREAD_MAX), factory, handler
        )
    }

    @JvmStatic
    fun threadPool(
        @IntRange(from = 0, to = 12) core: Int,
        @IntRange(from = 0, to = 128) max: Int,
        @IntRange(from = 0, to = 128) keepAliveTime: Long,
        unit: TimeUnit,
        queue: BlockingQueue<Runnable>,
        factory: ThreadFactory,
        handler: RejectedExecutionHandler
    ): ExecutorService {
        require(core >= 0 && core <= 12) { "core must be between 0 and 12" }
        require(max >= 0 && max <= 128) { "max must be between 0 and 128" }
        require(keepAliveTime >= 0) { "keepAliveTime must be non-negative" }
        return ThreadPoolExecutor(core, max, keepAliveTime, unit, queue, factory, handler)
    }


    class CustomThreadFactory @JvmOverloads constructor(
        @PoolType private val mPoolType: Int,
        @IntRange(from = 1, to = 10) private val mPriority: Int,
        private val mDaemon: Boolean = false
    ) : AtomicLong(), ThreadFactory {
        private val poolNumber = AtomicInteger(1)

        override fun newThread(run: Runnable): Thread {
            val namePrefix = when (mPoolType) {
                PoolType.SINGLE -> "single-pool"
                PoolType.FIXED -> "fixed-pool"
                PoolType.CACHED -> "cache-pool"
                PoolType.SCHEDULED -> "scheduled-pool"
                PoolType.IO -> "io-pool"
                PoolType.CPU -> "cpu-pool"
                PoolType.CUSTOM -> "other-pool"
                else -> "unknown-pool"
            }

            val thread = object :
                Thread(run, "$namePrefix-${poolNumber.get()}-thread-${incrementAndGet()}") {
                init {
                    isDaemon = mDaemon
                    priority = mPriority
                }

                override fun run() {
                    try {
                        super.run()
                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }
                }
            }
            // Avoid infinite recursion if someone uses a Thread as Runnable
            if (run !is Thread) poolNumber.incrementAndGet()
            return thread
        }

        override fun toByte() = COUNT.toByte()

        override fun toShort() = COUNT.toShort()

        companion object {
            private val COUNT = AtomicInteger(1)
        }
    }
}
