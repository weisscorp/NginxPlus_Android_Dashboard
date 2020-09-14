package ru.willdes.nginxplus

class UpstreamName {
    var idupstr = 0
    var upstrname: String? = null

    companion object {
        var upstreamName: UpstreamName? = null
            get() {
                if (field == null) {
                    field = UpstreamName()
                }
                return field
            }
            private set
    }
}