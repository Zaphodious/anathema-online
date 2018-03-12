(ns anathema-online.data
  (:require [clojure.spec.alpha :as s]
            [com.rpl.specter :as sp]
            [clojure.spec.gen.alpha :as sg]))

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