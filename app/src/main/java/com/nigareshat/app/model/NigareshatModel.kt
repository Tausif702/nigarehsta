import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NigareshatModel(
    var bookName: String,
    var quantity: String,
    var price: String,
    var discount: String,
    var amount: Int

) : Parcelable

