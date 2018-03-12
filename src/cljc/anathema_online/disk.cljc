(ns anathema-online.disk
  "Protocol and functions for making and handling things which store a particular kind of state."
  (:require [com.rpl.specter :as sp]
            [clojure.core.async :as async]
            [clojure.spec.alpha :as s]))

(defprotocol Disk
  (-read-object- [this category key]
    "Returns the map from the disk corresponding to the provided category and id.")
  (-write-object!- [this object]
    "Asynchronously puts a map containing a :category and :key field into the disk. Returns a core.async channel that puts 'this' when the write finishes.")
  (-clear-category!- [this category]
    "Deletes each record in a category. Necessary for testing."))

(s/def ::disk #(satisfies? Disk %))

(s/def ::category (s/or :keyword? keyword? :string? string?))

(s/def ::id string?)



(defn change-object!
  "Changes a small part of the state. Path-in is a vec of associative keys (ie, meaningful to (get)).
  Returns a core.async channel that returns the same as write-object!."
  [this category key path-in change-fn]
  (async/go
    (async/<! (-write-object!- this
                               (sp/transform
                                [(apply sp/keypath path-in)]
                                change-fn
                                (-read-object- this category key))))))
