(ns anathema-online.disk
  "Protocol and functions for making and handling things which store a particular kind of state."
  (:require [com.rpl.specter :as sp]
            [clojure.core.async :as async]
            [clojure.spec.alpha :as s]
            [anathema-online.data :as data]))

(defprotocol Disk
  (-read-object- [this category key]
    "Returns the map from the disk corresponding to the provided category and id.")
  (-write-object!- [this object]
    "Asynchronously puts a map containing a :category and :key field into the disk. Returns the disk.")
  (-clear-category!- [this category]
    "Deletes each record in a category. Necessary for testing."))

(s/def ::data/disk #(satisfies? Disk %))

(s/fdef read-object
        :args (s/cat :this ::data/disk :category ::data/category :key ::data/id)
        :ret ::data/disk)

(defn read-object [this category key] (-read-object- this category key))

(s/fdef write-object!
        :args (s/cat :this ::data/disk :object ::data/game-entity)
        :ret ::data/disk)

(defn write-object! [this object] (-write-object!- this object))

(s/fdef clear-category!
        :args (s/cat :this ::data/disk :object ::data/game-entity)
        :ret ::data/disk)

(defn clear-category! [this category] (-clear-category!- this category))

(s/fdef change-object!
        :args (s/cat :this ::data/disk :category ::data/category :path-in ::data/path :swap-fn ::data/swapper)
        :ret ::data/disk)

(defn change-object!
  "Changes a small part of the state. Path-in is a vec of associative keys (ie, meaningful to (get)).
  Returns the disk."
  [this category key path-in change-fn]
  (-write-object!- this
                   (sp/transform
                     [(apply sp/keypath path-in)]
                     change-fn
                     (-read-object- this category key))))
