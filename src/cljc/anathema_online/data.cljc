(ns anathema-online.data
  (:require [clojure.spec.alpha :as s]
            [com.rpl.specter :as sp]
            [clojure.spec.gen.alpha :as sg]
            [clojure.data.json :as cj]
            #?(:clj [hashids.core :as h])
            [cognitect.transit :as transit])
  (:import (java.io ByteArrayOutputStream)))

(s/def ::id (s/and string? #(not (empty? %))))

(s/def ::category (s/or :keyword? keyword? :string? ::id))

(s/def ::navigator (s/or :keyword keyword? :string-key ::id :index (s/and int? #(<= 0 %))))

(s/def ::path (s/+ ::navigator))

(s/def ::swapper (s/fspec :args any?
                          :ret any?))

(s/def ::game-entity (s/keys :req-un [::category ::id]))

(s/fdef change-game-entity
        :args (s/cat :path-in ::path, :new-thing any?, :game-entity ::game-entity)
        :ret ::game-entity)

(defn change-game-entity [path-in new-thing game-entity]
  (sp/transform [(sp/keypath path-in)]
                (fn [a] new-thing)
                game-entity))

(defn to-transit [thing]
  #?(:clj
      (let [out (ByteArrayOutputStream. 4096)
            writer (transit/writer out :json)]
        (transit/write writer thing)
        (.toString out))
     :cljs
       (transit/write (transit/writer :json) (pr-str thing))))

(defn write-data-as [thing format]
  (case format
    :edn (pr-str thing)
    :json (cj/write-str thing)
    :transit (to-transit thing)))

(defn content-type-for [format]
  (case (keyword format)
    :edn "text/edn"
    :json "text/json"
    :transit "text/json"))

#?(:clj (def ^:private hash-ops {:salt "Exalted Is Best Game!"}))
(defn new-id []
  #?(:clj
     (h/encode hash-ops (rand-int 99999) (rand-int 99999) (rand-int 99999))
     :cljs (str "temp_" (.toLocaleDateString (js/Date.)) (rand-int 999999) (rand-int 999999))))