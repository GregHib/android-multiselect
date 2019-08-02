package world.gregs.android.multiselect

import java.util.concurrent.atomic.AtomicInteger

data class Model(val text: String, val description: String) {
    val id = counter.getAndIncrement()

    companion object {
        val counter = AtomicInteger()

        val models = arrayListOf(
            Model("Socks", "An item of clothing worn on the feet and often covering the ankle or some part of the calf."),
            Model("Shirts", "A cloth garment for the upper body."),
            Model("Coat", "Designed to be worn as the outermost garment worn as outdoor wear."),
            Model("Blazer", "A type of jacket resembling a suit jacket, but cut more casually."),
            Model("Boots", "A boot, plural boots, is a type of footwear and not a specific type of shoe. Most boots mainly cover the foot and the ankle, while some also cover some part of the lower calf."),
            Model("Suit", "A set of men's wear comprising a lounge jacket and trousers."),
            Model("Skirt", "The lower part of a dress or gown, covering the person from the waist downwards, or a separate outer garment serving this purpose.")
        )
    }
}