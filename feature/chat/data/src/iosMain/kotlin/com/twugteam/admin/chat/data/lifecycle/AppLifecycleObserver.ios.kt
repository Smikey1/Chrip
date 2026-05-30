package com.twugteam.admin.chat.data.lifecycle

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.UIKit.UIApplicationDidEnterBackgroundNotification
import platform.UIKit.UIApplicationState
import platform.UIKit.UIApplicationWillEnterForegroundNotification
import platform.UIKit.UIApplicationWillResignActiveNotification

actual class AppLifecycleObserver {
    actual val isAppInForeground: Flow<Boolean> = callbackFlow<Boolean> {

        // CHECK THE CURRENT STATUS OF APP
        val currentState = UIApplication.sharedApplication.applicationState
        val isCurrentlyInForeground = when (currentState) {
            UIApplicationState.UIApplicationStateActive -> true

            // How, IOS sees UI Application State Inactive means, the app is active and actively
            // running but the notification Center id DRAGGED Down or currently phone call is going
            // on top of our application. In this case, the app is still active, but ios marked
            // the application state as inactive.

            /*
            Que: Why Inactive is treated as foreground ?
             --> iOS marks apps as Inactive during temporary interruptions:
            a) notification shade opened
            b) control center dragged
            c) incoming phone call
            d) Face ID prompt
            e) app transition animations

            The app is still visible.
             */
            UIApplicationState.UIApplicationStateInactive -> true
            UIApplicationState.UIApplicationStateBackground -> false
            else -> false
        }
        send(isCurrentlyInForeground)


        val notificationCenter = NSNotificationCenter.defaultCenter

        // Observe foreground events
        val foregroundObserver = notificationCenter.addObserverForName(
            name = UIApplicationDidBecomeActiveNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) {
            trySend(true)
        }

        val enteringForegroundObserver = notificationCenter.addObserverForName(
            name = UIApplicationWillEnterForegroundNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) {
            trySend(true)
        }

        // Observe background events
        val backgroundObserver = notificationCenter.addObserverForName(
            name = UIApplicationDidEnterBackgroundNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) {
            trySend(false)
        }

        /*
        That notification is sent when the app is about to move from the active state to an inactive state.
            Typical cases:

            User presses the Home button
            User switches apps
            Incoming phone call or system interruption
            Control Center / Notification Center opens
            App is about to go to background
         */
        val willResignActiveObserver = notificationCenter.addObserverForName(
            name = UIApplicationWillResignActiveNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) {
            trySend(false)
        }

        awaitClose {
            notificationCenter.removeObserver(foregroundObserver)
            notificationCenter.removeObserver(enteringForegroundObserver)
            notificationCenter.removeObserver(backgroundObserver)
            notificationCenter.removeObserver(willResignActiveObserver)
        }
    }
}