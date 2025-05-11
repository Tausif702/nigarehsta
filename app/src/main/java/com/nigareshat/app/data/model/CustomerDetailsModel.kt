import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomerDetailsModel(
    val customerName: String,
    val shipVia: String,
    val shipAmount: String,
    val shipDate: String

) : Parcelable
