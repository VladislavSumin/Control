package ru.falseteam.control.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.kodein.di.*

class DiViewModelFactory(
    private val injector: DirectDI,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return injector.instanceOrNull<ViewModel>(modelClass.name) as T? ?: modelClass.newInstance()
    }
}

inline fun <reified T : ViewModel> DI.Builder.bindViewModel(overrides: Boolean? = null): DI.Builder.TypeBinder<T> {
    return bind<T>(T::class.java.name, overrides)
}

@Composable
inline fun <reified VM : ViewModel> kodeinViewModel(
    key: String? = null
): VM = androidx.compose.ui.viewinterop.viewModel(
    VM::class.java,
    key,
    Kodein.direct.instance()
)

// inline fun <reified VM : ViewModel, T> T.kodeinViewModel(): Lazy<VM> where T : KodeinAware, T : AppCompatActivity {
//    return lazy { ViewModelProvider(this, direct.instance()).get(VM::class.java) }
// }
//
// inline fun <reified VM : ViewModel, T> T.kodeinViewModel(): Lazy<VM> where T : KodeinAware, T : Fragment {
//    return lazy { ViewModelProvider(this, direct.instance()).get(VM::class.java) }
// }