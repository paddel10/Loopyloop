package ch.patland.loopyloop.media

data class MediaDirectory(val directory: String, val directoryId: String) {
    fun isSameDirectoryId(directoryId: String): Boolean {
        return this.directoryId == directoryId
    }
}