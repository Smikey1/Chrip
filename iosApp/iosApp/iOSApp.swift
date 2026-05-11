import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init () {
        InitKoinKt.doInitKoin()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    IOSExternalUriHandler.shared.onNewUriArrived(uri: url.absoluteString)
                }
        }
    }
}
