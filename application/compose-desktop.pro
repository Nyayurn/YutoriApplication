-dontwarn okhttp3.internal.platform.**
-dontwarn korlibs.**
-keep class kotlinx.coroutines.internal.MainDispatcherFactory { *; }
#noinspection ShrinkerUnresolvedReference
-keep class kotlinx.coroutines.swing.SwingDispatcherFactory { *; }
-keep class io.ktor.client.** { *; }
-keep class okhttp3.** { *; }
-keep class coil3.** { *; }
-keep class cn.yurn.yutori.** { *; }