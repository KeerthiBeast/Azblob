package com.example.azblob.model
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

/*Model for Azure api response
* <EnumerationResults>
    <Blobs>
    *   <Blob>
 Sample Response Skeleton (Has much more than the above fields but only these are used)*/

//Main Element
@Root(name = "EnumerationResults", strict = false)
class BlobModel @JvmOverloads constructor(
    @field:Element(name = "Blobs")
    var blobs: Blobs? = null
)

//Second Element
@Root(name = "Blobs", strict = false)
class Blobs @JvmOverloads constructor(
    @field:ElementList(inline = true)
    var blobList: List<Blob>? = null
)

//Sub Element
@Root(name = "Blob", strict = false)
class Blob @JvmOverloads constructor(
    @field:Element(name = "Name", required = true)
    var name: String? = null,

    @field:Element(name = "Url", required = true)
    var url: String? = null,
)

