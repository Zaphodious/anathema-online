(ns anathema-online.disk
  "Protocol and functions for making and handling things which store a particular kind of state.")

(defprotocol Disk
  (read-object [this category key]
    "Returns the map from the disk corresponding to the provided category and id.")
  (write-object! [this object]
    "Asynchronously puts a map containing a :category and :key field into the disk. Returns a core.async channel that puts 'this' when the write finishes."))
