(ns anathema-online.disk
  "Functions for handling 'disk' components, which are maps (records, etc) with the following keys:
  :read-object (fn [this category key] Returns the map from the disk corresponding to the provided category and id.)
  :write-object! (fn [this object] Puts a map containing a :category and :key field into the disk. Returns the disk.)
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
        :args (s/cat :this ::data/disk :category ::data/category :key ::data/id)
        :ret ::data/disk)

(defn read-object [this category key] ((:read-object this) this category key))

(s/fdef write-object!
        :args (s/cat :this ::data/disk :object ::data/game-entity)
        :ret ::data/disk)

(defn write-object! [this object] ((:write-object! this) this object))

(s/fdef clear-category!
        :args (s/cat :this ::data/disk :object ::data/game-entity)
        :ret ::data/disk)

(defn clear-category! [this category] ((:clear-category! this) this category))

(s/fdef change-object!
        :args (s/cat :this ::data/disk :category ::data/category :key ::data/id :path-in ::data/path :swap-fn ::data/swapper)
        :ret ::data/disk)

(defn change-object!
  "Changes a small part of the state. Path-in is a vec of associative keys (ie, meaningful to (get)).
  Returns the disk."
  [this category key path-in change-fn]
  (write-object! this
                   (sp/transform
                     [(apply sp/keypath path-in)]
                     change-fn
                     (read-object this category key))))

(s/fdef get-for-player
        :args (s/cat :disk ::data/disk :player-key ::data/id)
        :ret map?)

(defn get-for-player [disk player-key]
  (->> (read-object disk :player player-key)
       (map (fn [[k v]]
              {k (when (coll? v)
                   (map #(read-object disk k %) v))}))
       (filter (fn [v]
                 (not (nil? (second (first (vec v)))))))
       (reduce into)))


