(ns anathema-online.disk)


(defprotocol Disk
  (read-object [this category key]
    "Returns the map from the disk corresponding to the provided category and id.")
  (write-object [this object]
    "Asynchronously puts a map containing a :category and :key field into the disk. Returns a core.async channel that puts 'this' when the write finishes."))
