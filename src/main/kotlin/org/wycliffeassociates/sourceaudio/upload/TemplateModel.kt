package org.wycliffeassociates.sourceaudio.upload

import org.wycliffeassociates.sourceaudio.upload.model.CompressedExtensions
import org.wycliffeassociates.sourceaudio.upload.model.Groupings
import org.wycliffeassociates.sourceaudio.upload.model.MediaQuality
import org.wycliffeassociates.sourceaudio.upload.model.UncompressedExtensions

class TemplateModel {
    val qualityList = MediaQuality.values().map { it.quality }
    val groupingList = Groupings.values().map { it.grouping }
    val extensionList: List<String>
        get() {
            val compressedExtensions = CompressedExtensions.values().map { enum ->
                enum.ext.toList()   //this ext is Array<String>
            }.flatten()
            val uncompressedExtensions = UncompressedExtensions.values().map { it.ext }
            return compressedExtensions + uncompressedExtensions
        }
}