(ns anathema-online.disk
  "Functions for handling 'disk' components.

  First thing to note, is that any time a data entity is requested, it will be returned as a 'viewmap'.

  A viewmap is a map containing
    :path - a vector of (get)-able identifiers that functions as a URI *into* the data
    :view - the thing itself.

  Disk components themselves are maps (records, etc) with the following keys:
  :read-object (fn [this [category key :as path]] Returning the viewmap for the resource. Only retrieves the first item under the key.)
  :write-object! (fn [this, object] Puts the entity (containing :category and :key) into the Disk. No-op if viewmap. Returns the disk.)
  :clear-category! (fn [this category] Deletes each record in a category. Necessary for testing.).
  Ideally, disk components would be records satisfying com.stuartsierra.component/Lifecycle, but no assumptions are made here."
  (:require [com.rpl.specter :as sp]
            [clojure.core.async :as async]
            [clojure.spec.alpha :as s]
            [anathema-online.data :as data]))

(s/def ::read-object (s/fspec))
(s/def ::write-object! (s/fspec))
(s/def ::clear-category! (s/fspec))

(s/def ::data/disk (s/keys :req-un [::read-object, ::write-object!, ::clear-category!]))

(s/fdef read-object
        :args (s/cat :this ::data/disk :path ::data/path)
        :ret ::data/disk)

(defn read-object [this [category key]] ((:read-object this) this [category key]))

(s/fdef write-object!
        :args (s/cat :this ::data/disk :object ::data/game-entity)
        :ret ::data/disk)

(defn write-object! [this object] ((:write-object! this) this object))

(s/fdef clear-category!
        :args (s/cat :this ::data/disk :object ::data/game-entity)
        :ret ::data/disk)

(defn clear-category! [this category] ((:clear-category! this) this category))

(s/fdef change-object!
        :args (s/cat :this ::data/disk :path-in ::data/path :swap-fn ::data/swapper)
        :ret ::data/disk)

(defn change-object!
  "Changes a small part of the state. Path-in is a vec of associative keys (ie, meaningful to (get)).
  Returns the disk."
  [this [category key & path-in :as path] change-fn]
  (write-object! this
                   (sp/transform
                     [(apply sp/keypath path-in)]
                     change-fn
                     (read-object this [this category key]))))

(s/fdef get-for-player
        :args (s/cat :disk ::data/disk :player-key ::data/id)
        :ret map?)

(defn get-for-player [disk player-key]
  (let [{:keys [view path] :as player-viewmap} (read-object disk [:player player-key])]
    (->> view
         (map (fn [[k v]]
                {k (if (coll? v)
                     (vec (map #(read-object disk [k % ]) v))
                     v)}))
         (reduce into))))


